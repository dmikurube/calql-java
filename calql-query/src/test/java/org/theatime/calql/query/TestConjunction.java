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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.theatime.calql.query.date.AfterYear;
import org.theatime.calql.query.date.BeforeYear;
import org.theatime.calql.query.date.EitherDate;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;

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
        final Conjunction<LocalDate> c = Conjunction.of(AfterYear.orEqualTo(1970), EitherYear.notOf(1975));
        assertEquals(Range.atLeast(LocalDate.of(1970, 1, 1)), c.range());
    }

    @Test
    public void testNotDateDoesNotNarrow() {
        final Conjunction<LocalDate> c = Conjunction.of(AfterYear.orEqualTo(1970), EitherDate.notOf(1970, 1, 5));
        assertEquals(Range.atLeast(LocalDate.of(1970, 1, 1)), c.range());
    }

    @Test
    public void testWith() {
        final Conjunction<LocalDate> c = Conjunction.<LocalDate>of(AfterYear.orEqualTo(1970)).with(BeforeYear.orEqualTo(1972));
        assertEquals(List.of(AfterYear.orEqualTo(1970), BeforeYear.orEqualTo(1972)), new ArrayList<>(c));
        assertEquals(Range.between(LocalDate.of(1970, 1, 1), LocalDate.of(1972, 12, 31)), c.range());
    }

    @Test
    public void testWithMultiple() {
        final Conjunction<LocalDate> c = Conjunction.<LocalDate>of(EitherYear.of(1970))
                .with(List.of(EitherMonth.of(6), EitherDate.of(1970, 6, 1)));
        assertEquals(List.of(EitherYear.of(1970), EitherMonth.of(6), EitherDate.of(1970, 6, 1)), new ArrayList<>(c));
        assertEquals(Range.between(LocalDate.of(1970, 6, 1), LocalDate.of(1970, 6, 1)), c.range());
    }
}
