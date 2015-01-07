package com.air.lib.communication.data;

public class MotorSpeedRate {

    private int max;
    private int min;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public String toString() {
        return "MotorSpeedRate{" +
                "max=" + max +
                ", min=" + min +
                '}';
    }
}
