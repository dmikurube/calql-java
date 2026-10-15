/*
 * Copyright 2023-2026 Dai MIKURUBE
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.theatime.calql.query.Conjunction;
import org.theatime.calql.query.Order;

public class TestDefaultDateSourceStreamer {
    @Test
    public void testSimple() {
        final Stream<LocalDate> stream = Conjunction.of(AfterYear.orEqualTo(1970))
                .streamBy(DefaultDateSourceStreamer.of(), Order.FROM_EARLIEST_TO_LATEST);

        final ArrayList<LocalDate> expected = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            expected.add(LocalDate.of(1970, 1, 1).plusDays(i));
        }
        assertDateStream(expected, stream);
    }

    @Test
    public void testEveryTenth() {
        final Stream<LocalDate> stream = Conjunction.of(AfterYear.orEqualTo(1970), EitherDayOfMonth.of(10))
                .streamBy(DefaultDateSourceStreamer.of(), Order.FROM_EARLIEST_TO_LATEST);

        final ArrayList<LocalDate> expected = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            expected.add(LocalDate.of(1970, 1, 10).plusMonths(i));
        }
        assertDateStream(expected, stream);
    }

    @Test
    public void testExactDates() {
        final Stream<LocalDate> stream = Conjunction.of(EitherDate.of(
                    LocalDate.of(1972, 2, 27),
                    LocalDate.of(1973, 1, 18),
                    LocalDate.of(1974, 10, 8)))
                .streamBy(DefaultDateSourceStreamer.of(), Order.FROM_EARLIEST_TO_LATEST);

        final ArrayList<LocalDate> expected = new ArrayList<>();
        expected.add(LocalDate.of(1972, 2, 27));
        expected.add(LocalDate.of(1973, 1, 18));
        expected.add(LocalDate.of(1974, 10, 8));
        assertDateStream(expected, stream);
    }

    @Test
    public void testExactDatesFromLatestToEarliest() {
        final Stream<LocalDate> stream = Conjunction.of(EitherDate.of(
                    LocalDate.of(1972, 2, 27),
                    LocalDate.of(1973, 1, 18),
                    LocalDate.of(1974, 10, 8)))
                .streamBy(DefaultDateSourceStreamer.of(), Order.FROM_LATEST_TO_EARLIEST);

        final ArrayList<LocalDate> expected = new ArrayList<>();
        expected.add(LocalDate.of(1974, 10, 8));
        expected.add(LocalDate.of(1973, 1, 18));
        expected.add(LocalDate.of(1972, 2, 27));
        assertDateStream(expected, stream);
    }

    @Test
    public void testExceptDate() {
        final Stream<LocalDate> stream = Conjunction.of(AfterYear.orEqualTo(1970), EitherDate.notOf(1970, 1, 3))
                .streamBy(DefaultDateSourceStreamer.of(), Order.FROM_EARLIEST_TO_LATEST);

        final ArrayList<LocalDate> expected = new ArrayList<>();
        expected.add(LocalDate.of(1970, 1, 1));
        expected.add(LocalDate.of(1970, 1, 2));
        expected.add(LocalDate.of(1970, 1, 4));
        expected.add(LocalDate.of(1970, 1, 5));
        assertDateStream(expected, stream);
    }

    @Test
    public void testExceptYear() {
        final Stream<LocalDate> stream = Conjunction.of(AfterYear.orEqualTo(1970), EitherYear.notOf(1970), EitherDayOfMonth.of(1))
                .streamBy(DefaultDateSourceStreamer.of(), Order.FROM_EARLIEST_TO_LATEST);

        final ArrayList<LocalDate> expected = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            expected.add(LocalDate.of(1971, 1, 1).plusMonths(i));
        }
        assertDateStream(expected, stream);
    }

    @Test
    public void testMultipleYears() {
        final Stream<LocalDate> stream = Conjunction.of(EitherYear.of(1970, 1972), EitherMonth.of(2), EitherDayOfMonth.of(29))
                .streamBy(DefaultDateSourceStreamer.of(), Order.FROM_EARLIEST_TO_LATEST);

        final ArrayList<LocalDate> expected = new ArrayList<>();
        expected.add(LocalDate.of(1972, 2, 29));
        assertEquals(expected, stream.collect(Collectors.toList()));
    }

    private static void assertDateStream(final List<LocalDate> expected, final Stream<LocalDate> actual) {
        assertEquals(expected, actual.limit(expected.size()).collect(Collectors.toList()));
    }
}
