/*
 * Copyright 2023-2024 Dai MIKURUBE
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

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Stream;
import org.theatime.calql.query.Atom;
import org.theatime.calql.query.Conjunction;
import org.theatime.calql.query.Order;
import org.theatime.calql.query.SourceStreamer;

/**
 * Generates an "optimized" stream of dates, estimated from {@link Conjunction} that includes {@link EitherDate}.
 */
public final class ExactDateSourceStreamer implements SourceStreamer<ChronoLocalDate, LocalDate> {
    private ExactDateSourceStreamer() {
    }

    public static ExactDateSourceStreamer of() {
        return new ExactDateSourceStreamer();
    }

    @Override
    public Stream<LocalDate> sourceStreamFrom(
            final Conjunction<ChronoLocalDate> conjunction,
            final Order order) {
        Objects.requireNonNull(conjunction, "conjunction is null.");
        requireLocalDate(conjunction);

        if (!conjunction.existsPossibly()) {
            return Stream.<LocalDate>empty();
        }

        final TreeSet<LocalDate> dates = new TreeSet<>();
        for (final Atom<ChronoLocalDate> atom : conjunction) {
            if (!(atom instanceof EitherDate)) {
                continue;
            }
            final EitherDate eitherDate = (EitherDate) atom;
            if (!eitherDate.includes()) {
                continue;
            }
            if (dates.isEmpty()) {
                dates.addAll(eitherDate.dates());
            } else {
                dates.retainAll(eitherDate.dates());
            }
        }
        return dates.stream();
    }

    @Override
    public boolean isApplicableTo(final Conjunction<ChronoLocalDate> conjunction, final Order order) {
        for (final Atom<ChronoLocalDate> atom : conjunction) {
            if (!(atom instanceof EitherDate)) {
                continue;
            }
            final EitherDate eitherDate = (EitherDate) atom;
            if (!eitherDate.includes()) {
                continue;
            }
            return true;
        }
        return false;
    }

    private static void requireLocalDate(final Conjunction<ChronoLocalDate> conjunction) {
        for (final Atom<ChronoLocalDate> atom : conjunction) {
            if (atom.unit() != LocalDate.class) {
                throw new IllegalArgumentException("conjunction contains non-date.");
            }
        }
    }
}
