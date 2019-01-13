package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.EyewearUserCalibrator;

public class Hardware2018Mecanum
{
    /* Public OpMode members. */
    public DcMotor  frontleftDrive   = null;
    public DcMotor  frontrightDrive  = null;
    public DcMotor  backleftDrive   = null;
    public DcMotor  backrightDrive  = null;
    public DcMotor  pitchArm = null;
    public Servo  graber = null;
    public DcMotor liftMotor = null;
    public NormalizedColorSensor leftColor = null;
    public NormalizedColorSensor rightColor = null;

    private ElapsedTime runtime = new ElapsedTime();

    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();
    public MM_IMU imu = null;


    /* Constructor */
    public Hardware2018Mecanum(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;
        imu = new MM_IMU();

        // Define and Initialize Motors
        frontleftDrive  = hwMap.get(DcMotor.class, "frontleft");
        frontrightDrive = hwMap.get(DcMotor.class, "frontright");
        backleftDrive  = hwMap.get(DcMotor.class, "backleft");
        backrightDrive = hwMap.get(DcMotor.class, "backright");
        pitchArm = hwMap.get(DcMotor.class, "pitch");
        graber = hwMap.get(Servo.class,"grab");
        liftMotor = hwMap.get(DcMotor.class, "lift");
        leftColor = hwMap.get(NormalizedColorSensor.class, "leftcolor");
        rightColor = hwMap.get(NormalizedColorSensor.class, "rightcolor");


        frontleftDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        frontrightDrive.setDirection(DcMotor.Direction.REVERSE);// Set to REVERSE if using AndyMark motors
        backleftDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        backrightDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        pitchArm.setDirection(DcMotor.Direction.FORWARD);
        pitchArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setDirection(DcMotor.Direction.FORWARD); //Positive is up
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        // Set all motors to zero power
        frontleftDrive.setPower(0);
        frontrightDrive.setPower(0);
        backleftDrive.setPower(0);
        backrightDrive.setPower(0);
        pitchArm.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        frontleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        pitchArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pitchArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Initialize imu
        imu.init(hwMap);
    }
    public void gamepadDriveCL(double gamepadX, double gamepadY, double rotation, double imu_yaw)
    {
        // Setup a variable for each drive wheel to save power level for telemetry
        double r = Math.hypot(gamepadX, -gamepadY);
        //double robotAngle = Math.atan2(gamepadY, -gamepadX) - Math.PI / 4
        double imu_correction = imu_yaw * (Math.PI/180);
        double robotAngle = Math.atan2(-gamepadY, gamepadX) - Math.PI / 4 - imu_correction;

        double rightX = rotation;//negative bc its intuitive if you put in x on a stick
        final double v1 = r * Math.cos(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        frontleftDrive.setPower(v1);
        frontrightDrive.setPower(v2);
        backleftDrive.setPower(v3);
        backrightDrive.setPower(v4);
    }
    public void gamepadDriveNoCL(double gamepadX, double gamepadY, double rotation)
    {
        // Setup a variable for each drive wheel to save power level for telemetry
        double r = Math.hypot(gamepadX, -gamepadY);
        //double robotAngle = Math.atan2(gamepadY, -gamepadX) - Math.PI / 4
        double robotAngle = Math.atan2(-gamepadY, gamepadX) - Math.PI / 4;

        double rightX = rotation;//negative bc its intuitive if you put in x on a stick
        final double v1 = r * Math.cos(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        frontleftDrive.setPower(v1);
        frontrightDrive.setPower(v2);
        backleftDrive.setPower(v3);
        backrightDrive.setPower(v4);
    }
    public void rotate(double power)
    {
        //positive power is clockwise rotation
        frontleftDrive.setPower(power);
        backleftDrive.setPower(power);

        frontrightDrive.setPower(-power);
        backrightDrive.setPower(-power);
    }
    public void straight(double power)
    {
        //positive power is forward
        frontleftDrive.setPower(power);
        backleftDrive.setPower(power);
        frontrightDrive.setPower(power);
        backrightDrive.setPower(power);
    }
    public void strafe(double power)
    {
        //positive power is left
        frontleftDrive.setPower(-power);
        backleftDrive.setPower(power);
        frontrightDrive.setPower(power);
        backrightDrive.setPower(-power);
    }
    public void resetAllEncoders()
    {

        frontrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(200);
        frontrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public double leftColorHue(){
        float[] hsvValues = new float[3];
        NormalizedRGBA colors = leftColor.getNormalizedColors(); //get colors from sensor
        Color.colorToHSV(colors.toColor(),hsvValues); //Color is a java class, sets hsvValues to the converted colors
        return hsvValues[0]; //returns hue
    }
    public double rightColorHue(){
        float[] hsvValues = new float[3];
        NormalizedRGBA colors = rightColor.getNormalizedColors(); //get colors from sensor
        Color.colorToHSV(colors.toColor(),hsvValues); //Color is a java class, sets hsvValues to the converted colors
        return hsvValues[0]; //returns hue
    }
    /*
    public void moveBetweenPoints(double x1,double y1,double x2,double y2,double imu_correction)
    {
        double deltaX = x2-x1;
        double deltaY = y2-y1;
        //get current position
        imu.updateAngles();
        double currentRotation = imu.yaw; //maybe replace with vuforia
        double finalRotation = (Math.atan2(deltaY,deltaX))*(180/Math.PI);//converts it back to degrees
        double distance = Math.hypot(deltaY,deltaX);
            //same as sqrt(x^2+y^2)
        if (finalRotation > currentRotation){
            imu.updateAngles();
            currentRotation = (imu.yaw+imu_correction)%360;
            while (currentRotation < finalRotation)
            {
                rotate(0.25);
            }
        }
        else if (finalRotation < currentRotation){
            imu.updateAngles();
            currentRotation = (imu.yaw+imu_correction)%360;
            while (currentRotation > finalRotation)
            {
                rotate(-0.25);
            }

        }
        //if we're already at the right heading, we dont need to rotate at all



    }
    */

    private void sleep(int ms){
        runtime.reset();
        while (runtime.milliseconds() < ms){}
    }
}



