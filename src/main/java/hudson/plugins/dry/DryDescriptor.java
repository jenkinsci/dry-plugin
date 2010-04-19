package hudson.plugins.dry;

import hudson.Extension;
import hudson.plugins.analysis.core.PluginDescriptor;
import hudson.util.FormValidation;

import org.kohsuke.stapler.QueryParameter;


/**
 * Descriptor for the class {@link DryPublisher}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
@Extension(ordinal = 100)
public final class DryDescriptor extends PluginDescriptor {
    /** Plug-in name. */
    private static final String PLUGIN_NAME = "dry";
    /** Icon to use for the result and project action. */
    private static final String ACTION_ICON = "/plugin/dry/icons/dry-24x24.png";

    /**
     * Instantiates a new find bugs descriptor.
     */
    public DryDescriptor() {
        super(DryPublisher.class);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return Messages.DRY_Publisher_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    /** {@inheritDoc} */
    @Override
    public String getIconUrl() {
        return ACTION_ICON;
    }

    /**
     * Performs on-the-fly validation on threshold for high warnings.
     *
     * @param highThreshold
     *            the threshold for high warnings
     * @param normalThreshold
     *            the threshold for normal warnings
     * @return the validation result
     */
    public FormValidation doCheckHighThreshold(@QueryParameter final String highThreshold, @QueryParameter final String normalThreshold) {
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
    public FormValidation doCheckNormalThreshold(@QueryParameter final String highThreshold, @QueryParameter final String normalThreshold) {
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