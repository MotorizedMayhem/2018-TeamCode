package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import static java.lang.Double.NaN;


@Autonomous(name = "FullAutoVuforia")
public class EncoderAutoQ2Q4 extends LinearOpMode { //FOR RED CRATER TODO EVENTUALLY CHANGE NAME AND FILE

    private ElapsedTime runtime = new ElapsedTime();
    Hardware2018Mecanum robot = new Hardware2018Mecanum();
    private MM_VuforiaRR vuforia = new MM_VuforiaRR(hardwareMap);
    boolean bumped = false;
    @Override
    public void runOpMode(){
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        robot.init(hardwareMap);
        boolean fullLoop = true;

        vuforia.init(0,0,0);


        telemetry.addData("Status", "READY");
        telemetry.update();
        robot.graber.setPosition(.6);//closed
        robot.pitchArm.setPower(0);
        waitForStart();

        while (opModeIsActive() && fullLoop) {
            //sleep(5000); //give coach time to skew it



            robot.liftMotor.setPower(1);
            while (robot.liftMotor.getCurrentPosition() < 16000) {
                telemetry.addData("Lift", robot.liftMotor.getCurrentPosition());
                telemetry.update();
            }
            robot.liftMotor.setPower(0);
            sleep(100);



            //robot.straight(.1);
            //sleep(250);

            //remove skew

            robot.imu.updateAngles();
            if (robot.imu.yaw > 0.5)           //left of 0 with 0.5 tolerance
            {
                robot.rotate(.17);
                while (robot.imu.yaw > 0.75 && opModeIsActive()) { //chage the .75 if over/under turn
                    robot.imu.updateAngles();
                    telemetry.addData("Rotation", robot.imu.yaw);
                    telemetry.update();
                }
            } else if (robot.imu.yaw < -0.75)    //right of zero with 0.75 tolerance
            {
                robot.rotate(-.17);
                while (robot.imu.yaw < -0.75 && opModeIsActive()) {
                    robot.imu.updateAngles();
                    telemetry.addData("Rotation", robot.imu.yaw);
                    telemetry.update();
                }

            }


            //robot.straight(0.22);
            //sleep(2000);
            robot.frontrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.frontleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.backrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.backleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            sleep(100); //Give time for the reset
            robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //bc its broken last we checked
            robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //so its symmetrical
            robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            double distance = inToDegrees(24); //24 inches to jewel //TODO change back to 24
            double avgEncoder = 0;

            robot.straight(.6); //To the Jewels
            while (avgEncoder < distance && opModeIsActive()) { //inToDegrees function below; distance declared earlier
                avgEncoder = (robot.backrightDrive.getCurrentPosition() + robot.backleftDrive.getCurrentPosition()) / 2; //frontright is cut out
                telemetry.addData("average Encoder", avgEncoder);
                telemetry.addData("goal", distance);
                telemetry.update();
            }

            robot.straight(0);//stop motors
            robot.backleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.backrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


            /*
            robot.straight(0.2);
            double rightDistance = robot.getRightDistance(DistanceUnit.MM);
            double leftDistance = robot.getLeftDistance(DistanceUnit.MM);
            boolean right = rightDistance != NaN || rightDistance > 60;
            boolean left = leftDistance != NaN || leftDistance > 60;
            while(right && left)
            {
                rightDistance = robot.getLeftDistance(DistanceUnit.MM);
                leftDistance = robot.getLeftDistance(DistanceUnit.MM);
                right = rightDistance != NaN || rightDistance > 60;
                left = leftDistance != NaN || leftDistance > 60;
                telemetry.addData("right", rightDistance);
                telemetry.addData("left", leftDistance);
                telemetry.update();
            }
            robot.straight(0);
            */




            sleep(100);
            robot.strafe(-.6); //gets to far jewel
            sleep(1200);
            robot.straight(0);//stop motors
            sleep(100); //time to stop
            robot.strafe(.25);//start path back


            runtime.reset();
            boolean lastLeft = false;
            boolean lastRight = false; //check if value is seen twice, confirming that it is yellow
            double remainingMs = 0;

            double msToRun = 7500; //for the whole strafe
            while (opModeIsActive() && runtime.milliseconds() < 6200) //if we dont see 1 and 2, move on
            {
                double leftColor = robot.leftColorHue();
                double rightColor = robot.rightColorHue();
                if (leftColor < 60 && leftColor > 10) //we see yellow
                {
                    if (lastLeft) {
                        telemetry.addData("Seen", "Left");
                        telemetry.addData("Left", leftColor);
                        telemetry.update();
                        remainingMs = msToRun - runtime.milliseconds();
                        YellowMineralSeen();
                        break;
                    }
                    lastLeft = true;
                }
                else {
                    lastLeft = false;
                }

                if (rightColor < 60 && rightColor > 10) //we see yellow
                {
                    if (lastRight) {
                        telemetry.addData("Seen", "Right");
                        telemetry.addData("Right", rightColor);
                        telemetry.update();
                        remainingMs = msToRun - runtime.milliseconds();
                        YellowMineralSeen();
                        break;
                    }
                    lastRight = true;

                } else {
                    lastRight = false;

                }
                telemetry.addData("Left", lastLeft);
                telemetry.addData("Color", leftColor);
                telemetry.addData("Right", lastRight);
                telemetry.addData("Color", rightColor);
                telemetry.update();
                sleep(100);
            }



            if (runtime.milliseconds() > 5500 && !bumped)
            {
                remainingMs = msToRun - runtime.milliseconds();
                YellowMineralSeen();

            }

            runtime.reset();
            if (remainingMs !=0) {//the != 0 is to check if we saw the mineral
                robot.strafe(.5);
                remainingMs /= 2;
                while (runtime.milliseconds() < remainingMs) // divide by 2 bc speed is twice
                {

                    telemetry.addData("Remaining", remainingMs - runtime.milliseconds());
                    telemetry.update();
                }
            }


            //robot.straight(-.5); //back away from jewel
            //sleep(100);





            robot.imu.updateAngles();
            robot.rotate(-0.3);
            while (robot.imu.yaw < 50 && opModeIsActive()) //we want 50 but it tends to overshoot
            {
                robot.imu.updateAngles();
            }




            double[] position = new double[6];
            double correction = 0;
            String seenPicture = null;

            robot.straight(.3);//slow forward
            runtime.reset();
            while (opModeIsActive() && runtime.milliseconds() < 5000) //three seconds to see it
            {
                if (vuforia.TargetVisible()) {
                    robot.straight(0); //stop robot
                    position = vuforia.getPosition();
                    correction = vuforia.getCorrection(robot.imu);
                    seenPicture = vuforia.getTarget();
                    telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                            position[0], position[1], position[2]);
                    telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f",
                            position[3], position[4], position[5]);
                    telemetry.addData("Correction", correction);
                    telemetry.addData("raw imu", robot.imu.yaw);
                    telemetry.addData("picture", seenPicture);
                    telemetry.update();
                    //sleep(250); //long pause
                    break;
                }
            }



