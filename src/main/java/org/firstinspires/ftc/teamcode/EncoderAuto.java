package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@Autonomous(name = "EncoderAutoCrater")
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
        robot.graber.setPosition(.7);//closed
        robot.pitchArm.setPower(0);
        waitForStart();

        robot.liftMotor.setPower(.75);
        while (robot.liftMotor.getCurrentPosition() < 6000 && opModeIsActive()){
            telemetry.addData("Lift", robot.liftMotor.getCurrentPosition());
            telemetry.update();
        }
        robot.liftMotor.setPower(0);
        sleep(250);

        robot.straight(.2);
        sleep(2000); //forward for 1 second


        robot.rotate(-0.3);
        while (robot.imu.yaw < 71 && opModeIsActive()) //angle to point to picture
        {
            robot.imu.updateAngles();
            telemetry.addData("yaw", robot.imu.yaw);
            telemetry.update();
        }
        robot.straight(0.3);
        sleep(2750);
        robot.rotate(.3);
        while (robot.imu.yaw > 45 && opModeIsActive()) //angle to point to picture
        {
            robot.imu.updateAngles();
            telemetry.addData("yaw", robot.imu.yaw);
            telemetry.update();
        }
        robot.strafe(.6); //into square
        sleep(4750);

        robot.rotate(.3);
        while (robot.imu.yaw > -45 && opModeIsActive()) //angle to get claw over area
        {
            robot.imu.updateAngles();
            telemetry.addData("yaw", robot.imu.yaw);
            telemetry.update();
        }
        robot.straight(0);
        robot.graber.setPosition(0.6);//open claw just a bit
        sleep(1000);

        robot.straight(.5);
        sleep(2000); //back away from it


        robot.rotate(-.3);
        sleep(400); //slight tweak to the left so we dont hit the on field thing

        robot.straight(.5);
        sleep(2000); //back away from it

        robot.rotate(.3);
        while (robot.imu.yaw > -45 && opModeIsActive()) //angle to point to picture
        {
            robot.imu.updateAngles();
            telemetry.addData("yaw", robot.imu.yaw);
            telemetry.update();
        }
        robot.straight(0); //stop drive motors

        robot.pitchArm.setPower(10); //dunno if any different than 1, but we need to save time soooo
        while (robot.pitchArm.getCurrentPosition() < 5000 && opModeIsActive()) //TODO CHECK IF 5000 ENOUGH AND FIX
        {
            telemetry.addData("position",robot.pitchArm.getCurrentPosition());
            telemetry.update();
        }
        robot.pitchArm.setPower(0);




    }
}




