
package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@TeleOp(name="MecanumTest", group="Iterative Opmode")
public class MecanumTest extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private Hardware2018Mecanum robot = new Hardware2018Mecanum();
    //private MM_VuforiaRR vuforia = new MM_VuforiaRR(hardwareMap);

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");
        robot.init(hardwareMap);
        //vuforia.init(125,-150,-165);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */

    boolean yPress = false;
    @Override
    public void loop() {
        robot.imu.updateAngles();

        if (yPress)
        {
            robot.gamepadDriveNoCL(gamepad1.left_stick_x,gamepad1.left_stick_y,gamepad1.right_stick_x);
        }
        else
        {
            robot.gamepadDriveCL(gamepad1.left_stick_x,gamepad1.left_stick_y,gamepad1.right_stick_x,robot.imu.yaw);
        }
        if (gamepad1.y){
            if (yPress)
            {
                yPress=false;
            }
            else
            {
               yPress=true;
            }
        }
        telemetry.addData("yPress", yPress);
        telemetry.addData("imu heading", robot.imu.yaw);
        if (gamepad1.a)
        {
            robot.straight(.2);
            double avgEncoder = (robot.backrightDrive.getCurrentPosition() + robot.backleftDrive.getCurrentPosition())/2;
            while (avgEncoder < 1000){
                avgEncoder = (robot.backrightDrive.getCurrentPosition() + robot.backleftDrive.getCurrentPosition())/2;
            }
            robot.straight(0);
        }
        while (gamepad1.b)
        {
            robot.frontleftDrive.setPower(0.25);
            robot.backleftDrive.setPower(0.25);

            robot.frontrightDrive.setPower(0.25);
            robot.backrightDrive.setPower(0.25);
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

    private void sleep(int ms){
        runtime.reset();
        while (runtime.milliseconds() < ms){}
    }
}