            double goalX = 0; //Desired end position of robot after correction
            double goalY = 0; //they keep the ball out of the net in soccer
            boolean depotSide = true; //false if crater side
            boolean redSide = true; //false if blue side
            double firstRotationCorrectedYaw = 0;

            switch (seenPicture) //god help us if its null
            {
                case("Red-Footprint"):
                    goalX = 0;
                    goalY = -62;
                    depotSide = false;
                    firstRotationCorrectedYaw = -80;
                    redSide = true;
                    //do stuff
                    break;
                case("Blue-Rover"):
                    goalX = 0;
                    goalY = 62;
                    depotSide = false;
                    firstRotationCorrectedYaw = 90;
                    redSide = false;

                    break;
                case("Back-Space"):
                    goalX = 62;
                    goalY = 0;
                    depotSide = true;
                    firstRotationCorrectedYaw = -3;
                    redSide = true;
                    break;
                case("Front-Craters"):
                    goalX = -62;
                    goalY = 0;
                    depotSide = true;
                    firstRotationCorrectedYaw = -170; //also +180
                    redSide = false;
                    break;


            }










            //robot.straight(0);
            //sleep(5000);
            robot.imu.updateCorrectedAngles(correction);
            if (depotSide){
                robot.rotate(0.25);
                while (robot.imu.correctedYaw > firstRotationCorrectedYaw && opModeIsActive()) //we want -90 but it tends to overshoot
                {
                    robot.imu.updateCorrectedAngles(correction);
                    telemetry.addData("yaw", robot.imu.correctedYaw);
                    telemetry.update();
                }
            }
            else { //crater side
                robot.rotate(0.25);
                while (robot.imu.correctedYaw > firstRotationCorrectedYaw && opModeIsActive()) //we want -90 but it tends to overshoot
                {
                    robot.imu.updateCorrectedAngles(correction);
                    telemetry.addData("yaw", robot.imu.correctedYaw);
                    telemetry.update();
                }
            }






