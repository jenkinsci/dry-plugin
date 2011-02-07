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
    private static final String WRONG_VALUE_FOR_NORMAL_THRESHOLD = "Wrong value for normal threshold";
    private static final String WRONG_VALUE_FOR_HIGH_THRESHOLD = "Wrong value for high threshold";
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

    /**
     * Verifies that the getters return the default values for illegal thresholds.
     */
    @Test
    public void testGetters() {
        ThresholdValidation validation = new ThresholdValidation();

        assertEquals(WRONG_VALUE_FOR_HIGH_THRESHOLD, 2, validation.getHighThreshold(1, 2));
        assertEquals(WRONG_VALUE_FOR_HIGH_THRESHOLD, 20, validation.getHighThreshold(1, 20));
        assertEquals(WRONG_VALUE_FOR_HIGH_THRESHOLD, 10, validation.getHighThreshold(5, 10));
        assertEquals(WRONG_VALUE_FOR_HIGH_THRESHOLD, 10, validation.getHighThreshold(9, 10));

        assertEquals(WRONG_VALUE_FOR_HIGH_THRESHOLD,
                ThresholdValidation.DEFAULT_HIGH_THRESHOLD, validation.getHighThreshold(10, 10));
        assertEquals(WRONG_VALUE_FOR_HIGH_THRESHOLD,
                ThresholdValidation.DEFAULT_HIGH_THRESHOLD, validation.getHighThreshold(0, 10));
        assertEquals(WRONG_VALUE_FOR_HIGH_THRESHOLD,
                ThresholdValidation.DEFAULT_HIGH_THRESHOLD, validation.getHighThreshold(-1, 10));
        assertEquals(WRONG_VALUE_FOR_HIGH_THRESHOLD,
                ThresholdValidation.DEFAULT_HIGH_THRESHOLD, validation.getHighThreshold(5, 0));

        assertEquals(WRONG_VALUE_FOR_NORMAL_THRESHOLD, 1, validation.getNormalThreshold(1, 2));
        assertEquals(WRONG_VALUE_FOR_NORMAL_THRESHOLD, 1, validation.getNormalThreshold(1, 20));
        assertEquals(WRONG_VALUE_FOR_NORMAL_THRESHOLD, 5, validation.getNormalThreshold(5, 10));
        assertEquals(WRONG_VALUE_FOR_NORMAL_THRESHOLD, 9, validation.getNormalThreshold(9, 10));

        assertEquals(WRONG_VALUE_FOR_NORMAL_THRESHOLD,
                ThresholdValidation.DEFAULT_NORMAL_THRESHOLD, validation.getNormalThreshold(10, 10));
        assertEquals(WRONG_VALUE_FOR_NORMAL_THRESHOLD,
                ThresholdValidation.DEFAULT_NORMAL_THRESHOLD, validation.getNormalThreshold(0, 10));
        assertEquals(WRONG_VALUE_FOR_NORMAL_THRESHOLD,
                ThresholdValidation.DEFAULT_NORMAL_THRESHOLD, validation.getNormalThreshold(-1, 10));
        assertEquals(WRONG_VALUE_FOR_NORMAL_THRESHOLD,
                ThresholdValidation.DEFAULT_NORMAL_THRESHOLD, validation.getNormalThreshold(5, 0));

    }
}

