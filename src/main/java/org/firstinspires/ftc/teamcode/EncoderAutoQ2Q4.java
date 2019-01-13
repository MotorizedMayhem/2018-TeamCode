package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "EncoderAutoDepot")
public class EncoderAutoQ2Q4 extends LinearOpMode { //FOR RED CRATER TODO EVENTUALLY CHANGE NAME AND FILE

    private ElapsedTime runtime = new ElapsedTime();
    Hardware2018Mecanum robot = new Hardware2018Mecanum();
    MM_VuforiaRR vuforia = new MM_VuforiaRR(hardwareMap);
    @Override
    public void runOpMode(){
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        robot.init(hardwareMap);

        vuforia.init(0,0,0);


        telemetry.addData("Status", "READY");
        telemetry.update();
        robot.graber.setPosition(.7);//closed
        robot.pitchArm.setPower(0);
        waitForStart();
        //sleep(5000); //give coach time to skew it

        /*
        robot.liftMotor.setPower(.75);
        while (robot.liftMotor.getCurrentPosition() < 6000){
            telemetry.addData("Lift", robot.liftMotor.getCurrentPosition());
            telemetry.update();
        }
        robot.liftMotor.setPower(0);
        sleep(250);
        */

        robot.straight(.1);
        sleep(250);

        //remove skew
        /*
        robot.imu.updateAngles();
        if (robot.imu.yaw > 0.5)           //left of 0 with 0.5 tolerance
            {
                robot.rotate(.12);
                while (robot.imu.yaw > 0.75 && opModeIsActive()) { //chage the .75 if over/under turn
                    robot.imu.updateAngles();
                    telemetry.addData("Rotation", robot.imu.yaw);
                    telemetry.update();
                }
            }

            else if (robot.imu.yaw < -0.75)    //right of zero with 0.75 tolerance
            {
                robot.rotate(-.12);
                while (robot.imu.yaw < -0.75 && opModeIsActive()) {
                    robot.imu.updateAngles();
                    telemetry.addData("Rotation", robot.imu.yaw);
                    telemetry.update();
                }

            }
            */

        robot.straight(0.22);
        sleep(2000);
        robot.straight(0);//stop motors
        robot.strafe(-.4); //gets to far jewel
        sleep(1800);
        robot.strafe(.25);//start path back

        runtime.reset();
        boolean lastLeft = false;
        boolean lastRight = false; //check if value is seen twice, confirming that it is yellow
        while(opModeIsActive() && runtime.milliseconds() < 8200) //move left for 8 seconds
        {

            if (robot.leftColorHue() < 80 && robot.leftColorHue() > 10) //we see yellow
            {
                if(lastLeft) {
                    telemetry.addData("Seen", "Left");
                    telemetry.addData("Left", robot.leftColorHue());
                    telemetry.update();
                    robot.straight(.3);
                    sleep(500);
                    robot.straight(-.3);
                    sleep(750);
                    break;
                }
                lastLeft = true;
            }
            else
            {
                lastLeft = false;
            }

            if (robot.rightColorHue() < 80 && robot.rightColorHue() > 10) //we see yellow
            {
                if(lastRight)
                {
                    telemetry.addData("Seen", "Right");
                    telemetry.addData("Right", robot.rightColorHue());
                    telemetry.update();
                    robot.straight(.3);
                    sleep(500);
                    robot.straight(-.3);
                    sleep(750);
                    break;
                }
                lastRight = true;

            }
            else
            {
                lastRight = false;
            }
            telemetry.addData("Left", robot.leftColorHue());
            telemetry.addData("Right", robot.rightColorHue());
            telemetry.update();
            sleep(250);
        }
        robot.straight(-.2); //back away from jewel
        sleep(500);




        robot.imu.updateAngles();
        robot.rotate(-0.2);
        while(robot.imu.yaw < 42.5) //we want 45 but it tends to overshoot
        {
            robot.imu.updateAngles();
        }


        double[] position = new double[6];
        double correction =0;

        robot.straight(0.15);//slow forward
        while (opModeIsActive() && runtime.milliseconds() < 3000) //three seconds to see it
        {
            if(vuforia.TargetVisible())
            {
                position = vuforia.getPosition();
                correction = vuforia.getCorrection(robot.imu);
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        position[0],position[1],position[2]);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f",
                        position[3], position[4], position[5]);
                telemetry.addData("Correction", correction);
                telemetry.update();
                sleep(5000);
                break;
            }
        }
        //robot.straight(0);
        //sleep(5000);
        double[] destination = {0,-50};

        double deltaY;
        try {
            double desiredY = destination[1]; //Destination
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
            double desiredX = -destination[0]; //0 inch
            deltaX = desiredX - position[0]; //position 0 is current x of robot
            telemetry.addData("deltaX", deltaX);
        }
        catch (java.lang.NullPointerException e)
        {
            telemetry.addData("ERROR", "Couldn't pull current position, assuming 0");
            deltaX= 0;
        }
        telemetry.update();


