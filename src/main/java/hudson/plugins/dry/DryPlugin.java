package hudson.plugins.dry;

import hudson.Plugin;
import hudson.plugins.analysis.util.DetailBuilder;

/**
 * Registers the new detail builder.
 *
 * @author Ulli Hafner
 */
public class DryPlugin extends Plugin {
    /** {@inheritDoc} */
    @Override
    public void start() throws Exception {
        DetailBuilder.setDetailBuilder(DryDetailBuilder.class);
    }
}
