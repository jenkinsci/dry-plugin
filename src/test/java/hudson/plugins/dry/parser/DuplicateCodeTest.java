package hudson.plugins.dry.parser;

import static org.junit.Assert.*;
import hudson.plugins.analysis.util.model.Priority;

import org.junit.Test;

/**
 * Tests the class {@link DuplicateCode}.
 *
 * @author Ulli Hafner
 */
public class DuplicateCodeTest {
    private static final String WRONG_PACKAGE_NAME = "Wrong package name";
    private static final String WRONG_PATH_NAME = "Wrong path name";
    private static final String WRONG_LINK_NAME = "Wrong link name";

    /** Windows file name. */
    private static final String WINDOWS_FILE = "C:\\Folder1\\Folder2\\file.txt";
    /** UNIX file name. */
    private static final String UNIX_FILE = "/Folder1/Folder2/file.txt";

    /**
     * Verifies that the link name is correctly computed.
     */
    @Test
    public void testLinkName() {
        DuplicateCode dry;

        dry = new DuplicateCode(Priority.HIGH, 1, 10, WINDOWS_FILE);
        assertEquals(WRONG_LINK_NAME, "C:/Folder1/Folder2/file.txt", dry.getLinkName());
        assertEquals(WRONG_PACKAGE_NAME, "Default Package", dry.getPackageName());

        dry.setPathName("C:/Folder1");
        assertEquals(WRONG_LINK_NAME, "Folder2/file.txt", dry.getLinkName());
        assertEquals(WRONG_PATH_NAME, "Folder2", dry.getPathName());

        dry = new DuplicateCode(Priority.HIGH, 1, 10, UNIX_FILE);
        assertEquals(WRONG_LINK_NAME, UNIX_FILE, dry.getLinkName());
        assertEquals(WRONG_PACKAGE_NAME, "Default Package", dry.getPackageName());

        dry.setPathName("/Folder1");
        assertEquals(WRONG_LINK_NAME, "Folder2/file.txt", dry.getLinkName());
        assertEquals(WRONG_PATH_NAME, "Folder2", dry.getPathName());
    }
}

