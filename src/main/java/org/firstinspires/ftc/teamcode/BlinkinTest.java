package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.PWMOutputController;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;


@TeleOp(name="BlinkinTest", group="Linear Opmode")
public class BlinkinTest extends LinearOpMode {
    private Servo lightStrip = null;
    private Telemetry.Item position = null;
    private Telemetry.Item index = null;


    @Override
    public void runOpMode() {
        telemetry.setAutoClear(false);
        lightStrip = hardwareMap.get(Servo.class, "lightStrip");
        position = telemetry.addData("Position", "start");
        index = telemetry.addData("Input", "start");

        waitForStart();
        while (opModeIsActive()) {
            setLights(lightStrip,1); // Rainbow, Rainbow Palette
            sleep(2000);
            setLights(lightStrip,22); //Fire, Large
            sleep(2000);
            setLights(lightStrip,39); //Heartbeat Blue
            sleep(2000);
            setLights(lightStrip,61); //Color 2 Light Chase
            sleep(2000);
            setLights(lightStrip,78); //Sinelon Color 1 and 2
            sleep(2000);
            setLights(lightStrip,79); //Hot pink
            sleep(2000);
            break;
            }

    }
    private void setLights(Servo lights, int input)
    {
        double newPWM = input*0.005 + 0.2475;
        lights.setPosition(newPWM);
        position.setValue(newPWM);
        index.setValue(input);
        telemetry.update();
    }



}
