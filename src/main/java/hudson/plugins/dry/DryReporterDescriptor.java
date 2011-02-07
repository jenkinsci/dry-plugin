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
@Extension(ordinal = 100) // NOCHECKSTYLE
public class DryReporterDescriptor extends ReporterDescriptor {
    /** Validates the thresholds user input. */
    private static final ThresholdValidation THRESHOLD_VALIDATION = new ThresholdValidation();

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
        return THRESHOLD_VALIDATION.validateHigh(highThreshold, normalThreshold);
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
        return THRESHOLD_VALIDATION.validateNormal(highThreshold, normalThreshold);
    }
}

