package hudson.plugins.dry;

import hudson.util.FormValidation;

/**
 * Validates the number of lines thresholds.
 *
 * @author Ulli Hafner
 */
public class ThresholdValidation {
    /** Minimum number of duplicate lines for a warning with priority high. */
    static final int DEFAULT_HIGH_THRESHOLD = 50;
    /** Minimum number of duplicate lines for a warning with priority normal. */
    static final int DEFAULT_NORMAL_THRESHOLD = 25;

    /**
     * Performs on-the-fly validation on threshold for high warnings.
     *
     * @param highThreshold
     *            the threshold for high warnings
     * @param normalThreshold
     *            the threshold for normal warnings
     * @return the validation result
     */
    public FormValidation validateHigh(final String highThreshold, final String normalThreshold) {
        return validate(highThreshold, normalThreshold, Messages.DRY_ValidationError_HighThreshold());
    }

    /**
     * Performs on-the-fly validation on threshold for normal warnings.
     *
     * @param highThreshold
     *            the threshold for high warnings
     * @param normalThreshold
     *            the threshold for normal warnings
     * @return the validation result
     */
    public FormValidation validateNormal(final String highThreshold, final String normalThreshold) {
        return validate(highThreshold, normalThreshold, Messages.DRY_ValidationError_NormalThreshold());
    }

    /**
     * Performs on-the-fly validation on thresholds for high and normal warnings.
     *
     * @param highThreshold
     *            the threshold for high warnings
     * @param normalThreshold
     *            the threshold for normal warnings
     * @param message
     *            the validation message
     * @return the validation result
     */
    private FormValidation validate(final String highThreshold, final String normalThreshold, final String message) {
        try {
            int high = Integer.parseInt(highThreshold);
            int normal = Integer.parseInt(normalThreshold);
            if (isValid(normal, high)) {
                return FormValidation.ok();
            }
        }
        catch (NumberFormatException exception) {
            // ignore and return failure
        }
        return FormValidation.error(message);
    }

    /**
     * Returns the minimum number of duplicate lines for a warning with priority
     * high.
     *
     * @param normalThreshold
     *            the normal threshold
     * @param highThreshold
     *            the high threshold
     * @return the minimum number of duplicate lines for a warning with priority
     *         high
     */
    public int getHighThreshold(final int normalThreshold, final int highThreshold) {
        if (!isValid(normalThreshold, highThreshold)) {
            return DEFAULT_HIGH_THRESHOLD;
        }
        return highThreshold;
    }

    private boolean isValid(final int normalThreshold, final int highThreshold) {
        return !(highThreshold <= 0 || normalThreshold <= 0 || highThreshold <= normalThreshold);
    }

    /**
     * Returns the minimum number of duplicate lines for a warning with priority
     * normal.
     *
     * @param normalThreshold
     *            the normal threshold
     * @param highThreshold
     *            the high threshold
     * @return the minimum number of duplicate lines for a warning with priority
     *         normal
     */
    public int getNormalThreshold(final int normalThreshold, final int highThreshold) {
        if (!isValid(normalThreshold, highThreshold)) {
            return DEFAULT_NORMAL_THRESHOLD;
        }
        return normalThreshold;
    }
}

