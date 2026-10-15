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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.theatime.calql.query.Atom;

/**
 * Tests the properties that every date atom must satisfy, both for itself and for its negation.
 */
public class TestDateAtomProperties {
    @TestFactory
    public Stream<DynamicTest> testNegationIsInvolution() {
        return atomsAndNegations().map(atom -> DynamicTest.dynamicTest(atom.toString(), () -> {
            assertEquals(atom, atom.negate().negate());
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testNegationIsNotIdentity() {
        return atomsAndNegations().map(atom -> DynamicTest.dynamicTest(atom.toString(), () -> {
            assertNotEquals(atom, atom.negate());
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testNegationIsComplement() {
        return atomsAndNegations().map(atom -> DynamicTest.dynamicTest(atom.toString(), () -> {
            final Atom<LocalDate> negated = atom.negate();
            for (final LocalDate date : sampleDates()) {
                assertEquals(!atom.test(date), negated.test(date), date.toString());
            }
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testBoundsContainAllMatchingDates() {
        return atomsAndNegations().map(atom -> DynamicTest.dynamicTest(atom.toString(), () -> {
            final Optional<? extends LocalDate> earliest = atom.earliest();
            final Optional<? extends LocalDate> latest = atom.latest();
            final Optional<? extends LocalDate> unique = atom.unique();
            for (final LocalDate date : sampleDates()) {
                if (!atom.test(date)) {
                    continue;
                }
                assertTrue(earliest.map(e -> !date.isBefore(e)).orElse(true), "earliest " + earliest + " is after " + date);
                assertTrue(latest.map(l -> !date.isAfter(l)).orElse(true), "latest " + latest + " is before " + date);
                assertTrue(unique.map(u -> date.isEqual(u)).orElse(true), "unique " + unique + " is not " + date);
            }
        }));
    }

    private static Stream<Atom<LocalDate>> atomsAndNegations() {
        return atoms().stream().flatMap(atom -> Stream.of(atom, atom.negate()));
    }

    private static List<Atom<LocalDate>> atoms() {
        final ArrayList<Atom<LocalDate>> atoms = new ArrayList<>();

        atoms.add(AfterYear.of(2020));
        atoms.add(AfterYear.orEqualTo(2020));
        atoms.add(BeforeYear.of(2021));
        atoms.add(BeforeYear.orEqualTo(2021));
        atoms.add(AfterMonth.of(6));
        atoms.add(AfterMonth.orEqualTo(6));
        atoms.add(BeforeMonth.of(6));
        atoms.add(BeforeMonth.orEqualTo(6));
        atoms.add(AfterDayOfMonth.of(15));
        atoms.add(AfterDayOfMonth.orEqualTo(15));
        atoms.add(BeforeDayOfMonth.of(15));
        atoms.add(BeforeDayOfMonth.orEqualTo(15));

        atoms.add(EitherYear.of(2020));
        atoms.add(EitherYear.notOf(2020));
        atoms.add(EitherYear.of(2020, 2022));
        atoms.add(EitherYear.notOf(2020, 2022));

        atoms.add(EitherMonth.of(2));
        atoms.add(EitherMonth.notOf(2));
        atoms.add(EitherMonth.of(2, 12));
        atoms.add(EitherMonth.notOf(2, 12));

        atoms.add(EitherDayOfMonth.of(29));
        atoms.add(EitherDayOfMonth.notOf(29));
        atoms.add(EitherDayOfMonth.of(1, 31));
        atoms.add(EitherDayOfMonth.notOf(1, 31));

        atoms.add(EitherDayOfWeek.of(DayOfWeek.WEDNESDAY));
        atoms.add(EitherDayOfWeek.notOf(DayOfWeek.WEDNESDAY));
        atoms.add(EitherDayOfWeek.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
        atoms.add(EitherDayOfWeek.notOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));

        atoms.add(EitherDate.of(2020, 2, 29));
        atoms.add(EitherDate.notOf(2020, 2, 29));
        atoms.add(EitherDate.of(LocalDate.of(2019, 12, 31), LocalDate.of(2021, 3, 1)));
        atoms.add(EitherDate.notOf(LocalDate.of(2019, 12, 31), LocalDate.of(2021, 3, 1)));

        return atoms;
    }

    /**
     * Returns every date from 2018-01-01 to 2023-12-31, which covers the years and the dates used in {@link #atoms()}.
     */
    private static List<LocalDate> sampleDates() {
        final ArrayList<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = LocalDate.of(2018, 1, 1); date.isBefore(LocalDate.of(2024, 1, 1)); date = date.plusDays(1)) {
            dates.add(date);
        }
        return dates;
    }
}
