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

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestEitherDate {
    @Test
    public void testToStringSingle() {
        assertEquals("date = 2024-01-01", EitherDate.of(2024, 1, 1).toString());
        assertEquals("date <> 2024-01-01", EitherDate.notOf(2024, 1, 1).toString());
    }

    @Test
    public void testToStringSingleInCollection() {
        // The earliest and the latest dates are taken from the collection, not from a single argument.
        assertEquals("date = 2024-01-01", EitherDate.of(List.of(LocalDate.of(2024, 1, 1))).toString());
        assertEquals("date <> 2024-01-01", EitherDate.notOf(List.of(LocalDate.of(2024, 1, 1))).toString());
    }

    @Test
    public void testToStringMultiple() {
        final List<LocalDate> dates = List.of(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2));
        assertEquals("date in " + EitherDate.of(dates).dates(), EitherDate.of(dates).toString());
        assertEquals("date not in " + EitherDate.notOf(dates).dates(), EitherDate.notOf(dates).toString());
    }
}
