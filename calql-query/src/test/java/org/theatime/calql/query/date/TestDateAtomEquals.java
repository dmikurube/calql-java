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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Object#equals(Object)} and {@link Object#hashCode()} of date atoms, which their base classes implement.
 */
public class TestDateAtomEquals {
    @Test
    public void testEqualMemberships() {
        assertEqualAtoms(EitherYear.of(2020, 2022), EitherYear.of(List.of(2022, 2020)));
        assertEqualAtoms(EitherMonth.notOf(2, 12), EitherMonth.of(12, 2).negate());
        assertEqualAtoms(EitherDayOfMonth.of(1), EitherDayOfMonth.of(List.of(1)));
        assertEqualAtoms(EitherDayOfWeek.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), EitherDayOfWeek.of(List.of(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY)));
        assertEqualAtoms(EitherDate.of(2020, 2, 29), EitherDate.of(LocalDate.of(2020, 2, 29)));
    }

    @Test
    public void testEqualOrderings() {
        assertEqualAtoms(AfterYear.orEqualTo(2020), AfterYear.of(2020, true));
        assertEqualAtoms(BeforeMonth.of(6), AfterMonth.orEqualTo(6).negate());
        assertEqualAtoms(AfterDayOfMonth.of(15), BeforeDayOfMonth.orEqualTo(15).negate());
    }

    @Test
    public void testMembershipsOfDifferentColumns() {
        assertNotEquals(EitherYear.of(2), EitherMonth.of(2));
        assertNotEquals(EitherMonth.of(2), EitherDayOfMonth.of(2));
        assertNotEquals(EitherDayOfMonth.of(2), EitherMonth.of(2));
    }

    @Test
    public void testOrderingsOfDifferentColumnsOrDirections() {
        assertNotEquals(AfterYear.of(6), AfterMonth.of(6));
        assertNotEquals(AfterMonth.of(6), AfterDayOfMonth.of(6));
        assertNotEquals(AfterMonth.of(6), BeforeMonth.of(6));
        assertNotEquals(BeforeMonth.orEqualTo(6), AfterMonth.orEqualTo(6));
    }

    @Test
    public void testDifferentInclusionOrInclusiveness() {
        assertNotEquals(EitherMonth.of(2), EitherMonth.notOf(2));
        assertNotEquals(AfterMonth.of(6), AfterMonth.orEqualTo(6));
    }

    private static void assertEqualAtoms(final DateAtom expected, final DateAtom actual) {
        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }
}
