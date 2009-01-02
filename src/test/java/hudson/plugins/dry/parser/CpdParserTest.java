package hudson.plugins.dry.parser;

import static org.junit.Assert.*;
import hudson.plugins.dry.util.model.FileAnnotation;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.junit.Test;

/**
 *  Tests the extraction of PMD's CPD analysis results.
 */
public class CpdParserTest {
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
     * Checks whether we correctly detect all 669 warnings.
     */
    @Test
    public void scanFileWithSeveralWarnings() throws InvocationTargetException {
        String fileName = "cpd.xml";
        Collection<FileAnnotation> annotations = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 4, annotations.size());
    }
}


/* Copyright (c) Avaloq Evolution AG */