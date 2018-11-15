package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="VuforiaNavigate", group="Linear Opmode")
public class VuforiaNavigate extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private Hardware2018Mecanum robot = null;
    private MM_VuforiaRR vuforia = new MM_VuforiaRR(hardwareMap);
    //private MM_IMU imu = new MM_IMU();
    //imu now initialized in Mecanum Class

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        robot.init(hardwareMap);
        //also inits imu

        //reset encoders, as we use them later
        robot.frontrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(250); //Give time for the reset
        robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Initailize our Vuforia Class, give displacements in mm
        vuforia.init(125,-150,-165);
        //vuforia.init(0,0,0);
        //TODO LEFT AND VERT BACKWARD?


        telemetry.addData("Status", "READY");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        robot.imu.updateAngles();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        //destination is (0,55) inch
        double[] position = null;
        double correction = 0;
        while (opModeIsActive()) {
            if (vuforia.TargetVisible())
            {
                position = vuforia.getPosition(); //returned in inches and degrees
                correction = vuforia.getCorrection(robot.imu);
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        position[0],position[1],position[2]);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f",
                        position[3], position[4], position[5]);
                telemetry.addData("Correction", correction);
                telemetry.update();
                break;
            }
            //TODO add else if for runtime going to far, do program with odometer
        }
        sleep(3000);
        robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double deltaY;
        try {
             double desiredY = 50;
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
            double desiredX = position[0];
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
        double distance  = inToDegrees(distanceInches);

        //calculated final rotation angle
        double finalRotation = Math.atan2(deltaY,deltaX); //converts normal coordinants to polar, and returns the angle
        double currentRotation = position[5];
        double deltaRotation = finalRotation - currentRotation;




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
        robot.imu.updateCorrectedAngles(correction); //correction found earlier
        while (robot.imu.correctedYaw > (finalRotation + 2) && robot.imu.correctedYaw < (finalRotation - 2) ) //go until within 4 degrees of final
        { //"2" can get changed as we determine how accurate it is and how fast it reports
            robot.imu.updateCorrectedAngles(correction);
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
        robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);






        robot.frontleftDrive.setPower(.25);
        robot.frontrightDrive.setPower(.25);
        robot.backleftDrive.setPower(.25);
        robot.backrightDrive.setPower(.25);

        double avgEncoder = 0;
        while (avgEncoder < distance){ //inToDegrees function below
            avgEncoder = (robot.frontleftDrive.getCurrentPosition() +robot.frontrightDrive.getCurrentPosition()
                    + robot.backrightDrive.getCurrentPosition() + robot.backleftDrive.getCurrentPosition())/4;
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
}
