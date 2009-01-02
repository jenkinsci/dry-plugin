package hudson.plugins.dry.parser;

import hudson.plugins.dry.Messages;
import hudson.plugins.dry.util.model.AbstractAnnotation;
import hudson.plugins.dry.util.model.Priority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * A serializable Java Bean class representing a duplicate code warning.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * </p>
 *
 * @author Ulli Hafner
 */
public class DuplicateCode extends AbstractAnnotation {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -6231614169627992548L;

    /** The links to the other code duplications. */
    @SuppressWarnings("Se")
    private final Set<DuplicateCode> links = new HashSet<DuplicateCode>();

    /**
     * Creates a new instance of {@link DuplicateCode}.
     *
     * @param firstLine
     *            the starting line of the duplication
     * @param numberOfLines
     *            total number of duplicate lines
     * @param fileName
     *            name of the file that contains the duplication
     */
    public DuplicateCode(final int firstLine, final int numberOfLines, final String fileName) {
        super(Priority.NORMAL, Messages.DRY_Warning_Message(numberOfLines),
                firstLine, firstLine +  numberOfLines - 1, "Duplicate Code", StringUtils.EMPTY);

        setFileName(fileName);
    }

    /** {@inheritDoc} */
    public String getToolTip() {
        return "Duplicate Code Tool Tip";
    }

    /**
     * Creates links to the specified collection of other code blocks.
     *
     * @param codeBlocks the code blocks to links to
     */
    public void linkTo(final ArrayList<DuplicateCode> codeBlocks) {
        links.addAll(codeBlocks);
        links.remove(this);
    }
}

