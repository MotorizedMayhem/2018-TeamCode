package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp(name="SensorTest", group="Linear Opmode")
public class SensorTest extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private NormalizedColorSensor colorSensor;
    private LED light = null;
    private DistanceSensor distanceSensor = null;
    private Servo lightStrip = null;
    private TouchSensor touchSensor = null;


    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");
        light = hardwareMap.get(LED.class, "lightStrip");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");











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
        telemetry.addData("Instructions", "Bring hand in until lights flash, then bring back out");
        Telemetry.Item distanceAway = telemetry.addData("Distance", "0 in");
        telemetry.update();
        double distance=5;
        while (opModeIsActive() && distance > 2)
        {
            distance = distanceSensor.getDistance(DistanceUnit.INCH);
            distanceAway.setValue(distance + "in");
            telemetry.update();
        }
        setLights(lightStrip, 88); //dark green
        sleep(200);
        setLights(lightStrip, 81); //back to red
        while (opModeIsActive() && distance < 10 )
        {
            distance = distanceSensor.getDistance(DistanceUnit.INCH);
            distanceAway.setValue(distance + "in");
            telemetry.update();
        }
        setLights(lightStrip, 88); //dark green
        sleep(200);

        telemetry.clear();
        telemetry.addData("Instructions", "Click touch sensor");
        Telemetry.Item touch = telemetry.addData("touch", "false");
        telemetry.update();
        while (opModeIsActive() && !touchSensor.isPressed())//touch sensor is not pressed
        {
            setLights(lightStrip, 81); //red
        }
        setLights(lightStrip, 88); //dark green
        sleep(200);


    }
    private void setLights(Servo lights, int input)
    {
        input = Range.clip(input, 1, 100) ;
        double newPWM = input*0.00555 + 0.22075;
        lights.setPosition(newPWM);
        //position.setValue("%6f",lights.getPosition());
        //index.setValue(input);
        //telemetry.update();
    }


}
