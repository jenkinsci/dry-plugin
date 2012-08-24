package hudson.plugins.dry.parser;

import static org.junit.Assert.*;

import java.util.Collection;

/**
 * Base class for duplication tests.
 *
 * @author Ulli Hafner
 */
public class AbstractDuplicationParserTest {
    /**
     * Verifies that the number of derived duplications is correct.
     *
     * @param annotations
     *            the annotations to check
     * @param expected
     *            the expected number of derived duplications
     */
    protected void verifyDerivedDuplications(final Collection<DuplicateCode> annotations, final int expected) {
        int derivedCount = 0;
        for (DuplicateCode duplicateCode : annotations) {
            if (duplicateCode.isDerived()) {
                derivedCount++;
            }
        }
        assertEquals("Wrong number of derived duplications",  expected, derivedCount);
    }
}
