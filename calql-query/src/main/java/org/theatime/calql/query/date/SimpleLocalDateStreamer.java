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
import org.theatime.calql.query.Streamer;

/**
 * Generates a stream of dates.
 */
public final class SimpleLocalDateStreamer implements Streamer<ChronoLocalDate, LocalDate> {
    private SimpleLocalDateStreamer(final DateOrder order) {
        this.order = order;
    }

    public static SimpleLocalDateStreamer of(final DateOrder order) {
        return new SimpleLocalDateStreamer(order);
    }

    @Override
    public Stream<LocalDate> streamFrom(final Conjunction<ChronoLocalDate> conjunction) {
        Objects.requireNonNull(conjunction, "conjunction is null.");
        requireDate(conjunction);

        if (!conjunction.existsPossibly()) {
            return Stream.<LocalDate>empty();
        }

        if (this.order == DateOrder.FROM_EARLIEST_TO_LATEST) {
            if (!earliest(conjunction).isPresent()) {
                throw new IllegalArgumentException("conjunction does not have the earliest date.");
            }
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                         new NaiveDateIterator(conjunction, earliest(conjunction).get(), latest(conjunction), this.order),
                         Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.NONNULL | Spliterator.IMMUTABLE),
                     false);
        } else if (this.order == DateOrder.FROM_LATEST_TO_EARLIEST) {
            if (!latest(conjunction).isPresent()) {
                throw new IllegalArgumentException("conjunction does not have the latest date.");
            }
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                         new NaiveDateIterator(conjunction, latest(conjunction).get(), earliest(conjunction), this.order),
                         Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.NONNULL | Spliterator.IMMUTABLE),
                     false);
        } else {
            throw new IllegalArgumentException("invalid date order: " + this.order);
        }
    }

    private static class NaiveDateIterator implements Iterator<LocalDate> {
        NaiveDateIterator(
                final Conjunction<ChronoLocalDate> conjunction,
                final LocalDate from,
                final Optional<LocalDate> to,
                final DateOrder order) {
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

            if (order == DateOrder.FROM_EARLIEST_TO_LATEST) {
                if (this.cursor.isAfter(this.to.get())) {
                    return false;
                }
                return true;
            } else if (order == DateOrder.FROM_LATEST_TO_EARLIEST) {
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

            if (order == DateOrder.FROM_EARLIEST_TO_LATEST) {
                do {
                    if (this.to.isPresent() && this.cursor.isAfter(this.to.get())) {
                        throw new NoSuchElementException();
                    }
                    this.cursor = this.cursor.plusDays(1);
                } while (!this.conjunction.test(this.cursor));
            } else if (order == DateOrder.FROM_LATEST_TO_EARLIEST) {
                do {
                    if (this.to.isPresent() && this.cursor.isBefore(this.to.get())) {
                        throw new NoSuchElementException();
                    }
                    this.cursor = this.cursor.minusDays(1);
                } while (!this.conjunction.test(this.cursor));
            } else {
                throw new IllegalArgumentException("invalid date order: " + this.order);
            }

            return beforeNext;
        }

        private final Conjunction<ChronoLocalDate> conjunction;

        private final LocalDate from;
        private final Optional<LocalDate> to;

        private final DateOrder order;

        private LocalDate cursor;
    }

    private static void requireDate(final Conjunction<ChronoLocalDate> conjunction) {
        for (final Atom<ChronoLocalDate> atom : conjunction) {
            if (atom.unit() != LocalDate.class) {
                throw new IllegalArgumentException("conjunction contains non-date.");
            }
        }
    }

    private static Optional<LocalDate> earliest(final Conjunction<ChronoLocalDate> conjunction) {
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

    private static Optional<LocalDate> latest(final Conjunction<ChronoLocalDate> conjunction) {
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

    private final DateOrder order;
}
