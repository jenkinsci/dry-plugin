package hudson.plugins.dry.parser;

import org.apache.commons.digester3.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * A duplication parser template for Digester based parsers.
 */
public abstract class AbstractDigesterParser extends AbstractDryParser {

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
    public boolean accepts(final InputStream file) {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(getClass().getClassLoader());

            InputSource inputSource = new InputSource(file);
            inputSource.setEncoding("UTF-8");

            digester.addObjectCreate(getMatchingPattern(), String.class);

            Object result = digester.parse(inputSource);
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

    protected abstract String getMatchingPattern();
}
