package hudson.plugins.dry;

import hudson.maven.MavenBuildProxy;
import hudson.maven.MojoInfo;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.model.Action;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.FilesParser;
import hudson.plugins.analysis.core.HealthAwareMavenReporter;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.plugins.dry.parser.DuplicationParserRegistry;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Publishes the results of the duplicate code analysis (maven 2 project type).
 *
 * @author Ulli Hafner
 */
public class DryReporter extends HealthAwareMavenReporter {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2272875032054063496L;

    /** Default DRY pattern. */
    private static final String DRY_XML_FILE = "cpd.xml";

    /** Minimum number of duplicate lines for high priority warnings. @since 2.5 */
    private final int highThreshold;
    /** Minimum number of duplicate lines for normal priority warnings. @since 2.5 */
    private final int normalThreshold;

    /**
     * Creates a new instance of <code>PmdReporter</code>.
     *
     * @param threshold
     *            Annotation threshold to be reached if a build should be considered as
     *            unstable.
     * @param newThreshold
     *            New annotations threshold to be reached if a build should be
     *            considered as unstable.
     * @param failureThreshold
     *            Annotation threshold to be reached if a build should be considered as
     *            failure.
     * @param newFailureThreshold
     *            New annotations threshold to be reached if a build should be
     *            considered as failure.
     * @param healthy
     *            Report health as 100% when the number of warnings is less than
     *            this value
     * @param unHealthy
     *            Report health as 0% when the number of warnings is greater
     *            than this value
     * @param thresholdLimit
     *            determines which warning priorities should be considered when
     *            evaluating the build stability and health
     * @param canRunOnFailed
     *            determines whether the plug-in can run for failed builds, too
     * @param highThreshold
     *            minimum number of duplicate lines for high priority warnings
     * @param normalThreshold
     *            minimum number of duplicate lines for normal priority warnings
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @DataBoundConstructor
    public DryReporter(final String threshold, final String newThreshold,
            final String failureThreshold, final String newFailureThreshold,
            final String healthy, final String unHealthy, final String thresholdLimit, final boolean canRunOnFailed,
            final int highThreshold, final int normalThreshold) {
        super(threshold, newThreshold, failureThreshold, newFailureThreshold,
                healthy, unHealthy, thresholdLimit, canRunOnFailed, "DRY");
        this.highThreshold = highThreshold;
        this.normalThreshold = normalThreshold;
    }
    // CHECKSTYLE:ON

    /**
     * Returns the minimum number of duplicate lines for high priority warnings.
     *
     * @return the minimum number of duplicate lines for high priority warnings
     */
    public int getHighThreshold() {
        if (highThreshold <= 0 || highThreshold <= normalThreshold) {
            return 50;
        }
        return highThreshold;
    }

    /**
     * Returns the minimum number of duplicate lines for high normal warnings.
     *
     * @return the minimum number of duplicate lines for high normal warnings
     */
    public int getNormalThreshold() {
        if (normalThreshold <= 0 || highThreshold <= normalThreshold) {
            return 25;
        }
        return normalThreshold;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean acceptGoal(final String goal) {
        return "cpd".equals(goal) || "site".equals(goal);
    }

    /** {@inheritDoc} */
    @Override
    public ParserResult perform(final MavenBuildProxy build, final MavenProject pom, final MojoInfo mojo,
            final PluginLogger logger) throws InterruptedException, IOException {
        FilesParser dryCollector = new FilesParser(logger, DRY_XML_FILE,
                new DuplicationParserRegistry(getNormalThreshold(), getHighThreshold()),
                true, false);

        return getTargetPath(pom).act(dryCollector);
    }

    /** {@inheritDoc} */
    @Override
    protected BuildResult persistResult(final ParserResult project, final MavenBuild build) {
        DryResult result = new DryResult(build, getDefaultEncoding(), project);
        build.getActions().add(new MavenDryResultAction(build, this, getDefaultEncoding(), result));
        build.registerAsProjectAction(DryReporter.this);

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<DryProjectAction> getProjectActions(final MavenModule module) {
        return Collections.singletonList(new DryProjectAction(module));
    }

    /** {@inheritDoc} */
    @Override
    protected Class<? extends Action> getResultActionClass() {
        return MavenDryResultAction.class;
    }
}

