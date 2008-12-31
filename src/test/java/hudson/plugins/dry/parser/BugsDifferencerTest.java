package hudson.plugins.dry.parser;

import hudson.plugins.dry.parser.Bug;
import hudson.plugins.dry.util.AnnotationDifferencer;
import hudson.plugins.dry.util.AnnotationDifferencerTest;
import hudson.plugins.dry.util.model.FileAnnotation;
import hudson.plugins.dry.util.model.Priority;

/**
 * Tests the {@link AnnotationDifferencer} for bugs.
 */
public class BugsDifferencerTest extends AnnotationDifferencerTest {
    /** {@inheritDoc} */
    @Override
    public FileAnnotation createAnnotation(final String fileName, final Priority priority, final String message, final String category,
            final String type, final int start, final int end) {
        Bug bug = new Bug(priority, message, message, message, start, end);
        bug.setFileName(fileName);
        return bug;
    }
}

