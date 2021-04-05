class Smoother {
    float rawValue = 0, smoothValue, smoothRate, maxVal, smoothMaxVal, sensitivity, thresh, normVal;

    Smoother(float rate) {
        smoothValue = 0.0;
        smoothRate = rate;
        maxVal = 0.0;
        sensitivity = 1;
        thresh = 0.0;
        smoothMaxVal = 0.0001;
        normVal = 0.0000001;
    }

    void update(float newValue) {
        float normalVal = (float)Math.pow(normalizeValue(newValue), 2);
        //float normalVal = normalizeValue(newValue);

        if (newValue > maxVal) {
            maxVal = newValue;
        } else {
          maxVal = maxVal * 0.99999;
        }
        
        if (newValue > rawValue) {
            smoothValue = normalVal;
            rawValue = newValue;
        } else {
            smoothValue = (smoothValue * smoothRate) / (smoothRate + 1);
            rawValue = (rawValue * smoothRate) / (smoothRate + 1);
            //println((maxVal / 10));
            //smoothValue = max(0, smoothValue - (smoothMaxVal / smoothRate));
        }
    }

    float value() {
        return rawValue;
    }

    float normalSmoothValue() {
        return smoothValue;
    }

    float normalizeValue(float newValue) {
      if (maxVal > 0) {
        return map(newValue, 0, maxVal / sensitivity, 0, 1);
      } else {
        return 0;
      }
    }

    float maxVal() {
        return maxVal;
    }
    
    void setSens(float sens) {
      sensitivity = sens;
    }
    
    void setSmooth(float rate) {
      smoothRate = rate;
    }
    
    void setThresh(float newThresh) {
      thresh = newThresh;
    }
}
