package hudson.plugins.dry;

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.MavenResultAction;
import hudson.plugins.analysis.core.ParserResult;

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
    public DryMavenResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final DryResult result) {
        super(new DryResultAction(owner, healthDescriptor, result), defaultEncoding, "DRY");
    }

    /** {@inheritDoc} */
    public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
        return new DryMavenResultAction(build, getHealthDescriptor(), getDisplayName(),
                new DryResult(build, getDefaultEncoding(), new ParserResult()));
    }

    /** {@inheritDoc} */
    public Action getProjectAction(final MavenModuleSet moduleSet) {
        return new DryProjectAction(moduleSet, DryMavenResultAction.class);
    }

    @Override
    public Class<? extends MavenResultAction<DryResult>> getIndividualActionType() {
        return DryMavenResultAction.class;
    }

    @Override
    protected DryResult createResult(final DryResult existingResult, final DryResult additionalResult) {
        return new DryReporterResult(getOwner(), additionalResult.getDefaultEncoding(), aggregate(existingResult, additionalResult));
    }
}

