package hudson.plugins.dry.parser;

import hudson.plugins.dry.Messages;
import hudson.plugins.dry.util.model.AbstractAnnotation;
import hudson.plugins.dry.util.model.Priority;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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

    /** The links to the other code duplications. */
    @SuppressWarnings("Se")
    private final Set<DuplicateCode> links = new HashSet<DuplicateCode>();
    /** The duplicate source code fragment. */
    private String sourceCode;

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

    /**
     * Returns the total number of duplicate lines.
     *
     * @return the number of duplicate lines
     */
    public int getNumberOfLines() {
        return getLineRanges().iterator().next().getEnd() - getPrimaryLineNumber() + 1;
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
        final int prime = 31;
        int result = (getFileName() == null) ? 0 : getFileName().hashCode();
        result = prime * result + ((sourceCode == null) ? 0 : sourceCode.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DuplicateCode other = (DuplicateCode)obj;
        if (getFileName() == null) {
            if (other.getFileName() != null) {
                return false;
            }
        }
        else if (!getFileName().equals(other.getFileName())) {
            return false;
        }
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
}

