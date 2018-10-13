package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;



public class MM_VuforiaRR {
    public List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
    OpenGLMatrix lastLocation = null;
    // Since ImageTarget trackables use mm to specifiy their dimensions, we must use mm for all the physical dimension.
    // We will define some constants and conversions here
    final float mmPerInch        = 25.4f;
    final float mmFTCFieldWidth  = (12*6) * mmPerInch;       // the width of the FTC field (from the center point to the outer panels)
    final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    private HardwareMap hardwareMap = null;
    //constructor
    MM_VuforiaRR(HardwareMap hardwareMap)
    {
        this.hardwareMap = hardwareMap;
    }
    public void init(int IN_CAMERA_FORWARD_DISPLACEMENT,int IN_CAMERA_LEFT_DISPLACEMENT,int IN_CAMERA_VERTICAL_DISPLACEMENT )
    {
        final String VUFORIA_KEY = "AW4mcUD/////AAAAGfEcj3JVME4OtxqGU15CdbR8KU+l1F5ydlW8/+eo5yhpCDnwc0cuzi5JOAP0NPPNcxHHwp2ItJKlVMGPloFdY155bwe/JZDyQyRNiFq8Q8h3IRv06QICR2Mig4xmD1KmnqVGKoZa/aT16h+0/EVULOlm+xX1OJvJLjiIOtVUWYNzUgMka1ls/FbCIh9BLK4z/BviY+C6tTZ94SPsKOLXwPRxngu1t/lHtixZ12hCPp50JGxtw5OYf47PC5MjRHhDO2x4rpgLE+SHrL+Ijx9KWG7RJV4Y4oo9ODsElgttiaI8eeT3nmol8m6ziaCNO9srrXLnJCbo5H2rxm1D2dpFyTV5NZfa64uFd443PTSZ0Hot";



        // Select which camera you want use.  The FRONT camera is the one on the same side as the screen.
        // Valid choices are:  BACK or FRONT
        final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;

        //Now declared in start of classes
        //OpenGLMatrix lastLocation = null;
        boolean targetVisible = false;

        /**
         * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
         * localization engine.
         */
        VuforiaLocalizer vuforia;

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        //VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();


        parameters.vuforiaLicenseKey = VUFORIA_KEY ;
        parameters.cameraDirection   = CAMERA_CHOICE;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Load the data sets that for the trackable objects. These particular data
        // sets are stored in the 'assets' part of our application.

        //TODO MAYBE BROKE THIS
        VuforiaTrackables targetsRoverRuckus = vuforia.loadTrackablesFromAsset("RoverRuckus");
        VuforiaTrackable blueRover = targetsRoverRuckus.get(0);
        blueRover.setName("Blue-Rover");
        VuforiaTrackable redFootprint = targetsRoverRuckus.get(1);
        redFootprint.setName("Red-Footprint");
        VuforiaTrackable frontCraters = targetsRoverRuckus.get(2);
        frontCraters.setName("Front-Craters");
        VuforiaTrackable backSpace = targetsRoverRuckus.get(3);
        backSpace.setName("Back-Space");

        // For convenience, gather together all the trackable objects in one easily-iterable collection */
        //List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        //now declared at top
        allTrackables.addAll(targetsRoverRuckus);


        OpenGLMatrix blueRoverLocationOnField = OpenGLMatrix
                .translation(0, mmFTCFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0));
        blueRover.setLocation(blueRoverLocationOnField);


        OpenGLMatrix redFootprintLocationOnField = OpenGLMatrix
                .translation(0, -mmFTCFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180));
        redFootprint.setLocation(redFootprintLocationOnField);


        OpenGLMatrix frontCratersLocationOnField = OpenGLMatrix
                .translation(-mmFTCFieldWidth, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90));
        frontCraters.setLocation(frontCratersLocationOnField);


        OpenGLMatrix backSpaceLocationOnField = OpenGLMatrix
                .translation(mmFTCFieldWidth, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90));
        backSpace.setLocation(backSpaceLocationOnField);


        final int CAMERA_FORWARD_DISPLACEMENT  = IN_CAMERA_FORWARD_DISPLACEMENT;   // eg: Camera is 110 mm in front of robot center
        final int CAMERA_VERTICAL_DISPLACEMENT = IN_CAMERA_VERTICAL_DISPLACEMENT;   // eg: Camera is 200 mm above ground
        final int CAMERA_LEFT_DISPLACEMENT     = IN_CAMERA_LEFT_DISPLACEMENT;     // eg: Camera is ON the robot's center line
        //TODO ARE VERT AND LEFT BACKWARD
        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES,
                        CAMERA_CHOICE == FRONT ? 90 : -90, 0, -90));
        //Third Angle is 0 in example, rotated 90 to work with last year game bot

        /**  Let all the trackable listeners know where the phone is.  */
        for (VuforiaTrackable trackable : allTrackables)
        {
            ((VuforiaTrackableDefaultListener)trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        }



        /** Start tracking the data sets we care about. */
        targetsRoverRuckus.activate();

    }
    public boolean TargetVisible()
    {
        boolean targetVisible = false;
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                targetVisible = true;

                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
                break;
            }
        }
        return targetVisible;

    }
    public double[] getPosition(){ //returns in format x,y,z,pitch,yaw,roll
        VectorF translation = lastLocation.getTranslation();
        //telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
          //      translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);
        // express the rotation of the robot in degrees.

        Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
        //telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
        double[] position = {translation.get(0)/mmPerInch,translation.get(1)/mmPerInch,translation.get(2)/mmPerInch,rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle};
        return position;
    }

}
