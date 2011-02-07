package hudson.plugins.dry.parser;

import hudson.plugins.analysis.core.AnnotationParser;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.dry.parser.cpd.CpdParser;
import hudson.plugins.dry.parser.simian.SimianParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Registry for duplication parsers.
 *
 * @author Ulli Hafner
 */
public class DuplicationParserRegistry implements AnnotationParser {
    private static final long serialVersionUID = -8114361417348412242L;
    @SuppressWarnings("SE")
    private final List<AbstractDryParser> parsers = new ArrayList<AbstractDryParser>();
    private String workspacePath;

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
        parsers.add(new SimianParser(highThreshold, normalThreshold));
    }

    /**
     * Creates a new instance of {@link DuplicationParserRegistry}.
     *
     * @param highThreshold
     *            minimum number of duplicate lines for high priority warnings
     * @param normalThreshold
     *            minimum number of duplicate lines for normal priority warnings
     * @param workspacePath
     *            path to the workspace files
     */
    public DuplicationParserRegistry(final int normalThreshold, final int highThreshold,
            final String workspacePath) {
        this(normalThreshold, highThreshold);

        this.workspacePath = workspacePath;
    }

    /** {@inheritDoc} */
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        FileInputStream inputStream = null;
        try {
            for (AbstractDryParser parser : parsers) {
                inputStream = new FileInputStream(file);
                if (parser.accepts(inputStream)) {
                    IOUtils.closeQuietly(inputStream);
                    inputStream = new FileInputStream(file);
                    Collection<DuplicateCode> result = parser.parse(inputStream, moduleName);
                    createLinkNames(result);
                    HashSet<FileAnnotation> warnings = Sets.newHashSet();
                    warnings.addAll(result);
                    return warnings;
                }
            }
            throw new IOException("No parser found for duplicated code results file " + file.getAbsolutePath());
        }
        catch (IOException exception) {
            throw new InvocationTargetException(exception);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * For each duplicate code annotation that does not have a package name
     * (i.e., for non Java sources), a link name is generated.
     *
     * @param result the annotations
     */
    private void createLinkNames(final Collection<DuplicateCode> result) {
        if (workspacePath != null) {
            for (FileAnnotation duplication : result) {
                duplication.setPathName(workspacePath);
            }
        }
    }
}

