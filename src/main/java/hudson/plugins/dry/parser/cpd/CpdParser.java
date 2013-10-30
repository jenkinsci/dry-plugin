package hudson.plugins.dry.parser.cpd;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.digester3.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import hudson.plugins.analysis.util.PackageDetectors;
import hudson.plugins.dry.parser.AbstractDryParser;
import hudson.plugins.dry.parser.DuplicateCode;

/**
 * A parser for PMD's CPD XML files.
 *
 * @author Ulli Hafner
 */
public class CpdParser extends AbstractDryParser {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6507147028628714706L;

    /**
     * Creates a new instance of {@link CpdParser}.
     *
     * @param highThreshold
     *            minimum number of duplicate lines for high priority warnings
     * @param normalThreshold
     *            minimum number of duplicate lines for normal priority warnings
     */
    public CpdParser(final int highThreshold, final int normalThreshold) {
        super(highThreshold, normalThreshold);
    }

    @Override
    public boolean accepts(final InputStream file) {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(CpdParser.class.getClassLoader());

            String duplicationXPath = "*/pmd-cpd";
            digester.addObjectCreate(duplicationXPath, String.class);
            InputSource inputSource = new InputSource(file);
            inputSource.setEncoding("UTF-8");

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

    @Override
    public Collection<DuplicateCode> parse(final InputStream file, final String moduleName) throws InvocationTargetException {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(CpdParser.class.getClassLoader());

            ArrayList<Duplication> duplications = new ArrayList<Duplication>();
            digester.push(duplications);

            String duplicationXPath = "*/pmd-cpd/duplication";
            digester.addObjectCreate(duplicationXPath, Duplication.class);
            digester.addSetProperties(duplicationXPath);
            digester.addCallMethod(duplicationXPath + "/codefragment", "setCodeFragment", 0);
            digester.addSetNext(duplicationXPath, "add");

            String fileXPath = duplicationXPath + "/file";
            digester.addObjectCreate(fileXPath, SourceFile.class);
            digester.addSetProperties(fileXPath);
            digester.addSetNext(fileXPath, "addFile", SourceFile.class.getName());

            Object result = digester.parse(file);
            if (result != duplications) { // NOPMD
                throw new SAXException("Input stream is not a valid CPD file.");
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
    private Collection<DuplicateCode> convert(final List<Duplication> duplications, final String moduleName) {
        List<DuplicateCode> annotations = new ArrayList<DuplicateCode>();

        Random random = new Random();
        int number = random.nextInt();
        for (Duplication duplication : duplications) {
            List<DuplicateCode> codeBlocks = new ArrayList<DuplicateCode>();
            for (SourceFile file : duplication.getFiles()) {
                // TODO: check why PMD reports a length + 1
                DuplicateCode annotation = new DuplicateCode(getPriority(duplication.getLines()), file.getLine(), duplication.getLines(), file.getPath());
                annotation.setSourceCode(duplication.getCodeFragment());
                annotation.setModuleName(moduleName);
                codeBlocks.add(annotation);
            }
            for (DuplicateCode block : codeBlocks) {
                block.linkTo(codeBlocks);
                block.setNumber(number);
                block.setPackageName(PackageDetectors.detectPackageName(block.getFileName()));
            }
            annotations.addAll(codeBlocks);
            number++;
        }
        return annotations;
    }
}
