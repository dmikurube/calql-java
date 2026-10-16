/*
 * Copyright 2026 Dai MIKURUBE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.theatime.calql.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class TestRange {
    @Test
    public void testEmpty() {
        final Range<Integer> empty = Range.empty();
        assertTrue(empty.isEmpty());
        assertFalse(empty.contains(0));
        assertFalse(empty.contains(Integer.MIN_VALUE));
        assertFalse(empty.contains(Integer.MAX_VALUE));
        assertThrows(IllegalStateException.class, () -> empty.lower());
        assertThrows(IllegalStateException.class, () -> empty.upper());
    }

    @Test
    public void testBetweenReversedIsEmpty() {
        assertEquals(Range.empty(), Range.between(5, 3));
        assertEquals(Range.<Integer>empty().hashCode(), Range.between(5, 3).hashCode());
        assertTrue(Range.between(5, 3).isEmpty());
    }

    @Test
    public void testWhole() {
        final Range<Integer> whole = Range.whole();
        assertFalse(whole.isEmpty());
        assertTrue(whole.contains(0));
        assertTrue(whole.contains(Integer.MIN_VALUE));
        assertTrue(whole.contains(Integer.MAX_VALUE));
        assertEquals(Optional.empty(), whole.lower());
        assertEquals(Optional.empty(), whole.upper());
    }

    @Test
    public void testAtLeast() {
        final Range<Integer> range = Range.atLeast(3);
        assertFalse(range.contains(2));
        assertTrue(range.contains(3));
        assertTrue(range.contains(Integer.MAX_VALUE));
        assertEquals(Optional.of(3), range.lower());
        assertEquals(Optional.empty(), range.upper());
    }

    @Test
    public void testAtMost() {
        final Range<Integer> range = Range.atMost(3);
        assertTrue(range.contains(Integer.MIN_VALUE));
        assertTrue(range.contains(3));
        assertFalse(range.contains(4));
        assertEquals(Optional.empty(), range.lower());
        assertEquals(Optional.of(3), range.upper());
    }

    @Test
    public void testBetween() {
        final Range<Integer> range = Range.between(3, 5);
        assertFalse(range.contains(2));
        assertTrue(range.contains(3));
        assertTrue(range.contains(5));
        assertFalse(range.contains(6));
        assertEquals(Optional.of(3), range.lower());
        assertEquals(Optional.of(5), range.upper());
    }

    @Test
    public void testBetweenSameEnds() {
        final Range<Integer> range = Range.between(3, 3);
        assertFalse(range.contains(2));
        assertTrue(range.contains(3));
        assertFalse(range.contains(4));
    }

    @Test
    public void testIntersection() {
        assertEquals(Range.between(4, 5), Range.between(3, 5).intersection(Range.between(4, 6)));
        assertEquals(Range.between(4, 5), Range.between(4, 6).intersection(Range.between(3, 5)));
        assertEquals(Range.between(4, 5), Range.between(3, 6).intersection(Range.between(4, 5)));
        assertEquals(Range.between(5, 5), Range.between(3, 5).intersection(Range.between(5, 7)));
        assertEquals(Range.empty(), Range.between(3, 5).intersection(Range.between(6, 7)));
    }

    @Test
    public void testIntersectionWithUnbounded() {
        assertEquals(Range.between(3, 5), Range.atLeast(3).intersection(Range.atMost(5)));
        assertEquals(Range.atLeast(5), Range.atLeast(3).intersection(Range.atLeast(5)));
        assertEquals(Range.atMost(3), Range.atMost(3).intersection(Range.atMost(5)));
        assertEquals(Range.empty(), Range.atLeast(5).intersection(Range.atMost(3)));
        assertEquals(Range.between(3, 5), Range.between(3, 5).intersection(Range.whole()));
        assertEquals(Range.between(3, 5), Range.<Integer>whole().intersection(Range.between(3, 5)));
    }

    @Test
    public void testIntersectionWithEmpty() {
        assertEquals(Range.empty(), Range.between(3, 5).intersection(Range.empty()));
        assertEquals(Range.empty(), Range.<Integer>empty().intersection(Range.between(3, 5)));
        assertEquals(Range.empty(), Range.<Integer>whole().intersection(Range.empty()));
    }

    @Test
    public void testSpan() {
        assertEquals(Range.between(3, 6), Range.between(3, 5).span(Range.between(4, 6)));
        assertEquals(Range.between(3, 6), Range.between(3, 6).span(Range.between(4, 5)));
        // Not the union: the values between the two ranges are included.
        assertEquals(Range.between(1, 9), Range.between(1, 2).span(Range.between(8, 9)));
        assertEquals(Range.between(1, 9), Range.between(8, 9).span(Range.between(1, 2)));
    }

    @Test
    public void testSpanWithUnbounded() {
        assertEquals(Range.atLeast(3), Range.atLeast(5).span(Range.between(3, 4)));
        assertEquals(Range.atMost(5), Range.atMost(3).span(Range.between(4, 5)));
        assertEquals(Range.whole(), Range.atLeast(5).span(Range.atMost(3)));
        assertEquals(Range.whole(), Range.between(3, 5).span(Range.whole()));
    }

    @Test
    public void testSpanWithEmpty() {
        assertEquals(Range.between(3, 5), Range.between(3, 5).span(Range.empty()));
        assertEquals(Range.between(3, 5), Range.<Integer>empty().span(Range.between(3, 5)));
        assertEquals(Range.empty(), Range.<Integer>empty().span(Range.empty()));
    }

    @Test
    public void testEquals() {
        assertEquals(Range.between(3, 5), Range.between(3, 5));
        assertEquals(Range.between(3, 5).hashCode(), Range.between(3, 5).hashCode());
        assertEquals(Range.whole(), Range.whole());
        assertNotEquals(Range.between(3, 5), Range.between(3, 6));
        assertNotEquals(Range.atLeast(3), Range.atMost(3));
        assertNotEquals(Range.whole(), Range.empty());
        assertFalse(Range.between(3, 5).equals(null));
    }

    @Test
    public void testNullEnds() {
        assertThrows(NullPointerException.class, () -> Range.atLeast(null));
        assertThrows(NullPointerException.class, () -> Range.atMost(null));
        assertThrows(NullPointerException.class, () -> Range.between(null, 3));
        assertThrows(NullPointerException.class, () -> Range.between(3, null));
        assertThrows(NullPointerException.class, () -> Range.between(3, 5).contains(null));
    }

    @Test
    public void testToString() {
        assertEquals("[3, 5]", Range.between(3, 5).toString());
        assertEquals("[3, Infinity)", Range.atLeast(3).toString());
        assertEquals("(-Infinity, 5]", Range.atMost(5).toString());
        assertEquals("(-Infinity, Infinity)", Range.whole().toString());
        assertEquals("[]", Range.empty().toString());
    }

    @Test
    public void testLocalDate() {
        // LocalDate implements Comparable<ChronoLocalDate>, not Comparable<LocalDate>.
        final Range<LocalDate> year2020 = Range.between(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
        assertTrue(year2020.contains(LocalDate.of(2020, 2, 29)));
        assertFalse(year2020.contains(LocalDate.of(2021, 1, 1)));
        assertEquals(Range.between(LocalDate.of(2020, 12, 31), LocalDate.of(2020, 12, 31)),
                     year2020.intersection(Range.atLeast(LocalDate.of(2020, 12, 31))));
    }
}
