package hudson.plugins.dry.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Java Bean class for a violations collection of the PMD format.
 *
 * @author Ulli Hafner
 */
public class Pmd {
    /** All files of this violations collection. */
    private final List<SourceFile> files = new ArrayList<SourceFile>();

    /**
     * Adds a new file to this bug collection.
     *
     * @param file the file to add
     */
    public void addFile(final SourceFile file) {
        files.add(file);
    }

    /**
     * Returns all files of this violations collection. The returned collection is
     * read-only.
     *
     * @return all files of this bug collection
     */
    public Collection<SourceFile> getFiles() {
        return Collections.unmodifiableCollection(files);
    }
}

