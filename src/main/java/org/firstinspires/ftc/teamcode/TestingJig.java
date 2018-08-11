package org.firstinspires.ftc.teamcode;

import android.transition.Slide;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;


@TeleOp(name="TestingJig", group="Linear Opmode")
public class TestingJig extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor Motor1 = null;
    private DcMotor Motor2 = null;
    private DcMotor Motor3 = null;
    private DcMotor Motor4 = null;

    private BNO055IMU imu;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        //##################Setup Motors###########################
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

        //########################Initialize IMU #############################################
        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator(); //TODO try commenting this and later line (after wait for start) out and see what happens

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
        telemetry.setAutoClear(false);

        // START MOTOR TESTING SEQUENCE
        // **********************
        Telemetry.Item MotorOne = telemetry.addData("Motor1", "Bind: Testing        Encoder: Waiting"); //Because we start right after
        Telemetry.Item MotorTwo = telemetry.addData("Motor2", "Waiting");
        Telemetry.Item MotorThree = telemetry.addData("Motor3", "Waiting");
        Telemetry.Item MotorFour = telemetry.addData("Motor4", "Waiting");
        telemetry.update();

        //####################### Motor 1 #############################
        Motor1.setPower(.5); //Forward 1.5 second
        sleep(1500);
        Motor1.setPower(0);  // Stop
        sleep(250);
        Motor1.setPower(-.5);
        sleep(1500); //Backward 1.5 second
        Motor1.setPower(0);
        //TODO find some way to check if motors ran w/o encoders
        MotorOne.addData("Motor1", "Blind: Don't Know       Encoder: Starting");
        telemetry.update();

        //Encoder
        Motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(250); //Stop
        Motor1.setTargetPosition(200);
        Motor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor1.setPower(.5);
        double currentPosition = Motor1.getCurrentPosition();
        if (currentPosition > 190 && currentPosition < 210)
        {
            MotorOne.addData("Motor1", "Blind: Don't Know       Encoder: Success");
        }
        else {MotorOne.addData("Motor1", "Blind: Don't Know       Encoder: Failure");}
        telemetry.update();


        //####################### Motor 2 #############################
        Motor2.setPower(.5); //Forward 1.5 second
        sleep(1500);
        Motor2.setPower(0);  // Stop
        sleep(250);
        Motor2.setPower(-.5);
        sleep(1500); //Backward 1.5 second
        Motor2.setPower(0);
    
        MotorTwo.addData("Motor2", "Blind: Don't Know       Encoder: Starting");
        telemetry.update();

        //Encoder
        Motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(250); //Stop
        Motor2.setTargetPosition(200);
        Motor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor2.setPower(.5);
        currentPosition = Motor2.getCurrentPosition();
        if (currentPosition > 190 && currentPosition < 210)
        {
            MotorTwo.addData("Motor2", "Blind: Don't Know       Encoder: Success");
        }
        else {MotorTwo.addData("Motor2", "Blind: Don't Know       Encoder: Failure");}
        telemetry.update();


        //####################### Motor 3 #############################
        Motor3.setPower(.5); //Forward 1.5 second
        sleep(1500);
        Motor3.setPower(0);  // Stop
        sleep(250);
        Motor3.setPower(-.5);
        sleep(1500); //Backward 1.5 second
        Motor3.setPower(0);

        MotorThree.addData("Motor3", "Blind: Don't Know       Encoder: Starting");
        telemetry.update();

        //Encoder
        Motor3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(250); //Stop
        Motor3.setTargetPosition(200);
        Motor3.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor3.setPower(.5);
        currentPosition = Motor3.getCurrentPosition();
        if (currentPosition > 190 && currentPosition < 210)
        {
            MotorThree.addData("Motor3", "Blind: Don't Know       Encoder: Success");
        }
        else {MotorThree.addData("Motor3", "Blind: Don't Know       Encoder: Failure");}
        telemetry.update();


        //####################### Motor 4 #############################
        Motor4.setPower(.5); //Forward 1.5 second
        sleep(1500);
        Motor4.setPower(0);  // Stop
        sleep(250);
        Motor4.setPower(-.5);
        sleep(1500); //Backward 1.5 second
        Motor4.setPower(0);

        MotorFour.addData("Motor4", "Blind: Don't Know       Encoder: Starting");
        telemetry.update();

        //Encoder
        Motor4.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(250); //Stop
        Motor4.setTargetPosition(200);
        Motor4.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor4.setPower(.5);
        currentPosition = Motor4.getCurrentPosition();
        if (currentPosition > 190 && currentPosition < 210)
        {
            MotorFour.addData("Motor4", "Blind: Don't Know       Encoder: Success");
        }
        else {MotorFour.addData("Motor4", "Blind: Don't Know       Encoder: Failure");}


        telemetry.addData("To Continue", "Press A on gamepad");
        telemetry.update();
        while (!gamepad1.a || !gamepad2.a){sleep(50);} //wait for a on either gamepad
        telemetry.clear();
        telemetry.addData("Controllers","We;re gonna test those now"); //TODO at robotics



    }
}
