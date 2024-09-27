/*
 * Copyright 2021-2024 Dai MIKURUBE
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

package org.theatime.calql.query.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.theatime.calql.query.date.EitherDayOfMonth;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;

public class TestFormula {
    @Test
    public void test() {
        final Formula and = And.of(
                Not.of(EitherDayOfMonth.of(29)),
                Or.of(Not.of(EitherMonth.of(12)), EitherMonth.of(11)),
                Or.of(Not.of(EitherYear.of(1999)), And.of(EitherMonth.of(10), Not.of(EitherYear.of(2021)))));
        assertEquals(
                And.of(
                        Not.of(EitherDayOfMonth.of(29)),
                        Or.of(Not.of(EitherMonth.of(12)), EitherMonth.of(11)),
                        Or.of(Not.of(EitherYear.of(1999)), And.of(EitherMonth.of(10), Not.of(EitherYear.of(2021))))),
                and);
        System.out.println(and);
        final NegationNormalFormula nnAnd = and.toNegationNormalForm();
        System.out.println(nnAnd);
        System.out.println(nnAnd.getDisjunctiveNormalForm());
        final NegationNormalFormula negatedNnAnd = and.negateInNegationNormalForm();
        System.out.println(negatedNnAnd);
        System.out.println(negatedNnAnd.getDisjunctiveNormalForm());
    }

    @Test
    public void test2() {
        final Formula f = And.of(EitherDayOfMonth.of(29), EitherMonth.of(11), EitherYear.of(1999));
        final NegationNormalFormula nnf = f.toNegationNormalForm();
        System.out.println(nnf);
        System.out.println(nnf.getDisjunctiveNormalForm());
    }
}
