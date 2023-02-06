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
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.calql.query.logic.Conjunction;

/**
 * Generates a stream of dates.
 */
public final class NaiveDateGenerator implements DateGeneratable {
    @Override
    public Stream<LocalDate> generate(final Conjunction conjunction) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                new NaiveDateIterator(conjunction, LocalDate.of(1970, 1, 1)), Spliterator.NONNULL | Spliterator.IMMUTABLE), false);
    }

    private static class NaiveDateIterator implements Iterator<LocalDate> {
        NaiveDateIterator(final Conjunction conjunction, final LocalDate cur) {
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

        private final Conjunction conjunction;

        private LocalDate cur;
    }
}
