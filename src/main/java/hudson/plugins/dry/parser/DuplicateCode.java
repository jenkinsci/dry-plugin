package hudson.plugins.dry.parser;

import hudson.plugins.dry.util.model.AbstractAnnotation;
import hudson.plugins.dry.util.model.Priority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A serializable Java Bean class representing a warning.
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
    private final Set<DuplicateCode> links = new HashSet<DuplicateCode>();

    /**
     * Creates a new instance of {@link DuplicateCode}.
     */
    public DuplicateCode(final int firstLine, final int numberOfLines, final String fileName) {
        super(Priority.NORMAL, "", firstLine, firstLine +  numberOfLines - 1, "Duplicate Code", "");

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

