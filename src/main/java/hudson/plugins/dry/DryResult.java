package hudson.plugins.dry;

import com.thoughtworks.xstream.XStream;

import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.plugins.analysis.core.BuildHistory;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ResultAction;
import hudson.plugins.dry.parser.DuplicateCode;

/**
 * Represents the results of the DRY analysis. One instance of this class is persisted for
 * each build via an XML file.
 *
 * @author Ulli Hafner
 */
public class DryResult extends BuildResult {
    private static final long serialVersionUID = 2768250056765266658L;

    /**
     * Creates a new instance of {@link DryResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     * @param usePreviousBuildAsReference
     *            determines whether to use the previous build as the reference
     *            build
     * @param useStableBuildAsReference
     *            determines whether only stable builds should be used as
     *            reference builds or not
     *
     * @deprecated see {@link }
     */
    @Deprecated
    public DryResult(final AbstractBuild<?, ?> build, final String defaultEncoding, final ParserResult result,
            final boolean usePreviousBuildAsReference, final boolean useStableBuildAsReference) {
        this((Run<?, ?>) build, defaultEncoding, result, usePreviousBuildAsReference, useStableBuildAsReference);
    }

    /**
     * Creates a new instance of {@link DryResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     * @param usePreviousBuildAsReference
     *            determines whether to use the previous build as the reference
     *            build
     * @param useStableBuildAsReference
     *            determines whether only stable builds should be used as
     *            reference builds or not
     */
    public DryResult(final Run<?, ?> build, final String defaultEncoding, final ParserResult result,
                     final boolean usePreviousBuildAsReference, final boolean useStableBuildAsReference) {
        this(build, defaultEncoding, result, usePreviousBuildAsReference, useStableBuildAsReference,
                DryResultAction.class);
    }

    /**
     * Creates a new instance of {@link DryResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     * @param usePreviousBuildAsReference
     *            determines whether to use the previous build as the reference
     *            build
     * @param useStableBuildAsReference
     *            determines whether only stable builds should be used as
     *            reference builds or not
     * @param actionType
     *            the type of the result action
     *
     * @deprecated {@link #DryResult(Run, String, ParserResult, boolean, boolean, Class)}
     */
    @Deprecated
    protected DryResult(final AbstractBuild<?, ?> build, final String defaultEncoding, final ParserResult result,
            final boolean usePreviousBuildAsReference, final boolean useStableBuildAsReference,
            final Class<? extends ResultAction<DryResult>> actionType) {
        this((Run<?, ?>) build, defaultEncoding, result, usePreviousBuildAsReference, useStableBuildAsReference, actionType);
    }

    /**
     * Creates a new instance of {@link DryResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     * @param usePreviousBuildAsReference
     *            determines whether to use the previous build as the reference
     *            build
     * @param useStableBuildAsReference
     *            determines whether only stable builds should be used as
     *            reference builds or not
     * @param actionType
     *            the type of the result action
     */
    protected DryResult(final Run<?, ?> build, final String defaultEncoding, final ParserResult result,
                        final boolean usePreviousBuildAsReference, final boolean useStableBuildAsReference,
                        final Class<? extends ResultAction<DryResult>> actionType) {
        this(build, new BuildHistory(build, actionType, usePreviousBuildAsReference,
                        useStableBuildAsReference),
                result, defaultEncoding, true);
    }

    @Deprecated
    DryResult(final AbstractBuild<?, ?> build, final BuildHistory history,
            final ParserResult result, final String defaultEncoding, final boolean canSerialize) {
        this((Run<?, ?>) build, history, result, defaultEncoding, canSerialize);
    }

    DryResult(final Run<?, ?> build, final BuildHistory history,
              final ParserResult result, final String defaultEncoding, final boolean canSerialize) {
        super(build, history, result, defaultEncoding);

        if (canSerialize) {
            serializeAnnotations(result.getAnnotations());
        }
    }

    @Override
    public String getHeader() {
        return Messages.DRY_ResultAction_Header();
    }

    @Override
    protected void configure(final XStream xstream) {
        xstream.alias("dry", DuplicateCode.class);
    }

    @Override
    public String getSummary() {
        return Messages.DRY_ProjectAction_Name() + ": "
                + createDefaultSummary(DryDescriptor.RESULT_URL, getNumberOfAnnotations(), getNumberOfModules());
    }

    @Override
    protected String createDeltaMessage() {
        return createDefaultDeltaMessage(DryDescriptor.RESULT_URL, getNumberOfNewWarnings(), getNumberOfFixedWarnings());
    }

    @Override
    protected String getSerializationFileName() {
        return "dry-warnings.xml";
    }

    @Override
    public String getDisplayName() {
        return Messages.DRY_ProjectAction_Name();
    }

    @Override
    protected Class<? extends ResultAction<? extends BuildResult>> getResultActionType() {
        return DryResultAction.class;
    }
}
