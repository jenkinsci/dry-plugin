package hudson.plugins.dry.parser.simian;

import static org.junit.Assert.*;
import hudson.plugins.dry.parser.DuplicateCode;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

/**
 *  Tests the extraction of PMD's CPD analysis results.
 */
public class SimianParserTest {
    private static final String MATRIX_RUN = "c:/java/hudson/matrix/MatrixRun.java";
    private static final String MAVEN_BUILD = "c:/java/hudson/maven/MavenBuild.java";

    private static final String WRONG_WARNING_PROPERTY = "Wrong warning property";
    private static final String ERROR_MESSAGE = "Wrong number of warnings detected.";
    private static final String VALID_SIMIAN_FILE = "Parser does not accept valid Simian file.";

    /**
     * Parses the specified file.
     *
     * @param fileName the file to read
     * @return the parsed module
     * @throws InvocationTargetException
     *             in case of an error
     */
    private Collection<DuplicateCode> parseFile(final String fileName) throws InvocationTargetException {
        return new SimianParser(50, 25).parse(getResource(fileName), "module");
    }

    /**
     * Checks if the specified file is a CPD file.
     *
     * @param fileName the file to read
     * @return <code>true</code> if the file is a CPD file
     */
    private boolean acceptsFile(final String fileName) {
        return new SimianParser(50, 25).accepts(getResource(fileName));
    }

    /**
     * Returns the specified resource as input stream.
     *
     * @param fileName
     *            the file to read
     * @return the input stream
     */
    private InputStream getResource(final String fileName) {
        return SimianParserTest.class.getResourceAsStream(fileName);
    }

    /**
     * Checks whether we correctly detect 1 duplication (i.e., 2 warnings) within the same file.
     *
     * @throws InvocationTargetException
     *             Signals a test failure
     */
    @Test
    public void scanFileWithOneDuplicationInOneFile() throws InvocationTargetException {
        String fileName = "onefile.xml";
        assertTrue(VALID_SIMIAN_FILE, acceptsFile(fileName));

        Collection<DuplicateCode> annotations = parseFile(fileName);
        assertEquals(ERROR_MESSAGE, 2, annotations.size());

        Iterator<DuplicateCode> iterator = annotations.iterator();
        assertDuplication(93, 98, MAVEN_BUILD, iterator.next());
        assertDuplication(76, 81, MAVEN_BUILD, iterator.next());
    }

    /**
     * Checks whether we correctly detect 1 duplication (i.e., 2 warnings) in two different files.
     *
     * @throws InvocationTargetException
     *             Signals a test failure
     */
    @Test
    public void scanFileWithOneDuplicationInTwoFiles() throws InvocationTargetException {
        String fileName = "twofile.xml";
        assertTrue(VALID_SIMIAN_FILE, acceptsFile(fileName));

        Collection<DuplicateCode> annotations = parseFile(fileName);
        assertEquals(ERROR_MESSAGE, 2, annotations.size());

        Iterator<DuplicateCode> iterator = annotations.iterator();
        assertDuplication(92, 97, MAVEN_BUILD, iterator.next());
        assertDuplication(61, 66, MATRIX_RUN, iterator.next());
    }

    /**
     * Checks whether we correctly detect 1 duplication (i.e., 2 warnings) in two different files.
     *
     * @throws InvocationTargetException
     *             Signals a test failure
     */
    @Test
    public void scanFileWithTwoDuplications() throws InvocationTargetException {
        String fileName = "twosets.xml";
        assertTrue(VALID_SIMIAN_FILE, acceptsFile(fileName));

        Collection<DuplicateCode> annotations = parseFile(fileName);
        assertEquals(ERROR_MESSAGE, 4, annotations.size());

        Iterator<DuplicateCode> iterator = annotations.iterator();
        DuplicateCode warning;

        warning = iterator.next();
        assertDuplication(92, 107, MAVEN_BUILD, warning);
        assertEquals("Wrong other file", MATRIX_RUN, getFileName(warning));

        warning = iterator.next();
        assertDuplication(61, 76, MATRIX_RUN, warning);
        assertEquals("Wrong other file", MAVEN_BUILD, getFileName(warning));

        warning = iterator.next();
        assertDuplication(93, 98, MAVEN_BUILD, warning);
        assertEquals("Wrong other file", MAVEN_BUILD, getFileName(warning));

        warning = iterator.next();
        assertDuplication(76, 81, MAVEN_BUILD, warning);
        assertEquals("Wrong other file", MAVEN_BUILD, getFileName(warning));
    }

    private String getFileName(final DuplicateCode warning) {
        return warning.getLinks().iterator().next().getFileName();
    }

    /**
     * Checks whether we correctly detect 1 duplication (i.e., 4 warnings) in four different files.
     *
     * @throws InvocationTargetException
     *             Signals a test failure
     */
    @Test
    public void scanFileWithOneDuplicationInFourFiles() throws InvocationTargetException {
        String fileName = "fourfile.xml";
        assertTrue(VALID_SIMIAN_FILE, acceptsFile(fileName));

        Collection<DuplicateCode> annotations = parseFile(fileName);
        assertEquals(ERROR_MESSAGE, 4, annotations.size());

        Iterator<DuplicateCode> iterator = annotations.iterator();
        assertDuplication(11, 16, "c:/java/foo1.java", iterator.next());
        assertDuplication(21, 26, "c:/java/foo2.java", iterator.next());
        assertDuplication(31, 36, "c:/java/foo3.java", iterator.next());
        assertDuplication(41, 46, "c:/java/foo4.java", iterator.next());
    }

    /**
     * Checks whether we correctly detect the warnings of the new Simian scanner (2.3.31).
     *
     * @throws InvocationTargetException
     *             Signals a test failure
     */
    @Test
    public void scanNewSimianParser() throws InvocationTargetException {
        String fileName = "simian-2.3.31.xml";
        assertTrue(VALID_SIMIAN_FILE, acceptsFile(fileName));

        Collection<DuplicateCode> annotations = parseFile(fileName);
        assertEquals(ERROR_MESSAGE, 132, annotations.size());
    }

    /**
     * Asserts that all properties of the duplication have the expected
     * properties.
     *
     * @param expectedStartLine
     *            expected start line number
     * @param expectedEndLine
     *            expected end line number
     * @param expectedFileName
     *            expected file name
     * @param duplication
     *            the actual duplication
     */
    private void assertDuplication(final int expectedStartLine, final int expectedEndLine,
            final String expectedFileName, final DuplicateCode duplication) {
        assertEquals(WRONG_WARNING_PROPERTY, expectedStartLine, duplication.getPrimaryLineNumber());
        assertEquals(WRONG_WARNING_PROPERTY, expectedEndLine, duplication.getLineRanges().iterator().next().getEnd());
        assertEquals(WRONG_WARNING_PROPERTY, expectedFileName, duplication.getFileName());
    }

    /**
     * Checks whether we don't accept a file of the wrong type.
     */
    @Test
    public void scanOtherFile() {
        assertFalse("Parser does accept invalid Simian file.", acceptsFile("other.xml"));
    }
}


/* Copyright (c) Avaloq Evolution AG */