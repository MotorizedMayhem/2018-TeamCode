package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;


@Autonomous(name = "ShortAuto")
public class AUtoShort extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    Hardware2018Mecanum robot = new Hardware2018Mecanum();
    private MM_VuforiaRR vuforia = new MM_VuforiaRR(hardwareMap);
    private MM_TFlow tFlow = new MM_TFlow(telemetry);
    boolean bumped = false;


    @Override
    public void runOpMode(){
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        robot.init(hardwareMap);
        boolean fullLoop = true;
        VuforiaLocalizer CameraInstance = vuforia.init(0,0,0);


        tFlow.init(hardwareMap, CameraInstance);


        telemetry.addData("Status", "READY");
        telemetry.update();
        robot.graber.setPosition(.6);//closed
        robot.pitchArm.setPower(0);
        waitForStart();

        while (opModeIsActive() && fullLoop) {
            //sleep(5000); //give coach time to skew it


            //Drop from Hook
            robot.liftMotor.setPower(1);
            while (robot.liftMotor.getCurrentPosition() < 18000) {
                telemetry.addData("Lift", robot.liftMotor.getCurrentPosition());
                telemetry.update();
            }
            robot.liftMotor.setPower(0);
            sleep(100);

            robot.liftMotor.setPower(-.5);
            robot.liftMotor.setTargetPosition(27000);
            robot.liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);





            //robot.straight(.1);
            //sleep(250);

            //######################## remove skew #################
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




            //########################## Drive To Jewel #####################
            robot.frontrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.frontleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.backrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.backleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            sleep(100); //Give time for the reset
            robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //bc its broken last we checked
            robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //so its symmetrical
            robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            double distance = inToDegrees(18); //24 inches to jewel
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








            sleep(100);
            robot.strafe(-.7); //gets to far jewel
            sleep(1200);
            robot.straight(0);//stop motors
            sleep(100); //time to stop
            //robot.strafe(.28);//start path back



            // ####################### TensorFlow Detection ###########################
            runtime.reset();
            double remainingMs = 0;
            double msToRun = 11000; //for the whole strafe //TODO WAS CHANGED
            tFlow.activate();
            int visMins = 0;
            bumped = false;


            /*
            robot.strafe(0);
            while(opModeIsActive() && runtime.milliseconds() < 2000)
            {
                visMins = tFlow.mineralsVisible();
                if (visMins == 1)
                {
                    if (tFlow.goodGoldFound())
                    {
                        remainingMs = msToRun - runtime.milliseconds();
                        YellowMineralSeen();
                        break;
                    }

                }
                telemetry.addData("Visble: ", visMins);
                telemetry.update();
            }
            */
            robot.strafe(0);
            while(opModeIsActive() && runtime.milliseconds() < 2000)
            {
                if (tFlow.goodGoldFound())
                {
                    remainingMs = msToRun - runtime.milliseconds();
                    YellowMineralSeen();
                    break;
                }
            }

            if(!bumped) {
                robot.strafe(.7);//start to Second
                sleep(1200);
            }


            /*
            robot.strafe(0);
            while(opModeIsActive() && runtime.milliseconds() < 5200 && !bumped)
            {
                visMins = tFlow.mineralsVisible();
                if (visMins == 1)
                {
                    if (tFlow.isGold())
                    {
                        remainingMs = msToRun - runtime.milliseconds();
                        YellowMineralSeen();
                        break;
                    }
                }
                telemetry.addData("Visble: ", visMins);
                telemetry.update();

            }
            */
            robot.strafe(0);
            while(opModeIsActive() && runtime.milliseconds() < 5200 && !bumped)
            {
                if (tFlow.goodGoldFound())
                {
                    remainingMs = msToRun - runtime.milliseconds();
                    YellowMineralSeen();
                    break;
                }
            }

            if (!bumped) {
                robot.strafe(.6);//To third
                sleep(1200);
            }

            if (!bumped) //hit third if still no
            {
                remainingMs = msToRun - runtime.milliseconds();
                YellowMineralSeen();
            }

            /*
            robot.strafe(0);
            while(opModeIsActive() && runtime.milliseconds() < 8400 && !bumped)
            {
                visMins = tFlow.mineralsVisible();
                if (visMins == 1)
                {
                    if (tFlow.isGold())
                    {
                        remainingMs = msToRun - runtime.milliseconds();
                        YellowMineralSeen();
                        break;
                    }
                }
                telemetry.addData("Visble: ", visMins);
                telemetry.update();

            }
            */

            /*
            while(opModeIsActive() && runtime.milliseconds() < 8500 )
            {
                visMins = tFlow.mineralsVisible();
                if (visMins == 1)
                {
                    if (tFlow.isGold())
                    {
                        remainingMs = msToRun - runtime.milliseconds();
                        YellowMineralSeen();
                        break;
                    }
                }
                telemetry.addData("Visble: ", visMins);
                telemetry.update();

            }
            */



            //remainingMs = msToRun - runtime.milliseconds();
            //TODO see if commenting above out messed it up


            tFlow.tfod.shutdown();


            //Start moving arms to over crater
            robot.extendArm.setPower(.4);  //get arms to start moving
            robot.extendArm.setTargetPosition(4000);
            robot.extendArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.pitchArm.setPower(-1);
            robot.pitchArm.setTargetPosition(11000);
            robot.pitchArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);



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
        robot.straight(.8);
        sleep(680);
        //robot.straight(-.8);
        //sleep(450);
        bumped = true;
    }
}




