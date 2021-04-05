import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import toxi.color.*; 
import oscP5.*; 
import netP5.*; 
import ddf.minim.analysis.*; 
import ddf.minim.*; 
import spout.*; 
import java.net.*; 
import java.util.Arrays; 
import java.util.Arrays; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class matrix_reloaded extends PApplet {


  





OPC opc;
Spout spout;

Smoothers smoothers;

ColorGradient gradient;
GradientController grads;

Minim minim;  
AudioInput audioInput;
BeatDetect beat;
FFT fftLin;
FFT fftLog;
String windowName;

OscP5 oscP5;
float varName;
NetAddress myRemoteLocation;

float height3;
float height23;
float spectrumScale = 4;
int spectrumDivisor = 5;
int spectrumSize;

float increment = 0.03f;
float zIndex = 0.0f;
float alphaIndex = 0.0f;

PFont font;
float maxValues[];

int chunkHeight;
int matrixHeight4;

PShape rectangle;
PShape sq;
int xDiff;

public void setup()
{
  
  frameRate(120);
  height3 = height/3;
  height23 = 2*height/3;
  
  spout = new Spout(this);
  spout.createSender("Spout Processing");

  maxValues = new float[9];
  
  oscP5 = new OscP5(this, 8000);   //listening
  myRemoteLocation = new NetAddress("127.0.0.1", 57120);  //  speak to
  
  // The method plug take 3 arguments. Wait for the <keyword>
  oscP5.plug(this, "varName", "keyword");

  // maxValues[0] = 53.05477;
  // maxValues[1] = 20.453335;
  // maxValues[2] = 15.642995;
  // maxValues[3] = 8.958029;
  // maxValues[4] = 7.492257;
  // maxValues[5] = 1.5066102;
  // maxValues[6] = 0.48962182;
  // maxValues[7] = 0.21129508;
  // maxValues[8] = 0.039143644;

  maxValues[0] = 180.5942f;
  maxValues[1] = 59.74217f;
  maxValues[2] = 30.849203f;
  maxValues[3] = 22.770254f;
  maxValues[4] = 11.008822f;
  maxValues[5] = 6.8135614f;
  maxValues[6] = 4.7134547f;
  maxValues[7] = 1.7664095f;
  maxValues[8] = 0.0000001f;

  // Connect to the local instance of fcserver. You can change this line to connect to another computer's fcserver
  opc = new OPC(this, "127.0.0.1", 7890);
  // opc = new OPC(this, "127.0.0.1", 7890);
  //opc = new OPC(this, "192.168.1.7", 7890);
  
   //opc.setColorCorrection(2.5, 2, 2, 2);

  chunkHeight = height / 4;
  matrixHeight4 = chunkHeight / 4;

  // oldskool matrix
  opc.ledStrip(320, 64, width / 4, 3 * matrixHeight4, width / 130.0f, 0, true);
  opc.ledStrip(256, 64, width / 4, 2 * matrixHeight4, width / 130.0f, 0,  true);
  opc.ledStrip(192, 64, width / 4, 1 * matrixHeight4, width / 130.0f, 0, true);
  opc.ledStrip(128, 64, 3 * (width / 4), 1 * matrixHeight4, width / 130.0f, 0,  false);
  opc.ledStrip(64, 64, 3 * (width / 4), 3 * matrixHeight4, width / 130.0f, 0,  false);
  opc.ledStrip(0, 64, 3 * (width / 4), 2 * matrixHeight4, width / 130.0f, 0, false);

  // iron cross wings 
  opc.ledStrip(384, 64,  width / 7, (1 * matrixHeight4) + chunkHeight, 3, 0, true);
  opc.ledStrip(448, 64, 6 * (width / 7), (1 * matrixHeight4) + chunkHeight, 3, 0, false);
  
  // iron cross angled tubes
  opc.ledStrip(512, 64, 1 * width / 4, (2 * matrixHeight4) + chunkHeight, width / 230.0f, 0, true);
  opc.ledStrip(640, 64, 3 * (width / 4), (2 * matrixHeight4) + chunkHeight, width / 230.0f, 0, false);
  
  // iron cross center tube
  opc.ledStrip(576, 64, width / 2, (3 * matrixHeight4) + chunkHeight, 0 - (width / 160.0f), 0, false);
  
  // opc.ledStrip(772, 64,1, 1.5 * (height / 4), width / 160.0, 0, true);
  // opc.ledStrip(836, 64, 1 * width / 4, 2.5 * (height / 4), width / 230.0, 0, true);
  // opc.ledStrip(900, 64, 3 * (width / 4), 2.5 * (height / 4), width / 230.0, 0, false);
  // opc.ledStrip(964, 64, width - 1, 1.5 * (height / 4), 0 - (width / 160.0), 0, false);
  
  //opc.ledStrip(772, 64, width / 4, (2 * matrixHeight4) + (chunkHeight * 2), width / 130.0, 0,  true);
  //opc.ledStrip(836, 64, width / 4, (1 * matrixHeight4) + (chunkHeight * 2), width / 130.0, 0, true);
  //opc.ledStrip(900, 64, 3 * (width / 4), (1 * matrixHeight4) + (chunkHeight * 2), width / 130.0, 0,  false);
  //opc.ledStrip(964, 64, 3 * (width / 4), (2 * matrixHeight4) + (chunkHeight * 2), width / 130.0, 0, false);
  
  
  // Make the status LED quiet
  opc.setStatusLed(false);
  opc.showLocations(true);
  
  colorMode(RGB, 255);
  
  minim = new Minim(this);
  audioInput = minim.getLineIn();
  
  // create an FFT object that has a time-domain buffer the same size as audioInput's sample buffer
  // note that this needs to be a power of two 
  // and that it means the size of the spectrum will be 1024. 
  // fftLin = new FFT( audioInput.bufferSize(), audioInput.sampleRate() );
  
  // calculate the averages by grouping frequency bands linearly. use 30 averages.
  // fftLin.linAverages( 256 );
  
  // create an FFT object for calculating logarithmically spaced averages
  fftLog = new FFT( audioInput.bufferSize(), audioInput.sampleRate() );

  // beat = new BeatDetect(audioInput.bufferSize(), audioInput.sampleRate());
  // beat.detectMode(BeatDetect.FREQ_ENERGY);
  // beat.setSensitivity(400);
  
  // calculate averages based on a miminum octave width of 22 Hz
  // split each octave into three bands
  // this should result in 30 averages
  fftLog.logAverages(22, 6);
  
  spectrumSize = fftLog.avgSize();
  
  smoothers = new Smoothers(spectrumSize);
  
  grads = new GradientController();
  grads.compose();

  windowName = FFT.BARTLETT.toString();

  xDiff = (width / 2) / spectrumSize;
  rectangle = createShape(RECT, 0, 0, xDiff, height);
  rectangle.setStrokeWeight(0);
}

public void draw() {
  background(0);
  
  // perform a forward FFT on the samples in audioInput's mix buffer
  // note that if audioInput were a MONO file, this would be the same as using audioInput.left or audioInput.right
  // fftLin.forward( audioInput.mix );
  fftLog.forward( audioInput.mix );
  // beat.detect( audioInput.mix );

  smoothers.update(fftLog);

  for (int i = 0; i < spectrumSize; i++) {
    int xLeft = xDiff * i;
    int xRight = xDiff * (i + 1);

     noStroke();
     pushMatrix();
     translate(xLeft, 0);
     rectangle.setFill(grads.getColor(xLeft, parseInt(map(smoothers.get(i), 0, 1, 0, 255))));
     shape(rectangle);
     popMatrix();
     pushMatrix();
     translate(width - xRight, 0);
     rectangle.setFill(grads.getColor(width - xLeft - 1, parseInt(map(smoothers.get(i), 0, 1, 0, 255))));
     shape(rectangle);
     popMatrix();
  }

  if (smoothers.kickDraw) {
    float kick = smoothers.getKick();
    fill(color(0, 0, 0));
    rect (0, 1.1f * matrixHeight4, width, 1.6f * matrixHeight4);
    fill(grads.getColor(0, parseInt(map(kick, 0, 1, 0, 255))));
    rect (0, 1.1f * matrixHeight4, width, 1.6f * matrixHeight4);
  }
  
  // println(fftSmoothers[0].maxVal(), fftSmoothers[1].maxVal(), fftSmoothers[2].maxVal(), fftSmoothers[3].maxVal(), fftSmoothers[4].maxVal(), fftSmoothers[5].maxVal(), fftSmoothers[6].maxVal(), fftSmoothers[7].maxVal());
  
  spout.sendTexture();
}

public int parsePush(OscMessage message) {
  String partial = message.addrPattern().replace("/1/push", "");
  return parseInt(partial);
}

/* incoming osc message are forwarded to the oscEvent method. */
public void oscEvent(OscMessage theOscMessage) {
  /* print the address pattern and the typetag of the received OscMessage */
  print("### received an osc message.");
  print(" addrpattern: "+theOscMessage.addrPattern());
  println(" typetag: "+theOscMessage.get(0).floatValue());
  
  if (theOscMessage.addrPattern().contains("/1/push")) {
    int index = parsePush(theOscMessage);
    if (index < 9) {
      grads.setA(index - 1);
    } else {
      grads.setB(index - 9);
    }
  }
  
  if (theOscMessage.checkAddrPattern("/1/fader1")) {
    grads.setCross(theOscMessage.get(0).floatValue());
  }
  
  if (theOscMessage.checkAddrPattern("/1/fader2")) {
    grads.setSens(theOscMessage.get(0).floatValue());
    smoothers.setSens(theOscMessage.get(0).floatValue() * 3);
  }
  
  if (theOscMessage.checkAddrPattern("/1/fader3")) {
    smoothers.setSmooth(theOscMessage.get(0).floatValue() * 2);
  }
    
  if (theOscMessage.checkAddrPattern("/2/multixy1/1")) {
    smoothers.setThresh(theOscMessage.get(0).floatValue());
    grads.setCross(theOscMessage.get(1).floatValue());
  }
  
  if (theOscMessage.checkAddrPattern("/2/multixy1/2")) {
    smoothers.setSens(theOscMessage.get(0).floatValue() * 2);
    smoothers.setSmooth(theOscMessage.get(1).floatValue() * 30);
  }

  if (theOscMessage.checkAddrPattern("/1/toggle1")) {
    smoothers.toggleKick();
  }
}

ColorGradient hulk;
ColorGradient satou;

class GradientController {
  
  int indexA, indexB;
  float crossPos, sensitivity;
  ColorGradient[] gradients = new ColorGradient[10];
  ColorList listA, listB;
  
  GradientController() {
    indexA = 0;
    indexB = 1;
    crossPos = 0;
    sensitivity = 1;
  }
  
  public void compose() {
      gradients[0] = new ColorGradient();
      
      // purple pink
      gradients[0].addColorAt(0.0f * (width / 10), TColor.newRGB(n(45), n(0), n(247)));
      gradients[0].addColorAt(3.0f * (width / 10), TColor.newRGB(n(137), n(0), n(242)));
      gradients[0].addColorAt(5.0f * (width / 10), TColor.newRGB(n(177), n(0), n(232)));
      gradients[0].addColorAt(7.0f * (width / 10), TColor.newRGB(n(209), n(0), n(209)));
      gradients[0].addColorAt(9.0f * (width / 10), TColor.newRGB(n(229), n(0), n(164)));
      gradients[0].addColorAt(10.0f * (width / 10), TColor.newRGB(n(242), n(0), n(137)));
      
      // satou
      gradients[1] = new ColorGradient();
      gradients[1].addColorAt(0.0f, TColor.newRGB(n(255), n(255), n(255)));
      gradients[1].addColorAt(width / 2, TColor.newRGB(n(255), n(0), n(0)));
      gradients[1].addColorAt(width, TColor.newRGB(n(255), n(255), n(255)));
      
      // aurora
      gradients[2] = new ColorGradient();
      gradients[2].addColorAt(0.0f, TColor.newRGB(n(20), n(232), n(30)));
      gradients[2].addColorAt(1 * (width / 10), TColor.newRGB(n(0), n(234), n(141)));
      gradients[2].addColorAt(3 * (width / 10), TColor.newRGB(n(1), n(126), n(255)));
      gradients[2].addColorAt(width / 2, TColor.newRGB(n(141), n(0), n(196)));
      gradients[2].addColorAt(7 * (width / 10), TColor.newRGB(n(1), n(126), n(255)));
      gradients[2].addColorAt(9 * (width / 10), TColor.newRGB(n(0), n(234), n(141)));
      gradients[2].addColorAt(width, TColor.newRGB(n(20), n(232), n(30)));
      
      //disco
      gradients[3] = new ColorGradient();
      gradients[3].addColorAt(0.0f, TColor.newRGB(n(255), n(201), n(0)));
      gradients[3].addColorAt(0.5f * (width / 10), TColor.newRGB(n(213), n(173), n(24)));
      gradients[3].addColorAt(1 * (width / 10), TColor.newRGB(n(234), n(234), n(234)));
      gradients[3].addColorAt(width / 2, TColor.newRGB(n(204), n(204), n(204)));
      gradients[3].addColorAt(9 * (width / 10), TColor.newRGB(n(234), n(234), n(234)));
      gradients[3].addColorAt(9.5f * (width / 10), TColor.newRGB(n(213), n(173), n(24)));
      gradients[3].addColorAt(width, TColor.newRGB(n(255), n(201), n(0)));

      // razzle dazzle
      gradients[4] = new ColorGradient();
      gradients[4].addColorAt(0.0f, TColor.newRGB(n(15), n(192), n(252)));
      gradients[4].addColorAt(1.5f * (width / 10), TColor.newRGB(n(123), n(29), n(175)));
      gradients[4].addColorAt(3 * (width / 10), TColor.newRGB(n(255), n(47), n(185)));
      gradients[4].addColorAt(width / 2, TColor.newRGB(n(212), n(255), n(71)));
      gradients[4].addColorAt(7 * (width / 10), TColor.newRGB(n(255), n(47), n(185)));
      gradients[4].addColorAt(8.5f * (width / 10), TColor.newRGB(n(123), n(29), n(175)));
      gradients[4].addColorAt(width, TColor.newRGB(n(15), n(192), n(252)));

      // sinestro
      gradients[5] = new ColorGradient();
      gradients[5].addColorAt(0 * (width / 10), TColor.newRGB(n(32), n(191), n(85)));
      gradients[5].addColorAt(3 * (width / 10), TColor.newRGB(n(11), n(79), n(108)));
      gradients[5].addColorAt(5 * (width / 10), TColor.newRGB(n(1), n(186), n(239)));
      gradients[5].addColorAt(7 * (width / 10), TColor.newRGB(n(11), n(79), n(108)));
      gradients[5].addColorAt(10 * (width / 10), TColor.newRGB(n(32), n(191), n(85)));

      // LA Vibes
      gradients[6] = new ColorGradient();
      gradients[6].addColorAt(0.0f, TColor.newRGB(n(0), n(254), n(202)));
      gradients[6].addColorAt(2 * (width / 10), TColor.newRGB(n(251), n(240), n(75)));
      gradients[6].addColorAt(4 * (width / 10), TColor.newRGB(n(255), n(130), n(225)));
      gradients[6].addColorAt(width / 2, TColor.newRGB(n(128), n(91), n(0237)));
      gradients[6].addColorAt(6 * (width / 10), TColor.newRGB(n(255), n(130), n(225)));
      gradients[6].addColorAt(8 * (width / 10), TColor.newRGB(n(251), n(240), n(75)));
      gradients[6].addColorAt(width, TColor.newRGB(n(0), n(254), n(202)));

      
      // o u t r u n
      gradients[7] = new ColorGradient();
      gradients[7].addColorAt(0.0f, TColor.newRGB(n(255), n(108), n(17)));
      gradients[7].addColorAt(3 * (width / 10), TColor.newRGB(n(255), n(56), n(100)));
      gradients[7].addColorAt(3.5f * (width / 10), TColor.newRGB(n(45), n(226), n(230)));
      gradients[7].addColorAt(width / 2, TColor.newRGB(n(101), n(13), n(137)));
      gradients[7].addColorAt(6.5f * (width / 10), TColor.newRGB(n(45), n(226), n(230)));
      gradients[7].addColorAt(7 * (width / 10), TColor.newRGB(n(255), n(56), n(100)));
      gradients[7].addColorAt(width, TColor.newRGB(n(255), n(108), n(17)));

      renderList();
  }
  
  public void renderList() {
    listA = gradients[indexA].calcGradient(0, width);
    listB = gradients[indexB].calcGradient(0, width);
  }
  
  public int getColor(int pos, int alpha) {
    float[] rgba = new float[4];
    listA.get(pos).blend(listB.get(pos), crossPos).toRGBAArray(rgba);
    return color(nn(rgba[0]), nn(rgba[1]), nn(rgba[2]), alpha);
  }
  
  public void setCross(float pos) {
    crossPos = pos;
    renderList();
  }
  
  public void setA(int index) {
    if (index >= 0) {
      indexA = index;
    }
    renderList();
  }
  
    public void setB(int index) {
    if (index >= 0) {
      indexB = index;
    }
    renderList();
  }
  
  public void setSens(float sens) {
    sensitivity = sens;
  }
  
  public float getSens() {
    return sensitivity;
  }
  
  public float n(int val) {
    return map(val, 0, 255, 0, 1);
  }

  public float nn(float val) {
    return map(val, 0, 1, 0, 255);
  }
}
class KickSmoother {
    float maxVal = 0, maxThresh = 0, value = 0, rate = 15.0f, kickPowerThresh = 100.00f, currentSum = 0, kickPowerMax = 180.00f;
    int bassLow = 8, bassHigh = 12, lastUpdated = 0, cooldown = 16;

    KickSmoother(float smoothRate) {
        rate = smoothRate;
    }

    public void update(float[] values) {
        float newSum = 0;
        float topVal = 0;
        for (int i = bassLow; i < bassHigh; i++) {
            float smootherValue = values[i];
            if (smootherValue > topVal) topVal = smootherValue;
            newSum += smootherValue;
        }

        if (topVal > maxVal) maxVal = topVal;

        if (topVal >= maxThresh && lastUpdated > cooldown) {
            println("fired: ", topVal, maxThresh);
            value = 1;
            lastUpdated = 0;
            maxThresh = min(maxVal, kickPowerMax);
        } else {
            float threshCap = min(maxThresh * 0.992f, kickPowerMax);
            maxThresh = max(threshCap, kickPowerThresh);
            value = (value * rate) / (rate + 1);
        }

        if (lastUpdated > 60 && maxVal > kickPowerThresh) {
            maxVal = maxVal * 0.9995f;
        }
        
        currentSum = newSum;
        lastUpdated += 1;
    }

    // void update(float[] values) {
    //     float topVal = 0;
    //     for (int i = bassLow; i < bassHigh; i++) {
    //         float smootherValue = values[i];
    //         if (smootherValue > topVal) topVal = smootherValue;
    //     }

    //     if (topVal > maxVal) {
    //         maxVal = topVal;
    //     }

    //     if (topVal >= (maxVal * 0.6) && lastUpdated > cooldown) {
    //         value = 1;
    //         lastUpdated = 0;
    //     } else {
    //         value = (value * rate) / (rate + 1);
    //     }

    //     if (lastUpdated > 60 && maxVal > kickPowerThresh) {
    //         maxVal = maxVal * 0.98;
    //     }
        
    //     lastUpdated += 1;
    // }

    public float get() {
        return value;
    }

    public void setBassRange(int low, int high) {
        bassLow = low;
        bassHigh = high;
    }
}
/*
 * Simple Open Pixel Control client for Processing,
 * designed to sample each LED's color from some point on the canvas.
 *
 * Micah Elizabeth Scott, 2013
 * This file is released into the public domain.
 */




public class OPC implements Runnable
{
  Thread thread;
  Socket socket;
  OutputStream output, pending;
  String host;
  int port;

  int[] pixelLocations;
  byte[] packetData;
  byte firmwareConfig;
  String colorCorrection;
  boolean enableShowLocations;

  OPC(PApplet parent, String host, int port)
  {
    this.host = host;
    this.port = port;
    thread = new Thread(this);
    thread.start();
    this.enableShowLocations = true;
    parent.registerMethod("draw", this);
  }

  // Set the location of a single LED
  public void led(int index, int x, int y)  
  {
    // For convenience, automatically grow the pixelLocations array. We do want this to be an array,
    // instead of a HashMap, to keep draw() as fast as it can be.
    if (pixelLocations == null) {
      pixelLocations = new int[index + 1];
    } else if (index >= pixelLocations.length) {
      pixelLocations = Arrays.copyOf(pixelLocations, index + 1);
    }

    pixelLocations[index] = x + width * y;
  }
  
  // Set the location of several LEDs arranged in a strip.
  // Angle is in radians, measured clockwise from +X.
  // (x,y) is the center of the strip.
  public void ledStrip(int index, int count, float x, float y, float spacing, float angle, boolean reversed)
  {
    float s = sin(angle);
    float c = cos(angle);
    for (int i = 0; i < count; i++) {
      led(reversed ? (index + count - 1 - i) : (index + i),
        (int)(x + (i - (count-1)/2.0f) * spacing * c + 0.5f),
        (int)(y + (i - (count-1)/2.0f) * spacing * s + 0.5f));
    }
  }

  // Set the locations of a ring of LEDs. The center of the ring is at (x, y),
  // with "radius" pixels between the center and each LED. The first LED is at
  // the indicated angle, in radians, measured clockwise from +X.
  public void ledRing(int index, int count, float x, float y, float radius, float angle)
  {
    for (int i = 0; i < count; i++) {
      float a = angle + i * 2 * PI / count;
      led(index + i, (int)(x - radius * cos(a) + 0.5f),
        (int)(y - radius * sin(a) + 0.5f));
    }
  }

  // Set the location of several LEDs arranged in a grid. The first strip is
  // at 'angle', measured in radians clockwise from +X.
  // (x,y) is the center of the grid.
  public void ledGrid(int index, int stripLength, int numStrips, float x, float y,
               float ledSpacing, float stripSpacing, float angle, boolean zigzag,
               boolean flip)
  {
    float s = sin(angle + HALF_PI);
    float c = cos(angle + HALF_PI);
    for (int i = 0; i < numStrips; i++) {
      ledStrip(index + stripLength * i, stripLength,
        x + (i - (numStrips-1)/2.0f) * stripSpacing * c,
        y + (i - (numStrips-1)/2.0f) * stripSpacing * s, ledSpacing,
        angle, zigzag && ((i % 2) == 1) != flip);
    }
  }

  // Set the location of 64 LEDs arranged in a uniform 8x8 grid.
  // (x,y) is the center of the grid.
  public void ledGrid8x8(int index, float x, float y, float spacing, float angle, boolean zigzag,
                  boolean flip)
  {
    ledGrid(index, 8, 8, x, y, spacing, spacing, angle, zigzag, flip);
  }

  // Should the pixel sampling locations be visible? This helps with debugging.
  // Showing locations is enabled by default. You might need to disable it if our drawing
  // is interfering with your processing sketch, or if you'd simply like the screen to be
  // less cluttered.
  public void showLocations(boolean enabled)
  {
    enableShowLocations = enabled;
  }
  
  // Enable or disable dithering. Dithering avoids the "stair-stepping" artifact and increases color
  // resolution by quickly jittering between adjacent 8-bit brightness levels about 400 times a second.
  // Dithering is on by default.
  public void setDithering(boolean enabled)
  {
    if (enabled)
      firmwareConfig &= ~0x01;
    else
      firmwareConfig |= 0x01;
    sendFirmwareConfigPacket();
  }

  // Enable or disable frame interpolation. Interpolation automatically blends between consecutive frames
  // in hardware, and it does so with 16-bit per channel resolution. Combined with dithering, this helps make
  // fades very smooth. Interpolation is on by default.
  public void setInterpolation(boolean enabled)
  {
    if (enabled)
      firmwareConfig &= ~0x02;
    else
      firmwareConfig |= 0x02;
    sendFirmwareConfigPacket();
  }

  // Put the Fadecandy onboard LED under automatic control. It blinks any time the firmware processes a packet.
  // This is the default configuration for the LED.
  public void statusLedAuto()
  {
    firmwareConfig &= 0x0C;
    sendFirmwareConfigPacket();
  }    

  // Manually turn the Fadecandy onboard LED on or off. This disables automatic LED control.
  public void setStatusLed(boolean on)
  {
    firmwareConfig |= 0x04;   // Manual LED control
    if (on)
      firmwareConfig |= 0x08;
    else
      firmwareConfig &= ~0x08;
    sendFirmwareConfigPacket();
  } 

  // Set the color correction parameters
  public void setColorCorrection(float gamma, float red, float green, float blue)
  {
    colorCorrection = "{ \"gamma\": " + gamma + ", \"whitepoint\": [" + red + "," + green + "," + blue + "]}";
    sendColorCorrectionPacket();
  }
  
  // Set custom color correction parameters from a string
  public void setColorCorrection(String s)
  {
    colorCorrection = s;
    sendColorCorrectionPacket();
  }

  // Send a packet with the current firmware configuration settings
  public void sendFirmwareConfigPacket()
  {
    if (pending == null) {
      // We'll do this when we reconnect
      return;
    }
 
    byte[] packet = new byte[9];
    packet[0] = (byte)0x00; // Channel (reserved)
    packet[1] = (byte)0xFF; // Command (System Exclusive)
    packet[2] = (byte)0x00; // Length high byte
    packet[3] = (byte)0x05; // Length low byte
    packet[4] = (byte)0x00; // System ID high byte
    packet[5] = (byte)0x01; // System ID low byte
    packet[6] = (byte)0x00; // Command ID high byte
    packet[7] = (byte)0x02; // Command ID low byte
    packet[8] = (byte)firmwareConfig;

    try {
      pending.write(packet);
    } catch (Exception e) {
      dispose();
    }
  }

  // Send a packet with the current color correction settings
  public void sendColorCorrectionPacket()
  {
    if (colorCorrection == null) {
      // No color correction defined
      return;
    }
    if (pending == null) {
      // We'll do this when we reconnect
      return;
    }

    byte[] content = colorCorrection.getBytes();
    int packetLen = content.length + 4;
    byte[] header = new byte[8];
    header[0] = (byte)0x00;               // Channel (reserved)
    header[1] = (byte)0xFF;               // Command (System Exclusive)
    header[2] = (byte)(packetLen >> 8);   // Length high byte
    header[3] = (byte)(packetLen & 0xFF); // Length low byte
    header[4] = (byte)0x00;               // System ID high byte
    header[5] = (byte)0x01;               // System ID low byte
    header[6] = (byte)0x00;               // Command ID high byte
    header[7] = (byte)0x01;               // Command ID low byte

    try {
      pending.write(header);
      pending.write(content);
    } catch (Exception e) {
      dispose();
    }
  }

  // Automatically called at the end of each draw().
  // This handles the automatic Pixel to LED mapping.
  // If you aren't using that mapping, this function has no effect.
  // In that case, you can call setPixelCount(), setPixel(), and writePixels()
  // separately.
  public void draw()
  {
    if (pixelLocations == null) {
      // No pixels defined yet
      return;
    }
    if (output == null) {
      return;
    }

    int numPixels = pixelLocations.length;
    int ledAddress = 4;

    setPixelCount(numPixels);
    loadPixels();

    for (int i = 0; i < numPixels; i++) {
      int pixelLocation = pixelLocations[i];
      int pixel = pixels[pixelLocation];

      packetData[ledAddress] = (byte)(pixel >> 16);
      packetData[ledAddress + 1] = (byte)(pixel >> 8);
      packetData[ledAddress + 2] = (byte)pixel;
      ledAddress += 3;

      if (enableShowLocations) {
        pixels[pixelLocation] = 0xFFFFFF ^ pixel;
      }
    }

    writePixels();

    if (enableShowLocations) {
      updatePixels();
    }
  }
  
  // Change the number of pixels in our output packet.
  // This is normally not needed; the output packet is automatically sized
  // by draw() and by setPixel().
  public void setPixelCount(int numPixels)
  {
    int numBytes = 3 * numPixels;
    int packetLen = 4 + numBytes;
    if (packetData == null || packetData.length != packetLen) {
      // Set up our packet buffer
      packetData = new byte[packetLen];
      packetData[0] = (byte)0x00;              // Channel
      packetData[1] = (byte)0x00;              // Command (Set pixel colors)
      packetData[2] = (byte)(numBytes >> 8);   // Length high byte
      packetData[3] = (byte)(numBytes & 0xFF); // Length low byte
    }
  }
  
  // Directly manipulate a pixel in the output buffer. This isn't needed
  // for pixels that are mapped to the screen.
  public void setPixel(int number, int c)
  {
    int offset = 4 + number * 3;
    if (packetData == null || packetData.length < offset + 3) {
      setPixelCount(number + 1);
    }

    packetData[offset] = (byte) (c >> 16);
    packetData[offset + 1] = (byte) (c >> 8);
    packetData[offset + 2] = (byte) c;
  }
  
  // Read a pixel from the output buffer. If the pixel was mapped to the display,
  // this returns the value we captured on the previous frame.
  public int getPixel(int number)
  {
    int offset = 4 + number * 3;
    if (packetData == null || packetData.length < offset + 3) {
      return 0;
    }
    return (packetData[offset] << 16) | (packetData[offset + 1] << 8) | packetData[offset + 2];
  }

  // Transmit our current buffer of pixel values to the OPC server. This is handled
  // automatically in draw() if any pixels are mapped to the screen, but if you haven't
  // mapped any pixels to the screen you'll want to call this directly.
  public void writePixels()
  {
    if (packetData == null || packetData.length == 0) {
      // No pixel buffer
      return;
    }
    if (output == null) {
      return;
    }

    try {
      output.write(packetData);
    } catch (Exception e) {
      dispose();
    }
  }

  public void dispose()
  {
    // Destroy the socket. Called internally when we've disconnected.
    // (Thread continues to run)
    if (output != null) {
      println("Disconnected from OPC server");
    }
    socket = null;
    output = pending = null;
  }

  public void run()
  {
    // Thread tests server connection periodically, attempts reconnection.
    // Important for OPC arrays; faster startup, client continues
    // to run smoothly when mobile servers go in and out of range.
    for(;;) {

      if(output == null) { // No OPC connection?
        try {              // Make one!
          socket = new Socket(host, port);
          socket.setTcpNoDelay(true);
          pending = socket.getOutputStream(); // Avoid race condition...
          println("Connected to OPC server");
          sendColorCorrectionPacket();        // These write to 'pending'
          sendFirmwareConfigPacket();         // rather than 'output' before
          output = pending;                   // rest of code given access.
          // pending not set null, more config packets are OK!
        } catch (ConnectException e) {
          dispose();
        } catch (IOException e) {
          dispose();
        }
      }

      // Pause thread to avoid massive CPU load
      try {
        Thread.sleep(500);
      }
      catch(InterruptedException e) {
      }
    }
  }
}
class Smoother {
    float rawValue = 0, smoothValue, smoothRate, maxVal, smoothMaxVal, sensitivity, thresh;

    Smoother(float rate) {
        smoothValue = 0.0f;
        smoothRate = rate;
        maxVal = 0;
        sensitivity = 1;
        thresh = 0.0f;
        smoothMaxVal = 0.5f;
    }

    public void update(float newValue) {
        float normalVal = normalizeValue(newValue, smoothMaxVal);
        rawValue = newValue;

        if (newValue > maxVal) {
            maxVal = newValue;
            smoothMaxVal = newValue;
        } else {
            smoothMaxVal = maxVal * 0.999f;
        }
        if ((newValue > smoothValue) && (normalVal > thresh)) {
            smoothValue = newValue;
        } else {
            smoothValue = (smoothValue * smoothRate) / (smoothRate + 1);
        }
    }

    public float value() {
        return rawValue;
    }

    public float normalSmoothValue() {
        return normalizeValue(smoothValue, smoothMaxVal / sensitivity);
    }

    public float normalizeValue(float newValue, float maxNormal) {
        float freshVal = map(newValue, 0, maxNormal, 0, 1);
        return (float)Math.pow(freshVal, 2);
    }

    public float maxVal() {
        return maxVal;
    }
    
    public void setSens(float sens) {
      sensitivity = sens;
    }
    
    public void setSmooth(float rate) {
      smoothRate = rate;
    }
    
    public void setThresh(float newThresh) {
      thresh = newThresh;
    }
}


class Smoothers {
    float smoothValue, smoothRate, sensitivity, thresh;
    int size;
    boolean kickDraw = false;
    Smoother[] smoothers = new Smoother[1];;
    KickSmoother kick;
    
    Smoothers(int spectrumSize) {
        size = spectrumSize;
        smoothRate = 30.0f;
        smoothValue = 0.0f;
        sensitivity = 2;
        thresh = 0;
        initializeSmoothers();
    }
    
    private void initializeSmoothers() {
      smoothers = (Smoother[])expand(smoothers, size);
      for (int i = 0; i < spectrumSize; i++) {
        smoothers[i] = new Smoother(smoothRate);
      }
      Smoother[] bassSmoothers = Arrays.copyOfRange(smoothers, 1, 5);
      kick = new KickSmoother(smoothRate);
    }

    public void update(FFT fftLog) {
      float[] values = new float[size];
      for (int i = 0; i < size; i++) {
        smoothers[i].update(fftLog.getAvg(i));
        values[i] = smoothers[i].value();
      }

      kick.update(values);
    }
    
    public void setSens(float sens) {
      sensitivity = sens;
      updateValuesForSmoothers();
    }
    
    public void setSmooth(float rate) {
      smoothRate = rate;
      updateValuesForSmoothers();
    }
    
    public void setThresh(float newThresh) {
      thresh = newThresh;
      updateValuesForSmoothers();
    }
    
    private void updateValuesForSmoothers() {
      for (int i = 0; i < size; i++) {
        smoothers[i].setSens(sensitivity);
        smoothers[i].setThresh(thresh);
        smoothers[i].setSmooth(smoothRate);
      }
    }
    
    public float get(int index) {
      return smoothers[index].normalSmoothValue();
    }

    public float getWithoutSmooth(int index) {
      return smoothers[index].normalSmoothValue();
    }
    
    private int chunkSize() {
      return Math.round(size / 3);
    }
    
    public float getKick() {
      return kick.get();
    }
    
    public float getMid() {
      float maxVal = 0;
      for (int i = chunkSize() - 1; i < chunkSize(); i ++) {
        if (get(i) > maxVal) maxVal = get(i);
      }
      return maxVal;
    }
    
    public float getHigh() {
      float maxVal = 0;
      for (int i = (chunkSize() * 2) - 1; i < chunkSize(); i ++) {
        if (get(i) > maxVal) maxVal = get(i);
      }
      return maxVal;
    }

    public void toggleKick() {
      kickDraw = !kickDraw;
    }
}

  public void settings() {  size(720, 256, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "matrix_reloaded" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
