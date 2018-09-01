package org.firstinspires.ftc.teamcode;

import android.transition.Slide;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import javax.security.auth.callback.Callback;


@TeleOp(name="Motor+ControllerTest", group="Linear Opmode")
public class MotorTest extends LinearOpMode {

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

          testMotor(Motor1, MotorOne);
          testMotor(Motor2, MotorTwo);
          testMotor(Motor3, MotorThree);
          testMotor(Motor4, MotorFour);




    }
    private void testMotor(DcMotor Motor, Telemetry.Item motorTelem)
    {
        Motor.setPower(.5); //Forward 1.5 second
        sleep(1500);
        Motor.setPower(0);  // Stop
        sleep(250);
        Motor.setPower(-.5);
        sleep(1500); //Backward 1.5 second
        Motor.setPower(0);
        //TODO find some way to check if motors ran w/o encoders
        motorTelem.setValue("Blind: Don't Know       Encoder: Starting");
        telemetry.update();


        //Encoder
        Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(250); //Stop
        Motor.setTargetPosition(200);
        Motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor.setPower(.5);
        while (Motor.isBusy() && opModeIsActive()){sleep(50);}
        double currentPosition = Motor.getCurrentPosition();
        if (currentPosition > 190 && currentPosition < 210)
        {
            motorTelem.setValue("Blind: Don't Know       Encoder: Success, (%.2f)", currentPosition);
        }
        else {motorTelem.setValue("Blind: Don't Know       Encoder: Failure, (%.2f)", currentPosition);}
        telemetry.update();
    }

    private void testGamepad(Gamepad gamepad, String gamepadName) //Unused in this program
    {
        Telemetry.Item buttonA = telemetry.addData(gamepadName + "A", "False");
        Telemetry.Item buttonB = telemetry.addData(gamepadName + "B", "False");
        Telemetry.Item buttonX = telemetry.addData(gamepadName + "X", "False");
        Telemetry.Item buttonY = telemetry.addData(gamepadName + "Y", "False");
        Telemetry.Item TimeRemaining = telemetry.addData("Time Remaining", "10");
        telemetry.addData(gamepadName, "Press all letters on right");
        telemetry.update();
        runtime.reset();
        boolean[] gamepadLetters = new boolean[]{false,false,false,false};
        while (runtime.seconds() < 10 && opModeIsActive()) {
            if (gamepad.a){
                buttonA.setValue("True");
                gamepadLetters[0] = true;
            }
            if (gamepad.b){
                buttonB.setValue("True");
                gamepadLetters[1] = true;
            }
            if (gamepad.x){
                buttonX.setValue("True");
                gamepadLetters[2] = true;
            }
            if (gamepad.y){
                buttonY.setValue("True");
                gamepadLetters[3] = true;
            }
            if (gamepadLetters[0] && gamepadLetters[1] && gamepadLetters[2] && gamepadLetters[3])
            {
                break;
            }
            TimeRemaining.setValue("(%.1f)",10-runtime.seconds());
            telemetry.update();
        }
        sleep(2000);


        //################ TEST STICKS #######################
        telemetry.clear();
        runtime.reset();
        Telemetry.Item leftStick = telemetry.addData(gamepadName + "leftStick", "False");
        Telemetry.Item rightStick = telemetry.addData(gamepadName + "rightStick", "False");
        telemetry.addData("Directions", "Spin both sticks around and press both");
        TimeRemaining = telemetry.addData("Time Remaining", "10");

        boolean leftStickDone = false, rightStickDone = false;
        boolean leftStickRight = false, leftStickLeft = false, leftStickUp = false, leftStickDown = false, leftStickPress= false;
        boolean rightStickRight = false, rightStickLeft = false, rightStickUp = false, rightStickDown = false, rightStickPress = false; //creates 4 booleans
        while (runtime.seconds() < 10 && opModeIsActive())
        {
            // ######## LEFT STICK #################
            if (gamepad.left_stick_x > 0.9){ leftStickRight = true;}
            if (gamepad.left_stick_x < -0.9){ leftStickLeft = true;}
            if (gamepad.left_stick_y > 0.9){ leftStickDown = true;}
            if (gamepad.left_stick_y < -0.9){ leftStickUp = true;}
            if (gamepad.left_stick_button){leftStickPress = true;}
            if (leftStickRight && leftStickLeft && leftStickUp && leftStickDown && leftStickPress)
            {
                leftStick.setValue("True");
                leftStickDone = true;
                telemetry.update();
            }

            // ######### RIGHT STICK ###############

            if (gamepad.right_stick_x > 0.9){ rightStickRight = true;}
            if (gamepad.right_stick_x < -0.9){ rightStickLeft = true;}
            if (gamepad.right_stick_y > 0.9){ rightStickDown = true;}
            if (gamepad.right_stick_y < -0.9){ rightStickUp = true;}
            if (gamepad.right_stick_button){rightStickPress = true;}
            if (rightStickRight && rightStickLeft && rightStickUp && rightStickDown && rightStickPress)
            {
                rightStick.setValue("True");
                rightStickDone = true;
                telemetry.update();
            }

            if (rightStickDone && leftStickDone){break;}
            TimeRemaining.setValue("(%.1f)",10-runtime.seconds());
            telemetry.update();
        }
        sleep(2000);
        telemetry.clear();


        //################ Test Bumpers And Triggers #######################
        runtime.reset();
        Telemetry.Item leftBumper = telemetry.addData(gamepadName + "leftBumper", "False");
        Telemetry.Item rightBumper = telemetry.addData(gamepadName + "rightBumper", "False");
        Telemetry.Item leftTrigger = telemetry.addData(gamepadName + "leftTrigger", "False");
        Telemetry.Item rightTrigger = telemetry.addData(gamepadName + "rightTrigger", "False");
        telemetry.addData("Directions", "Press both bumpers and squeeze both triggers");
        TimeRemaining = telemetry.addData("Time Remaining", "10");

        boolean leftBumperDone = false, rightBumperDone = false;
        boolean leftTriggerDone = false, rightTriggerDone = false;
        while (runtime.seconds() < 10 && opModeIsActive())
        {
            // ######## LEFT  #################
            if (gamepad.left_bumper){
                leftBumperDone = true;
                leftBumper.setValue("True");
            }
            if (gamepad.left_trigger > 0.9){
                leftTriggerDone = true;
                leftTrigger.setValue("True");
            }

            // ######### RIGHT STICK ###############
            if (gamepad.right_bumper){
                rightBumperDone = true;
                rightBumper.setValue("True");
            }
            if (gamepad.right_trigger > 0.9){
                rightTriggerDone = true;
                rightTrigger.setValue("True");
            }

            if (leftBumperDone && leftTriggerDone && rightBumperDone && rightTriggerDone){break;}
            TimeRemaining.setValue("(%.1f)",10-runtime.seconds());
            telemetry.update();
        }
        sleep(2000);
        telemetry.clear();

        // ############### D-pad #################
        telemetry.clear();
        runtime.reset();
        Telemetry.Item dPadUp = telemetry.addData(gamepadName + "dPadUp", "False");
        Telemetry.Item dPadDown = telemetry.addData(gamepadName + "dPadDown", "False");
        Telemetry.Item dPadRight = telemetry.addData(gamepadName + "dPadRight", "False");
        Telemetry.Item dPadLeft = telemetry.addData(gamepadName + "dPadLeft", "False");
        telemetry.addData("Directions", "Press all of the buttons on the D-Pad");
        TimeRemaining = telemetry.addData("Time Remaining", "10");

        boolean dPadUpDone = false, dPadDownDone = false;
        boolean dPadRightDone = false, dPadLeftDone = false;
        while (runtime.seconds() < 10 && opModeIsActive())
        {
            if (gamepad.dpad_up){
                dPadUpDone = true;
                dPadUp.setValue("True");
            }
            if (gamepad.dpad_right){
                dPadRightDone = true;
                dPadRight.setValue("True");
            }
            if (gamepad.dpad_down){
                dPadDownDone = true;
                dPadDown.setValue("True");
            }
            if (gamepad.dpad_left){
                dPadLeftDone = true;
                dPadLeft.setValue("True");
            }

            if (dPadDownDone && dPadLeftDone && dPadRightDone && dPadUpDone){break;}
            TimeRemaining.setValue("(%.1f)",10-runtime.seconds());
            telemetry.update();
        }
        sleep(2000);
        telemetry.clear();


    }

}
