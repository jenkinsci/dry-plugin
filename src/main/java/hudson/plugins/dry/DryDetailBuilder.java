package hudson.plugins.dry;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.util.model.AnnotationContainer;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.views.DetailFactory;
import hudson.plugins.analysis.views.SourceDetail;
import hudson.plugins.analysis.views.TabDetail;
import hudson.plugins.dry.parser.DuplicateCode;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/**
 * A detail builder for dry annotations capable of showing details of linked annotations.
 *
 * @author Ulli Hafner
 */
public class DryDetailBuilder extends DetailFactory {
    @Override
    public Object createDetails(final String link, final AbstractBuild<?, ?> owner,
            final AnnotationContainer container, final String defaultEncoding, final String displayName) {
        if (link.startsWith("link.")) {
            String suffix = StringUtils.substringAfter(link, "link.");
            String[] fromToStrings = StringUtils.split(suffix, ".");
            if (fromToStrings.length == 2) {
                return createDrySourceDetail(owner, container, defaultEncoding, fromToStrings[0], fromToStrings[1]);
            }
            return null;
        }
        return super.createDetails(link, owner, container, defaultEncoding, displayName);
    }

    /**
     * Creates the dry source detail view.
     *
     * @param owner
     *            the owner
     * @param container
     *            the container
     * @param defaultEncoding
     *            the default encoding
     * @param fromString
     *            from ID
     * @param toString
     *            to ID
     * @return the detail view or <code>null</code>
     */
    private Object createDrySourceDetail(final AbstractBuild<?, ?> owner,
            final AnnotationContainer container, final String defaultEncoding,
            final String fromString, final String toString) {
        long from = Long.parseLong(fromString);
        long to = Long.parseLong(toString);

        FileAnnotation fromAnnotation = container.getAnnotation(from);
        if (fromAnnotation instanceof DuplicateCode) {
            return new SourceDetail(owner, ((DuplicateCode)fromAnnotation).getLink(to), defaultEncoding);
        }
        return null;
    }

    @Override
    protected TabDetail createTabDetail(final AbstractBuild<?, ?> owner, final Collection<FileAnnotation> annotations,
            final String url, final String defaultEncoding) {
        return new DryTabDetail(owner, this, annotations, url, defaultEncoding);
    }
}

