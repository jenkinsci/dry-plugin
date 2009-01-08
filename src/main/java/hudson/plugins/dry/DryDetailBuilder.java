package hudson.plugins.dry;

import hudson.model.AbstractBuild;
import hudson.plugins.dry.parser.DuplicateCode;
import hudson.plugins.dry.util.DetailBuilder;
import hudson.plugins.dry.util.SourceDetail;
import hudson.plugins.dry.util.model.AnnotationContainer;
import hudson.plugins.dry.util.model.FileAnnotation;

import org.apache.commons.lang.StringUtils;

/**
 * A detail builder for dry annotations capable of showing details of linked annotations.
 *
 * @author Ulli Hafner
 */
public class DryDetailBuilder extends DetailBuilder {
    /** {@inheritDoc} */
    @Override
    public Object createDetails(final String link, final AbstractBuild<?, ?> owner,
            final AnnotationContainer container, final String defaultEncoding, final String displayName) {
        if (link.startsWith("link.")) {
            String suffix = StringUtils.substringAfter(link, "link.");
            String[] fromToStrings = StringUtils.split(suffix, ".");
            if (fromToStrings.length == 2) {
                long from = Long.parseLong(fromToStrings[0]);
                long to = Long.parseLong(fromToStrings[1]);

                FileAnnotation fromAnnotation = container.getAnnotation(from);
                if (fromAnnotation instanceof DuplicateCode) {
                    return new SourceDetail(owner, ((DuplicateCode)fromAnnotation).getLink(to), defaultEncoding);
                }
            }
        }

        return super.createDetails(link, owner, container, defaultEncoding, displayName);
    }
}

