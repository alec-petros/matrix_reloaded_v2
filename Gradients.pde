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
  
  void compose() {
      gradients[0] = new ColorGradient();
      
      // purple pink
      gradients[0].addColorAt(0.0 * (width / 10), TColor.newRGB(n(45), n(0), n(247)));
      gradients[0].addColorAt(3.0 * (width / 10), TColor.newRGB(n(137), n(0), n(242)));
      gradients[0].addColorAt(5.0 * (width / 10), TColor.newRGB(n(177), n(0), n(232)));
      gradients[0].addColorAt(7.0 * (width / 10), TColor.newRGB(n(209), n(0), n(209)));
      gradients[0].addColorAt(9.0 * (width / 10), TColor.newRGB(n(229), n(0), n(164)));
      gradients[0].addColorAt(10.0 * (width / 10), TColor.newRGB(n(242), n(0), n(137)));
      
      // satou
      gradients[1] = new ColorGradient();
      gradients[1].addColorAt(0.0, TColor.newRGB(n(255), n(255), n(255)));
      gradients[1].addColorAt(width / 2, TColor.newRGB(n(255), n(0), n(0)));
      gradients[1].addColorAt(width, TColor.newRGB(n(255), n(255), n(255)));
      
      // aurora
      gradients[2] = new ColorGradient();
      gradients[2].addColorAt(0.0, TColor.newRGB(n(20), n(232), n(30)));
      gradients[2].addColorAt(1 * (width / 10), TColor.newRGB(n(0), n(234), n(141)));
      gradients[2].addColorAt(3 * (width / 10), TColor.newRGB(n(1), n(126), n(255)));
      gradients[2].addColorAt(width / 2, TColor.newRGB(n(141), n(0), n(196)));
      gradients[2].addColorAt(7 * (width / 10), TColor.newRGB(n(1), n(126), n(255)));
      gradients[2].addColorAt(9 * (width / 10), TColor.newRGB(n(0), n(234), n(141)));
      gradients[2].addColorAt(width, TColor.newRGB(n(20), n(232), n(30)));
      
      //disco
      gradients[3] = new ColorGradient();
      gradients[3].addColorAt(0.0, TColor.newRGB(n(255), n(201), n(0)));
      gradients[3].addColorAt(0.5 * (width / 10), TColor.newRGB(n(213), n(173), n(24)));
      gradients[3].addColorAt(1 * (width / 10), TColor.newRGB(n(234), n(234), n(234)));
      gradients[3].addColorAt(width / 2, TColor.newRGB(n(204), n(204), n(204)));
      gradients[3].addColorAt(9 * (width / 10), TColor.newRGB(n(234), n(234), n(234)));
      gradients[3].addColorAt(9.5 * (width / 10), TColor.newRGB(n(213), n(173), n(24)));
      gradients[3].addColorAt(width, TColor.newRGB(n(255), n(201), n(0)));

      // razzle dazzle
      gradients[4] = new ColorGradient();
      gradients[4].addColorAt(0.0, TColor.newRGB(n(15), n(192), n(252)));
      gradients[4].addColorAt(1.5 * (width / 10), TColor.newRGB(n(123), n(29), n(175)));
      gradients[4].addColorAt(3 * (width / 10), TColor.newRGB(n(255), n(47), n(185)));
      gradients[4].addColorAt(width / 2, TColor.newRGB(n(212), n(255), n(71)));
      gradients[4].addColorAt(7 * (width / 10), TColor.newRGB(n(255), n(47), n(185)));
      gradients[4].addColorAt(8.5 * (width / 10), TColor.newRGB(n(123), n(29), n(175)));
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
      gradients[6].addColorAt(0.0, TColor.newRGB(n(0), n(254), n(202)));
      gradients[6].addColorAt(2 * (width / 10), TColor.newRGB(n(251), n(240), n(75)));
      gradients[6].addColorAt(4 * (width / 10), TColor.newRGB(n(255), n(130), n(225)));
      gradients[6].addColorAt(width / 2, TColor.newRGB(n(128), n(91), n(0237)));
      gradients[6].addColorAt(6 * (width / 10), TColor.newRGB(n(255), n(130), n(225)));
      gradients[6].addColorAt(8 * (width / 10), TColor.newRGB(n(251), n(240), n(75)));
      gradients[6].addColorAt(width, TColor.newRGB(n(0), n(254), n(202)));

      
      // o u t r u n
      gradients[7] = new ColorGradient();
      gradients[7].addColorAt(0.0, TColor.newRGB(n(255), n(108), n(17)));
      gradients[7].addColorAt(3 * (width / 10), TColor.newRGB(n(255), n(56), n(100)));
      gradients[7].addColorAt(3.5 * (width / 10), TColor.newRGB(n(45), n(226), n(230)));
      gradients[7].addColorAt(width / 2, TColor.newRGB(n(101), n(13), n(137)));
      gradients[7].addColorAt(6.5 * (width / 10), TColor.newRGB(n(45), n(226), n(230)));
      gradients[7].addColorAt(7 * (width / 10), TColor.newRGB(n(255), n(56), n(100)));
      gradients[7].addColorAt(width, TColor.newRGB(n(255), n(108), n(17)));

      renderList();
  }
  
  void renderList() {
    listA = gradients[indexA].calcGradient(0, width);
    listB = gradients[indexB].calcGradient(0, width);
  }
  
  color getColor(int pos, int alpha) {
    float[] rgba = new float[4];
    listA.get(pos).blend(listB.get(pos), crossPos).toRGBAArray(rgba);
    return color(nn(rgba[0]), nn(rgba[1]), nn(rgba[2]), alpha);
  }
  
  void setCross(float pos) {
    crossPos = pos;
    renderList();
  }
  
  void setA(int index) {
    if (index >= 0) {
      indexA = index;
    }
    renderList();
  }
  
    void setB(int index) {
    if (index >= 0) {
      indexB = index;
    }
    renderList();
  }
  
  void setSens(float sens) {
    sensitivity = sens;
  }
  
  float getSens() {
    return sensitivity;
  }
  
  float n(int val) {
    return map(val, 0, 255, 0, 1);
  }

  float nn(float val) {
    return map(val, 0, 1, 0, 255);
  }
}
