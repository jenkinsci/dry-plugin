package hudson.plugins.dry.parser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A duplication parser template for Digester based parsers.
 *
 * @param <T> the type of the parsed warnings
 */
public abstract class AbstractDigesterParser<T> extends AbstractDryParser {
    /**
     * Creates a new instance of {@link AbstractDigesterParser}.
     *
     * @param highThreshold   minimum number of duplicate lines for high priority warnings
     * @param normalThreshold minimum number of duplicate lines for normal priority warnings
     */
    protected AbstractDigesterParser(final int highThreshold, final int normalThreshold) {
        super(highThreshold, normalThreshold);
    }

    @Override
    public final boolean accepts(final InputStream file) {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(getClass().getClassLoader());
            digester.addObjectCreate(getMatchingPattern(), String.class);

            Object result = digester.parse(createInputSource(file));
            if (result instanceof String) {
                return true;
            }
        }
        catch (IOException exception) {
            // ignore and return false
        }
        catch (SAXException exception) {
            // ignore and return false
        }
        return false;

    }

    /**
     * Creates the input source that is used by the SAX parser. Default encoding is set to UTF8.
     *
     * @param file the input stream
     * @return the input source
     */
    protected InputSource createInputSource(final InputStream file) {
        InputSource inputSource = new InputSource(file);
        inputSource.setEncoding("UTF-8");
        return inputSource;
    }

    /**
     * Returns the pattern that must match the input file in order to be accepted as valid.
     *
     * @return the pattern to match
     */
    protected abstract String getMatchingPattern();

    @Override
    public final Collection<DuplicateCode> parse(final InputStream file, final String moduleName)
            throws InvocationTargetException {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(getClass().getClassLoader());

            configureParser(digester);

            List<T> duplications = new ArrayList<T>();
            digester.push(duplications);

            Object result = digester.parse(createInputSource(file));
            if (result != duplications) { // NOPMD
                throw new SAXException("Input stream is not a valid duplications file.");
            }

            return convertWarnings(duplications, moduleName);
        }
        catch (IOException exception) {
            throw new InvocationTargetException(exception);
        }
        catch (SAXException exception) {
            throw new InvocationTargetException(exception);
        }
    }

    /**
     * Configures the Digester parser. Register all rules that are required to parse the file.
     *
     * @param digester the parser to configure
     */
    protected abstract void configureParser(final Digester digester);

    /**
     * Converts the parsed warnings from the original format to the format of the dry plug-in.
     *
     * @param duplications the parsed warnings
     * @param moduleName the module these warnings belong to
     * @return the converted warnings
     */
    protected abstract Collection<DuplicateCode> convertWarnings(final List<T> duplications, final String moduleName);
}
