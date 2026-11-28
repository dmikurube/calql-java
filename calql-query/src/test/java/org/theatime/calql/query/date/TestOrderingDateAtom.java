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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the accessors of the "after" and the "before" date atoms, which share {@link OrderingDateAtom}.
 */
public class TestOrderingDateAtom {
    @Test
    public void testValue() {
        assertEquals(2020, AfterYear.of(2020).year());
        assertEquals(2021, BeforeYear.of(2021).year());
        assertEquals(6, AfterMonth.of(6).month());
        assertEquals(7, BeforeMonth.of(7).month());
        assertEquals(15, AfterDayOfMonth.of(15).dayOfMonth());
        assertEquals(16, BeforeDayOfMonth.of(16).dayOfMonth());
    }

    @Test
    public void testIsInclusive() {
        assertFalse(AfterYear.of(2020).isInclusive());
        assertTrue(AfterYear.orEqualTo(2020).isInclusive());
        assertFalse(BeforeMonth.of(6, false).isInclusive());
        assertTrue(BeforeMonth.of(6, true).isInclusive());
        assertFalse(AfterDayOfMonth.of(15).isInclusive());
        assertTrue(BeforeDayOfMonth.orEqualTo(15).isInclusive());
    }

    @Test
    public void testNegationKeepsValueAndFlipsInclusiveness() {
        // month > 6 is negated to month <= 6.
        final BeforeMonth negated = (BeforeMonth) AfterMonth.of(6).negate();
        assertEquals(6, negated.month());
        assertTrue(negated.isInclusive());

        // year <= 2021 is negated to year > 2021.
        final AfterYear negatedYear = (AfterYear) BeforeYear.orEqualTo(2021).negate();
        assertEquals(2021, negatedYear.year());
        assertFalse(negatedYear.isInclusive());
    }
}
