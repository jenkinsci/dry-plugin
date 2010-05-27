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
@Extension(ordinal = 100) // NOCHECKSTYLE
public final class DryDescriptor extends PluginDescriptor {
    /** Plug-in name. */
    private static final String PLUGIN_NAME = "dry";
    /** Icon to use for the result and project action. */
    private static final String ACTION_ICON = "/plugin/dry/icons/dry-24x24.png";
    /** Validates the user input. */
    private static final ThresholdValidation VALIDATION = new ThresholdValidation();

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
        return VALIDATION.validateHigh(highThreshold, normalThreshold);
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
        return VALIDATION.validateNormal(highThreshold, normalThreshold);
    }
}