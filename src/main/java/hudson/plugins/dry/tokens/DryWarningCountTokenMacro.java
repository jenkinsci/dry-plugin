package hudson.plugins.dry.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractResultTokenMacro;
import hudson.plugins.dry.DryResultAction;

/**
 * Provides a token that evaluates to the number of DRY warnings.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class DryWarningCountTokenMacro extends AbstractResultTokenMacro {
    /**
     * Creates a new instance of {@link DryWarningCountTokenMacro}.
     */
    public DryWarningCountTokenMacro() {
        super(DryResultAction.class, "DRY_COUNT");
    }
}

