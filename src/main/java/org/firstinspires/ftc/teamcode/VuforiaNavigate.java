
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.MM_VuforiaRR;


@TeleOp(name="VuforiaNavigate", group="Linear Opmode")
public class VuforiaNavigate extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    MM_VuforiaRR vuforia = new MM_VuforiaRR();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Initailize our Vuforia Class, give displacements in mm
        vuforia.init(125,-150,-165);
        //vuforia.init(0,0,0);
        //TODO LEFT AND VERT BACKWARD?

        telemetry.addData("Status", "READY");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
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
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double desiredY;
        try {
             desiredY = 55 - position[1];
             telemetry.addData("desiredY", desiredY);
        }
        catch (java.lang.NullPointerException e)
        {
            telemetry.addData("ERROR", "Couldn't pull current position, assuming 0");
            desiredY= 55;
        }
        telemetry.update();
        sleep(3000);
        rightDrive.setPower(.25);
        leftDrive.setPower(.25);
        //only checks right, for now
        while (rightDrive.getCurrentPosition() < inToDegrees(desiredY,4, 1000)){
            telemetry.addData("Right", rightDrive.getCurrentPosition());
            telemetry.update();
        }
        rightDrive.setPower(0);
        leftDrive.setPower(0);
        sleep(5000);

    }
    public double inToDegrees(double inches, double wheelDiameter, int ticsPerRev)
    {
        double wheelCircum = 3.14159 * wheelDiameter;
        double rotations = inches/wheelCircum;
        return rotations*ticsPerRev;
    }
}
