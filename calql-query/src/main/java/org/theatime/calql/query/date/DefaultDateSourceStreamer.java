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
import java.util.stream.Stream;
import org.theatime.calql.query.Atom;
import org.theatime.calql.query.Conjunction;
import org.theatime.calql.query.Order;
import org.theatime.calql.query.SourceStreamer;

/**
 * Generates a stream of possible dates with being hinted from {@link Conjunction}.
 */
public final class DefaultDateSourceStreamer implements SourceStreamer<ChronoLocalDate, LocalDate> {
    private DefaultDateSourceStreamer() {
        this.naiveStreamer = NaiveDateSourceStreamer.of();
    }

    public static DefaultDateSourceStreamer of() {
        return new DefaultDateSourceStreamer();
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

        // Last resort -- iterate all dates naively.
        return this.naiveStreamer.sourceStreamFrom(conjunction, order);
    }

    private static void requireLocalDate(final Conjunction<ChronoLocalDate> conjunction) {
        for (final Atom<ChronoLocalDate> atom : conjunction) {
            if (atom.unit() != LocalDate.class) {
                throw new IllegalArgumentException("conjunction contains non-date.");
            }
        }
    }

    private final NaiveDateSourceStreamer naiveStreamer;
}
