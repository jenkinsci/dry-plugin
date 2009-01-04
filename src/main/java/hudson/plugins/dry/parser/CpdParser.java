package hudson.plugins.dry.parser;

import hudson.plugins.dry.util.AnnotationParser;
import hudson.plugins.dry.util.JavaPackageDetector;
import hudson.plugins.dry.util.model.FileAnnotation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * A parser for PMD's CPD XML files.
 *
 * @author Ulli Hafner
 */
public class CpdParser implements AnnotationParser {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6507147028628714706L;

    /** {@inheritDoc} */
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        try {
            if (accepts(new FileInputStream(file))) {
                return parse(new FileInputStream(file), moduleName);
            }
            else {
                throw new IOException("Can't parse CPD file " + file.getAbsolutePath());
            }
        }
        catch (IOException exception) {
            throw new InvocationTargetException(exception);
        }
    }


    /**
     * Returns whether this parser accepts the specified stream as a valid CPD
     * file.
     *
     * @param file
     *            the file to parse
     * @return <code>true</code> if this parser accepts the specified stream as
     *         a valid CPD file, false if the parser can'r read this file
     */
    public boolean accepts(final InputStream file) {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(CpdParser.class.getClassLoader());

            String duplicationXPath = "*/pmd-cpd";
            digester.addObjectCreate(duplicationXPath, String.class);

            Object result = digester.parse(file);
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

    /**
     * Returns the annotations found in the specified file.
     *
     * @param file
     *            the file to parse
     * @param moduleName
     *            name of the maven module
     * @return the parsed annotations
     * @throws InvocationTargetException
     *             if the file could not be parsed (wrap your exception in this exception)
     */
    public Collection<FileAnnotation> parse(final InputStream file, final String moduleName) throws InvocationTargetException {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(CpdParser.class.getClassLoader());

            ArrayList<Duplication> duplications = new ArrayList<Duplication>();
            digester.push(duplications);

            String duplicationXPath = "*/pmd-cpd/duplication";
            digester.addObjectCreate(duplicationXPath, Duplication.class);
            digester.addSetProperties(duplicationXPath);
            digester.addCallMethod(duplicationXPath + "/codefragment", "setCodeFragment", 0);
            digester.addSetNext(duplicationXPath, "add");

            String fileXPath = duplicationXPath + "/file";
            digester.addObjectCreate(fileXPath, SourceFile.class);
            digester.addSetProperties(fileXPath);
            digester.addSetNext(fileXPath, "addFile", SourceFile.class.getName());

            Object result = digester.parse(file);
            if (result != duplications) {
                throw new SAXException("Input stream is not a valid CPD file.");
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
    private Collection<FileAnnotation> convert(final ArrayList<Duplication> duplications, final String moduleName) {
        JavaPackageDetector javaPackageDetector = new JavaPackageDetector();
        ArrayList<FileAnnotation> annotations = new ArrayList<FileAnnotation>();

        for (Duplication duplication : duplications) {
            ArrayList<DuplicateCode> codeBlocks = new ArrayList<DuplicateCode>();
            for (SourceFile file : duplication.getFiles()) {
                // TODO: check why PMD reports a length + 1
                DuplicateCode annotation = new DuplicateCode(file.getLine(), duplication.getLines(), file.getPath());
                annotation.setSourceCode(duplication.getCodeFragment());
                annotation.setModuleName(moduleName);
                codeBlocks.add(annotation);
            }
            for (DuplicateCode block : codeBlocks) {
                block.linkTo(codeBlocks);

                String packageName = javaPackageDetector.detectPackageName(block.getFileName());
                block.setPackageName(packageName);
            }
            annotations.addAll(codeBlocks);
        }
        return annotations;
    }

    /** {@inheritDoc} */
    public String getName() {
        return "CPD";
    }
}
