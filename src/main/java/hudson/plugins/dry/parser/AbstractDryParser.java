package hudson.plugins.dry.parser;

import hudson.plugins.dry.util.AnnotationParser;
import hudson.plugins.dry.util.model.FileAnnotation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

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

    /** {@inheritDoc} */
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        try {
            if (accepts(new FileInputStream(file))) {
                return parse(new FileInputStream(file), moduleName);
            }
            else {
                throw new IOException("Can't parse CPD file " + file.getAbsolutePath());
            }
        }
        catch (IOException exception) {
            throw new InvocationTargetException(exception);
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
}

