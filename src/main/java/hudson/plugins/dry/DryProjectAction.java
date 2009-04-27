package hudson.plugins.dry;

import hudson.model.AbstractProject;
import hudson.plugins.dry.util.AbstractProjectAction;

/**
 * Entry point to visualize the DRY trend graph in the project screen.
 * Drawing of the graph is delegated to the associated
 * {@link DryResultAction}.
 *
 * @author Ulli Hafner
 */
public class DryProjectAction extends AbstractProjectAction<DryResultAction> {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -654316141132780561L;

    /**
     * Instantiates a new find bugs project action.
     *
     * @param project
     *            the project that owns this action
     * @param height
     *            the height of the trend graph
     */
    public DryProjectAction(final AbstractProject<?, ?> project, final int height) {
        super(project, DryResultAction.class, DryPublisher.DRY_DESCRIPTOR, height);
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