//this is in radians below //TODO ADDED NEGATIVE
        double finalRotation = Math.atan2(deltaY,deltaX);//*(180/Math.PI); //converts normal coordinants to polar, and returns the angle
        double currentRotation = position[5] * (Math.PI/180);
        double deltaRotation = finalRotation - currentRotation;


        double distanceInches = Math.hypot(deltaX,deltaY); //Shorter and more readable form of a^2+b^2=c^2 solved for c
        double distance  = inToDegrees(distanceInches); //Degrees as in rotation of wheel



        if (deltaRotation > 0){ //turn left
            robot.frontleftDrive.setPower(-0.2); //should be -.2
            robot.backleftDrive.setPower(-0.2);

            robot.frontrightDrive.setPower(0.2); //should be .2
            robot.backrightDrive.setPower(0.2);
        }
        else //turning right or not at all(unlikely)
        {

            robot.frontleftDrive.setPower(0.2);//should be .2
            robot.backleftDrive.setPower(0.2);

            robot.frontrightDrive.setPower(-0.2);//should be -.2
            robot.backrightDrive.setPower(-0.2);
        }
        robot.imu.updateCorrectedAngles(correction); //correction found earlier
        while (opModeIsActive())
        {
            if (robot.imu.correctedYaw < (finalRotation + 2) && robot.imu.correctedYaw > (finalRotation - 2)) //go until within 4 degrees of final)
            // "2" can get changed as we determine how accurate it is and how fast it reports
            {
                break;
            }
            robot.imu.updateCorrectedAngles(correction);
            telemetry.addData("CorrectedAngle",robot.imu.correctedYaw);
            robot.imu.updateAngles();
            telemetry.addData("Uncorrected", robot.imu.yaw);
            telemetry.update();
        }
        robot.straight(0);


        robot.frontrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(250); //Give time for the reset
        robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //bc its broken last we checked
        robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //so its symmetrical
        robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        robot.straight(0.25);
        double avgEncoder = 0;
        while (avgEncoder < distance && opModeIsActive()){ //inToDegrees function below; distance declared earlier
            avgEncoder = (robot.backrightDrive.getCurrentPosition() + robot.backleftDrive.getCurrentPosition())/2; //frontright is cut out
            telemetry.addData("average Encoder", avgEncoder);
            telemetry.addData("goal",distance);
            telemetry.update();
        }

/* //TODO OLD METHOD
        //mecanum drive software
        double r = .4; //we dont care about how fast, as long as precise
        //double robotAngle = Math.atan2(gamepadY, -gamepadX) - Math.PI / 4
        double robotAngle = -deltaRotation;

        final double v1 = r * Math.cos(robotAngle);
        final double v2 = r * Math.sin(robotAngle);
        final double v3 = r * Math.sin(robotAngle);
        final double v4 = r * Math.cos(robotAngle);

        robot.frontleftDrive.setPower(v1);
        robot.frontrightDrive.setPower(v2);
        robot.backleftDrive.setPower(v3);
        robot.backrightDrive.setPower(v4);
        sleep((long) distanceInches * 45);//.1 seconds per inch seems fair

*/

        robot.imu.updateCorrectedAngles(correction);
        robot.rotate(0.25); //counter clock rotation
        while (robot.imu.correctedYaw > -43) //toward depot
        {
            robot.imu.updateCorrectedAngles(correction);
            telemetry.addData("yaw", robot.imu.correctedYaw);
            telemetry.update();
        }


        robot.straight(-0.6);
        sleep(3000);
        robot.straight(0);
        robot.graber.setPosition(0.2);

        robot.rotate(-0.2);
        sleep(100);//small twitch to left
        robot.straight(0.6);

        //robot.pitchArm.setPower(1); //start moving arm

        sleep(4500);
        robot.straight(0);


        /*
        while (robot.pitchArm.getCurrentPosition() < 10000 && opModeIsActive())
        {
            telemetry.addData("position",robot.pitchArm.getCurrentPosition());
            telemetry.update();
        }
        robot.pitchArm.setPower(0);
        */
        //TODO PUT BACK IN TO MAKE ARMS MOVE AGAIN













        /*
        robot.rotate(-0.2);
        while (robot.imu.yaw < 62 && opModeIsActive()) //angle to point to picture
        {
            robot.imu.updateAngles();
            telemetry.addData("yaw", robot.imu.yaw);
            telemetry.update();
        }
        robot.straight(0.3);
        sleep(3000);
        robot.rotate(.2);
        while (robot.imu.yaw > 50 && opModeIsActive()) //angle to point to picture
        {
            robot.imu.updateAngles();
            telemetry.addData("yaw", robot.imu.yaw);
            telemetry.update();
        }
        robot.strafe(-.6);
        sleep(3750);

        robot.rotate(-.2);
        while (robot.imu.yaw < 135 && opModeIsActive()) //angle to get claw over area
        {
            robot.imu.updateAngles();
            telemetry.addData("yaw", robot.imu.yaw);
            telemetry.update();
        }

        robot.straight(0);
        robot.graber.setPosition(0.6);//open claw just a bit
        sleep(1000);

        robot.straight(.5);
        sleep(1000); //back away from it

        robot.rotate(.2);
        sleep(300); //slight tweak to the left so we dont hit the on field thing

        robot.straight(.5);
        sleep(3000); //back away from it
        robot.straight(0); //stop

*/

        /*
        robot.pitchArm.setPower(1);
        while (robot.pitchArm.getCurrentPosition() < 15000 && opModeIsActive())
        {
            telemetry.addData("position",robot.pitchArm.getCurrentPosition());
            telemetry.update();
        }
        robot.pitchArm.setPower(0);
        */




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




