package hudson.plugins.dry.dashboard;

import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.dashboard.AbstractWarningsGraphPortlet;
import hudson.plugins.dry.DryProjectAction;

/**
 * A base class for portlets of the Dry plug-in.
 *
 * @author Ulli Hafner
 */
public abstract class DryPortlet extends AbstractWarningsGraphPortlet {
    /**
     * Creates a new instance of {@link DryPortlet}.
     *
     * @param name
     *            the name of the portlet
     */
    public DryPortlet(final String name) {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected Class<? extends AbstractProjectAction<?>> getAction() {
        return DryProjectAction.class;
    }

    /** {@inheritDoc} */
    @Override
    protected String getPluginName() {
        return "dry";
    }
}
