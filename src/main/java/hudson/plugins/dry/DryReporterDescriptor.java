package hudson.plugins.dry;

import hudson.Extension;
import hudson.maven.MavenReporter;
import hudson.plugins.analysis.core.ReporterDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for the class {@link DryReporter}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
@Extension(ordinal = 100)
public class DryReporterDescriptor extends ReporterDescriptor {
    /**
     * Creates a new instance of {@link DryReporterDescriptor}.
     */
    public DryReporterDescriptor() {
        super(DryReporter.class, new DryDescriptor());
    }

    /** {@inheritDoc} */
    @Override
    public MavenReporter newInstance(final StaplerRequest request, final JSONObject formData) throws FormException {
        return request.bindJSON(DryReporter.class, formData);
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

