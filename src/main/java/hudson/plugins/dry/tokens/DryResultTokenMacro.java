package hudson.plugins.dry.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractResultTokenMacro;
import hudson.plugins.dry.DryMavenResultAction;
import hudson.plugins.dry.DryResultAction;

/**
 * Provides a token that evaluates to the DRY build result.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class DryResultTokenMacro extends AbstractResultTokenMacro {
    /**
     * Creates a new instance of {@link DryResultTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public DryResultTokenMacro() {
        super("DRY_RESULT", DryResultAction.class, DryMavenResultAction.class);
    }
}

