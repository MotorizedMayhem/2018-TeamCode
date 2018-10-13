package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
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
    private ColorSensor colorSensor;
    private DistanceSensor distanceSensor = null;
    private Servo lightStrip = null;
    private DigitalChannel touchSensor = null;


    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");
        //distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        // ^^ wasnt working
        touchSensor = hardwareMap.get(DigitalChannel.class, "touchSensor");
        lightStrip = hardwareMap.get(Servo.class, "lightStrip");




        telemetry.addData("Status", "Initialized");
        telemetry.update();






        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        telemetry.setAutoClear(false);



        //############################## Color Sensor ###############################
        Telemetry.Item ColorSeen = telemetry.addData("Color Seen", "R: , G: , B:");
        Telemetry.Item ColorRed = telemetry.addData("Red", "False");
        Telemetry.Item ColorBlue = telemetry.addData("Blue", "False");
        Telemetry.Item ColorGreen = telemetry.addData("Green", "False");
        boolean redSeen = false, blueSeen = false, greenSeen = false;
        setLights(lightStrip, 2); //visually pleasing pattern while we wait to see colors
        while (opModeIsActive())
        {
            ColorSeen.setValue("R: %1d, G: %1d, B: %1d", colorSensor.red(), colorSensor.green(), colorSensor.blue());
            telemetry.update();
            if (colorSensor.red() > 250) //means it sees mostly blue
            {
                ColorRed.setValue("True");
                redSeen = true;
                setLights(lightStrip, 81); //Set Lights to red
            }
            if (colorSensor.blue() > 250) //means it sees mostly blue
            {
                ColorBlue.setValue("True");
                blueSeen = true;
                setLights(lightStrip, 94); //Set Lights to blue
            }
            if (colorSensor.green() > 250) //means it sees mostly blue
            {
                ColorGreen.setValue("True");
                greenSeen = true;
                setLights(lightStrip, 89); //Set Lights to green
            }
            if (greenSeen && blueSeen && redSeen)
            {
                sleep(1000); //Give a little bit of time for the last color light to be seen
                setLights(lightStrip, 97); //Set Lights to white
                break; //end if we see them all
            }
            telemetry.update();
        }
        sleep(2000);
        telemetry.clear();


        //####################### Distance Sensor ###############################
        /*
        telemetry.addData("Instructions", "Bring hand in until lights flash, then bring back out");
        Telemetry.Item distanceAway = telemetry.addData("Distance", "0 in");
        telemetry.update();
        double distance=5;
        while (opModeIsActive() && distance > 2) //Wait till hand is within 2 in
        {
            distance = distanceSensor.getDistance(DistanceUnit.INCH);
            distanceAway.setValue(distance + "in");
            telemetry.update();
        }
        setLights(lightStrip, 88); //dark green
        sleep(500);
        setLights(lightStrip, 81); //back to red
        while (opModeIsActive() && distance < 10 ) //Wait till hand is more than 10 in away
        {
            distance = distanceSensor.getDistance(DistanceUnit.INCH);
            distanceAway.setValue(distance + "in");
            telemetry.update();
        }
        setLights(lightStrip, 88); //dark green
        sleep(2000);
        telemetry.clear();
        */

        // ########################### Touch Sensor #####################
        telemetry.addData("Instructions", "Click touch sensor");
        Telemetry.Item touch = telemetry.addData("touch", "false");
        telemetry.update();

        while (opModeIsActive() && touchSensor.getState())//touch sensor is not pressed
        {
            setLights(lightStrip, 81); //red
            //sleep(50); //give the processor a break
        }
        while (opModeIsActive() && !touchSensor.getState())
        {
            setLights(lightStrip, 88); //dark green
            //sleep(50); //give the processor a break
        }
        setLights(lightStrip, 81); //back to red to show release
        sleep(2000);


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
