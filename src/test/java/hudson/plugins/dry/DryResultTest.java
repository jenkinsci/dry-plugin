package hudson.plugins.dry;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.BuildHistory;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.test.BuildResultTest;

/**
 * Tests the class {@link DryResult}.
 */
public class DryResultTest extends BuildResultTest<DryResult> {
    @Override
    protected DryResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project, final BuildHistory history) {
        return new DryResult(build, history, project, "UTF8", false);
    }
}

