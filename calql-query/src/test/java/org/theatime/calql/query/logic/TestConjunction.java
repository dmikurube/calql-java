/*
 * Copyright 2024 Dai MIKURUBE
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
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.theatime.calql.query.date.AfterYear;
import org.theatime.calql.query.date.BeforeYear;
import org.theatime.calql.query.date.EitherDate;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;

public class TestConjunction {
    @Test
    public void testYearsNoIntersection() {
        final Conjunction c = Conjunction.of(EitherYear.of(1970), EitherYear.of(1972), EitherYear.of(1974));
        assertFalse(c.earliest().isPresent());
        assertFalse(c.latest().isPresent());
        assertFalse(c.unique().isPresent());
    }

    @Test
    public void testYearAndDate() {
        final Conjunction c = Conjunction.of(EitherYear.of(1970), EitherDate.of(1970, 6, 1));
        assertEquals(LocalDate.of(1970, 6, 1), c.earliest().get());
        assertEquals(LocalDate.of(1970, 6, 1), c.latest().get());
        assertEquals(LocalDate.of(1970, 6, 1), c.unique().get());
    }

    @Test
    public void testNoIntersectionYearAndDate() {
        final Conjunction c = Conjunction.of(EitherYear.of(1970), EitherDate.of(1975, 6, 1));
        assertFalse(c.earliest().isPresent());
        assertFalse(c.latest().isPresent());
        assertFalse(c.unique().isPresent());
    }

    @Test
    public void testYearAndMonth() {
        final Conjunction c = Conjunction.of(EitherYear.of(1970), EitherMonth.of(6));
        assertEquals(LocalDate.of(1970, 1, 1), c.earliest().get());
        assertEquals(LocalDate.of(1970, 12, 31), c.latest().get());
        assertFalse(c.unique().isPresent());
    }

    @Test
    public void testAfterBeforeYear() {
        final Conjunction c = Conjunction.of(AfterYear.of(1980), BeforeYear.of(1985));
        assertEquals(LocalDate.of(1981, 1, 1), c.earliest().get());
        assertEquals(LocalDate.of(1984, 12, 31), c.latest().get());
        assertFalse(c.unique().isPresent());
    }

    @Test
    public void testAfterBeforeOrEqualToYear() {
        final Conjunction c = Conjunction.of(AfterYear.orEqualTo(1980), BeforeYear.orEqualTo(1985));
        assertEquals(LocalDate.of(1980, 1, 1), c.earliest().get());
        assertEquals(LocalDate.of(1985, 12, 31), c.latest().get());
        assertFalse(c.unique().isPresent());
    }

    @Test
    public void testSingleDate() {
        final Conjunction c = Conjunction.of(EitherDate.of(1982, 3, 4));
        assertEquals(LocalDate.of(1982, 3, 4), c.earliest().get());
        assertEquals(LocalDate.of(1982, 3, 4), c.latest().get());
        assertEquals(LocalDate.of(1982, 3, 4), c.unique().get());
    }

    @Test
    public void testSameDates() {
        final Conjunction c = Conjunction.of(EitherDate.of(1982, 3, 4), EitherDate.of(1982, 3, 4));
        assertEquals(LocalDate.of(1982, 3, 4), c.earliest().get());
        assertEquals(LocalDate.of(1982, 3, 4), c.latest().get());
        assertEquals(LocalDate.of(1982, 3, 4), c.unique().get());
    }

    @Test
    public void testDatesNoIntersection() {
        final Conjunction c = Conjunction.of(EitherDate.of(1982, 3, 4), EitherDate.of(1982, 3, 5));
        assertFalse(c.earliest().isPresent());
        assertFalse(c.latest().isPresent());
        assertFalse(c.unique().isPresent());
    }
}
