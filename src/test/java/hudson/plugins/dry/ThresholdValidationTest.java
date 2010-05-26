package hudson.plugins.dry;

import static org.junit.Assert.*;
import hudson.util.FormValidation;

import org.junit.Test;

/**
 * Tests the class {@link ThresholdValidation}.
 *
 * @author Ulli Hafner
 */
public class ThresholdValidationTest {
    /** Error message. */
    private static final String RESULT_IS_NOT_VALID = "Result is not valid";

    /**
     * Verifies valid values.
     */
    @Test
    public void testValid() {
        ThresholdValidation validation = new ThresholdValidation();

        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.OK, validation.validateHigh("50", "25").kind);
        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.OK, validation.validateHigh("50", "49").kind);

        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.OK, validation.validateNormal("50", "25").kind);
        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.OK, validation.validateNormal("50", "49").kind);
    }

    /**
     * Verifies invalid values.
     */
    @Test
    public void testInvalid() {
        ThresholdValidation validation = new ThresholdValidation();

        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.ERROR, validation.validateHigh("50", "50").kind);
        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.ERROR, validation.validateNormal("50", "50").kind);

        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.ERROR, validation.validateHigh("50", "-1").kind);
        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.ERROR, validation.validateNormal("50", "-1").kind);

        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.ERROR, validation.validateHigh("50", "").kind);
        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.ERROR, validation.validateNormal("50", "").kind);

        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.ERROR, validation.validateHigh("50", "500").kind);
        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.ERROR, validation.validateNormal("50", "500").kind);

        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.ERROR, validation.validateHigh("", "500").kind);
        assertEquals(RESULT_IS_NOT_VALID, FormValidation.Kind.ERROR, validation.validateNormal("", "500").kind);
    }
}

