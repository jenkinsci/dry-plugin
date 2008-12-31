package hudson.plugins.dry;

import hudson.plugins.dry.util.AbstractHealthDescriptor;
import hudson.plugins.dry.util.HealthDescriptor;
import hudson.plugins.dry.util.model.AnnotationProvider;

import org.jvnet.localizer.Localizable;

/**
 * A health descriptor for DRY build results.
 *
 * @author Ulli Hafner
 */
public class DryHealthDescriptor extends AbstractHealthDescriptor {
    /** Unique ID of this class. */
    private static final long serialVersionUID = -3404826986876607396L;

    /**
     * Creates a new instance of {@link DryHealthDescriptor} based on the
     * values of the specified descriptor.
     *
     * @param healthDescriptor the descriptor to copy the values from
     */
    public DryHealthDescriptor(final HealthDescriptor healthDescriptor) {
        super(healthDescriptor);
    }

    /** {@inheritDoc} */
    @Override
    protected Localizable createDescription(final AnnotationProvider result) {
        if (result.getNumberOfAnnotations() == 0) {
            return Messages._DRY_ResultAction_HealthReportNoItem();
        }
        else if (result.getNumberOfAnnotations() == 1) {
            return Messages._DRY_ResultAction_HealthReportSingleItem();
        }
        else {
            return Messages._DRY_ResultAction_HealthReportMultipleItem(result.getNumberOfAnnotations());
        }
    }
}

