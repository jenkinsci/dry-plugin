package hudson.plugins.dry;

import javax.annotation.CheckForNull;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.FilesParser;
import hudson.plugins.analysis.core.HealthAwarePublisher;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.plugins.dry.parser.DuplicationParserRegistry;

/**
 * Publishes the results of the duplicate code analysis (freestyle project type).
 *
 * @author Ulli Hafner
 */
public class DryPublisher extends HealthAwarePublisher {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6711252664481150129L;

    private static final String PLUGIN_NAME = "DRY";

    /** Validates the thresholds user input. */
    private static final ThresholdValidation THRESHOLD_VALIDATION = new ThresholdValidation();

    private static final String DEFAULT_DRY_PATTERN = "**/cpd.xml";
    /** Ant file-set pattern of files to work with. */
    private String pattern;

    /** Minimum number of duplicate lines for high priority warnings. @since 2.5 */
    private int highThreshold = 50;
    /** Minimum number of duplicate lines for normal priority warnings. @since 2.5 */
    private int normalThreshold = 25;

    @DataBoundConstructor
    public DryPublisher() {
        super(PLUGIN_NAME);
    }

    /**
     * Returns the Ant file-set pattern of files to work with.
     *
     * @return Ant file-set pattern of files to work with
     */
    @CheckForNull
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the Ant file-set pattern of files to work with.
     *
     * @param pattern the file pattern
     */
    @DataBoundSetter
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

    /**
     * Returns the minimum number of duplicate lines for high priority warnings.
     *
     * @return the minimum number of duplicate lines for high priority warnings
     */
    public int getHighThreshold() {
        return THRESHOLD_VALIDATION.getHighThreshold(normalThreshold, highThreshold);
    }

    /**
     * Sets the minimum number of duplicate lines for high priority warnings.
     *
     * @param highThreshold the number of lines for priority high
     */
    @DataBoundSetter
    public void setHighThreshold(final int highThreshold) {
        this.highThreshold = highThreshold;
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
     * Sets the minimum number of duplicate lines for normal priority warnings.
     *
     * @param normalThreshold the number of lines for priority normal
     */
    @DataBoundSetter
    public void setNormalThreshold(final int normalThreshold) {
        this.normalThreshold = normalThreshold;
    }

    @Override
    public BuildResult perform(final Run<?, ?> build, final FilePath workspace, final PluginLogger logger) throws
            InterruptedException, IOException {
        logger.log("Collecting duplicate code analysis files...");

        FilesParser dryCollector = new FilesParser(PLUGIN_NAME,
                StringUtils.defaultIfEmpty(expandFilePattern(getPattern(), build.getEnvironment(TaskListener.NULL)), DEFAULT_DRY_PATTERN),
                    new DuplicationParserRegistry(getNormalThreshold(), getHighThreshold(), workspace.getRemote(),
                            getDefaultEncoding()),
                    shouldDetectModules(), isMavenBuild(build));

        ParserResult project = workspace.act(dryCollector);
        logger.logLines(project.getLogMessages());

        blame(project.getAnnotations(), build, workspace);

        DryResult result = new DryResult(build, getDefaultEncoding(), project,
                usePreviousBuildAsReference(), useOnlyStableBuildsAsReference());
        build.addAction(new DryResultAction(build, this, result));

        return result;
    }

    @Override
    public DryDescriptor getDescriptor() {
        return (DryDescriptor)super.getDescriptor();
    }

    @Override
    public MatrixAggregator createAggregator(final MatrixBuild build, final Launcher launcher,
            final BuildListener listener) {
        return new DryAnnotationsAggregator(build, launcher, listener, this, getDefaultEncoding(),
                usePreviousBuildAsReference(), useOnlyStableBuildsAsReference());
    }
}
