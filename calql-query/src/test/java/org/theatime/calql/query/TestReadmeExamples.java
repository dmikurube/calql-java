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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.theatime.calql.query.date.AfterDayOfMonth;
import org.theatime.calql.query.date.BeforeDayOfMonth;
import org.theatime.calql.query.date.DefaultDateSourceStreamer;
import org.theatime.calql.query.date.EitherDayOfMonth;
import org.theatime.calql.query.date.EitherDayOfWeek;
import org.theatime.calql.query.date.EitherMonth;
import org.theatime.calql.query.date.EitherYear;

/**
 * Tests the example queries in README.md, written with the Java API until the query language is implemented.
 */
public class TestReadmeExamples {
    /**
     * {@code select day_of_week from dates where year = 2024 and month = 9 and day_of_month = 29;}
     */
    @Test
    public void testDayOfWeek() {
        final List<LocalDate> dates = query(Conjunction.of(
                EitherYear.of(2024),
                EitherMonth.of(9),
                EitherDayOfMonth.of(29)));

        assertEquals(List.of(LocalDate.of(2024, 9, 29)), dates);
        assertEquals(7, dates.get(0).getDayOfWeek().getValue());  // 7 represents Sunday in ISO 8601.
    }

    /**
     * {@code select month, day_of_month from dates where year = 2023 and day_of_month >= 1 and day_of_month <= 7 and day_of_week = 3;}
     */
    @Test
    public void testFirstWednesdays() {
        final List<LocalDate> dates = query(Conjunction.of(
                EitherYear.of(2023),
                AfterDayOfMonth.orEqualTo(1),
                BeforeDayOfMonth.orEqualTo(7),
                EitherDayOfWeek.of(DayOfWeek.WEDNESDAY)));

        final List<List<Integer>> expected = List.of(
                List.of(1, 4),
                List.of(2, 1),
                List.of(3, 1),
                List.of(4, 5),
                List.of(5, 3),
                List.of(6, 7),
                List.of(7, 5),
                List.of(8, 2),
                List.of(9, 6),
                List.of(10, 4),
                List.of(11, 1),
                List.of(12, 6));
        assertEquals(expected, dates.stream()
                .map(date -> List.of(date.getMonthValue(), date.getDayOfMonth()))
                .collect(Collectors.toList()));
    }

    private static List<LocalDate> query(final Conjunction<ChronoLocalDate> conjunction) {
        return conjunction.streamBy(DefaultDateSourceStreamer.of(), Order.FROM_EARLIEST_TO_LATEST).collect(Collectors.toList());
    }
}
