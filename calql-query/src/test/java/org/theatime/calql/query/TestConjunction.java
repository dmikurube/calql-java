/*
 * Copyright 2024-2026 Dai MIKURUBE
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.theatime.calql.query.date.AfterYear;
import org.theatime.calql.query.date.BeforeYear;
import org.theatime.calql.query.date.EitherDate;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;
import org.theatime.calql.query.date.NeitherDate;
import org.theatime.calql.query.date.NeitherYear;

public class TestConjunction {
    @Test
    public void testYearsNoIntersection() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherYear.of(1970), EitherYear.of(1972), EitherYear.of(1974));
        assertEquals(Range.empty(), c.range());
    }

    @Test
    public void testYearAndDate() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherYear.of(1970), EitherDate.of(1970, 6, 1));
        assertEquals(Range.between(LocalDate.of(1970, 6, 1), LocalDate.of(1970, 6, 1)), c.range());
    }

    @Test
    public void testNoIntersectionYearAndDate() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherYear.of(1970), EitherDate.of(1975, 6, 1));
        assertEquals(Range.empty(), c.range());
    }

    @Test
    public void testYearAndMonth() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherYear.of(1970), EitherMonth.of(6));
        assertEquals(Range.between(LocalDate.of(1970, 1, 1), LocalDate.of(1970, 12, 31)), c.range());
    }

    @Test
    public void testAfterBeforeYear() {
        final Conjunction<LocalDate> c = Conjunction.of(AfterYear.of(1980), BeforeYear.of(1985));
        assertEquals(Range.between(LocalDate.of(1981, 1, 1), LocalDate.of(1984, 12, 31)), c.range());
    }

    @Test
    public void testAfterBeforeOrEqualToYear() {
        final Conjunction<LocalDate> c = Conjunction.of(AfterYear.orEqualTo(1980), BeforeYear.orEqualTo(1985));
        assertEquals(Range.between(LocalDate.of(1980, 1, 1), LocalDate.of(1985, 12, 31)), c.range());
    }

    @Test
    public void testSingleDate() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherDate.of(1982, 3, 4));
        assertEquals(Range.between(LocalDate.of(1982, 3, 4), LocalDate.of(1982, 3, 4)), c.range());
    }

    @Test
    public void testSameDates() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherDate.of(1982, 3, 4), EitherDate.of(1982, 3, 4));
        assertEquals(Range.between(LocalDate.of(1982, 3, 4), LocalDate.of(1982, 3, 4)), c.range());
    }

    @Test
    public void testDatesNoIntersection() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherDate.of(1982, 3, 4), EitherDate.of(1982, 3, 5));
        assertEquals(Range.empty(), c.range());
    }

    @Test
    public void testMultipleYears() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherYear.of(1970, 1972));
        assertEquals(Range.between(LocalDate.of(1970, 1, 1), LocalDate.of(1972, 12, 31)), c.range());
    }

    @Test
    public void testNotYearDoesNotNarrow() {
        final Conjunction<LocalDate> c = Conjunction.of(AfterYear.orEqualTo(1970), NeitherYear.of(1975));
        assertEquals(Range.atLeast(LocalDate.of(1970, 1, 1)), c.range());
    }

    @Test
    public void testNotDateDoesNotNarrow() {
        final Conjunction<LocalDate> c = Conjunction.of(AfterYear.orEqualTo(1970), NeitherDate.of(1970, 1, 5));
        assertEquals(Range.atLeast(LocalDate.of(1970, 1, 1)), c.range());
    }

    @Test
    public void testWith() {
        final Conjunction<LocalDate> c = Conjunction.<LocalDate>of(AfterYear.orEqualTo(1970)).with(BeforeYear.orEqualTo(1972));
        assertEquals(Set.of(AfterYear.orEqualTo(1970), BeforeYear.orEqualTo(1972)), c);
        assertEquals(Range.between(LocalDate.of(1970, 1, 1), LocalDate.of(1972, 12, 31)), c.range());
    }

    @Test
    public void testWithMultiple() {
        final Conjunction<LocalDate> c = Conjunction.<LocalDate>of(EitherYear.of(1970))
                .with(List.of(EitherMonth.of(6), EitherDate.of(1970, 6, 1)));
        assertEquals(Set.of(EitherYear.of(1970), EitherMonth.of(6), EitherDate.of(1970, 6, 1)), c);
        assertEquals(Range.between(LocalDate.of(1970, 6, 1), LocalDate.of(1970, 6, 1)), c.range());
    }

    @Test
    public void testEqualsRegardlessOfOrder() {
        final Conjunction<LocalDate> c1 = Conjunction.of(EitherYear.of(1970), EitherMonth.of(6));
        final Conjunction<LocalDate> c2 = Conjunction.of(EitherMonth.of(6), EitherYear.of(1970));
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void testDuplicatedAtomsAreRemoved() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherYear.of(1970), EitherMonth.of(6), EitherYear.of(1970));
        assertEquals(2, c.size());
        assertEquals(Conjunction.of(EitherYear.of(1970), EitherMonth.of(6)), c);
        assertEquals("(year = 1970 AND month = 6)", c.toString());
    }

    @Test
    public void testDuplicatedAtomsAreRemovedByWith() {
        final Conjunction<LocalDate> c = Conjunction.<LocalDate>of(EitherYear.of(1970)).with(EitherMonth.of(6), EitherYear.of(1970));
        assertEquals(2, c.size());
        assertEquals(Conjunction.of(EitherYear.of(1970), EitherMonth.of(6)), c);
    }

    @Test
    public void testIteratesInOrderOfFirstAppearance() {
        final Conjunction<LocalDate> c = Conjunction.of(
                EitherMonth.of(6), EitherYear.of(1970), EitherMonth.of(6), EitherDate.of(1970, 6, 1));
        assertEquals(List.of(EitherMonth.of(6), EitherYear.of(1970), EitherDate.of(1970, 6, 1)), List.copyOf(c));
        assertEquals("(month = 6 AND year = 1970 AND date = 1970-06-01)", c.toString());
    }

    @Test
    public void testEqualsOtherSets() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherYear.of(1970), EitherMonth.of(6));
        final Set<Atom<LocalDate>> set = Set.of(EitherMonth.of(6), EitherYear.of(1970));
        assertEquals(set, c);
        assertEquals(c, set);
        assertEquals(set.hashCode(), c.hashCode());
        assertTrue(new HashSet<>(List.of(set)).contains(c));
        assertTrue(new HashSet<>(List.of(c)).contains(set));

        // A set never equals a list, even of the same elements.
        assertNotEquals(List.of(EitherYear.of(1970), EitherMonth.of(6)), c);
    }

    @Test
    public void testContains() {
        final Conjunction<LocalDate> c = Conjunction.of(EitherYear.of(1970), EitherMonth.of(6));
        assertTrue(c.contains(EitherYear.of(1970)));
        assertFalse(c.contains(EitherYear.of(1971)));
        assertFalse(c.contains(NeitherYear.of(1970)));
    }
}
