package hudson.plugins.dry;

import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.ResultAction;
import hudson.plugins.analysis.core.AbstractProjectAction;

/**
 * Entry point to visualize the DRY trend graph in the project screen. Drawing
 * of the graph is delegated to the associated {@link ResultAction}.
 *
 * @author Ulli Hafner
 */
public class DryProjectAction extends AbstractProjectAction<ResultAction<DryResult>> {
    /**
     * Instantiates a new {@link DryProjectAction}.
     *
     * @param project
     *            the project that owns this action
     */
    public DryProjectAction(final AbstractProject<?, ?> project) {
        this(project, DryResultAction.class);
    }

    /**
     * Instantiates a new {@link DryProjectAction}.
     *
     * @param project
     *            the project that owns this action
     * @param type
     *            the result action type
     */
    public DryProjectAction(final AbstractProject<?, ?> project, final Class<? extends ResultAction<DryResult>> type) {
        super(project, type, Messages._DRY_ProjectAction_Name(), Messages._DRY_Trend_Name(),
                DryDescriptor.PLUGIN_ID, DryDescriptor.ICON_URL, DryDescriptor.RESULT_URL);
    }
}

