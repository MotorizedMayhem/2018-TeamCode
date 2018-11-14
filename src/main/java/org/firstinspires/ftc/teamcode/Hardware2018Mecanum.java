/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import static java.lang.Thread.sleep;


/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */
public class Hardware2018Mecanum
{
    /* Public OpMode members. */
    public DcMotor  frontleftDrive   = null;
    public DcMotor  frontrightDrive  = null;
    public DcMotor  backleftDrive   = null;
    public DcMotor  backrightDrive  = null;

    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();
    public MM_IMU imu = null;


    /* Constructor */
    public Hardware2018Mecanum(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;
        imu = new MM_IMU();

        // Define and Initialize Motors
        frontleftDrive  = hwMap.get(DcMotor.class, "frontleft");
        frontrightDrive = hwMap.get(DcMotor.class, "frontright");
        backleftDrive  = hwMap.get(DcMotor.class, "backleft");
        backrightDrive = hwMap.get(DcMotor.class, "backright");
        frontleftDrive.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        frontrightDrive.setDirection(DcMotor.Direction.FORWARD);// Set to REVERSE if using AndyMark motors
        backleftDrive.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        backrightDrive.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors

        // Set all motors to zero power
        frontleftDrive.setPower(0);
        frontrightDrive.setPower(0);
        backleftDrive.setPower(0);
        backrightDrive.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        frontleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backleftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backrightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Define and initialize ALL installed servos.

        //Initialize imu
        imu.init(hwMap);
    }
    public void gamepadDrive(double gamepadX, double gamepadY, double rotation, double imu_yaw)
    {
        // Setup a variable for each drive wheel to save power level for telemetry
        double r = Math.hypot(-gamepadX, gamepadY);
        //double robotAngle = Math.atan2(gamepadY, -gamepadX) - Math.PI / 4
        double imu_correction = imu_yaw * (Math.PI/180);
        double robotAngle = Math.atan2(gamepadY, -gamepadX) - Math.PI / 4 - imu_correction;

        double rightX = -rotation;//negative bc its intuitive if you put in x on a stick
        final double v1 = r * Math.cos(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        frontleftDrive.setPower(v1);
        frontrightDrive.setPower(v2);
        backleftDrive.setPower(v3);
        backrightDrive.setPower(v4);
    }
    public void rotate(double power)
    {
        //positive power is clockwise rotation
        frontleftDrive.setPower(power);
        backleftDrive.setPower(power);

        frontrightDrive.setPower(-power);
        backrightDrive.setPower(-power);
    }
    public void resetAllEncoders()
    {

        frontrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backrightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backleftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //sleep(250);//Give time for the reset
        try { //stolen off stackOverflow, who knows if it works
            sleep(200,0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        frontrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backleftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backrightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void moveBetweenPoints(double x1,double y1,double x2,double y2,double imu_correction)
    {
        double deltaX = x2-x1;
        double deltaY = y2-y1;
        //get current position
        imu.updateAngles();
        double currentRotation = imu.yaw; //maybe replace with vuforia
        double finalRotation = (Math.atan2(deltaY,deltaX))*(180/Math.PI);//converts it back to degrees
        double distance = Math.hypot(deltaY,deltaX);
            //same as sqrt(x^2+y^2)
        if (finalRotation > currentRotation){
            imu.updateAngles();
            currentRotation = (imu.yaw+imu_correction)%360;
            while (currentRotation < finalRotation)
            {
                rotate(0.25);
            }
        }
        else if (finalRotation < currentRotation){
            imu.updateAngles();
            currentRotation = (imu.yaw+imu_correction)%360;
            while (currentRotation > finalRotation)
            {
                rotate(-0.25);
            }

        }
        //if we're already at the right heading, we dont need to rotate at all



    }
    public double getImuCorrection(MM_VuforiaRR vuforia) { //should only be called if we see a picture
        imu.updateAngles();
        double current = imu.yaw;
        double correct = vuforia.getPosition()[5];//z or yaw in that class
        return (correct-current);
    }




}



