package hudson.plugins.dry.parser.simian;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.digester3.Digester;

import hudson.plugins.analysis.util.PackageDetectors;
import hudson.plugins.dry.parser.AbstractDigesterParser;
import hudson.plugins.dry.parser.DuplicateCode;

/**
 * A parser for Simian XML files.
 *
 * @author Ulli Hafner
 */
public class SimianParser extends AbstractDigesterParser<Set> {
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

    @Override
    protected String getMatchingPattern() {
        return "*/simian";
    }

    @Override
    protected void configureParser(final Digester digester) {
        String duplicationXPath = "*/simian/check/set";
        digester.addObjectCreate(duplicationXPath, Set.class);
        digester.addSetProperties(duplicationXPath);
        digester.addSetNext(duplicationXPath, "add");

        String fileXPath = duplicationXPath + "/block";
        digester.addObjectCreate(fileXPath, Block.class);
        digester.addSetProperties(fileXPath);
        digester.addSetNext(fileXPath, "addBlock", Block.class.getName());
    }

    @Override
    protected Collection<DuplicateCode> convertWarnings(final List<Set> duplications, final String moduleName) {
        List<DuplicateCode> annotations = new ArrayList<DuplicateCode>();

        Random random = new Random();
        int number = random.nextInt();
        for (Set duplication : duplications) {
            List<DuplicateCode> codeBlocks = new ArrayList<DuplicateCode>();
            for (Block file : duplication.getBlocks()) {
                DuplicateCode annotation = new DuplicateCode(getPriority(duplication.getLineCount()), file.getStartLineNumber(), duplication.getLineCount(), file.getSourceFile());
                annotation.setModuleName(moduleName);
                codeBlocks.add(annotation);
            }
            for (DuplicateCode block : codeBlocks) {
                block.linkTo(codeBlocks);
                block.setNumber(number);
                String packageName = PackageDetectors.detectPackageName(block.getFileName());
                block.setPackageName(packageName);
            }
            annotations.addAll(codeBlocks);
            number++;
        }
        return annotations;
    }
}
