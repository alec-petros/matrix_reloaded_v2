import toxi.color.*;
import oscP5.*;  
import netP5.*;
import ddf.minim.analysis.*;
import ddf.minim.spi.*;
import ddf.minim.*;
import spout.*;

Spout spout;

Smoothers smoothers;

ColorGradient gradient;
GradientController grads;

Minim minim;  
AudioInput audioInput;
AudioStream in;
MultiChannelBuffer sampleBuffer;

BeatDetect beat;
float kickSize, snareSize, hatSize;
FFT fftLog;
String windowName;

OscP5 oscP5;
float varName;
NetAddress myRemoteLocation;

int spectrumSize;

float increment = 0.03;
float zIndex = 0.0;
float alphaIndex = 0.0;

PFont font;
float maxValues[];

int chunkHeight;
int matrixHeight4;

PShape rectangle;
PShape sq;
int xDiff;

int channels, buffer, bitRate;
float sampleRate;


void setup()
{
  // Visual stuff
  size(720, 256, P3D);
  frameRate(60);
  colorMode(RGB, 255);
  
  // Audio settings - all of these must match your audio interface
  // If you wanna do loopback audio - responding to whatever is coming out of your computer - you need something like Voicemeeter or Loopback
  // This setup assumes you're listening to the stereo inputs on your interface.
  channels = 2; // stereo in
  buffer = 1024; // sampleRate / buffer = minimum detectable frequency
  bitRate = 16; // unsure how this affects the FFT cause this interface can't break 16 bit
  sampleRate = 44100.0; // sampleRate / 2 = maximum detectable frequency, ie nyquist frequency
  
  // For MacOS you'd want to switch this to syphon. Spout sends the video out to Resolume, which distributes everything to the LEDs
  spout = new Spout(this);
  spout.createSender("Spout Processing");
  
  // Create OSC server so everything can be controlled from phone
  oscP5 = new OscP5(this, 8000);   //listening
  myRemoteLocation = new NetAddress("127.0.0.1", 57120);  //  speak to
  
  // The method plug take 3 arguments. Wait for the <keyword>
  oscP5.plug(this, "varName", "keyword");
  
  // minim is our audio handler. it does all the magic
  minim = new Minim(this);
  // getLineIn latency grows over time. Trying the MultiChannelBuffer to fix but, it's happening there too
  // is this just unavoidable with Minim?
  //audioInput = minim.getLineIn();
  in = minim.getInputStream(channels, buffer, sampleRate, bitRate);
  in.open();
  // we pass the input stream to the multichannelbuffer to try to avoid waiting for backed up input stream
  sampleBuffer = new MultiChannelBuffer(buffer, channels);
  
  // create an FFT object for calculating logarithmically spaced averages
  fftLog = new FFT( buffer, sampleRate );
  beat = new BeatDetect(buffer, sampleRate );
  kickSize = snareSize = hatSize = 15;
  
  // beatdetect stuff. Idk if i want to use this yet, leaving commented
  // beat.detectMode(BeatDetect.FREQ_ENERGY);
  // beat.setSensitivity(400);
  
  // calculate averages based on a miminum octave width of 11 Hz
  // split each octave into eight bands
  // needs more tweaking probs. 22 / 3 works nice but doesn't give enough bins
  // I think this setting is going a bit past the nyquist  freq
  // since we end up getting empty bands at the very top end.
  // Can be adjusted around but there should be a goldilocks setting here?
  fftLog.logAverages(11, 8);
  
  // Currently using bartlett FFT window. Can experiment with the others here, idk the diff tbh
  windowName = FFT.BARTLETT.toString();
  fftLog.window(FFT.BARTLETT);
  
  spectrumSize = fftLog.avgSize();
  
  // Smoothers controls instances of smoother class that read, normalize, and apply smoothing to FFT bins
  // Takes them from jittery wide variance numbers to something smooth and useable
  // Number of smoothers dictated by spectrumSize
  smoothers = new Smoothers(spectrumSize);
  
  // Gradient controller handles color palettes. Need to run compose to initialize them on startup
  grads = new GradientController();
  grads.compose();

  // xDiff is the width of each spectrum band, visually. Used for drawing the colored lines across the screen
  xDiff = (width / 2) / spectrumSize;
  // Create the color rectangle once here so we don't need to rebuild one every loop
  rectangle = createShape(RECT, 0, 0, xDiff, height);
  rectangle.setStrokeWeight(0);
}

