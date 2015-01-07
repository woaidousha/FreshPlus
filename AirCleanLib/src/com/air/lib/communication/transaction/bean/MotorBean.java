package com.air.lib.communication.transaction.bean;

public class MotorBean {

    private int workMode;
    private int motor1Speed = -1;
    private int motor2Speed = -1;

    public int getWorkMode() {
        return workMode;
    }

    public void setWorkMode(int workMode) {
        this.workMode = workMode;
    }

    public int getMotor1Speed() {
        return motor1Speed;
    }

    public void setMotor1Speed(int motor1Speed) {
        this.motor1Speed = motor1Speed;
    }

    public int getMotor2Speed() {
        return motor2Speed;
    }

    public void setMotor2Speed(int motor2Speed) {
        this.motor2Speed = motor2Speed;
    }

    public boolean isWorthSave() {
        return motor1Speed >= 0 && motor2Speed >= 0;
    }

    @Override
    public String toString() {
        return "MotorBean{" +
                "workMode=" + workMode +
                ", motor1Speed=" + motor1Speed +
                ", motor2Speed=" + motor2Speed +
                '}';
    }
}
