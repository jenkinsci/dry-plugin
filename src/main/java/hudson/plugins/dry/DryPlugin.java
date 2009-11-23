package hudson.plugins.dry;

import hudson.Plugin;
import hudson.plugins.analysis.views.DetailFactory;

/**
 * Registers the new detail builder.
 *
 * @author Ulli Hafner
 */
public class DryPlugin extends Plugin {
    /** {@inheritDoc} */
    @Override
    public void start() throws Exception {
        DetailFactory.setDetailBuilder(DryDetailBuilder.class);
    }
}
