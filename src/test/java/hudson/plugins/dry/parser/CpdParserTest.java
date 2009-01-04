package hudson.plugins.dry.parser;

import static org.junit.Assert.*;
import hudson.plugins.dry.util.model.FileAnnotation;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

/**
 *  Tests the extraction of PMD's CPD analysis results.
 */
public class CpdParserTest {
    /** First line in publisher. */
    private static final int PUBLISHER_LINE = 69;
    /** First line in reporter. */
    private static final int REPORTER_LINE = 76;
    /** File name of reporter. */
    private static final String REPORTER = "/home/ulli/Hudson/jobs/M-Single-Freestyle/workspace/src/main/java/hudson/plugins/warnings/util/HealthAwareMavenReporter.java";
    /** File name of publisher. */
    private static final String PUBLISHER = "/home/ulli/Hudson/jobs/M-Single-Freestyle/workspace/src/main/java/hudson/plugins/warnings/util/HealthAwarePublisher.java";
    /** Error message. */
    private static final String WRONG_WARNING_PROPERTY = "Wrong warning property";
    /** Error message. */
    private static final String ERROR_MESSAGE = "Wrong number of warnings detected.";

    /**
     * Parses the specified file.
     *
     * @param fileName the file to read
     * @return the parsed module
     * @throws InvocationTargetException
     *             in case of an error
     */
    private Collection<FileAnnotation> parseFile(final String fileName) throws InvocationTargetException {
        return new CpdParser().parse(CpdParserTest.class.getResourceAsStream(fileName), "module");
    }

    /**
     * Checks whether we correctly detect all 2 duplications (i.e., 4 warnings).
     */
    @Test
    public void scanFileWithTwoDuplications() throws InvocationTargetException {
        String fileName = "cpd.xml";
        Collection<FileAnnotation> annotations = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 4, annotations.size());
    }

    /**
     * Checks whether we correctly detect 1 duplication (i.e., 1 warnings).
     */
    @Test
    public void scanFileWithOneDuplication() throws InvocationTargetException {
        String fileName = "one-cpd.xml";
        Collection<FileAnnotation> annotations = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 2, annotations.size());

        Iterator<FileAnnotation> iterator = annotations.iterator();
        DuplicateCode first = (DuplicateCode)iterator.next();
        DuplicateCode second = (DuplicateCode)iterator.next();

        if (first.getPrimaryLineNumber() == REPORTER_LINE) {
            assertDuplication(REPORTER_LINE, REPORTER, first);
            assertDuplication(PUBLISHER_LINE, PUBLISHER, second);
        }
        else if (first.getPrimaryLineNumber() == PUBLISHER_LINE) {
            assertDuplication(REPORTER_LINE, REPORTER, second);
            assertDuplication(PUBLISHER_LINE, PUBLISHER, first);
        }
        else {
            Assert.fail("No annotation has the correct line number.");
        }
    }

    /**
     * Asserts that all properties of the duplication have the expected
     * properties.
     *
     * @param expectedLine
     *            expected line number
     * @param expectedFileName
     *            expected file name
     * @param duplication
     *            the actual duplication
     */
    private void assertDuplication(final int expectedLine, final String expectedFileName, final DuplicateCode duplication) {
        assertEquals(WRONG_WARNING_PROPERTY, expectedLine, duplication.getPrimaryLineNumber());
        assertEquals(WRONG_WARNING_PROPERTY, expectedLine + 36 - 1, duplication.getLineRanges().iterator().next().getEnd());
        assertEquals(WRONG_WARNING_PROPERTY, expectedFileName, duplication.getFileName());
        assertTrue("Wrong duplicate code fragment", duplication.getSourceCode().startsWith("public HealthAwarePublisher(final String threshold, final String healthy, final String unHealthy"));
    }

    /**
     * Checks whether we throw an exception on the wrong type.
     */
    @Test
    public void scanOtherFile() throws InvocationTargetException {
        String fileName = "otherfile.xml";

        Collection<FileAnnotation> annotations = parseFile(fileName);
        assertEquals(ERROR_MESSAGE, 0, annotations.size());
    }
}


/* Copyright (c) Avaloq Evolution AG */