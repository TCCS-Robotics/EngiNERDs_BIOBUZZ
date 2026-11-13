package org.firstinspires.ftc.teamcode.SharedClasses;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import java.util.List;

/** Angle-only aiming. Camera MUST rotate with turret for closed-loop turret aiming. */
public class AprilTagTargeting_Turret {
    public enum Alliance { RED, BLUE }
    public enum AimMode { CLUSTER_CENTER, SCORING_OPENING }

    public static double KP = 0.015;
    public static double MIN_POWER = 0.08;
    public static double MAX_POWER = 0.25;
    public static double TOLERANCE_DEG = 1.5;
    public static double AIM_DIRECTION = 1.0; // Flip sign if turret moves away from target.
    public static long MAX_AGE_MS = 500;
    public static double SCORING_ANGLE_OFFSET_DEG = 0.0;

    /*
     * For partial-cluster aiming, calibrate each tag's horizontal offset relative
     * to the desired cluster center. Defaults of zero are ONLY a test approximation.
     * offset[i] = (measured tag tx) - (measured center tx), when all four visible.
     * Red scoring 30-33, red audience 34-37,
     * blue audience 38-41, blue scoring 42-45.
     */
    public static final double[] TAG_OFFSETS_DEG = new double[16];

    private final Limelight3A camera;
    public AprilTagTargeting_Turret(Limelight3A camera) { this.camera = camera; }

    public static class AimResult {
        public final boolean valid;
        public final double errorDeg, motorPower;
        public final int count, firstTagId;
        public final String status;
        AimResult(boolean valid, double errorDeg, double motorPower,
                  int count, int firstTagId, String status) {
            this.valid = valid;
            this.errorDeg = errorDeg;
            this.motorPower = motorPower;
            this.count = count;
            this.firstTagId = firstTagId;
            this.status = status;
        }
    }

    private AimResult invalid(String reason) {
        return new AimResult(false, 0, 0, 0, -1, reason);
    }

    public AimResult calculate(Alliance alliance, AimMode mode) {
        LLResult frame = camera.getLatestResult();
        if (frame == null || !frame.isValid() || camera.getTimeSinceLastUpdate() > MAX_AGE_MS)
            return invalid("No fresh valid frame");
        List<LLResultTypes.FiducialResult> tags = frame.getFiducialResults();
        if (tags == null || tags.isEmpty()) return invalid("No AprilTags visible");

        // Select whichever cluster on the chosen alliance has more usable tags.
        // Tie preference: scoring side.
        int scoringFirst = alliance == Alliance.RED ? 30 : 42;
        int audienceFirst = alliance == Alliance.RED ? 34 : 38;
        int scoringCount = 0, audienceCount = 0;
        boolean[] seen = new boolean[16];
        double[] tx = new double[16];
        for (LLResultTypes.FiducialResult tag : tags) {
            int id = tag.getFiducialId();
            if (id < 30 || id > 45) continue;
            double angle = tag.getTargetXDegrees();
            if (!Double.isFinite(angle) || seen[id - 30]) continue;
            seen[id - 30] = true;
            tx[id - 30] = angle;
            if (id >= scoringFirst && id < scoringFirst + 4) scoringCount++;
            if (id >= audienceFirst && id < audienceFirst + 4) audienceCount++;
        }
        int first = scoringCount >= audienceCount ? scoringFirst : audienceFirst;
        int count = Math.max(scoringCount, audienceCount);
        if (count == 0) return invalid("No selected-alliance tags visible");

        double sum = 0;
        for (int id = first; id < first + 4; id++) {
            if (seen[id - 30]) sum += tx[id - 30] - TAG_OFFSETS_DEG[id - 30];
        }
        double error = sum / count;
        if (mode == AimMode.SCORING_OPENING) error += SCORING_ANGLE_OFFSET_DEG;

        double power = 0;
        if (Math.abs(error) > TOLERANCE_DEG) {
            power = AIM_DIRECTION * KP * error;
            power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));
            if (Math.abs(power) < MIN_POWER) power = Math.copySign(MIN_POWER, power);
        }
        return new AimResult(true, error, power, count, first,
                (first == scoringFirst ? "Scoring" : "Audience") + " cluster; " +
                count + "/4 tags (offsets require calibration)");
    }
}
