package hudson.plugins.dry.parser;

import hudson.plugins.analysis.core.AnnotationParser;
import hudson.plugins.analysis.util.ContextHashCode;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.dry.parser.cpd.CpdParser;
import hudson.plugins.dry.parser.dupfinder.DupFinderParser;
import hudson.plugins.dry.parser.simian.SimianParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.xerces.parsers.SAXParser;

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
    private final String defaultEncoding;

    /** Property of SAX parser factory. */
    private static final String SAX_DRIVER_PROPERTY = "org.xml.sax.driver";

   /**
     * Creates a new instance of {@link DuplicationParserRegistry}.
     *
     * @param highThreshold
     *            minimum number of duplicate lines for high priority warnings
     * @param normalThreshold
     *            minimum number of duplicate lines for normal priority warnings
     * @param defaultEncoding
     *            default encoding of the files
     */
    public DuplicationParserRegistry(final int normalThreshold, final int highThreshold, final String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
        parsers.add(new CpdParser(highThreshold, normalThreshold));
        parsers.add(new SimianParser(highThreshold, normalThreshold));
        parsers.add(new DupFinderParser(highThreshold, normalThreshold));
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
     * @param defaultEncoding
     *            default encoding of the files
     */
    public DuplicationParserRegistry(final int normalThreshold, final int highThreshold,
            final String workspacePath, final String defaultEncoding) {
        this(normalThreshold, highThreshold, defaultEncoding);

        this.workspacePath = workspacePath;
    }

    /** {@inheritDoc} */
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        String oldProperty = System.getProperty(SAX_DRIVER_PROPERTY);
        if (oldProperty != null) {
            System.setProperty(SAX_DRIVER_PROPERTY, SAXParser.class.getName());
        }

        FileInputStream inputStream = null;
        try {
            for (AbstractDryParser parser : parsers) {
                inputStream = new FileInputStream(file);
                if (parser.accepts(inputStream)) {
                    IOUtils.closeQuietly(inputStream);
                    inputStream = new FileInputStream(file);
                    Collection<DuplicateCode> result = parser.parse(inputStream, moduleName);
                    createLinkNames(result);
                    Set<FileAnnotation> warnings = Sets.newHashSet();
                    warnings.addAll(result);
                    ContextHashCode hashCode = new ContextHashCode();
                    for (FileAnnotation duplication : warnings) {
                        duplication.setContextHashCode(hashCode.create(duplication.getFileName(), duplication.getPrimaryLineNumber(),
                                defaultEncoding));
                    }

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
            if (oldProperty != null) {
                System.setProperty(SAX_DRIVER_PROPERTY, oldProperty);
            }
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

