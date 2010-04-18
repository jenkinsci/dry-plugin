package hudson.plugins.dry;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.views.DetailFactory;
import hudson.plugins.analysis.views.TabDetail;

import java.util.Collection;

/**
 * Detail view for the DRY plug-in: uses different table visualization.
 *
 * @author Ulli Hafner
 */
public class DryTabDetail extends TabDetail {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 4817560593426732391L;

    /**
     * Creates a new instance of {@link TabDetail}.
     *
     * @param owner
     *            current build as owner of this action.
     * @param detailFactory
     *            the detail factory to use
     * @param annotations
     *            the module to show the details for
     * @param url
     *            URL to render the content of this tab
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public DryTabDetail(final AbstractBuild<?, ?> owner, final DetailFactory detailFactory, final Collection<FileAnnotation> annotations, final String url, final String defaultEncoding) {
        super(owner, detailFactory, annotations, url, defaultEncoding);
    }

    @Override
    public String getUrl() {
        if ("/tabview/warnings.jelly".equals(super.getUrl())) {
            return "local-warnings.jelly";
        }
        return super.getUrl();
    }
}

