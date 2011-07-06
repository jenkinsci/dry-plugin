package hudson.plugins.dry.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractFixedAnnotationsTokenMacro;
import hudson.plugins.dry.DryMavenResultAction;
import hudson.plugins.dry.DryResultAction;

/**
 * Provides a token that evaluates to the number of fixed duplicate code warnings.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class FixedDryWarningsTokenMacro extends AbstractFixedAnnotationsTokenMacro {
    /**
     * Creates a new instance of {@link FixedDryWarningsTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public FixedDryWarningsTokenMacro() {
        super("DRY_FIXED", DryResultAction.class, DryMavenResultAction.class);
    }
}

