package hudson.plugins.dry.parser;

import hudson.plugins.analysis.core.AnnotationParser;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;

/**
 * A base class for duplicate code parsers. Use this class as a starting point
 * for your duplication result parser and register an instance in the
 * {@link DuplicationParserRegistry}.
 *
 * @author Ulli Hafner
 */
public abstract class AbstractDryParser implements AnnotationParser {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6328121785037117886L;

    /** Minimum number of duplicate lines for high priority warnings. @since 2.5 */
    private final int highThreshold;
    /** Minimum number of duplicate lines for normal priority warnings. @since 2.5 */
    private final int normalThreshold;

    /**
     * Creates a new instance of {@link AbstractDryParser}.
     *
     * @param highThreshold
     *            minimum number of duplicate lines for high priority warnings
     * @param normalThreshold
     *            minimum number of duplicate lines for normal priority warnings
     */
    public AbstractDryParser(final int highThreshold, final int normalThreshold) {
        this.highThreshold = highThreshold;
        this.normalThreshold = normalThreshold;
    }

    /** {@inheritDoc} */
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            if (accepts(inputStream)) {
                IOUtils.closeQuietly(inputStream);
                inputStream = new FileInputStream(file);

                return parse(inputStream, moduleName);
            }
            else {
                throw new IOException("Can't parse CPD file " + file.getAbsolutePath());
            }
        }
        catch (IOException exception) {
            throw new InvocationTargetException(exception);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Returns the duplication annotations found in the specified file.
     *
     * @param inputStream
     *            the file to parse
     * @param moduleName
     *            name of the maven module
     * @return the parsed annotations
     * @throws InvocationTargetException
     *             if the file could not be parsed (wrap your actual exception in this exception)
     */
    protected abstract Collection<FileAnnotation> parse(InputStream inputStream, String moduleName)  throws InvocationTargetException;

    /**
     * Returns whether this parser accepts the specified stream as a valid
     * file.
     *
     * @param inputStream
     *            the file to parse
     * @return <code>true</code> if this parser accepts the specified stream as
     *         a valid file, <code>false</code> if the parser can't read this file
     */
    protected abstract boolean accepts(InputStream inputStream);

    /**
     * Returns the priority of the warning.
     *
     * @param lines
     *            number of duplicate lines
     * @return the priority of the warning
     */
    protected Priority getPriority(final int lines) {
        if (lines >= highThreshold) {
            return Priority.HIGH;
        }
        else if (lines >= normalThreshold) {
            return Priority.NORMAL;
        }
        else {
            return Priority.LOW;
        }
    }
}

