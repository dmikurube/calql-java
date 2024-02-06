/*
 * Copyright 2023 Dai MIKURUBE
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

package org.calql.query.date;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.calql.query.logic.Atom;
import org.calql.query.logic.Conjunction;

/**
 * Generates a stream of dates.
 */
public final class SimpleLocalDateStreamBuilder implements LocalDateStreamBuilder {
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

    @Override
    public Stream<LocalDate> build() {
        return buildFromConjunction(this.conjunction);
    }

    private static Stream<LocalDate> buildFromConjunction(final Conjunction<ChronoLocalDate> conjunction) {
        Objects.requireNonNull(conjunction, "conjunction is null.");
        requireDate(conjunction);

        final Optional<LocalDate> earliestDate = earliest(conjunction);
        if (!earliestDate.isPresent()) {
            throw new IllegalArgumentException("conjunction does not have the earliest date.");
        }

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                        new NaiveDateIterator(conjunction, earliestDate.get()),
                        Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.NONNULL | Spliterator.IMMUTABLE),
                false);
    }

    private static class NaiveDateIterator implements Iterator<LocalDate> {
        NaiveDateIterator(final Conjunction<ChronoLocalDate> conjunction, final LocalDate cur) {
            this.conjunction = conjunction;
            this.cur = cur;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public LocalDate next() {
            final LocalDate t = this.cur;
            this.cur = this.cur.plusDays(1);
            return t;
        }

        private final Conjunction<ChronoLocalDate> conjunction;

        private LocalDate cur;
    }

    private static void requireDate(final Conjunction<ChronoLocalDate> conjunction) {
        for (final Atom<ChronoLocalDate> atom : conjunction) {
            if (atom.unit() != LocalDate.class) {
                throw new IllegalArgumentException("conjunction contains non-date.");
            }
        }
    }

    private static Optional<LocalDate> earliest(final Conjunction<ChronoLocalDate> conjunction) {
        Optional<LocalDate> earliestDate = Optional.empty();
        for (final Atom<ChronoLocalDate> atom : conjunction) {
            if (atom.unit() != LocalDate.class) {
                throw new IllegalArgumentException("conjunction contains non-date.");
            }
            final Optional<LocalDate> date = asDateAtom(atom).earliest();
            if (date.isPresent()) {
                if ((!earliestDate.isPresent()) || date.get().isAfter(earliestDate.get())) {
                    earliestDate = date;
                }
            }
        }
        return earliestDate;
    }

    private static Optional<LocalDate> latest(final Conjunction<ChronoLocalDate> conjunction) {
        Optional<LocalDate> latestDate = Optional.empty();
        for (final Atom<ChronoLocalDate> atom : conjunction) {
            if (atom.unit() != LocalDate.class) {
                throw new IllegalArgumentException("conjunction contains non-date.");
            }
            final Optional<LocalDate> date = asDateAtom(atom).latest();
            if (date.isPresent()) {
                if ((!latestDate.isPresent()) || date.get().isBefore(latestDate.get())) {
                    latestDate = date;
                }
            }
        }
        return latestDate;
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
