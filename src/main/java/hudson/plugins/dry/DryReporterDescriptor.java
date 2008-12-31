package hudson.plugins.dry;

import hudson.maven.MavenReporter;
import hudson.plugins.dry.util.PluginDescriptor;
import hudson.plugins.dry.util.ReporterDescriptor;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for the class {@link DryReporter}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
public class DryReporterDescriptor extends ReporterDescriptor {
    /**
     * Creates a new instance of <code>PmdReporterDescriptor</code>.
     *
     * @param pluginDescriptor
     *            the plug-in descriptor of the publisher
     */
    public DryReporterDescriptor(final PluginDescriptor pluginDescriptor) {
        super(DryReporter.class, pluginDescriptor);
    }

    /** {@inheritDoc} */
    @Override
    public MavenReporter newInstance(final StaplerRequest request, final JSONObject formData) throws FormException {
        return request.bindJSON(DryReporter.class, formData);
    }
}

