package hudson.plugins.dry.parser;

import hudson.plugins.dry.parser.cpd.CpdParser;
import hudson.plugins.dry.util.AnnotationParser;
import hudson.plugins.dry.util.model.FileAnnotation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Registry for duplication parsers.
 *
 * @author Ulli Hafner
 */
public class DuplicationParserRegistry implements AnnotationParser {
    /** Unique ID of this class. */
    private static final long serialVersionUID = -8114361417348412242L;
    /** The registered parsers. */
    @SuppressWarnings("Se")
    private final List<AbstractDryParser> parsers = new ArrayList<AbstractDryParser>();

    /**
     * Creates a new instance of {@link DuplicationParserRegistry}.
     */
    public DuplicationParserRegistry() {
        parsers.add(new CpdParser());
    }

    /** {@inheritDoc} */
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        try {
            for (AbstractDryParser parser : parsers) {
                if (parser.accepts(new FileInputStream(file))) {
                    return parser.parse(new FileInputStream(file), moduleName);
                }
            }
            throw new IOException("No parser found for duplicated code results file " + file.getAbsolutePath());
        }
        catch (IOException exception) {
            throw new InvocationTargetException(exception);
        }
    }
}

