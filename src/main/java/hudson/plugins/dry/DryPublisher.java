package hudson.plugins.dry;

import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.FilesParser;
import hudson.plugins.analysis.core.HealthAwarePublisher;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.plugins.dry.parser.DuplicationParserRegistry;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Publishes the results of the duplicate code analysis (freestyle project type).
 *
 * @author Ulli Hafner
 */
public class DryPublisher extends HealthAwarePublisher {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6711252664481150129L;

    /** Validates the thresholds user input. */
    private static final ThresholdValidation THRESHOLD_VALIDATION = new ThresholdValidation();

    private static final String DEFAULT_DRY_PATTERN = "**/cpd.xml";
    /** Ant file-set pattern of files to work with. */
    private final String pattern;

    /** Minimum number of duplicate lines for high priority warnings. @since 2.5 */
    private final int highThreshold;
    /** Minimum number of duplicate lines for normal priority warnings. @since 2.5 */
    private final int normalThreshold;

    /**
     * Creates a new instance of <code>PmdPublisher</code>.
     *
     * @param pattern
     *            Ant file-set pattern to scan for DRY files
     * @param threshold
     *            Annotation threshold to be reached if a build should be
     *            considered as unstable.
     * @param newThreshold
     *            New annotations threshold to be reached if a build should be
     *            considered as unstable.
     * @param failureThreshold
     *            Annotation threshold to be reached if a build should be
     *            considered as failure.
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
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param useDeltaValues
     *            determines whether the absolute annotations delta or the
     *            actual annotations set difference should be used to evaluate
     *            the build stability
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
    public DryPublisher(final String pattern, final String threshold, final String newThreshold,
            final String failureThreshold, final String newFailureThreshold,
            final String healthy, final String unHealthy, final String thresholdLimit,
            final String defaultEncoding, final boolean useDeltaValues, final boolean canRunOnFailed,
            final int highThreshold, final int normalThreshold) {
        super(threshold, newThreshold, failureThreshold, newFailureThreshold,
                healthy, unHealthy, thresholdLimit, defaultEncoding, useDeltaValues, canRunOnFailed, "DRY");
        this.pattern = pattern;
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
        return THRESHOLD_VALIDATION.getHighThreshold(normalThreshold, highThreshold);
    }

    /**
     * Returns the minimum number of duplicate lines for normal warnings.
     *
     * @return the minimum number of duplicate lines for normal warnings
     */
    public int getNormalThreshold() {
        return THRESHOLD_VALIDATION.getNormalThreshold(normalThreshold, highThreshold);
    }

    /**
     * Returns the Ant file-set pattern of files to work with.
     *
     * @return Ant file-set pattern of files to work with
     */
    public String getPattern() {
        return pattern;
    }

    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final AbstractProject<?, ?> project) {
        return new DryProjectAction(project);
    }

    /** {@inheritDoc} */
    @Override
    public BuildResult perform(final AbstractBuild<?, ?> build, final PluginLogger logger) throws InterruptedException, IOException {
        logger.log("Collecting duplicate code analysis files...");
        FilesParser dryCollector = new FilesParser(logger, StringUtils.defaultIfEmpty(getPattern(), DEFAULT_DRY_PATTERN),
                new DuplicationParserRegistry(getNormalThreshold(), getHighThreshold(), build.getWorkspace().getRemote()),
                isMavenBuild(build), isAntBuild(build));

        ParserResult project = build.getWorkspace().act(dryCollector);
        DryResult result = new DryResult(build, getDefaultEncoding(), project);
        build.getActions().add(new DryResultAction(build, this, result));

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public DryDescriptor getDescriptor() {
        return (DryDescriptor)super.getDescriptor();
    }
}
