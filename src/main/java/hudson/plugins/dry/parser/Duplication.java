package hudson.plugins.dry.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Java Bean class for a violation of the PMD format.
 *
 * @author Ulli Hafner
 */
public class Duplication {
    /** Number of duplicate lines. */
    private int lines;
    /** Number of duplicate tokens. */
    private int tokens;

    /** All files of this duplication. */
    private final List<SourceFile> files = new ArrayList<SourceFile>();

    /**
     * Adds a new file to this duplication.
     *
     * @param file
     *            the new file
     */
    public void addFile(final SourceFile file) {
        files.add(file);
    }

    /**
     * Returns all files of the duplication. The returned collection is
     * read-only.
     *
     * @return all files
     */
    public Collection<SourceFile> getFiles() {
        return Collections.unmodifiableCollection(files);
    }

    /**
     * Returns the lines.
     *
     * @return the lines
     */
    public int getLines() {
        return lines;
    }

    /**
     * Sets the lines to the specified value.
     *
     * @param lines the value to set
     */
    public void setLines(final int lines) {
        this.lines = lines;
    }

    /**
     * Returns the tokens.
     *
     * @return the tokens
     */
    public int getTokens() {
        return tokens;
    }

    /**
     * Sets the tokens to the specified value.
     *
     * @param tokens the value to set
     */
    public void setTokens(final int tokens) {
        this.tokens = tokens;
    }
}

