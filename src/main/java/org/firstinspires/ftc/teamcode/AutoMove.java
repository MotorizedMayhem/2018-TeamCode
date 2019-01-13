package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class AutoMove {
    Hardware2018Mecanum robot = null;
    private MM_VuforiaRR vuforia = null;
    private Telemetry telemetry = null;
    private ElapsedTime runtime = new ElapsedTime();
    public double imuCorrection = 0;//declare so it can be used later
    private double[] lastPosition = new double[]{0,0,0,0,0,0};

    AutoMove(Hardware2018Mecanum robotClass, MM_VuforiaRR vuforiaClass, Telemetry telemetryClass){
        this.robot = robotClass;
        this.vuforia = vuforiaClass;
        this.telemetry = telemetryClass;
    }






    public void VuforiaMove(double finalX, double finalY){
        robot.imu.updateAngles();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        double[] position = null; //declare so it can be used later
        //double correction = 0; //now declared in higher level of class
        while (true) { //TODO find better way to do this than true
            if (vuforia.TargetVisible())
            {
                position = vuforia.getPosition(); //returned in inches and degrees
                imuCorrection = vuforia.getCorrection(robot.imu);
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        position[0],position[1],position[2]);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f",
                        position[3], position[4], position[5]);
                telemetry.addData("Correction", imuCorrection);
                telemetry.update();
                break;
            }
            else if (runtime.seconds() > 10)
            {
                break; //we already have backup values in the initialization of those variables.
            }
        }
        sleep(2000);

        double deltaY;
        try {
            double desiredY = finalY; //Destination
            deltaY = desiredY - position[1]; //position 1 is current y of robot
            telemetry.addData("deltaX", deltaY);
        }
        catch (java.lang.NullPointerException e)
        {
            telemetry.addData("ERROR", "Couldn't pull current position, assuming 0");
            deltaY= 0;
        }


        double deltaX;
        try {
            double desiredX = finalX; //passed to the function
            deltaX = desiredX - position[0]; //position 0 is current x of robot
            telemetry.addData("deltaX", deltaX);
        }
        catch (java.lang.NullPointerException e)
        {
            telemetry.addData("ERROR", "Couldn't pull current position, assuming 0");
            deltaX= 0;
        }
        telemetry.update();

        //Calculated Distance between points
        double distanceInches = Math.hypot(deltaX,deltaY); //Shorter and more readable form of a^2+b^2=c^2 solved for c
        double distance  = inToDegrees(distanceInches); //Degrees as in rotation of wheel

        //calculated final rotation angle
        double finalRotation = Math.atan2(deltaY,deltaX) * (180/Math.PI); //converts normal coordinants to polar, and returns the angle
        double currentRotation = position[5];
        double deltaRotation = finalRotation - currentRotation;

        telemetry.addData("Distance (in)",distanceInches);
        telemetry.addData("Rotation", deltaRotation);
        robot.imu.updateCorrectedAngles(imuCorrection);
        telemetry.addData("CurrentRotation",robot.imu.correctedYaw);

        telemetry.update();
        sleep(2000);


        robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        if (deltaRotation > 0){ //turn left
            robot.frontleftDrive.setPower(-0.2);
            robot.backleftDrive.setPower(-0.2);

            robot.frontrightDrive.setPower(0.2);
            robot.backrightDrive.setPower(0.2);
        }
        else //turning right or not at all(unlikely)
        {
            robot.frontleftDrive.setPower(0.2);
            robot.backleftDrive.setPower(0.2);

            robot.frontrightDrive.setPower(-0.2);
            robot.backrightDrive.setPower(-0.2);
        }
        robot.imu.updateCorrectedAngles(imuCorrection); //correction found earlier
        while (true)
        {
            if (robot.imu.correctedYaw < (finalRotation + .5) && robot.imu.correctedYaw > (finalRotation - .5)) //go until within 4 degrees of final)
            // ".5" can get changed as we determine how accurate it is and how fast it reports
            {
                break;
            }
            robot.imu.updateCorrectedAngles(imuCorrection);
            telemetry.addData("CorrectedAngle",robot.imu.correctedYaw);
            telemetry.update();
        }
        robot.frontleftDrive.setPower(0);
        robot.frontrightDrive.setPower(0);
        robot.backleftDrive.setPower(0);
        robot.backrightDrive.setPower(0);
        sleep(3000);






        //reset encoders
        robot.frontrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(250); //Give time for the reset
        robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //Motor emcoder broken
        robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //for symmetry
        robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);






        robot.frontleftDrive.setPower(.25);
        robot.frontrightDrive.setPower(.25);
        robot.backleftDrive.setPower(.25);
        robot.backrightDrive.setPower(.25);

        double avgEncoder = 0;
        while (avgEncoder < distance){ //inToDegrees function below; distance declared earlier
            avgEncoder = (robot.backrightDrive.getCurrentPosition() + robot.backleftDrive.getCurrentPosition())/2;
            telemetry.addData("average Encoder", avgEncoder);
            telemetry.addData("Goal", distance);
            telemetry.update();
        }
        robot.frontleftDrive.setPower(0);
        robot.frontrightDrive.setPower(0);
        robot.backleftDrive.setPower(0);
        robot.backrightDrive.setPower(0);
        sleep(2000);
        lastPosition[0] = finalX;
        lastPosition[1] = finalY;
        lastPosition[5] = finalRotation;

    }














    public void OdometerMove(double finalX, double finalY) //should be used after doing a vuforia one
    {
        robot.imu.updateAngles();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        double[] position = lastPosition; //end of last move
        //double correction = 0; //now declared in higher level of class

        double deltaY;
        try {
            double desiredY = finalY; //Destination
            deltaY = desiredY - position[1]; //position 1 is current y of robot
            telemetry.addData("deltaX", deltaY);
        }
        catch (java.lang.NullPointerException e)
        {
            telemetry.addData("ERROR", "Couldn't pull current position, assuming 0");
            deltaY= 0;
        }


        double deltaX;
        try {
            double desiredX = finalX; //passed to the function
            deltaX = desiredX - position[0]; //position 0 is current x of robot
            telemetry.addData("deltaX", deltaX);
        }
        catch (java.lang.NullPointerException e)
        {
            telemetry.addData("ERROR", "Couldn't pull current position, assuming 0");
            deltaX= 0;
        }
        telemetry.update();

        //Calculated Distance between points
        double distanceInches = Math.hypot(deltaX,deltaY); //Shorter and more readable form of a^2+b^2=c^2 solved for c
        double distance  = inToDegrees(distanceInches); //Degrees as in rotation of wheel

        //calculated final rotation angle
        double finalRotation = Math.atan2(deltaY,deltaX) * (180/Math.PI); //converts normal coordinants to polar, and returns the angle
        double currentRotation = position[5];
        double deltaRotation = finalRotation - currentRotation;


        robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        if (deltaRotation > 0){ //turn left
            robot.frontleftDrive.setPower(-0.2);
            robot.backleftDrive.setPower(-0.2);

            robot.frontrightDrive.setPower(0.2);
            robot.backrightDrive.setPower(0.2);
        }
        else //turning right or not at all(unlikely)
        {
            robot.frontleftDrive.setPower(0.2);
            robot.backleftDrive.setPower(0.2);

            robot.frontrightDrive.setPower(-0.2);
            robot.backrightDrive.setPower(-0.2);
        }
        robot.imu.updateCorrectedAngles(imuCorrection); //correction found earlier
        while (robot.imu.correctedYaw < (finalRotation + 1) && robot.imu.correctedYaw > (finalRotation - 1) ) //go until within 4 degrees of final
        { //"2" can get changed as we determine how accurate it is and how fast it reports
            robot.imu.updateCorrectedAngles(imuCorrection);
        }
        robot.frontleftDrive.setPower(0);
        robot.frontrightDrive.setPower(0);
        robot.backleftDrive.setPower(0);
        robot.backrightDrive.setPower(0);
        sleep(3000);






        //reset encoders
        robot.frontrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(250); //Give time for the reset
        robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);






        robot.frontleftDrive.setPower(.25);
        robot.frontrightDrive.setPower(.25);
        robot.backleftDrive.setPower(.25);
        robot.backrightDrive.setPower(.25);

        double avgEncoder = 0;
        while (avgEncoder < distance){ //inToDegrees function below; distance declared earlier
            avgEncoder = (robot.frontleftDrive.getCurrentPosition()
                    + robot.backrightDrive.getCurrentPosition() + robot.backleftDrive.getCurrentPosition())/3;
            telemetry.addData("average Encoder", avgEncoder);
            telemetry.update();
        }
        robot.frontleftDrive.setPower(0);
        robot.frontrightDrive.setPower(0);
        robot.backleftDrive.setPower(0);
        robot.backrightDrive.setPower(0);
        sleep(5000);
    }

    private double inToDegrees(double inches)
    {
        int wheelDiameter = 4; //Declared here bc always 4 with this bot
        int ticsPerRev = 1000; //Declared here bc always 4 with this bot
        double wheelCircum = Math.PI * wheelDiameter;
        double rotations = inches/wheelCircum;
        return rotations*ticsPerRev;
    }
    private void sleep(int ms){
        runtime.reset();
        while (runtime.milliseconds() < ms){}
    }

}
