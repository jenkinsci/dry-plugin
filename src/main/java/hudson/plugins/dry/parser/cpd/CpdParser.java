package hudson.plugins.dry.parser.cpd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.digester3.Digester;
import org.xml.sax.InputSource;

import hudson.plugins.analysis.util.PackageDetectors;
import hudson.plugins.dry.parser.AbstractDigesterParser;
import hudson.plugins.dry.parser.DuplicateCode;

/**
 * A parser for PMD's CPD XML files.
 *
 * @author Ulli Hafner
 */
public class CpdParser extends AbstractDigesterParser<Duplication> {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6507147028628714706L;
    private final boolean forceUtf8;

    /**
     * Creates a new instance of {@link CpdParser}.
     *
     * @param highThreshold
     *            minimum number of duplicate lines for high priority warnings
     * @param normalThreshold
     *            minimum number of duplicate lines for normal priority warnings
     */
    public CpdParser(final int highThreshold, final int normalThreshold) {
        this(highThreshold, normalThreshold, true);
    }

    /**
     * Creates a new instance of {@link CpdParser}.
     *
     * @param highThreshold   minimum number of duplicate lines for high priority warnings
     * @param normalThreshold minimum number of duplicate lines for normal priority warnings
     * @param forceUtf8       forces the parser to use UTF8 encoding
     */
    public CpdParser(final int highThreshold, final int normalThreshold, final boolean forceUtf8) {
        super(highThreshold, normalThreshold);
        this.forceUtf8 = forceUtf8;
    }

    @Override
    protected InputSource createInputSource(final InputStream file) {
        if (forceUtf8) {
            return super.createInputSource(file);
        }
        else {
            return new InputSource(file);
        }
    }

    @Override
    protected String getMatchingPattern() {
        return "*/pmd-cpd";
    }

    @Override
    protected void configureParser(final Digester digester) {
        String duplicationXPath = "*/pmd-cpd/duplication";
        digester.addObjectCreate(duplicationXPath, Duplication.class);
        digester.addSetProperties(duplicationXPath);
        digester.addCallMethod(duplicationXPath + "/codefragment", "setCodeFragment", 0);
        digester.addSetNext(duplicationXPath, "add");

        String fileXPath = duplicationXPath + "/file";
        digester.addObjectCreate(fileXPath, SourceFile.class);
        digester.addSetProperties(fileXPath);
        digester.addSetNext(fileXPath, "addFile", SourceFile.class.getName());
    }

    @Override
    protected Collection<DuplicateCode> convertWarnings(final List<Duplication> duplications, final String moduleName) {
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
