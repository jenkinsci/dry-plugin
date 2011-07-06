package hudson.plugins.dry.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractAnnotationsCountTokenMacro;
import hudson.plugins.dry.DryMavenResultAction;
import hudson.plugins.dry.DryResultAction;

/**
 * Provides a token that evaluates to the number of DRY warnings.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class DryWarningCountTokenMacro extends AbstractAnnotationsCountTokenMacro {
    /**
     * Creates a new instance of {@link DryWarningCountTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public DryWarningCountTokenMacro() {
        super("DRY_COUNT", DryResultAction.class, DryMavenResultAction.class);
    }
}

