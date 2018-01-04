package hudson.plugins.dry.parser.dupfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import hudson.plugins.analysis.util.PackageDetectors;
import hudson.plugins.analysis.util.SecureDigester;
import hudson.plugins.dry.parser.AbstractDigesterParser;
import hudson.plugins.dry.parser.DuplicateCode;

/**
 * A parser for Reshaper Dupfinder XML files.
 *
 * @author Rafal Jasica
 */
public class DupFinderParser extends AbstractDigesterParser<Duplicate> {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 1357147358617711901L;

    /**
     * Creates a new instance of {@link DupFinderParser}.
     *
     * @param highThreshold
     *            minimum number of duplicate lines for high priority warnings
     * @param normalThreshold
     *            minimum number of duplicate lines for normal priority warnings
     */
    public DupFinderParser(final int highThreshold, final int normalThreshold) {
        super(highThreshold, normalThreshold);
    }

    @Override
    protected String getMatchingPattern() {
        return "*/DuplicatesReport";
    }

    @Override
    protected void configureParser(final SecureDigester digester) {
        String duplicationXPath = "*/DuplicatesReport/Duplicates/Duplicate";
        digester.addObjectCreate(duplicationXPath, Duplicate.class);
        digester.addSetProperties(duplicationXPath, "Cost", "cost");
        digester.addSetNext(duplicationXPath, "add");

        String fragmentXPath = duplicationXPath + "/Fragment";
        digester.addObjectCreate(fragmentXPath, Fragment.class);
        digester.addBeanPropertySetter(fragmentXPath + "/FileName", "fileName");
        digester.addBeanPropertySetter(fragmentXPath + "/Text", "text");
        digester.addSetNext(fragmentXPath, "addFragment", Fragment.class.getName());

        String lineRangeXPath = fragmentXPath + "/LineRange";
        digester.addObjectCreate(lineRangeXPath, Range.class);
        digester.addSetProperties(lineRangeXPath, "Start", "start");
        digester.addSetProperties(lineRangeXPath, "End", "end");
        digester.addSetNext(lineRangeXPath, "setLineRange", Range.class.getName());

        String offsetRangeXPath = fragmentXPath + "/OffsetRange";
        digester.addObjectCreate(offsetRangeXPath, Range.class);
        digester.addSetProperties(offsetRangeXPath, "Start", "start");
        digester.addSetProperties(offsetRangeXPath, "End", "end");
        digester.addSetNext(offsetRangeXPath, "setOffsetRange", Range.class.getName());
    }

    @Override
    protected Collection<DuplicateCode> convertWarnings(final List<Duplicate> duplications, final String moduleName) {
        List<DuplicateCode> annotations = new ArrayList<DuplicateCode>();

        Random random = new Random();
        int number = random.nextInt();
        for (Duplicate duplication : duplications) {
            List<DuplicateCode> codeBlocks = new ArrayList<DuplicateCode>();
            Collection<Fragment> fragments = duplication.getFragments();
            for (Fragment fragment : fragments) {
                Range lineRange = fragment.getLineRange();
                int count = lineRange.getEnd() - lineRange.getStart() + 1;
                DuplicateCode annotation = new DuplicateCode(getPriority(count), lineRange.getStart(), count,
                        fragment.getFileName());
                String text = fragment.getText();
                if (text != null) {
                    annotation.setSourceCode(text);
                }
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
