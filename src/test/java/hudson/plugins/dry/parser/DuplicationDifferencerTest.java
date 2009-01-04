package hudson.plugins.dry.parser;

import hudson.plugins.dry.util.AnnotationDifferencer;
import hudson.plugins.dry.util.AnnotationDifferencerTest;
import hudson.plugins.dry.util.model.FileAnnotation;
import hudson.plugins.dry.util.model.Priority;

/**
 * Tests the {@link AnnotationDifferencer} for {@link DuplicateCode} instances.
 */
public class DuplicationDifferencerTest extends AnnotationDifferencerTest {
    /** {@inheritDoc} */
    @Override
    public FileAnnotation createAnnotation(final String fileName, final Priority priority, final String message, final String category,
            final String type, final int start, final int end) {
        DuplicateCode annotation = new DuplicateCode(start, end - start + 1, fileName);
        annotation.setFileName(fileName);
        annotation.setPriority(priority);
        annotation.setSourceCode(message + type + category);
        return annotation;
    }
}

