package org.xbib.content.json.pointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public final class ReferenceTokenTest {

    @Test
    public void nullCookedRaisesError() {
        Assertions.assertThrows(NullPointerException.class, () ->
            ReferenceToken.fromCooked(null));
    }

    @Test
    public void nullRawRaisesError() {
        Assertions.assertThrows(NullPointerException.class, () ->
            ReferenceToken.fromRaw(null));
    }

    @Test
    public void emptyEscapeRaisesTheAppropriateException() {
        Assertions.assertThrows(JsonPointerException.class, () ->
            ReferenceToken.fromCooked("whatever~"));
    }

    @Test
    public void illegalEscapeRaisesTheAppropriateException() {
        Assertions.assertThrows(JsonPointerException.class, () ->
            ReferenceToken.fromCooked("~a"));
    }

    @Test
    public void fromCookedOrFromRawYieldsSameResults()
            throws JsonPointerException {
        List<Object[]> list = Arrays.asList(
                new Object[]{"~0", "~"},
                new Object[]{"~1", "/"},
                new Object[]{"", ""},
                new Object[]{"~0user", "~user"},
                new Object[]{"foobar", "foobar"},
                new Object[]{"~1var~1lib~1mysql", "/var/lib/mysql"}
        );
        for (Object[] o : list) {
            String cooked = (String) o[0];
            String raw = (String) o[1];
            final ReferenceToken token1 = ReferenceToken.fromCooked(cooked);
            final ReferenceToken token2 = ReferenceToken.fromRaw(raw);
            assertEquals(token1, token2);
            assertEquals(token2.toString(), cooked);
        }
    }

    @Test
    public void fromIndexOrStringYieldsSameResults()
            throws JsonPointerException {
        List<Object[]> list = Arrays.asList(
                new Object[]{0, "0"},
                new Object[]{-1, "-1"},
                new Object[]{13, "13"}
        );
        for (Object[] o : list) {
            int index = (int) o[0];
            String asString = (String) o[1];
            final ReferenceToken fromInt = ReferenceToken.fromInt(index);
            final ReferenceToken cooked = ReferenceToken.fromCooked(asString);
            final ReferenceToken raw = ReferenceToken.fromRaw(asString);
            assertEquals(fromInt, cooked);
            assertEquals(cooked, raw);
            assertEquals(raw, fromInt);
            assertEquals(fromInt.toString(), asString);
        }
    }

    @Test
    public void zeroAndZeroZeroAreNotTheSame()
            throws JsonPointerException {
        final ReferenceToken zero = ReferenceToken.fromCooked("0");
        final ReferenceToken zerozero = ReferenceToken.fromCooked("00");
        assertNotEquals(zero, zerozero);
    }
}
