package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.PWMOutputController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

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
        int indexLight = 50;
        double currentPWM = setLights(lightStrip,indexLight);
        while (opModeIsActive()) {
            /*
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
            */

            if (gamepad1.b){
                indexLight++;
                currentPWM=setLights(lightStrip,indexLight);
                sleep(300);
            }
            if (gamepad1.a){
                indexLight--;
                currentPWM=setLights(lightStrip,indexLight);
                sleep(300);
            }
            if (gamepad1.y){
                indexLight+=10;
                currentPWM=setLights(lightStrip,indexLight);
                sleep(300);
            }
            if (gamepad1.x){
                indexLight-=10;
                currentPWM=setLights(lightStrip,indexLight);
                sleep(300);
            }

            if (gamepad1.dpad_up){
                currentPWM+=0.0001;
                setLights(lightStrip, currentPWM);
                sleep(300);
            }
            if (gamepad1.dpad_down)
            {
                currentPWM -= 0.0001;
                setLights(lightStrip,currentPWM);
                sleep(300);
            }
        }

    }
    private double setLights(Servo lights, int input)
    {
        input = Range.clip(input, 1, 100) ;
        double newPWM = input*0.00555 + 0.22075;
        lights.setPosition(newPWM);
        position.setValue("%6f",lights.getPosition());
        index.setValue(input);
        telemetry.update();
        return  newPWM;
    }
    private void setLights(Servo lights, double newPWM)
    {
        lights.setPosition(newPWM);
        position.setValue("%6f",lights.getPosition());
        telemetry.update();
    }



}
