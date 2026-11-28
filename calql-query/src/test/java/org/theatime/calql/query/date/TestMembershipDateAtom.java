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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Tests the pairs of the "either" and the "neither" date atoms, which share {@link MembershipDateAtom}.
 */
public class TestMembershipDateAtom {
    @Test
    public void testIsNeither() {
        assertFalse(EitherYear.of(2020).isNeither());
        assertTrue(NeitherYear.of(2020).isNeither());
        assertFalse(EitherMonth.of(2).isNeither());
        assertTrue(NeitherMonth.of(2).isNeither());
        assertFalse(EitherDayOfMonth.of(29).isNeither());
        assertTrue(NeitherDayOfMonth.of(29).isNeither());
        assertFalse(EitherDayOfWeek.of(DayOfWeek.WEDNESDAY).isNeither());
        assertTrue(NeitherDayOfWeek.of(DayOfWeek.WEDNESDAY).isNeither());
        assertFalse(EitherDate.of(2020, 2, 29).isNeither());
        assertTrue(NeitherDate.of(2020, 2, 29).isNeither());
    }

    @Test
    public void testNegationSwitchesEitherAndNeither() {
        // The atoms of different classes never equal, so that it also tests the class of the negation.
        assertEquals(NeitherYear.of(2020, 2022), EitherYear.of(2020, 2022).negate());
        assertEquals(EitherYear.of(2020, 2022), NeitherYear.of(2020, 2022).negate());
        assertEquals(NeitherMonth.of(2, 12), EitherMonth.of(2, 12).negate());
        assertEquals(EitherMonth.of(2, 12), NeitherMonth.of(2, 12).negate());
        assertEquals(NeitherDayOfMonth.of(1, 31), EitherDayOfMonth.of(1, 31).negate());
        assertEquals(EitherDayOfMonth.of(1, 31), NeitherDayOfMonth.of(1, 31).negate());
        assertEquals(NeitherDayOfWeek.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), EitherDayOfWeek.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).negate());
        assertEquals(EitherDayOfWeek.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), NeitherDayOfWeek.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).negate());
        assertEquals(NeitherDate.of(LocalDate.of(2019, 12, 31), LocalDate.of(2021, 3, 1)), EitherDate.of(LocalDate.of(2019, 12, 31), LocalDate.of(2021, 3, 1)).negate());
        assertEquals(EitherDate.of(LocalDate.of(2019, 12, 31), LocalDate.of(2021, 3, 1)), NeitherDate.of(LocalDate.of(2019, 12, 31), LocalDate.of(2021, 3, 1)).negate());
    }

    @Test
    public void testToString() {
        assertEquals("month = 2", EitherMonth.of(2).toString());
        assertEquals("month <> 2", NeitherMonth.of(2).toString());
        assertEquals("month in [2, 12]", EitherMonth.of(12, 2).toString());
        assertEquals("month not in [2, 12]", NeitherMonth.of(12, 2).toString());
    }
}
