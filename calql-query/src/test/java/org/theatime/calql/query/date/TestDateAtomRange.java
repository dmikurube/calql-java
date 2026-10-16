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

package org.theatime.calql.query.date;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.theatime.calql.query.Range;

/**
 * Tests the exact ranges of date atoms.
 *
 * <p>{@link TestDateAtomProperties} tests that the ranges contain all the matching dates. This tests that the ranges
 * are not wider than expected.
 */
public class TestDateAtomRange {
    @Test
    public void testAfterYear() {
        assertEquals(Range.atLeast(LocalDate.of(2021, 1, 1)), AfterYear.of(2020).range());
        assertEquals(Range.atLeast(LocalDate.of(2020, 1, 1)), AfterYear.orEqualTo(2020).range());
    }

    @Test
    public void testBeforeYear() {
        assertEquals(Range.atMost(LocalDate.of(2019, 12, 31)), BeforeYear.of(2020).range());
        assertEquals(Range.atMost(LocalDate.of(2020, 12, 31)), BeforeYear.orEqualTo(2020).range());
    }

    @Test
    public void testEitherYear() {
        assertEquals(Range.between(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31)), EitherYear.of(2020).range());
        assertEquals(Range.between(LocalDate.of(2018, 1, 1), LocalDate.of(2022, 12, 31)), EitherYear.of(2022, 2018, 2020).range());
        assertEquals(Range.whole(), EitherYear.notOf(2020).range());
    }

    @Test
    public void testEitherYearOfNone() {
        assertEquals(Range.empty(), EitherYear.of(List.of()).range());
        assertEquals(Range.whole(), EitherYear.notOf(List.of()).range());
    }

    @Test
    public void testEitherDate() {
        final LocalDate date = LocalDate.of(2020, 2, 29);
        assertEquals(Range.between(date, date), EitherDate.of(date).range());
        assertEquals(Range.between(LocalDate.of(2019, 12, 31), LocalDate.of(2021, 3, 1)),
                     EitherDate.of(LocalDate.of(2021, 3, 1), LocalDate.of(2019, 12, 31), date).range());
        assertEquals(Range.whole(), EitherDate.notOf(date).range());
    }

    @Test
    public void testEitherDateOfNone() {
        assertEquals(Range.empty(), EitherDate.of(List.of()).range());
        assertEquals(Range.whole(), EitherDate.notOf(List.of()).range());
    }

    @Test
    public void testAtomsThatDoNotNarrow() {
        assertEquals(Range.whole(), AfterMonth.of(6).range());
        assertEquals(Range.whole(), BeforeMonth.of(6).range());
        assertEquals(Range.whole(), AfterDayOfMonth.of(15).range());
        assertEquals(Range.whole(), BeforeDayOfMonth.of(15).range());
        assertEquals(Range.whole(), EitherMonth.of(2).range());
        assertEquals(Range.whole(), EitherDayOfMonth.of(29).range());
        assertEquals(Range.whole(), EitherDayOfWeek.of(DayOfWeek.WEDNESDAY).range());
    }
}
