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
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.theatime.calql.query.date.AfterDayOfMonth;
import org.theatime.calql.query.date.AfterYear;
import org.theatime.calql.query.date.BeforeYear;
import org.theatime.calql.query.date.EitherDayOfMonth;
import org.theatime.calql.query.date.EitherDayOfWeek;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;

/**
 * Tests that a formula, its negation normal form, and its disjunctive normal form match the same dates, and that the
 * range of its negation normal form contains all the dates it matches.
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
    public Stream<DynamicTest> testNegateIsNot() {
        return formulae().map(formula -> DynamicTest.dynamicTest(formula.toString(), () -> {
            assertEquals(Not.of(formula), formula.negate());
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testNegateOfNegationNormalFormIsComplement() {
        return formulae().map(formula -> DynamicTest.dynamicTest(formula.toString(), () -> {
            final NegationNormalFormula<LocalDate> nnf = formula.toNegationNormalForm();
            final NegationNormalFormula<LocalDate> negated = nnf.negate();
            for (final LocalDate date : sampleDates()) {
                assertEquals(!nnf.test(date), negated.test(date), date.toString());
            }
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testDisjunctiveNormalFormIsEquivalent() {
        return formulae().map(formula -> DynamicTest.dynamicTest(formula.toString(), () -> {
            final DisjunctiveNormalFormula<LocalDate> dnf = formula.toNegationNormalForm().expandToDisjunctiveNormalForm();
            final DisjunctiveNormalFormula<LocalDate> negatedDnf = formula.negateInNegationNormalForm().expandToDisjunctiveNormalForm();
            for (final LocalDate date : sampleDates()) {
                assertEquals(formula.test(date), testDisjunctiveNormalForm(dnf, date), date.toString());
                assertEquals(!formula.test(date), testDisjunctiveNormalForm(negatedDnf, date), date.toString());
            }
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testRangeContainsAllMatchingDates() {
        return formulae().flatMap(formula -> Stream.of(formula.toNegationNormalForm(), formula.negateInNegationNormalForm()))
                .map(nnf -> DynamicTest.dynamicTest(nnf.toString(), () -> {
                    final Range<LocalDate> range = nnf.range();
                    for (final LocalDate date : sampleDates()) {
                        if (!nnf.test(date)) {
                            continue;
                        }
                        assertTrue(range.contains(date), "range " + range + " does not contain " + date);
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
            for (final Conjunction<LocalDate> conjunction : formula.toNegationNormalForm().expandToDisjunctiveNormalForm()) {
                assertThrows(NullPointerException.class, () -> conjunction.test(null));
            }
        }));
    }

    @Test
    public void testNullFormulaeAreRejected() {
        final Formula<LocalDate> year = EitherYear.of(2000);
        final Atom<LocalDate> atom = EitherYear.of(2000);
        assertThrows(NullPointerException.class, () -> year.and(null));
        assertThrows(NullPointerException.class, () -> year.or(null));
        assertThrows(NullPointerException.class, () -> And.of(year, null));
        assertThrows(NullPointerException.class, () -> Or.of(year, null));
        assertThrows(NullPointerException.class, () -> Not.of(null));
        assertThrows(NullPointerException.class, () -> NegationNormalAnd.of(atom, null));
        assertThrows(NullPointerException.class, () -> NegationNormalOr.of(atom, null));
        assertThrows(NullPointerException.class, () -> Conjunction.of(atom, null));
        assertThrows(NullPointerException.class, () -> Conjunction.of(atom).with(atom, null));
        assertThrows(NullPointerException.class, () -> DisjunctiveNormalFormula.of(Conjunction.of(atom), null));
        assertThrows(NullPointerException.class, () -> DisjunctiveNormalFormula.of(Conjunction.of(atom)).with(Conjunction.of(atom), null));
    }

    @Test
    public void testEmptyAndIsTrue() {
        final Formula<LocalDate> and = And.<LocalDate>of();
        for (final LocalDate date : sampleDates()) {
            assertTrue(and.test(date), date.toString());
        }
        assertEquals(DisjunctiveNormalFormula.of(Conjunction.<LocalDate>of()), and.toNegationNormalForm().expandToDisjunctiveNormalForm());
    }

    @Test
    public void testEmptyOrIsFalse() {
        final Formula<LocalDate> or = Or.<LocalDate>of();
        for (final LocalDate date : sampleDates()) {
            assertFalse(or.test(date), date.toString());
        }
        assertEquals(DisjunctiveNormalFormula.<LocalDate>of(), or.toNegationNormalForm().expandToDisjunctiveNormalForm());
    }

    @Test
    public void testExpansionRemovesDuplicates() {
        final Atom<LocalDate> year = EitherYear.of(1970);
        final Atom<LocalDate> month = EitherMonth.of(6);
        // (year OR month) AND (month OR year) distributes to (year AND month), (year AND year), (month AND month), and
        // (month AND year). (year AND year) is (year), and (month AND year) is a duplicate of (year AND month).
        final DisjunctiveNormalFormula<LocalDate> dnf =
                NegationNormalAnd.of(NegationNormalOr.of(year, month), NegationNormalOr.of(month, year)).expandToDisjunctiveNormalForm();
        assertEquals(Set.of(Conjunction.of(year, month), Conjunction.of(year), Conjunction.of(month)), dnf);
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
    public void testAndOrOfFormulae() {
        final Formula<LocalDate> february = EitherMonth.of(2);
        final Formula<LocalDate> twentyNinth = EitherDayOfMonth.of(29);
        assertEquals(And.of(february, twentyNinth), february.and(twentyNinth));
        assertEquals(Or.of(february, twentyNinth), february.or(twentyNinth));
    }

    @Test
    public void testAndOrWithLambda() {
        final Formula<LocalDate> february = EitherMonth.of(2);
        // A lambda expression is not a formula, so Predicate#and() and Predicate#or() are called instead.
        final Predicate<LocalDate> and = february.and(date -> date.getYear() == 2000);
        final Predicate<LocalDate> or = february.or(date -> date.getYear() == 2000);
        assertFalse(and instanceof Formula);
        assertFalse(or instanceof Formula);
        assertTrue(and.test(LocalDate.of(2000, 2, 1)));
        assertFalse(and.test(LocalDate.of(2021, 2, 1)));
        assertTrue(or.test(LocalDate.of(2000, 3, 1)));
        assertFalse(or.test(LocalDate.of(2021, 3, 1)));
    }

    @Test
    public void testFilterByFormula() {
        // A formula is a Predicate, so it can be given to Stream#filter() as is.
        final Formula<LocalDate> leapDay = And.of(EitherMonth.of(2), EitherDayOfMonth.of(29));
        assertEquals(List.of(LocalDate.of(2000, 2, 29)), sampleDates().stream().filter(leapDay).collect(Collectors.toList()));
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
        formulae.add(Or.of(And.of(AfterYear.of(1999), EitherMonth.of(2)), BeforeYear.orEqualTo(1999)));
        formulae.add(And.of(Or.of(EitherYear.of(1999), EitherYear.of(2021)), Not.of(BeforeYear.of(2000))));

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
