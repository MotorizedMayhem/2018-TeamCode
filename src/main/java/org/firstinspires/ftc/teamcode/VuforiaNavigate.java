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
    //private MM_IMU imu = new MM_IMU(hardwareMap);

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
        while (opModeIsActive()) {
            if (vuforia.TargetVisible())
            {
                position = vuforia.getPosition(); //returned in inches and degrees
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        position[0],position[1],position[2]);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f",
                        position[3], position[4], position[5]);
                telemetry.update();
                break;
            }
        }
        sleep(3000);
        robot.frontrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double desiredY;
        try {
             desiredY = 50 - position[1];
             telemetry.addData("desiredY", desiredY);
        }
        catch (java.lang.NullPointerException e)
        {
            telemetry.addData("ERROR", "Couldn't pull current position, assuming 0");
            desiredY= 50;
        }
        telemetry.update();
        sleep(3000);
        robot.frontleftDrive.setPower(.25);
        robot.frontrightDrive.setPower(.25);
        robot.backleftDrive.setPower(.25);
        robot.backrightDrive.setPower(.25);

        //only checks right, for now
        while (robot.frontleftDrive.getCurrentPosition() < inToDegrees(desiredY)){
            telemetry.addData("Front_Right", robot.frontrightDrive.getCurrentPosition());
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
        double wheelCircum = 3.14159 * wheelDiameter;
        double rotations = inches/wheelCircum;
        return rotations*ticsPerRev;
    }
}
