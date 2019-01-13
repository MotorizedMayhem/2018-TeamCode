
package org.firstinspires.ftc.teamcode;

import android.text.method.MovementMethod;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Demonstrates empty OpMode
 */
@Autonomous(name = "AutonomousV1")
public class AutoV1 extends LinearOpMode { //FOR QUADRANT 1 and 3

    private ElapsedTime runtime = new ElapsedTime();
    Hardware2018Mecanum robot = new Hardware2018Mecanum();
    MM_VuforiaRR vuforia = new MM_VuforiaRR(hardwareMap);
    AutoMove movement = new AutoMove(robot, vuforia, telemetry);

    @Override
    public void runOpMode() { //RIGHT NOW FOR STARTING QUADRANT 3; Left of red pit
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        robot.init(hardwareMap);
        vuforia.init(0, 0, 0);

        telemetry.addData("Status", "READY");
        telemetry.update();

        waitForStart();
        runtime.reset();
        //TODO DROP
        robot.straight(.2);
        sleep(500); //forward for 1 second
        robot.straight(0); //stop robot
        sleep(100);
        robot.rotate(-0.10);
        int i = 0;
        while (opModeIsActive()) //rotate left until target seen
        {
            if(vuforia.TargetVisible()){
                break;
            }
            sleep(50);
            telemetry.addData("times", i);
            telemetry.update();
            i++;
        }
        robot.straight(0);
        sleep(1000); //for use to check if pointed right direction
        movement.VuforiaMove(0,-65);
        movement.VuforiaMove(0,-65); //run twice for safety
        sleep(1000);
        robot.rotate(-.15);
        while(opModeIsActive())
        {
            robot.imu.updateCorrectedAngles(movement.imuCorrection);
            if(robot.imu.correctedYaw < .5 && robot.imu.correctedYaw > -.5) //go until within 2 degrees of 0, which is our goal)
            {
            break;
            }
            telemetry.addData("Current Rotation" , robot.imu.correctedYaw);
        }
        robot.straight(.6);
        sleep(3000); //TODO GET NUMBER RIGHT
        robot.straight(0);
    }


}
