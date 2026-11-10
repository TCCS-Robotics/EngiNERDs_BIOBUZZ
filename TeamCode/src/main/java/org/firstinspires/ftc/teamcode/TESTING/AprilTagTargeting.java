package org.firstinspires.ftc.teamcode.TESTING;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import java.util.List;

/** First working chassis aiming test: require ALL FOUR cluster tags. */
public class AprilTagTargeting {
    public enum Cluster {
        RED_SCORING(30), RED_AUDIENCE(34), BLUE_SCORING(42), BLUE_AUDIENCE(38);
        public final int firstId;
        Cluster(int firstId) { this.firstId = firstId; }
    }
    public enum AimMode { CLUSTER_CENTER, SCORING_OPENING }

    public static double KP = 0.015;
    public static double MIN_ROTATE = 0.12;
    public static double MAX_ROTATE = 0.25;
    public static double TOLERANCE_DEG = 1.5;
    public static double AIM_DIRECTION = 1.0; // Reverse if turning away
    public static long MAX_AGE_MS = 500;
    public static double SCORING_ANGLE_OFFSET_DEG = 0.0;
    public static boolean SCORING_OFFSET_CALIBRATED = false;

    private final Limelight3A camera;
    public AprilTagTargeting(Limelight3A camera) { this.camera = camera; }

    public static class AimResult {
        public final boolean valid;
        public final double errorDeg, rotation, estimatedRangeIn;
        public final int count;
        public final String status;
        AimResult(boolean valid, double errorDeg, double rotation,
                  double rangeIn, int count, String status) {
            this.valid = valid; this.errorDeg = errorDeg;
            this.rotation = rotation; this.estimatedRangeIn = rangeIn;
            this.count = count; this.status = status;
        }
    }
    private AimResult invalid(int count, String reason) {
        return new AimResult(false, 0, 0, 0, count, reason);
    }

    public AimResult calculate(Cluster cluster, AimMode mode) {
        if (mode == AimMode.SCORING_OPENING && !SCORING_OFFSET_CALIBRATED)
            return invalid(0, "Scoring offset not calibrated; press A");

        LLResult frame = camera.getLatestResult();
        if (frame == null || !frame.isValid() ||
                camera.getTimeSinceLastUpdate() > MAX_AGE_MS)
            return invalid(0, "No fresh valid frame");

        List<LLResultTypes.FiducialResult> detections = frame.getFiducialResults();
        if (detections == null) return invalid(0, "No fiducials");

        boolean[] seen = new boolean[4];
        double[] angles = new double[4];
        int count = 0;
        for (LLResultTypes.FiducialResult tag : detections) {
            int index = tag.getFiducialId() - cluster.firstId;
            if (index < 0 || index >= 4 || seen[index]) continue;
            double tx = tag.getTargetXDegrees();
            if (!Double.isFinite(tx)) continue;
            seen[index] = true;
            angles[index] = tx;
            count++;
        }
        // Do not average a partial cluster: it creates a systematic offset.
        if (count != 4) return invalid(count, "Need all 4 selected-cluster tags");

        // Approximate optical center when all four tags are visible.
        double error = (angles[0] + angles[1] + angles[2] + angles[3]) / 4.0;
        if (mode == AimMode.SCORING_OPENING) error += SCORING_ANGLE_OFFSET_DEG;

        /////////////////////// CALCULATE ROTATION ///////////////////////

        double rotation;

// If within tolerance, stop rotating
        if (Math.abs(error) <= TOLERANCE_DEG) {

            rotation = 0.0;

        } else {

            // Calculate proportional rotation power
            rotation = AIM_DIRECTION * KP * error;

            // Limit maximum rotation power
            rotation = Math.max(-MAX_ROTATE,
                    Math.min(MAX_ROTATE, rotation));

            // Apply minimum rotation power
            if (Math.abs(rotation) < MIN_ROTATE) {
                rotation = Math.copySign(MIN_ROTATE, rotation);
            }
        }

        return new AimResult(true, error, rotation, 0, count,
                "Four-tag angle-only approximation");
    }
}
