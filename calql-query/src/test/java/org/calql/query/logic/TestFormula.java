/*
 * Copyright 2021-2023 Dai MIKURUBE
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

package org.calql.query.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.calql.query.date.ExactDayOfMonth;
import org.calql.query.date.ExactMonth;
import org.calql.query.date.ExactYear;
import org.junit.jupiter.api.Test;

public class TestFormula {
    @Test
    public void test() {
        final Formula and = And.of(
                Not.of(ExactDayOfMonth.of(29)),
                Or.of(Not.of(ExactMonth.of(12)), ExactMonth.of(11)),
                Or.of(Not.of(ExactYear.of(1999)), And.of(ExactMonth.of(10), Not.of(ExactYear.of(2021)))));
        assertEquals(
                And.of(
                        Not.of(ExactDayOfMonth.of(29)),
                        Or.of(Not.of(ExactMonth.of(12)), ExactMonth.of(11)),
                        Or.of(Not.of(ExactYear.of(1999)), And.of(ExactMonth.of(10), Not.of(ExactYear.of(2021))))),
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
        final Formula f = And.of(ExactDayOfMonth.of(29), ExactMonth.of(11), ExactYear.of(1999));
        final NegationNormalFormula nnf = f.toNegationNormalForm();
        System.out.println(nnf);
        System.out.println(nnf.getDisjunctiveNormalForm());
    }
}