            robot.strafe(0.7);
            runtime.reset();


            if(depotSide){
                if (redSide) {
                    while (runtime.seconds() < 5) {
                        if (vuforia.TargetVisible()) {
                            position = vuforia.getPosition();
                            telemetry.addData("Vuforia Y", position[1]);
                            telemetry.update();
                            if (position[1] > 0) {
                                break;
                            }

                        }
                    }
                }
                else {
                    while (runtime.seconds() < 5) {
                        if (vuforia.TargetVisible()) {
                            position = vuforia.getPosition();
                            telemetry.addData("Vuforia Y", position[1]);
                            telemetry.update();
                            if (position[1] > 0) {
                                break;
                            }

                        }
                    }
                }
            }


            else {
                if (redSide) {
                    while (runtime.seconds() < 5) {
                        if (vuforia.TargetVisible()) {
                            position = vuforia.getPosition();
                            telemetry.addData("Vuforia X", position[0]);
                            telemetry.update();
                            if (position[0] > 0) {
                                break;
                            }

                        }
                    }
                }
                else {
                    while (runtime.seconds() < 5) {
                        if (vuforia.TargetVisible()) {
                            position = vuforia.getPosition();
                            telemetry.addData("Vuforia X", position[0]);
                            telemetry.update();
                            if (position[0] < 0) {
                                break;
                            }

                        }
                    }
                }
            }

