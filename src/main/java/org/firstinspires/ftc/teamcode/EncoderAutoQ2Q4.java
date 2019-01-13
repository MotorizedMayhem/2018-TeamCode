package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "EncoderAutoQ1+Q3")
public class EncoderAuto extends LinearOpMode { //FOR QUADRANT 1 and 3

    private ElapsedTime runtime = new ElapsedTime();
    Hardware2018Mecanum robot = new Hardware2018Mecanum();
    @Override
    public void runOpMode(){
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        robot.init(hardwareMap);


        telemetry.addData("Status", "READY");
        telemetry.update();
        waitForStart();



        //TODO DROP
        robot.straight(.2);
        sleep(2000); //forward for 1 second
        robot.straight(0); //stop robot
        sleep(100);
        robot.rotate(-0.2);
        while (robot.imu.yaw < 71 && opModeIsActive()) //angle to point to picture
        {
            robot.imu.updateAngles();
            telemetry.addData("yaw", robot.imu.yaw);
            telemetry.update();
        }
        robot.straight(0.3);
        sleep(2750);
        robot.rotate(.2);
        while (robot.imu.yaw > 45 && opModeIsActive()) //angle to point to picture
        {
            robot.imu.updateAngles();
            telemetry.addData("yaw", robot.imu.yaw);
            telemetry.update();
        }
        robot.strafe(.6);
        sleep(5000);
        robot.straight(0);
        sleep(1000);
        robot.strafe(-0.8);
        sleep(2000);

        robot.rotate(-.2);
        sleep(250); //slight tweak to the left so we dont hit the on field thing

        robot.strafe(-0.8);
        sleep(2750);

        robot.rotate(.2);
        while (robot.imu.yaw > -45 && opModeIsActive()) //angle to point to picture
        {
            robot.imu.updateAngles();
            telemetry.addData("yaw", robot.imu.yaw);
            telemetry.update();
        }

        //TODO EXTEND ARM



    }
}




