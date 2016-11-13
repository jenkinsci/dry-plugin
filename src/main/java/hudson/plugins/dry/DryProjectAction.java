package hudson.plugins.dry;

import java.util.List;

import hudson.model.Job;
import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.core.ResultAction;
import hudson.plugins.analysis.graph.BuildResultGraph;

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
     * @param job
     *            the job that owns this action
     */
    public DryProjectAction(final Job<?, ?> job) {
        this(job, DryResultAction.class);
    }

    /**
     * Instantiates a new {@link DryProjectAction}.
     *
     * @param job
     *            the job that owns this action
     * @param type
     *            the result action type
     */
    public DryProjectAction(final Job<?, ?> job, final Class<? extends ResultAction<DryResult>> type) {
        super(job, type, Messages._DRY_ProjectAction_Name(), Messages._DRY_Trend_Name(),
                DryDescriptor.PLUGIN_ID, DryDescriptor.ICON_URL, DryDescriptor.RESULT_URL);
    }

    @Override
    protected List<BuildResultGraph> getAvailableGraphs() {
        List<BuildResultGraph> availableGraphs = super.getAvailableGraphs();
        availableGraphs.add(0, new DuplicatedLinesGraph());
        return availableGraphs;
    }
}

