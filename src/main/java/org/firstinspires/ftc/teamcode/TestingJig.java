package org.firstinspires.ftc.teamcode;

import android.transition.Slide;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name="TestingJig", group="Linear Opmode")
public class TestingJig extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor Motor1 = null;
    private DcMotor Motor2 = null;
    private DcMotor Motor3 = null;
    private DcMotor Motor4 = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        Motor1 = hardwareMap.get(DcMotor.class, "Motor1");
        Motor2 = hardwareMap.get(DcMotor.class, "Motor2");
        Motor3 = hardwareMap.get(DcMotor.class, "Motor3");
        Motor4 = hardwareMap.get(DcMotor.class, "Motor4");

        Motor1.setDirection(DcMotor.Direction.FORWARD);
        Motor2.setDirection(DcMotor.Direction.FORWARD);
        Motor3.setDirection(DcMotor.Direction.FORWARD);
        Motor4.setDirection(DcMotor.Direction.FORWARD);

        Motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Motor3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Motor4.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor4.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        Motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Motor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Motor3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Motor4.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // START TESTING SEQUENCE
        // **********************
        telemetry.addData("Motor1", "Starting Blind Test");
        telemetry.update();
        Motor1.setPower(.5); //Forward 1 second
        sleep(1500);
        Motor1.setPower(0);  // Stop
        sleep(250);
        Motor1.setPower(-.5);
        sleep(1500); //Backward 1 second
        Motor1.setPower(0);
        double currentPosition = Motor1.getCurrentPosition();
        if (currentPosition > -50 && currentPosition < 50)
        {
            telemetry.addData("Motor1", "Success For Blind");
        }
        else {telemetry.addData("Motor1", "Failure For Blind");}

        telemetry.addData("Motor1", "Starting Encoder Test");
        telemetry.update();
        Motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(250); //Stop
        Motor1.setTargetPosition(200);
        Motor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor1.setPower(.5);
        currentPosition = Motor1.getCurrentPosition();
        if (currentPosition > 175 && currentPosition < 225)
        {
            telemetry.addData("Motor1", "Success For Blind");
        }
        else {telemetry.addData("Motor1", "Failure For Blind");}

        sleep(10000);

    }
}
