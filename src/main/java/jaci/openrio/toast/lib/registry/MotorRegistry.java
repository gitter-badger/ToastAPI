package jaci.openrio.toast.lib.registry;

import edu.wpi.first.wpilibj.SpeedController;
import jaci.openrio.toast.core.Toast;

import java.util.LinkedList;
import java.util.List;

/**
 * Keeps track of Motor Instances and makes sure motors don't continue to run when we want them to stop.
 *
 * @author Jaci
 */
public class MotorRegistry {

    public static List<SpeedController> motors = new LinkedList<>();

    /**
     * Register a new SpeedController on the registry. This is automatically done in the patches
     * MotorSafetyHelper.
     */
    public static void register(Object motor) {
        if (motor instanceof SpeedController)
            motors.add((SpeedController) motor);
    }

    /**
     * Disable all motors
     */
    public static void stopAll() {
        for (SpeedController s : motors) {
            try {
                s.set(0);
            } catch (Exception e) {
                Toast.log().error("Motor Safety issue: " + e);
                Toast.log().exception(e);
            }
        }
    }

}
