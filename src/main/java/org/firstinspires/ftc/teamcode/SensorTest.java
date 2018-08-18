package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp(name="SensorTest", group="Linear Opmode")
public class SensorTest extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private NormalizedColorSensor colorSensor;
    private LED light = null;
    private DistanceSensor distanceSensor = null;
    private Servo s1 = null;


    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");
        light = hardwareMap.get(LED.class, "lightStrip");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");



        Servo.Direction one  = s1.getDirection();












        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        telemetry.setAutoClear(false);
        Telemetry.Item ColorRed = telemetry.addData("Red", "False");
        Telemetry.Item ColorBlue = telemetry.addData("Blue", "False");
        Telemetry.Item ColorGreen = telemetry.addData("Green", "False");
        boolean redSeen = false, blueSeen = false, greenSeen = false;

        NormalizedRGBA colors = null;
        while (opModeIsActive())
        {
            colors = colorSensor.getNormalizedColors(); //Returns red, blue, green, and alpha from [0,1]
            if (colors.red > 0.9) //means it sees mostly blue
            {
                ColorRed.setValue("True");
                redSeen = true;
            }
            if (colors.blue > 0.9) //means it sees mostly blue
            {
                ColorBlue.setValue("True");
                blueSeen = true;
            }
            if (colors.green > 0.9) //means it sees mostly blue
            {
                ColorGreen.setValue("True");
                greenSeen = true;
            }
            if (greenSeen && blueSeen && redSeen)
            {
                break; //end if we see them all
            }
            telemetry.update();
        }
        telemetry.clear();
        telemetry.update();
        Telemetry.Item distanceAway = telemetry.addData("Distance", "0 in");
        while (opModeIsActive())
        {
            distanceAway.setValue(distanceSensor.getDistance(DistanceUnit.INCH)+ "in");
            telemetry.update();
        }




    }


}
