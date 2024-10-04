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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.theatime.calql.query.Atom;
import org.theatime.calql.query.Conjunction;
import org.theatime.calql.query.Order;
import org.theatime.calql.query.SourceStreamer;

/**
 * Generates a naive stream of all dates.
 */
public final class NaiveDateSourceStreamer implements SourceStreamer<ChronoLocalDate, LocalDate> {
    private NaiveDateSourceStreamer() {
    }

    public static NaiveDateSourceStreamer of() {
        return new NaiveDateSourceStreamer();
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

        final Optional<LocalDate> earliestLocalDate = earliestLocalDate(conjunction);
        final Optional<LocalDate> latestLocalDate = latestLocalDate(conjunction);

        if (order == Order.FROM_EARLIEST_TO_LATEST) {
            if (!earliestLocalDate.isPresent()) {
                throw new IllegalArgumentException("conjunction does not have the earliest date.");
            }
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                         new NaiveDateIterator(conjunction, earliestLocalDate.get(), latestLocalDate, order),
                         Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.NONNULL | Spliterator.IMMUTABLE),
                     false);
        } else if (order == Order.FROM_LATEST_TO_EARLIEST) {
            if (!latestLocalDate.isPresent()) {
                throw new IllegalArgumentException("conjunction does not have the latest date.");
            }
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                         new NaiveDateIterator(conjunction, latestLocalDate.get(), earliestLocalDate, order),
                         Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.NONNULL | Spliterator.IMMUTABLE),
                     false);
        } else {
            throw new IllegalArgumentException("invalid date order: " + order);
        }
    }

    @Override
    public boolean isApplicableTo(
            final Conjunction<ChronoLocalDate> conjunction,
            final Order order) {
        return true;
    }

    private static class NaiveDateIterator implements Iterator<LocalDate> {
        NaiveDateIterator(
                final Conjunction<ChronoLocalDate> conjunction,
                final LocalDate from,
                final Optional<LocalDate> to,
                final Order order) {
            this.conjunction = conjunction;
            this.from = from;
            this.to = to;
            this.order = order;

            this.cursor = from;
        }

        @Override
        public boolean hasNext() {
            if (!this.to.isPresent()) {
                return true;
            }

            if (order == Order.FROM_EARLIEST_TO_LATEST) {
                if (this.cursor.isAfter(this.to.get())) {
                    return false;
                }
                return true;
            } else if (order == Order.FROM_LATEST_TO_EARLIEST) {
                if (this.cursor.isBefore(this.to.get())) {
                    return false;
                }
                return true;
            } else {
                throw new IllegalArgumentException("invalid date order: " + this.order);
            }
        }

        @Override
        public LocalDate next() {
            final LocalDate beforeNext = this.cursor;

            if (order == Order.FROM_EARLIEST_TO_LATEST) {
                if (this.to.isPresent() && this.cursor.isAfter(this.to.get())) {
                    throw new NoSuchElementException();
                }
                this.cursor = this.cursor.plusDays(1);
            } else if (order == Order.FROM_LATEST_TO_EARLIEST) {
                if (this.to.isPresent() && this.cursor.isBefore(this.to.get())) {
                    throw new NoSuchElementException();
                }
                this.cursor = this.cursor.minusDays(1);
            } else {
                throw new IllegalArgumentException("invalid date order: " + this.order);
            }

            return beforeNext;
        }

        private final Conjunction<ChronoLocalDate> conjunction;

        private final LocalDate from;
        private final Optional<LocalDate> to;

        private final Order order;

        private LocalDate cursor;
    }

    private static void requireLocalDate(final Conjunction<ChronoLocalDate> conjunction) {
        for (final Atom<ChronoLocalDate> atom : conjunction) {
            if (atom.unit() != LocalDate.class) {
                throw new IllegalArgumentException("conjunction contains non-date.");
            }
        }
    }

    private static Optional<LocalDate> earliestLocalDate(final Conjunction<ChronoLocalDate> conjunction) {
        final Optional<ChronoLocalDate> earliest = conjunction.earliest();
        if (earliest.isPresent()) {
            final ChronoLocalDate chronoEarliest = earliest.get();
            if (chronoEarliest instanceof LocalDate) {
                return Optional.<LocalDate>of((LocalDate) chronoEarliest);
            } else {
                throw new ClassCastException("not LocalDate");
            }
        } else {
            return Optional.<LocalDate>empty();
        }
    }

    private static Optional<LocalDate> latestLocalDate(final Conjunction<ChronoLocalDate> conjunction) {
        final Optional<ChronoLocalDate> latest = conjunction.latest();
        if (latest.isPresent()) {
            final ChronoLocalDate chronoLatest = latest.get();
            if (chronoLatest instanceof LocalDate) {
                return Optional.<LocalDate>of((LocalDate) chronoLatest);
            } else {
                throw new ClassCastException("not LocalDate");
            }
        } else {
            return Optional.<LocalDate>empty();
        }
    }
}
