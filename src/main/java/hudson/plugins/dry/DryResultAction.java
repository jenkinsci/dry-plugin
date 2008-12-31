package hudson.plugins.dry;

import hudson.model.AbstractBuild;
import hudson.plugins.dry.util.AbstractResultAction;
import hudson.plugins.dry.util.HealthDescriptor;
import hudson.plugins.dry.util.PluginDescriptor;

import java.util.NoSuchElementException;

/**
 * Controls the live cycle of the DRY results. This action persists the
 * results of the DRY analysis of a build and displays the results on the
 * build page. The actual visualization of the results is defined in the
 * matching <code>summary.jelly</code> file.
 * <p>
 * Moreover, this class renders the DRY result trend.
 * </p>
 *
 * @author Ulli Hafner
 */
public class DryResultAction extends AbstractResultAction<DryResult> {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -5329651349674842873L;

    /**
     * Creates a new instance of <code>PmdResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param result
     *            the result in this build
     */
    public DryResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor, final DryResult result) {
        super(owner, new DryHealthDescriptor(healthDescriptor), result);
    }

    /**
     * Creates a new instance of <code>PmdResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     */
    public DryResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor) {
        super(owner, new DryHealthDescriptor(healthDescriptor));
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.DRY_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    protected PluginDescriptor getDescriptor() {
        return DryPublisher.DRY_DESCRIPTOR;
    }

    /**
     * Gets the DRY result of the previous build.
     *
     * @return the DRY result of the previous build.
     * @throws NoSuchElementException
     *             if there is no previous build for this action
     */
    public DryResultAction getPreviousResultAction() {
        AbstractResultAction<DryResult> previousBuild = getPreviousBuild();
        if (previousBuild instanceof DryResultAction) {
            return (DryResultAction)previousBuild;
        }
        throw new NoSuchElementException("There is no previous build for action " + this);
    }

    /** {@inheritDoc} */
    public String getMultipleItemsTooltip(final int numberOfItems) {
        return Messages.DRY_ResultAction_MultipleWarnings(numberOfItems);
    }

    /** {@inheritDoc} */
    public String getSingleItemTooltip() {
        return Messages.DRY_ResultAction_OneWarning();
    }
}
