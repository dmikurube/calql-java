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
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;

/**
 * Tests the general contract of {@link Object#equals(Object)} for formulae and their containers.
 */
public class TestEquals {
    @TestFactory
    public Stream<DynamicTest> testNotEqualToNull() {
        return instances().map(instance -> DynamicTest.dynamicTest(instance.getClass().getSimpleName(), () -> {
            assertFalse(instance.equals(null));
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testNotEqualToOtherType() {
        return instances().map(instance -> DynamicTest.dynamicTest(instance.getClass().getSimpleName(), () -> {
            assertFalse(instance.equals("string"));
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testReflexive() {
        return instances().map(instance -> DynamicTest.dynamicTest(instance.getClass().getSimpleName(), () -> {
            assertEquals(instance, instance);
        }));
    }

    private static Stream<Object> instances() {
        final Formula<LocalDate> year = EitherYear.of(1970);
        final Formula<LocalDate> month = EitherMonth.of(6);
        final NegationNormalFormula<LocalDate> nnYear = EitherYear.of(1970);
        final NegationNormalFormula<LocalDate> nnMonth = EitherMonth.of(6);
        return Stream.of(
                And.of(year, month),
                Or.of(year, month),
                Not.of(year),
                NegationNormalAnd.of(nnYear, nnMonth),
                NegationNormalOr.of(nnYear, nnMonth),
                Conjunction.of(EitherYear.of(1970), EitherMonth.of(6)),
                DisjunctiveNormalFormula.of(Conjunction.of(EitherYear.of(1970)), Conjunction.of(EitherMonth.of(6))));
    }
}
