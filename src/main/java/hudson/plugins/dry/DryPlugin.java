package hudson.plugins.dry;

import hudson.Plugin;
import hudson.plugins.analysis.views.DetailFactory;

/**
 * Registers the new detail builder.
 *
 * @author Ulli Hafner
 */
public class DryPlugin extends Plugin {
    @Override
    public void start() {
        DryDetailBuilder detailBuilder = new DryDetailBuilder();
        DetailFactory.addDetailBuilder(DryResultAction.class, detailBuilder);
        DetailFactory.addDetailBuilder(DryMavenResultAction.class, detailBuilder);
    }
}
