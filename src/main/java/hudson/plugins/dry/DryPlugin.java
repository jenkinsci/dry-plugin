package hudson.plugins.dry;

import hudson.Plugin;
import hudson.maven.MavenReporters;
import hudson.plugins.dry.util.DetailBuilder;
import hudson.tasks.BuildStep;

/**
 * Registers the DRY plug-in publisher.
 *
 * @author Ulli Hafner
 */
public class DryPlugin extends Plugin {
    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("DRY")
    public void start() throws Exception {
        BuildStep.PUBLISHERS.addRecorder(DryPublisher.DRY_DESCRIPTOR);

        MavenReporters.LIST.add(DryReporter.DRY_SCANNER_DESCRIPTOR);

        DetailBuilder.setDetailBuilder(DryDetailBuilder.class);
    }
}
