package hudson.plugins.dry.parser;

import hudson.plugins.analysis.core.AnnotationParser;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.dry.parser.cpd.CpdParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Registry for duplication parsers.
 *
 * @author Ulli Hafner
 */
public class DuplicationParserRegistry implements AnnotationParser {
    /** Unique ID of this class. */
    private static final long serialVersionUID = -8114361417348412242L;
    /** The registered parsers. */
    @SuppressWarnings("SE")
    private final List<AbstractDryParser> parsers = new ArrayList<AbstractDryParser>();

    /**
     * Creates a new instance of {@link DuplicationParserRegistry}.
     *
     * @param highThreshold
     *            minimum number of duplicate lines for high priority warnings
     * @param normalThreshold
     *            minimum number of duplicate lines for normal priority warnings
     */
    public DuplicationParserRegistry(final int normalThreshold, final int highThreshold) {
        parsers.add(new CpdParser(highThreshold, normalThreshold));
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

