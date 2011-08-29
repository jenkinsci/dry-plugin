package hudson.plugins.dry.parser.simian;

import hudson.plugins.analysis.util.PackageDetectors;
import hudson.plugins.dry.parser.AbstractDryParser;
import hudson.plugins.dry.parser.DuplicateCode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * A parser for Simian XML files.
 *
 * @author Ulli Hafner
 */
public class SimianParser extends AbstractDryParser {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6507147028628714706L;

    /**
     * Creates a new instance of {@link SimianParser}.
     *
     * @param highThreshold
     *            minimum number of duplicate lines for high priority warnings
     * @param normalThreshold
     *            minimum number of duplicate lines for normal priority warnings
     */
    public SimianParser(final int highThreshold, final int normalThreshold) {
        super(highThreshold, normalThreshold);
    }

    /** {@inheritDoc} */
    @Override
    public boolean accepts(final InputStream file) {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(SimianParser.class.getClassLoader());

            String duplicationXPath = "*/simian";
            digester.addObjectCreate(duplicationXPath, String.class);

            Object result = digester.parse(file);
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

    /** {@inheritDoc} */
    @Override
    public Collection<DuplicateCode> parse(final InputStream file, final String moduleName) throws InvocationTargetException {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(SimianParser.class.getClassLoader());

            ArrayList<Set> duplications = new ArrayList<Set>();
            digester.push(duplications);

            String duplicationXPath = "*/simian/check/set";
            digester.addObjectCreate(duplicationXPath, Set.class);
            digester.addSetProperties(duplicationXPath);
            digester.addSetNext(duplicationXPath, "add");

            String fileXPath = duplicationXPath + "/block";
            digester.addObjectCreate(fileXPath, Block.class);
            digester.addSetProperties(fileXPath);
            digester.addSetNext(fileXPath, "addBlock", Block.class.getName());

            Object result = digester.parse(file);
            if (result != duplications) { // NOPMD
                throw new SAXException("Input stream is not a valid Simian file.");
            }

            return convert(duplications, moduleName);
        }
        catch (IOException exception) {
            throw new InvocationTargetException(exception);
        }
        catch (SAXException exception) {
            throw new InvocationTargetException(exception);
        }
    }

    /**
     * Converts the internal structure to the annotations API.
     *
     * @param duplications
     *            the internal maven module
     * @param moduleName
     *            name of the maven module
     * @return a maven module of the annotations API
     */
    private Collection<DuplicateCode> convert(final List<Set> duplications, final String moduleName) {
        List<DuplicateCode> annotations = new ArrayList<DuplicateCode>();

        for (Set duplication : duplications) {
            List<DuplicateCode> codeBlocks = new ArrayList<DuplicateCode>();
            for (Block file : duplication.getBlocks()) {
                DuplicateCode annotation = new DuplicateCode(getPriority(duplication.getLineCount()), file.getStartLineNumber(), duplication.getLineCount(), file.getSourceFile());
                annotation.setModuleName(moduleName);
                codeBlocks.add(annotation);
            }
            for (DuplicateCode block : codeBlocks) {
                block.linkTo(codeBlocks);

                String packageName = PackageDetectors.detectPackageName(block.getFileName());
                block.setPackageName(packageName);
            }
            annotations.addAll(codeBlocks);
        }
        return annotations;
    }
}
