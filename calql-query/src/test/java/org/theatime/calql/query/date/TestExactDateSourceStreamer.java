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
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.theatime.calql.query.Conjunction;
import org.theatime.calql.query.Order;

public class TestExactDateSourceStreamer {
    @Test
    public void testIntersection() {
        final Conjunction<LocalDate> conjunction = Conjunction.of(
                EitherDate.of(JAN1, JAN2, JAN3),
                EitherDate.of(JAN2, JAN3, JAN4));
        assertEquals(List.of(JAN2, JAN3), source(conjunction, Order.FROM_EARLIEST_TO_LATEST));
    }

    @Test
    public void testIntersectionBecomingEmpty() {
        // The intersection becomes empty at the second EitherDate. It must stay empty at the third EitherDate.
        final Conjunction<LocalDate> conjunction = Conjunction.of(
                EitherDate.of(JAN1, JAN3),
                EitherDate.of(JAN2),
                EitherDate.of(JAN2));
        assertEquals(List.of(), source(conjunction, Order.FROM_EARLIEST_TO_LATEST));
    }

    @Test
    public void testFromEarliestToLatest() {
        final Conjunction<LocalDate> conjunction = Conjunction.of(EitherDate.of(JAN3, JAN1, JAN2));
        assertEquals(List.of(JAN1, JAN2, JAN3), source(conjunction, Order.FROM_EARLIEST_TO_LATEST));
    }

    @Test
    public void testFromLatestToEarliest() {
        final Conjunction<LocalDate> conjunction = Conjunction.of(EitherDate.of(JAN3, JAN1, JAN2));
        assertEquals(List.of(JAN3, JAN2, JAN1), source(conjunction, Order.FROM_LATEST_TO_EARLIEST));
    }

    private static List<LocalDate> source(final Conjunction<LocalDate> conjunction, final Order order) {
        return ExactDateSourceStreamer.of().sourceStreamFrom(conjunction, order).collect(Collectors.toList());
    }

    private static final LocalDate JAN1 = LocalDate.of(1970, 1, 1);
    private static final LocalDate JAN2 = LocalDate.of(1970, 1, 2);
    private static final LocalDate JAN3 = LocalDate.of(1970, 1, 3);
    private static final LocalDate JAN4 = LocalDate.of(1970, 1, 4);
}
