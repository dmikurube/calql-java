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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.theatime.calql.query.date.EitherDayOfMonth;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;

public class TestNegationNormalCompound {
    @Test
    public void testOperandsOfAndAreAsWritten() {
        final NegationNormalCompound<LocalDate> and = (NegationNormalCompound<LocalDate>) NegationNormalAnd.of(MONTH, YEAR, MONTH);
        assertEquals(List.of(MONTH, YEAR, MONTH), and.operands());
    }

    @Test
    public void testOperandsOfOrAreAsWritten() {
        final NegationNormalCompound<LocalDate> or = (NegationNormalCompound<LocalDate>) NegationNormalOr.of(MONTH, YEAR, MONTH);
        assertEquals(List.of(MONTH, YEAR, MONTH), or.operands());
    }

    @Test
    public void testOperandsOfEmpty() {
        assertEquals(List.of(), ((NegationNormalCompound<LocalDate>) NegationNormalAnd.<LocalDate>of()).operands());
        assertEquals(List.of(), ((NegationNormalCompound<LocalDate>) NegationNormalOr.<LocalDate>of()).operands());
    }

    @Test
    public void testOperandsAreNotFlattened() {
        final NegationNormalFormula<LocalDate> inner = NegationNormalOr.of(YEAR, MONTH);
        final NegationNormalCompound<LocalDate> outer = (NegationNormalCompound<LocalDate>) NegationNormalOr.of(inner, DAY);
        assertEquals(List.of(inner, DAY), outer.operands());
    }

    @Test
    public void testOperandsKeepTheNestingOfFormula() {
        // f1.and(f2).and(f3) is And(And(f1, f2), f3), and its negation normal form keeps the nesting.
        final Formula<LocalDate> formula = YEAR.and(MONTH).and(DAY);
        final NegationNormalCompound<LocalDate> nnf = (NegationNormalCompound<LocalDate>) formula.toNegationNormalForm();
        assertEquals(List.of(NegationNormalAnd.of(YEAR, MONTH), DAY), nnf.operands());
    }

    @Test
    public void testOperandsAreUnmodifiable() {
        final NegationNormalCompound<LocalDate> and = (NegationNormalCompound<LocalDate>) NegationNormalAnd.of(YEAR);
        assertThrows(UnsupportedOperationException.class, () -> and.operands().add(MONTH));
        final NegationNormalCompound<LocalDate> or = (NegationNormalCompound<LocalDate>) NegationNormalOr.of(YEAR);
        assertThrows(UnsupportedOperationException.class, () -> or.operands().add(MONTH));
    }

    @Test
    public void testOperandsAreCopied() {
        final ArrayList<NegationNormalFormula<LocalDate>> given = new ArrayList<>(List.of(YEAR));
        final NegationNormalCompound<LocalDate> and = (NegationNormalCompound<LocalDate>) NegationNormalAnd.of(given);
        final NegationNormalCompound<LocalDate> or = (NegationNormalCompound<LocalDate>) NegationNormalOr.of(given);
        given.add(MONTH);
        assertEquals(List.of(YEAR), and.operands());
        assertEquals(List.of(YEAR), or.operands());
    }

    private static final Atom<LocalDate> YEAR = EitherYear.of(2000);
    private static final Atom<LocalDate> MONTH = EitherMonth.of(2);
    private static final Atom<LocalDate> DAY = EitherDayOfMonth.of(29);
}
