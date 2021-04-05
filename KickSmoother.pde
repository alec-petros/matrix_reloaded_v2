class KickSmoother {
    float maxVal = 0, deltaThresh = 0, thresh = 0, value = 0, rate = 15.0, kickPowerThresh = 0.00, kickPowerMax = 180.00, lastVal = 0, secondLastVal = 0, thirdLastVal = 0;
    int bassLow = 8, bassHigh = 12, lastUpdated = 0, cooldown = 16;

    KickSmoother(float smoothRate) {
        rate = smoothRate;
    }

    void update(FFT fftLog) {
        float topVal = 0;
        float smootherValue = fftLog.calcAvg(100, 300);
        float delta = (smootherValue / ((lastVal + secondLastVal) / 2));
        thirdLastVal = secondLastVal;
        secondLastVal = lastVal;
        lastVal = smootherValue;
        if (smootherValue > topVal) topVal = smootherValue;


        if (topVal > maxVal) maxVal = topVal;
        
        //if (delta > deltaThresh) println("delta thresh breach: ", delta, deltaThresh);

        if (delta > deltaThresh && smootherValue > thresh) {
            println("fired: ", delta, deltaThresh, smootherValue, thresh);
            value = 1;
            lastUpdated = 0;
            deltaThresh += 1;
            thresh = smootherValue;
        } else {
            float modRate = map(value, 0, 1, 0.995, 0.999999);
            float threshCap = max(deltaThresh * 0.99, 2.2);
            deltaThresh = min(threshCap, 20);
            thresh = max(thresh * modRate, 10);
            value = (value * rate) / (rate + 1);
        }
        
        lastUpdated = min(60, lastUpdated + 1);
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

    float get() {
        return value;
    }

    void setBassRange(int low, int high) {
        bassLow = low;
        bassHigh = high;
    }
}
