package hudson.plugins.dry.parser;

import hudson.plugins.analysis.util.model.AbstractAnnotation;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.dry.Messages;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.java2html.converter.JavaSource2HTMLConverter;
import de.java2html.javasource.JavaSource;
import de.java2html.javasource.JavaSourceParser;
import de.java2html.options.JavaSourceConversionOptions;
import de.java2html.util.IllegalConfigurationException;
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
    /** Origin of the annotation. */
    public static final String ORIGIN = "dry";

    /** The links to the other code duplications. */
    @SuppressWarnings("Se")
    private final Set<DuplicateCode> links = new HashSet<DuplicateCode>();
    /** The duplicate source code fragment. */
    private String sourceCode;

    /**
     * Creates a new instance of {@link DuplicateCode}.
     *
     * @param priority
     *            the priority of the warning
     * @param firstLine
     *            the starting line of the duplication
     * @param numberOfLines
     *            total number of duplicate lines
     * @param fileName
     *            name of the file that contains the duplication
     */
    public DuplicateCode(final Priority priority, final int firstLine, final int numberOfLines, final String fileName) {
        super(priority, Messages.DRY_Warning_Message(numberOfLines),
                firstLine, firstLine +  numberOfLines - 1, StringUtils.EMPTY, Messages.DRY_Warning_Type());

        setOrigin(ORIGIN);
        setFileName(fileName);
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return Messages.DRY_Warning_Type();
    }

    /** {@inheritDoc} */
    @Override
    public String getMessage() {
        return Messages.DRY_Warning_Message(size());
    }

    /**
     * Returns the total number of duplicate lines.
     *
     * @return the number of duplicate lines
     */
    public int getNumberOfLines() {
        return getLineRanges().iterator().next().getEnd() - getPrimaryLineNumber() + 1;
    }

    /**
     * Returns the total number of duplicate lines.
     *
     * @return the number of duplicate lines
     */
    public int size() {
        return getNumberOfLines();
    }

    /**
     * Returns the total number of duplicate lines.
     *
     * @return the number of duplicate lines
     */
    public int length() {
        return getNumberOfLines();
    }

    /** {@inheritDoc} */
    public String getToolTip() {
        StringBuilder message = new StringBuilder();
        message.append("<p>");
        message.append(Messages.DRY_Duplications_Header());
        message.append("<ul>");
        for (DuplicateCode duplication : links) {
            message.append("<li>");
            message.append(String.format("<a href=\"link.%s.%s/#%s\">%s (%s)</a>",
                    getKey(), duplication.getKey(), duplication.getPrimaryLineNumber(),
                    duplication.getLinkName(), duplication.getPrimaryLineNumber()));
            message.append("</li>");
        }
        message.append("</ul>");
        message.append("</p>");
        return message.toString();
    }

    /**
     * Creates links to the specified collection of other code blocks.
     *
     * @param codeBlocks the code blocks to links to
     */
    public void linkTo(final List<DuplicateCode> codeBlocks) {
        links.addAll(codeBlocks);
        links.remove(this);
    }

    /**
     * Returns the links to the duplicated code in other files.
     *
     * @return the links
     */
    public Collection<DuplicateCode> getLinks() {
        return Collections.unmodifiableCollection(links);
    }

   /**
     * Returns the duplicate source code fragment.
     *
     * @return the duplicate source code fragment
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * Returns the duplicate source code fragment as formatted HTML string.
     *
     * @return the duplicate source code fragment
     */
    public String getFormattedSourceCode() {
        try {
            JavaSource source = new JavaSourceParser().parse(new StringReader(sourceCode));
            JavaSource2HTMLConverter converter = new JavaSource2HTMLConverter();
            StringWriter writer = new StringWriter();
            JavaSourceConversionOptions options = JavaSourceConversionOptions.getDefault();
            options.setShowLineNumbers(false);
            options.setAddLineAnchors(false);
            converter.convert(source, options, writer);

            return writer.toString();
        }
        catch (IllegalConfigurationException exception) {
            return sourceCode;
        }
        catch (IOException exception) {
            return sourceCode;
        }
    }

    /**
     * Sets the duplicate source code fragment to the specified value.
     *
     * @param sourceCode the duplicate code fragment
     */
    public void setSourceCode(final String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int prime = 31; // NOCHECKSTYLE
        int result = super.hashCode();
        result = prime * result + ((sourceCode == null) ? 0 : sourceCode.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DuplicateCode other = (DuplicateCode)obj;
        if (sourceCode == null) {
            if (other.sourceCode != null) {
                return false;
            }
        }
        else if (!sourceCode.equals(other.sourceCode)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the link with the specified hash code.
     *
     * @param linkHashCode
     *            the hash code of the linked annotation
     * @return the link with the specified hash code
     */
    public FileAnnotation getLink(final long linkHashCode) {
        for (FileAnnotation link : links) {
            if (link.getKey() == linkHashCode) {
                return link;
            }
        }
        throw new NoSuchElementException("Linked annotation not found: key=" + linkHashCode);
    }
}

