package hudson.plugins.dry;

import hudson.plugins.analysis.views.DetailFactory;

/**
 * Initialization of Maven classes.
 *
 * @author Ulli Hafner
 */
public final class MavenInitialization {
    /**
     * Initializes the detail builder for Maven builds.
     *
     * @param detailBuilder
     *            the builder to use
     */
    public static void run(final DetailFactory detailBuilder) {
        DetailFactory.addDetailBuilder(DryMavenResultAction.class, detailBuilder);
    }

    /**
     * Creates a new instance of {@link MavenInitialization}.
     */
    private MavenInitialization() {
        // prevents instantiation
    }
}
