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

import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;

public class TestDisjunctiveNormalFormula {
    @Test
    public void testWith() {
        final Conjunction<ChronoLocalDate> c1 = Conjunction.of(EitherYear.of(1970));
        final Conjunction<ChronoLocalDate> c2 = Conjunction.of(EitherMonth.of(6));
        final DisjunctiveNormalFormula<ChronoLocalDate> dnf = DisjunctiveNormalFormula.of(c1).with(c2);
        assertEquals(List.of(c1, c2), new ArrayList<>(dnf));
    }

    @Test
    public void testWithMultiple() {
        final Conjunction<ChronoLocalDate> c1 = Conjunction.of(EitherYear.of(1970));
        final Conjunction<ChronoLocalDate> c2 = Conjunction.of(EitherMonth.of(6));
        final Conjunction<ChronoLocalDate> c3 = Conjunction.of(EitherMonth.of(7));
        final DisjunctiveNormalFormula<ChronoLocalDate> dnf = DisjunctiveNormalFormula.of(c1).with(List.of(c2, c3));
        assertEquals(List.of(c1, c2, c3), new ArrayList<>(dnf));
    }
}
