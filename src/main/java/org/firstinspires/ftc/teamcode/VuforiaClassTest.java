
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.teamcode.MM_VuforiaRR;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

@Disabled
@TeleOp(name="VuforiaClassTest", group="Linear Opmode")
public class VuforiaClassTest extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    MM_VuforiaRR vuforia = new MM_VuforiaRR(hardwareMap);
    MM_IMU imu = new MM_IMU();


    @Override
    public void runOpMode() {
        vuforia.init(0,0,0);
        imu.init(hardwareMap);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        double correction = 0;
        while (opModeIsActive()) {
            /*
            if (gamepad1.a && vuforia.TargetVisible())
            {
                double current = imu.yaw;
                double correct = vuforia.getPosition()[5];//gets heading
                correction = correct - current;

            }
            */
            if (gamepad1.a){
                correction = vuforia.getCorrection(imu);
            }
            if(vuforia.TargetVisible())
            {
                double[] position = vuforia.getPosition();
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        position[0],position[1],position[2]);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f",
                        position[3], position[4], position[5]);
                telemetry.addData("Raw IMU", imu.yaw);
                telemetry.addData("Correction", correction);
                telemetry.addData("Corrected", imu.correctedYaw);
                telemetry.update();


            }
            else {
                telemetry.addData("Status","No Target Visible");
                telemetry.addData("Raw IMU", imu.yaw);
                telemetry.addData("Correction", correction);
                telemetry.addData("Corrected", imu.correctedYaw);
                telemetry.update();

            }
            imu.updateCorrectedAngles(correction);


        }
    }
    /*
    private double getCorrected(double addedCorr)
    {

        double corrected = addedCorr;
        if (addedCorr > 180){
            corrected = addedCorr-360;
        }
        else if(addedCorr < -180)
        {
            corrected = addedCorr+360;
        }

        return corrected;
    }
    */
}
