package hudson.plugins.dry.parser;

import hudson.XmlFile;
import hudson.plugins.analysis.test.AbstractSerializeModelTest;
import hudson.plugins.analysis.util.model.AbstractAnnotation;
import hudson.plugins.analysis.util.model.AnnotationStream;
import hudson.plugins.analysis.util.model.JavaProject;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.dry.Messages;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;

/**
 * Tests the serialization of the model.
 *
 * @see <a href="http://www.ibm.com/developerworks/library/j-serialtest.html">Testing object serialization</a>
 */
public class DuplicationSerializeModelTest extends AbstractSerializeModelTest {
    /** Serialization provider. */
    private static final XStream XSTREAM = new AnnotationStream();

    static {
        XSTREAM.alias("dry", DuplicateCode.class);
    }

    /**
     * Verifies the first created annotation.
     *
     * @param annotation
     *            the first created annotation
     */
    @Override
    protected void verifyFirstAnnotation(final AbstractAnnotation annotation) {
        DuplicateCode bug = (DuplicateCode)annotation;
        Assert.assertEquals("Wrong detail message." , Messages.DRY_Warning_Message(1), bug.getMessage());
    }

    /**
     * Creates an annotation.
     *
     * @param line
     *            the line
     * @param message
     *            the message
     * @param priority
     *            the priority
     * @param fileName
     *            the file name
     * @param packageName
     *            the package name
     * @param moduleName
     *            the module name
     * @return the annotation
     */
    @Override
    protected AbstractAnnotation createAnnotation(final int line, final String message, final Priority priority, final String fileName, final String packageName, final String moduleName) {
        DuplicateCode duplicateCode = new DuplicateCode(priority, line, 1, message);
        duplicateCode.setFileName(fileName);
        duplicateCode.setPackageName(packageName);
        duplicateCode.setModuleName(moduleName);
        duplicateCode.setSourceCode(message);

        return duplicateCode;
    }

    /**
     * Test whether a serialized project is the same object after
     * deserialization of the file format of release 2.2.
     */
    @Test
    public void ensureSameSerialization() {
        JavaProject project = deserialize("project.ser");

        verifyProject(project);
    }

    /**
     * Test whether a serialized project is the same object after
     * deserialization of the file format of release 2.2.
     */
    @Test
    public void ensureSameXmlSerialization() {
        ensureSerialization("project.ser.xml");
    }

    @Override
    protected XmlFile createXmlFile(final File file) {
        return new XmlFile(XSTREAM, file);
    }
}

