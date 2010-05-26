package hudson.plugins.dry;

import hudson.util.FormValidation;

/**
 * Validates the number of lines thresholds.
 *
 * @author Ulli Hafner
 */
public class ThresholdValidation {
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
            if (normal >= 0 && high > normal) {
                return FormValidation.ok();
            }
        }
        catch (NumberFormatException exception) {
            // ignore and return failure
        }
        return FormValidation.error(message);
    }
}

