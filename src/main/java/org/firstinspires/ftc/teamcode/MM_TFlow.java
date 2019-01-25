package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

public class MM_TFlow {
    private ElapsedTime runtime = new ElapsedTime();


    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = "Adxgm9L/////AAABmf4X4r11gU5QjdS+o++UzoZdYE8ZWx5AnTVVr3lhgbm7NXTbtSGDU2CeUqRgcliLekQqIQtK4SCFCGmTrC9fu/fN0Mlnl1ul2djmLaT+4y7bxti+F9IMOFl2bh9yO3qeny+yyv1/uzupVJM522Jt8kEjMl6wklFQCKjow+pCDDvKQ8/HiA/HjIV4qIcc/sqnIJys6BWUt6Oj5c1NuJIIU6L7A8dkYh29xC1DHAt9jnIRefQHr7wo/OjfvqvL6x2VFkh2/o7z600lMwWjRv+X6oQ3df8JvFn3DOaOiw1Qs6pnLo4DcSZrQY0F9Y/RjM4/u+BrtF53QTw188j6t0PTrsh5hWwuUDLnp1WLA0zFZNs/";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */


    public TFObjectDetector tfod;



    public Recognition recognition = null;
    public int numberSeen = 0;
    Telemetry telemetry;

    MM_TFlow(Telemetry tell){
        this.telemetry = tell;
    }

    public void init(HardwareMap hardwareMap, VuforiaLocalizer vuf){
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia(vuf);
        telemetry.addData("vuforiaInit", "worked");
        telemetry.update();


        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            telemetry.addData("got instance", "true");
            telemetry.update();
            sleep(100);
            initTfod(hardwareMap);
            telemetry.addData("initTFOD", "worked");
            telemetry.update();
            sleep(100);
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
            throw new RuntimeException();
        }

    }




    public void activate(){
        if (tfod != null) {
            tfod.activate();
        }
        else{
            throw new ArithmeticException();
        }
    }












    private int mineralsVisible(){

        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if(updatedRecognitions != null) {
            numberSeen = updatedRecognitions.size();
            if (numberSeen == 1) {
                recognition = updatedRecognitions.get(0); //sets the class-wide variable if theres only 1 mineral
            }

        }
        return  numberSeen; //if updated, responds new value
        // if not, reponds value from before
    }
    private boolean isGold(){
        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
            return true;
        }
        else {
            return false;
        }

    }
    public boolean goodGoldFound(){

        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if(updatedRecognitions != null) {
            numberSeen = updatedRecognitions.size();
            for (Recognition r: updatedRecognitions) {
                if(r.getConfidence() > 0.95 && r.getLabel().equals(LABEL_GOLD_MINERAL))
                {
                    telemetry.addData("Confidence", r.getConfidence());
                    telemetry.update();
                    return true;
                }
                telemetry.addData("Confidence", r.getConfidence());
                telemetry.update();

            }
        }
        return  false; //if updated, responds new value
    }












    private void initVuforia(VuforiaLocalizer obj) {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
        */
        this.vuforia = obj;
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod(HardwareMap hardwareMap) {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        //TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters();
        telemetry.addData("moniterID", "worked");
        telemetry.update();
        sleep(100);
        ClassFactory factory = ClassFactory.getInstance();

        tfod = factory.createTFObjectDetector(tfodParameters, vuforia);


        telemetry.addData("Created Instance", "worked");
        telemetry.update();
        sleep(100);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }
    private void sleep(int ms)
    {
        runtime.reset();
        while(runtime.milliseconds() < ms){};
    }

}
