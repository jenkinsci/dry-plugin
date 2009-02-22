package hudson.plugins.dry;

import static junit.framework.Assert.*;
import hudson.model.AbstractBuild;
import hudson.plugins.dry.util.BuildResult;
import hudson.plugins.dry.util.BuildResultTest;
import hudson.plugins.dry.util.ParserResult;

/**
 * Tests the class {@link DryResult}.
 */
public class DryResultTest extends BuildResultTest<DryResult> {
    /** {@inheritDoc} */
    @Override
    protected DryResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project) {
        return new DryResult(build, null, project);
    }

    /** {@inheritDoc} */
    @Override
    protected DryResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project, final DryResult previous) {
        return new DryResult(build, null, project, previous);
    }

    /** {@inheritDoc} */
    @Override
    protected void verifyHighScoreMessage(final int expectedZeroWarningsBuildNumber, final boolean expectedIsNewHighScore, final long expectedHighScore, final long gap, final DryResult result) {
        if (result.hasNoAnnotations() && result.getDelta() == 0) {
            assertTrue(result.getDetails().contains(Messages.DRY_ResultAction_NoWarningsSince(expectedZeroWarningsBuildNumber)));
            if (expectedIsNewHighScore) {
                long days = BuildResult.getDays(expectedHighScore);
                if (days == 1) {
                    assertTrue(result.getDetails().contains(Messages.DRY_ResultAction_OneHighScore()));
                }
                else {
                    assertTrue(result.getDetails().contains(Messages.DRY_ResultAction_MultipleHighScore(days)));
                }
            }
            else {
                long days = BuildResult.getDays(gap);
                if (days == 1) {
                    assertTrue(result.getDetails().contains(Messages.DRY_ResultAction_OneNoHighScore()));
                }
                else {
                    assertTrue(result.getDetails().contains(Messages.DRY_ResultAction_MultipleNoHighScore(days)));
                }
            }
        }
    }
}

