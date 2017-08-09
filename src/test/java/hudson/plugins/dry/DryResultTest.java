package hudson.plugins.dry;

import hudson.model.Run;
import hudson.plugins.analysis.core.HistoryProvider;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ReferenceProvider;
import hudson.plugins.analysis.test.BuildResultTest;

/**
 * Tests the class {@link DryResult}.
 */
public class DryResultTest extends BuildResultTest<DryResult> {
    @Override
    protected DryResult createBuildResult(final Run<?, ?> build, final ParserResult project, final ReferenceProvider referenceProvider, final HistoryProvider historyProvider) {
        return new DryResult(build, referenceProvider, project, "UTF8", false) {
            @Override
            protected HistoryProvider createBuildHistory(final Run<?, ?> build) {
                return historyProvider;
            }
        };
    }
}

