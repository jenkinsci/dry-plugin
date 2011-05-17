package hudson.plugins.dry;

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.MavenResultAction;

import java.util.List;
import java.util.Map;

/**
 * A {@link DryResultAction} for native Maven jobs. This action
 * additionally provides result aggregation for sub-modules and for the main
 * project.
 *
 * @author Ulli Hafner
 */
public class DryMavenResultAction extends MavenResultAction<DryResult> {
    /**
     * Creates a new instance of {@link DryMavenResultAction}. This instance
     * will have no result set in the beginning. The result will be set
     * successively after each of the modules are build.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public DryMavenResultAction(final MavenModuleSetBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding) {
        super(new DryResultAction(owner, healthDescriptor), defaultEncoding, "DRY");
    }

    /**
     * Creates a new instance of {@link DryMavenResultAction}.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the result in this build
     */
    public DryMavenResultAction(final MavenBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final DryResult result) {
        super(new DryResultAction(owner, healthDescriptor, result), defaultEncoding, "DRY");
    }

    /** {@inheritDoc} */
    public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
        return new DryMavenResultAction(build, getHealthDescriptor(), getDisplayName());
    }

    /** {@inheritDoc} */
    public Action getProjectAction(final MavenModuleSet moduleSet) {
        return new DryProjectAction(moduleSet);
    }

    @Override
    public Class<? extends MavenResultAction<DryResult>> getIndividualActionType() {
        return DryMavenResultAction.class;
    }

    @Override
    protected DryResult createResult(final DryResult existingResult, final DryResult additionalResult) {
        return new DryResult(getOwner(), additionalResult.getDefaultEncoding(), aggregate(existingResult, additionalResult));
    }
}

