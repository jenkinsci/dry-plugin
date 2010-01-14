package hudson.plugins.dry;

import hudson.Extension;
import hudson.maven.MavenReporter;
import hudson.plugins.analysis.core.ReporterDescriptor;
import net.sf.json.JSONObject;

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
}

