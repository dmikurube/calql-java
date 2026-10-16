/*
 * Copyright 2021-2026 Dai MIKURUBE
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.theatime.calql.query.date.AfterDayOfMonth;
import org.theatime.calql.query.date.EitherDayOfMonth;
import org.theatime.calql.query.date.EitherDayOfWeek;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;

/**
 * Tests that a formula, its negation normal form, and its disjunctive normal form match the same dates.
 */
public class TestFormula {
    @TestFactory
    public Stream<DynamicTest> testNegationNormalFormIsEquivalent() {
        return formulae().map(formula -> DynamicTest.dynamicTest(formula.toString(), () -> {
            final NegationNormalFormula<LocalDate> nnf = formula.toNegationNormalForm();
            for (final LocalDate date : sampleDates()) {
                assertEquals(formula.test(date), nnf.test(date), date.toString());
            }
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testNegationInNegationNormalFormIsComplement() {
        return formulae().map(formula -> DynamicTest.dynamicTest(formula.toString(), () -> {
            final NegationNormalFormula<LocalDate> negated = formula.negateInNegationNormalForm();
            for (final LocalDate date : sampleDates()) {
                assertEquals(!formula.test(date), negated.test(date), date.toString());
            }
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testNotIsComplement() {
        return formulae().map(formula -> DynamicTest.dynamicTest(formula.toString(), () -> {
            final Formula<LocalDate> not = Not.of(formula);
            for (final LocalDate date : sampleDates()) {
                assertEquals(!formula.test(date), not.test(date), date.toString());
            }
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testDisjunctiveNormalFormIsEquivalent() {
        return formulae().map(formula -> DynamicTest.dynamicTest(formula.toString(), () -> {
            final DisjunctiveNormalFormula<LocalDate> dnf = formula.toNegationNormalForm().getDisjunctiveNormalForm();
            final DisjunctiveNormalFormula<LocalDate> negatedDnf = formula.negateInNegationNormalForm().getDisjunctiveNormalForm();
            for (final LocalDate date : sampleDates()) {
                assertEquals(formula.test(date), testDisjunctiveNormalForm(dnf, date), date.toString());
                assertEquals(!formula.test(date), testDisjunctiveNormalForm(negatedDnf, date), date.toString());
            }
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testTestingNullThrows() {
        return formulae().map(formula -> DynamicTest.dynamicTest(formula.toString(), () -> {
            assertThrows(NullPointerException.class, () -> formula.test(null));
            assertThrows(NullPointerException.class, () -> formula.toNegationNormalForm().test(null));
            assertThrows(NullPointerException.class, () -> formula.negateInNegationNormalForm().test(null));
            assertThrows(NullPointerException.class, () -> Not.of(formula).test(null));
            for (final Conjunction<LocalDate> conjunction : formula.toNegationNormalForm().getDisjunctiveNormalForm()) {
                assertThrows(NullPointerException.class, () -> conjunction.test(null));
            }
        }));
    }

    @Test
    public void testEmptyAndIsTrue() {
        final Formula<LocalDate> and = And.<LocalDate>of();
        for (final LocalDate date : sampleDates()) {
            assertTrue(and.test(date), date.toString());
        }
        assertEquals(DisjunctiveNormalFormula.of(Conjunction.<LocalDate>of()), and.toNegationNormalForm().getDisjunctiveNormalForm());
    }

    @Test
    public void testEmptyOrIsFalse() {
        final Formula<LocalDate> or = Or.<LocalDate>of();
        for (final LocalDate date : sampleDates()) {
            assertFalse(or.test(date), date.toString());
        }
        assertEquals(DisjunctiveNormalFormula.<LocalDate>of(), or.toNegationNormalForm().getDisjunctiveNormalForm());
    }

    @Test
    public void testAnd() {
        final Formula<LocalDate> and = And.of(EitherMonth.of(2), EitherDayOfMonth.of(29));
        assertTrue(and.test(LocalDate.of(2000, 2, 29)));
        assertFalse(and.test(LocalDate.of(2000, 2, 28)));
        assertFalse(and.test(LocalDate.of(2000, 3, 29)));
    }

    @Test
    public void testOr() {
        final Formula<LocalDate> or = Or.of(EitherMonth.of(2), EitherDayOfMonth.of(29));
        assertTrue(or.test(LocalDate.of(2000, 2, 28)));
        assertTrue(or.test(LocalDate.of(2000, 3, 29)));
        assertFalse(or.test(LocalDate.of(2000, 3, 28)));
    }

    @Test
    public void testNot() {
        final Formula<LocalDate> not = Not.of(EitherMonth.of(2));
        assertFalse(not.test(LocalDate.of(2000, 2, 28)));
        assertTrue(not.test(LocalDate.of(2000, 3, 28)));
    }

    private static boolean testDisjunctiveNormalForm(final DisjunctiveNormalFormula<LocalDate> dnf, final LocalDate date) {
        for (final Conjunction<LocalDate> conjunction : dnf) {
            if (conjunction.test(date)) {
                return true;
            }
        }
        return false;
    }

    private static Stream<Formula<LocalDate>> formulae() {
        final ArrayList<Formula<LocalDate>> formulae = new ArrayList<>();

        formulae.add(And.of(
                Not.of(EitherDayOfMonth.of(29)),
                Or.of(Not.of(EitherMonth.of(12)), EitherMonth.of(11)),
                Or.of(Not.of(EitherYear.of(1999)), And.of(EitherMonth.of(10), Not.of(EitherYear.of(2021))))));
        formulae.add(And.of(EitherDayOfMonth.of(29), EitherMonth.of(11), EitherYear.of(1999)));
        formulae.add(Or.of(EitherMonth.of(2), EitherDayOfWeek.of(DayOfWeek.SUNDAY)));
        formulae.add(Not.of(Not.of(EitherMonth.of(2))));
        formulae.add(Not.of(And.of(EitherYear.of(2000), Or.of(EitherMonth.of(1), Not.of(AfterDayOfMonth.of(15))))));
        formulae.add(Or.of(And.of(EitherMonth.of(1), EitherDayOfMonth.of(1)), And.of(EitherMonth.of(12), EitherDayOfMonth.of(31))));
        formulae.add(And.of(EitherMonth.of(2)));
        formulae.add(Or.of(EitherMonth.of(2)));
        formulae.add(And.<LocalDate>of());
        formulae.add(Or.<LocalDate>of());
        formulae.add(And.of(EitherMonth.of(2), Or.<LocalDate>of()));
        formulae.add(Or.of(EitherMonth.of(2), And.<LocalDate>of()));

        return formulae.stream();
    }

    /**
     * Returns every date in 1999, 2000, and 2021, which covers the years used in {@link #formulae()}, and a leap year.
     */
    private static List<LocalDate> sampleDates() {
        final ArrayList<LocalDate> dates = new ArrayList<>();
        for (final int year : new int[] { 1999, 2000, 2021 }) {
            for (LocalDate date = LocalDate.of(year, 1, 1); date.getYear() == year; date = date.plusDays(1)) {
                dates.add(date);
            }
        }
        return dates;
    }
}
