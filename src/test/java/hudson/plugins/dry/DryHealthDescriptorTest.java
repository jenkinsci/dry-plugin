package hudson.plugins.dry;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import hudson.plugins.dry.util.AbstractEnglishLocaleTest;
import hudson.plugins.dry.util.NullHealthDescriptor;
import hudson.plugins.dry.util.model.AnnotationProvider;

import org.junit.Test;
import org.jvnet.localizer.Localizable;

/**
 * Tests the class {@link DryHealthDescriptor}.
 *
 * @author Ulli Hafner
 */
public class DryHealthDescriptorTest extends AbstractEnglishLocaleTest {
    /**
     * Verifies the different messages if the number of items are 0, 1, and 2.
     */
    @Test
    public void verifyNumberOfItems() {
        AnnotationProvider provider = mock(AnnotationProvider.class);
        DryHealthDescriptor healthDescriptor = new DryHealthDescriptor(NullHealthDescriptor.NULL_HEALTH_DESCRIPTOR);

        Localizable description = healthDescriptor.createDescription(provider);
        assertEquals(Messages.DRY_ResultAction_HealthReportNoItem(), description.toString());

        when(provider.getNumberOfAnnotations()).thenReturn(1);
        description = healthDescriptor.createDescription(provider);
        assertEquals(Messages.DRY_ResultAction_HealthReportSingleItem(), description.toString());

        when(provider.getNumberOfAnnotations()).thenReturn(2);
        description = healthDescriptor.createDescription(provider);
        assertEquals(Messages.DRY_ResultAction_HealthReportMultipleItem(2), description.toString());
    }
}

