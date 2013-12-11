package hudson.plugins.dry.parser.dupfinder;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import hudson.plugins.dry.parser.DuplicateCode;

/**
 * Tests the extraction of Reshaper DupFinder analysis results.
 *
 * @author Rafal Jasica
 */
public class DupFinderParserTest {
    /** First line in publisher. */
    private static final int PUBLISHER_LINE = 12;
    /** First line in reporter. */
    private static final int REPORTER_LINE = 26;
    /** File name of reporter. */
    private static final String REPORTER = "test/Reporter.cs";
    /** File name of publisher. */
    private static final String PUBLISHER = "test/Publisher.cs";
    /** Source code. */
    private static final String SOURCECODE = "if (items == null) throw new ArgumentNullException(\"items\");";
    /** Error message. */
    private static final String ERROR_MESSAGE = "Wrong number of warnings detected.";
    /** Error message. */
    private static final String VALID_CPD_FILE = "Parser does not accept valid DupFinder file.";
    /** Error message. */
    private static final String INVALID_CPD_FILE = "Parser does accept invalid CPD file.";
    /** Error message. */
    private static final String WRONG_WARNING_PROPERTY = "Wrong warning property";

    /**
     * Checks whether we accept a file with Windows encoding.
     */
    @Test
    public void assertCanReadWindowsFile() {
        assertTrue(INVALID_CPD_FILE, acceptsFile("sorucecode.xml"));
    }

    /**
     * Checks whether we accept a file with normal encoding.
     */
    @Test
    public void scanOtherFile() {
        assertFalse(INVALID_CPD_FILE, acceptsFile("otherfile.xml"));
    }

    /**
     * Checks whether we can parse a file with source code snippet.
     *
     * @throws InvocationTargetException
     *             Signals a test failure
     */
    @Test
    public void scanFileWithSourceCode() throws InvocationTargetException {
        String fileName = "sorucecode.xml";
        assertTrue(VALID_CPD_FILE, acceptsFile(fileName));
        Collection<DuplicateCode> annotations = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 2, annotations.size());

        Iterator<DuplicateCode> iterator = annotations.iterator();
        DuplicateCode first = iterator.next();
        DuplicateCode second = iterator.next();

        assertEquals(WRONG_WARNING_PROPERTY, SOURCECODE, first.getSourceCode());
        assertEquals(WRONG_WARNING_PROPERTY, SOURCECODE, second.getSourceCode());

        if (first.getPrimaryLineNumber() == REPORTER_LINE) {
            assertDuplication(REPORTER_LINE, REPORTER, first);
            assertDuplication(PUBLISHER_LINE, PUBLISHER, second);
        }
        else if (first.getPrimaryLineNumber() == PUBLISHER_LINE) {
            assertDuplication(REPORTER_LINE, REPORTER, second);
            assertDuplication(PUBLISHER_LINE, PUBLISHER, first);
        }
        else {
            fail("No annotation has the correct line number.");
        }
    }

    /**
     * Checks whether we can parse a file without source code snippet.
     *
     * @throws InvocationTargetException
     *             Signals a test failure
     */
    @Test
    public void scanFileWithoutSourceCode() throws InvocationTargetException {
        String fileName = "withoutsourcode.xml";
        assertTrue(VALID_CPD_FILE, acceptsFile(fileName));
        Collection<DuplicateCode> annotations = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 2, annotations.size());

        Iterator<DuplicateCode> iterator = annotations.iterator();
        DuplicateCode first = iterator.next();
        DuplicateCode second = iterator.next();

        assertEquals(WRONG_WARNING_PROPERTY, null, first.getSourceCode());
        assertEquals(WRONG_WARNING_PROPERTY, null, second.getSourceCode());

        if (first.getPrimaryLineNumber() == REPORTER_LINE) {
            assertDuplication(REPORTER_LINE, REPORTER, first);
            assertDuplication(PUBLISHER_LINE, PUBLISHER, second);
        }
        else if (first.getPrimaryLineNumber() == PUBLISHER_LINE) {
            assertDuplication(REPORTER_LINE, REPORTER, second);
            assertDuplication(PUBLISHER_LINE, PUBLISHER, first);
        }
        else {
            fail("No annotation has the correct line number.");
        }
    }

    /**
     * Checks if the specified file is a DupFinder file.
     *
     * @param fileName the file to read
     * @return <code>true</code> if the file is a DupFinder file
     */
    private boolean acceptsFile(final String fileName) {
        return new DupFinderParser(50, 25).accepts(getResource(fileName));
    }

    /**
     * Returns the specified resource as input stream.
     *
     * @param fileName
     *            the file to read
     * @return the input stream
     */
    private InputStream getResource(final String fileName) {
        return DupFinderParserTest.class.getResourceAsStream(fileName);
    }

    /**
     * Parses the specified file.
     *
     * @param fileName the file to read
     * @return the parsed module
     * @throws InvocationTargetException
     *             in case of an error
     */
    private Collection<DuplicateCode> parseFile(final String fileName) throws InvocationTargetException {
        return new DupFinderParser(50, 25).parse(getResource(fileName), "module");
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
        assertEquals(WRONG_WARNING_PROPERTY, expectedLine + 12 - 1, duplication.getLineRanges().iterator().next().getEnd());
        assertEquals(WRONG_WARNING_PROPERTY, expectedFileName, duplication.getFileName());
    }
}

