package hudson.plugins.dry.parser.dupfinder;

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
 * A parser for Reshaper Dupfinder XML files.
 *
 * @author Rafal Jasica
 */
public class DupFinderParser extends AbstractDryParser {
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
    public boolean accepts(final InputStream file) {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(DupFinderParser.class.getClassLoader());

            String duplicationXPath = "*/DuplicatesReport";
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
    public Collection<DuplicateCode> parse(final InputStream file, final String moduleName)
            throws InvocationTargetException {
        try {
            Digester digester = new Digester();

            digester.setValidating(false);
            digester.setClassLoader(DupFinderParser.class.getClassLoader());

            ArrayList<Duplicate> duplications = new ArrayList<Duplicate>();
            digester.push(duplications);

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

            Object result = digester.parse(file);
            if (result != duplications) { // NOPMD
                throw new SAXException("Input stream is not a valid Reshaper DupFinder file.");
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
    private Collection<DuplicateCode> convert(final List<Duplicate> duplications, final String moduleName) {
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
