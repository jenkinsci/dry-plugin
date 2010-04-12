package hudson.plugins.dry;

import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.AbstractProjectAction;

/**
 * Entry point to visualize the DRY trend graph in the project screen.
 * Drawing of the graph is delegated to the associated
 * {@link DryResultAction}.
 *
 * @author Ulli Hafner
 */
public class DryProjectAction extends AbstractProjectAction<DryResultAction> {
    /**
     * Instantiates a new find bugs project action.
     *
     * @param project
     *            the project that owns this action
     */
    public DryProjectAction(final AbstractProject<?, ?> project) {
        super(project, DryResultAction.class, new DryDescriptor());
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.DRY_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getTrendName() {
        return Messages.DRY_Trend_Name();
    }
}

