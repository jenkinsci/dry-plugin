package hudson.plugins.dry.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractNewAnnotationsTokenMacro;
import hudson.plugins.dry.DryMavenResultAction;
import hudson.plugins.dry.DryResultAction;

/**
 * Provides a token that evaluates to the number of new duplicate code warnings.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class NewDryWarningsTokenMacro extends AbstractNewAnnotationsTokenMacro {
    /**
     * Creates a new instance of {@link NewDryWarningsTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public NewDryWarningsTokenMacro() {
        super("DRY_NEW", DryResultAction.class, DryMavenResultAction.class);
    }
}

