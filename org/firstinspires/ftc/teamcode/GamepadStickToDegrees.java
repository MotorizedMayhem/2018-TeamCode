package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="GamepadStuff", group="Iterative Opmode")

public class GamepadStickToDegrees extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private float degree = 0;
    private float gamepadX = 0;
    private float gamepadY = 0;

    @Override
    public void init() {
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
        gamepadX = gamepad1.left_stick_x;
        gamepadY = gamepad1.left_stick_y;

        // Show the elapsed game time and wheel power.
        telemetry.addData("gamepadX", gamepadX);
        telemetry.addData("gamepadY", gamepadY);
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    @Override
    public void stop() {
    }

}