            robot.pitchArm.setPower(-.9); //Pitch to bring out claw
            robot.pitchArm.setTargetPosition(1000);
            robot.pitchArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);



            robot.straight(0.8);
            runtime.reset();
            if (depotSide) {
                if (redSide) {
                    while (runtime.seconds() < 6) {
                        if (vuforia.TargetVisible()) {
                            position = vuforia.getPosition();
                            telemetry.addData("Vuforia X", position[0]);
                            telemetry.update();
                            if (position[0] > goalX) {
                                break;
                            }

                        }
                    }
                }
                else  {
                    while (runtime.seconds() < 6) {
                        if (vuforia.TargetVisible()) {
                            position = vuforia.getPosition();
                            telemetry.addData("Vuforia X", position[0]);
                            telemetry.update();
                            if (position[0] < goalX) {
                                break;
                            }

                        }
                    }
                }
            }
            else {//crater side
                if (redSide) {
                    while (runtime.seconds() < 6) {
                        if (vuforia.TargetVisible()) {
                            position = vuforia.getPosition();
                            telemetry.addData("Vuforia Y", position[1]);
                            telemetry.update();
                            if (position[1] < goalY) {
                                break;
                            }

                        }
                    }
                }
                else{
                    while (runtime.seconds() < 6) {
                        if (vuforia.TargetVisible()) {
                            position = vuforia.getPosition();
                            telemetry.addData("Vuforia Y", position[1]);
                            telemetry.update();
                            if (position[1] > goalY) {
                                break;
                            }

                        }
                    }
                }
            }


            robot.extendArm.setPower(.6);  //get arms to start moving
            robot.extendArm.setTargetPosition(3000);
            robot.extendArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            if (depotSide) {
                robot.rotate(-0.35);  //turn to depot
                while (robot.imu.yaw < 120 && opModeIsActive()) //we want more, so let it overshoot (looking for 120)
                {
                    robot.imu.updateAngles();
                }
            }
            else {//craterside
                robot.rotate(0.35);  //turn to depot
                while (robot.imu.yaw > -30 && opModeIsActive()) //we want more, so let it overshoot (looking for 45)
                {
                    robot.imu.updateAngles();
                }
            }


            robot.frontrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.frontleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.backrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.backleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            sleep(250); //Give time for the reset
            robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //bc its broken last we checked
            robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //so its symmetrical
            robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


            distance = -inToDegrees(53); //50 inches to depot
            avgEncoder = 0;

            robot.straight(-1); //back into depot
            while (avgEncoder > distance && opModeIsActive()) { //inToDegrees function below; distance declared earlier
                avgEncoder = (robot.backrightDrive.getCurrentPosition() + robot.backleftDrive.getCurrentPosition()) / 2; //frontright is cut out
                telemetry.addData("average Encoder", avgEncoder);
                telemetry.addData("goal", distance);
                telemetry.update();
            }
            robot.straight(0);
            robot.backleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.backrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.extendArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.pitchArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.extendArm.setPower(0);
            robot.pitchArm.setPower(0);
            robot.graber.setPosition(0.2);
            sleep(250);




            if (depotSide) {
                robot.rotate(0.2);  //turn to depot
                while (robot.imu.yaw < 130 && opModeIsActive()) //we want more, so let it overshoot (looking for 45)
                {
                    robot.imu.updateAngles();
                }
            }
            else {//craterside
                robot.rotate(-0.2); //correction turn
                while (robot.imu.yaw > -40 && opModeIsActive()) //we want 45, so let it overshoot
                {
                    robot.imu.updateAngles();
                }
            }









            robot.extendArm.setPower(.4);  //get arms to start moving
            robot.extendArm.setTargetPosition(4000);
            robot.extendArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.pitchArm.setPower(-1);
            robot.pitchArm.setTargetPosition(11000);
            robot.pitchArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            robot.frontrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.frontleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.backrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.backleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            sleep(250); //Give time for the reset
            robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //bc its broken last we checked
            robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //so its symmetrical
            robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            distance = inToDegrees(65); //50 inches to depot
            avgEncoder = 0;

            robot.straight(1); //back into depot
            while (avgEncoder < distance && opModeIsActive()) { //inToDegrees function below; distance declared earlier
                avgEncoder = (robot.backrightDrive.getCurrentPosition() + robot.backleftDrive.getCurrentPosition()) / 2; //frontright is cut out
                telemetry.addData("average Encoder", avgEncoder);
                telemetry.addData("goal", distance);
                telemetry.update();
            }

            robot.straight(0);
            fullLoop = false;
            while (opModeIsActive()) {
            } //end auto


        }
    }
    private double inToDegrees(double inches)
    {
        int wheelDiameter = 4; //Declared here bc always 4 with this bot
        int ticsPerRev = 1000; //Declared here bc always 4 with this bot
        double wheelCircum = Math.PI * wheelDiameter;
        double rotations = inches/wheelCircum;
        return rotations*ticsPerRev;
    }
    private void YellowMineralSeen()
    {
        robot.straight(.3);
        sleep(500);
        robot.straight(-.3);
        sleep(750);
        bumped = true;
    }
}




