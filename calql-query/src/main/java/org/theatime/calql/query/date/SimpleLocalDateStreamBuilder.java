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
import org.theatime.calql.query.logic.Atom;
import org.theatime.calql.query.logic.Conjunction;

/**
 * Generates a stream of dates.
 */
public final class SimpleLocalDateStreamBuilder {
    private SimpleLocalDateStreamBuilder() {
        this.conjunction = null;
    }

    public static SimpleLocalDateStreamBuilder builder() {
        return new SimpleLocalDateStreamBuilder();
    }

    public SimpleLocalDateStreamBuilder fromConjunction(final Conjunction<ChronoLocalDate> conjunction) {
        this.conjunction = conjunction;
        return this;
    }

    public Stream<LocalDate> build(final DateOrder order) {
        return buildFromConjunction(this.conjunction, order);
    }

    private static Stream<LocalDate> buildFromConjunction(
            final Conjunction<ChronoLocalDate> conjunction,
            final DateOrder order) {
        Objects.requireNonNull(conjunction, "conjunction is null.");
        requireDate(conjunction);

        if (!conjunction.existsPossibly()) {
            return Stream.<LocalDate>empty();
        }

        if (order == DateOrder.FROM_EARLIEST_TO_LATEST) {
            if (!earliest(conjunction).isPresent()) {
                throw new IllegalArgumentException("conjunction does not have the earliest date.");
            }
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                         new NaiveDateIterator(conjunction, earliest(conjunction).get(), latest(conjunction), order),
                         Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.NONNULL | Spliterator.IMMUTABLE),
                     false);
        } else if (order == DateOrder.FROM_LATEST_TO_EARLIEST) {
            if (!latest(conjunction).isPresent()) {
                throw new IllegalArgumentException("conjunction does not have the latest date.");
            }
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                         new NaiveDateIterator(conjunction, latest(conjunction).get(), earliest(conjunction), order),
                         Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.NONNULL | Spliterator.IMMUTABLE),
                     false);
        } else {
            throw new IllegalArgumentException("invalid date order: " + order);
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
            final LocalDate t = this.cursor;

            if (order == DateOrder.FROM_EARLIEST_TO_LATEST) {
                if (this.to.isPresent() && this.cursor.isAfter(this.to.get())) {
                    throw new NoSuchElementException();
                }
                this.cursor = this.cursor.plusDays(1);
            } else if (order == DateOrder.FROM_LATEST_TO_EARLIEST) {
                if (this.to.isPresent() && this.cursor.isBefore(this.to.get())) {
                    throw new NoSuchElementException();
                }
                this.cursor = this.cursor.minusDays(1);
            } else {
                throw new IllegalArgumentException("invalid date order: " + this.order);
            }

            return t;
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

    @SuppressWarnings("unchecked")
    private static DateAtom asDateAtom(final Atom atom) {
        if (atom instanceof DateAtom) {
            return (DateAtom) atom;
        }
        throw new ClassCastException("Atom cannot be casted to DateAtom.");
    }

    private Conjunction<ChronoLocalDate> conjunction;
}
