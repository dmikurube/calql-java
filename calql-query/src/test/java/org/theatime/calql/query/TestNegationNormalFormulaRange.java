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

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.theatime.calql.query.date.AfterYear;
import org.theatime.calql.query.date.BeforeYear;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;

public class TestNegationNormalFormulaRange {
    @Test
    public void testAndIsIntersection() {
        final Formula<LocalDate> and = And.of(AfterYear.orEqualTo(2000), BeforeYear.of(2010), EitherMonth.of(2));
        assertEquals(Range.between(LocalDate.of(2000, 1, 1), LocalDate.of(2009, 12, 31)), and.toNegationNormalForm().range());
    }

    @Test
    public void testOrIsSpan() {
        final Formula<LocalDate> or = Or.of(EitherYear.of(2010), EitherYear.of(2000));
        assertEquals(Range.between(LocalDate.of(2000, 1, 1), LocalDate.of(2010, 12, 31)), or.toNegationNormalForm().range());
    }

    @Test
    public void testNested() {
        final Formula<LocalDate> formula = Or.of(And.of(EitherYear.of(2000), EitherMonth.of(2)), BeforeYear.of(1990));
        assertEquals(Range.atMost(LocalDate.of(2000, 12, 31)), formula.toNegationNormalForm().range());
    }

    @Test
    public void testNegated() {
        // NOT (year = 2000 OR year = 2010) is (year <> 2000 AND year <> 2010), which cannot be narrowed down.
        final Formula<LocalDate> formula = Or.of(EitherYear.of(2000), EitherYear.of(2010));
        assertEquals(Range.whole(), formula.negateInNegationNormalForm().range());

        // NOT (year < 2000 OR year > 2010) is (year >= 2000 AND year <= 2010).
        final Formula<LocalDate> outside = Or.of(BeforeYear.of(2000), AfterYear.of(2010));
        assertEquals(Range.between(LocalDate.of(2000, 1, 1), LocalDate.of(2010, 12, 31)), outside.negateInNegationNormalForm().range());
    }

    @Test
    public void testEmptyAndIsWhole() {
        assertEquals(Range.whole(), And.<LocalDate>of().toNegationNormalForm().range());
        assertEquals(Range.whole(), Or.of(EitherYear.of(2000), And.<LocalDate>of()).toNegationNormalForm().range());
    }

    @Test
    public void testEmptyOrIsEmpty() {
        assertEquals(Range.empty(), Or.<LocalDate>of().toNegationNormalForm().range());
        assertEquals(Range.empty(), And.of(EitherYear.of(2000), Or.<LocalDate>of()).toNegationNormalForm().range());
    }
}
