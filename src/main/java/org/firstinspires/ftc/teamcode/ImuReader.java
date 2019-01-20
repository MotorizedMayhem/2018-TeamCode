
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.configuration.annotations.DigitalIoDeviceType;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
@Disabled
@TeleOp(name="ImuReader", group="Iterative Opmode")
public class ImuReader extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private Hardware2018Mecanum robot = new Hardware2018Mecanum();
    private MM_VuforiaRR vuforia = new MM_VuforiaRR(hardwareMap);

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");
        robot.init(hardwareMap);
        telemetry.addData("Status", "READY");
        vuforia.init(0,0,0);
    }


    @Override
    public void init_loop() {
    }


    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {
        robot.imu.updateAngles();
        telemetry.addData("Heading", robot.imu.yaw);
        while (gamepad1.a)
        {
            robot.pitchArm.setPower(.4);

        }
        while (gamepad1.y)
        {
            robot.pitchArm.setPower(-.4);

        }


        while (gamepad1.right_bumper)
        {
            robot.extendArm.setPower(0.5);

        }
        while (gamepad1.left_bumper)
        {
            robot.extendArm.setPower(-0.5);

        }
        while(gamepad1.x)
        {
            robot.extendArm.setPower(.5);
            robot.extendArm.setTargetPosition(1000);
            robot.extendArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.pitchArm.setPower(.8);
            robot.pitchArm.setTargetPosition(1000);
            robot.pitchArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        }
        while (gamepad1.b)
        {
            robot.extendArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.pitchArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.graber.setPosition(0.2);
        }
        if (gamepad1.right_bumper)
        {

            robot.extendArm.setPower(-.3);
            robot.extendArm.setTargetPosition(0);
            robot.extendArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            while (runtime.seconds() < 3){}

            robot.pitchArm.setPower(-.4);
            robot.pitchArm.setTargetPosition(0);
            robot.pitchArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            while (runtime.seconds() < 16){}

        }
        /*
        double[] colorStuff = robot.leftColorCollection();
        telemetry.addData("distance", colorStuff[0]);
        telemetry.addData("alpha","%.3f", colorStuff[1]);
        telemetry.addData("red", "%.3f",colorStuff[2]);
        telemetry.addData("green", "%.3f",colorStuff[3]);
        telemetry.addData("blue","%.3f", colorStuff[4]);
        telemetry.addData("H", "%.3f",colorStuff[5]);
        telemetry.addData("S", "%.3f",colorStuff[6]);
        telemetry.addData("V", "%.3f",colorStuff[7]);
        */


        robot.extendArm.setPower(0);
        robot.pitchArm.setPower(0);
        //telemetry.addData("Extend", robot.extendArm.getCurrentPosition());
        //telemetry.addData("Pitch", robot.pitchArm.getCurrentPosition());
        //telemetry.addData("Target Visible" , vuforia.getTarget());



    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