void draw() {
  background(0);
  
  // perform a forward FFT on the samples in sampleBuffer
  in.read(sampleBuffer);
  
  // Currently unused, but to explore later. Beat detect isn't as reliable as i'd like
  // Writing my own variation in smoothers.getKick()
  beat.detect( sampleBuffer.getChannel(0) );
 
  // Perform actual FFT on sample buffer
  fftLog.forward( sampleBuffer.getChannel(0) );

  // Pass fftLog to smoothers to normalize and smooth our numbers
  smoothers.update(fftLog);

  // This is where we loop across the bins and draw each color
  // Right now it's simply amplitude = alpha, but you can do whatever you want with smoother values
  for (int i = 0; i < fftLog.avgSize(); i++) {
    int xLeft = xDiff * i;
    int xRight = xDiff * (i + 1);

     noStroke();
     // pushMatrix and popMatrix let us move the rectangle we draw around the screen - first is from the left
     pushMatrix();
     translate(xLeft, 0);
     // We pass xLeft to get the color we want from the palette, and the smoother val mapped to alpha
     rectangle.setFill(grads.getColor(xLeft, parseInt(map(smoothers.get(i), 0, 1, 0, 255))));
     shape(rectangle);
     popMatrix();
     // Second pushMatrix draws the same value but from the right side of the screen
     pushMatrix();
     translate(width - xRight, 0);
     rectangle.setFill(grads.getColor(width - xLeft - 1, parseInt(map(smoothers.get(i), 0, 1, 0, 255))));
     shape(rectangle);
     popMatrix();
  }


  // Currently disabled, need to tweak the kick detect now that low freq bins are changed
  if (smoothers.kickDraw) {
    float kick = smoothers.getKick();
    fill(color(0, 0, 0));
    //rect (0, 1.1 * matrixHeight4, width, 1.6 * matrixHeight4);
    //fill(grads.getColor(0, parseInt(map(kick, 0, 1, 0, 255))));
    //rect (0, 1.1 * matrixHeight4, width, 1.6 * matrixHeight4);
    
    rect (0, 0, width, height);
    fill(grads.getColor(0, parseInt(map(kick, 0, 1, 0, 255))));
    rect (0, 0, width, height);
  }

  // Sends the screen out on Spout (currently to resolume) to be distributed to LEDs
  spout.sendTexture();
}

// Handles OSC messages
int parsePush(OscMessage message) {
  String partial = message.addrPattern().replace("/1/push", "");
  return parseInt(partial);
}

/* incoming osc message are forwarded to the oscEvent method. */
void oscEvent(OscMessage theOscMessage) {
  /* print the address pattern and the typetag of the received OscMessage */
  print("### received an osc message.");
  print(" addrpattern: "+theOscMessage.addrPattern());
  println(" typetag: "+theOscMessage.get(0).floatValue());
  
  // Selects color palettes for A and B gradients
  if (theOscMessage.addrPattern().contains("/1/push")) {
    int index = parsePush(theOscMessage);
    if (index < 9) {
      grads.setA(index - 1);
    } else {
      grads.setB(index - 9);
    }
  }
  
  // Crossfader for color gradients
  if (theOscMessage.checkAddrPattern("/1/fader1")) {
    grads.setCross(theOscMessage.get(0).floatValue());
  }
  
  // Sets sensitivity of algorithm
  if (theOscMessage.checkAddrPattern("/1/fader2")) {
    grads.setSens(theOscMessage.get(0).floatValue());
    smoothers.setSens(theOscMessage.get(0).floatValue());
  }
  
  // Sets smooth time 
  if (theOscMessage.checkAddrPattern("/1/fader3")) {
    smoothers.setSmooth(theOscMessage.get(0).floatValue() * 2);
  }
  
  // XY pad for input threshold and gradient crossfader
  if (theOscMessage.checkAddrPattern("/2/multixy1/1")) {
    smoothers.setThresh(theOscMessage.get(0).floatValue());
    grads.setCross(theOscMessage.get(1).floatValue());
  }
  
  // XY pad for sensitivity and smoother
  if (theOscMessage.checkAddrPattern("/2/multixy1/2")) {
    smoothers.setSens(theOscMessage.get(0).floatValue());
    smoothers.setSmooth(theOscMessage.get(1).floatValue() * 30);
  }

  // Button to toggle kickDraw
  if (theOscMessage.checkAddrPattern("/1/toggle1")) {
    smoothers.toggleKick();
  }
}
