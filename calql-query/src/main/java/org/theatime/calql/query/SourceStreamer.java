/*
 * Copyright 2024-2026 Dai MIKURUBE
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

import java.util.stream.Stream;

/**
 * Streams the candidates of the values that may satisfy a conjunction,
 * which are filtered by the conjunction afterwards. See {@link Conjunction#streamBy(SourceStreamer, Order)}.
 *
 * <p>An implementation can stream fewer candidates by looking at the atoms in the conjunction,
 * such as only the dates of {@link org.theatime.calql.query.date.EitherDate} instead of all the dates.
 * It must not miss any values that satisfy the conjunction, as filtering afterwards cannot bring them back.
 *
 * @param <T> the type of values to stream
 */
public interface SourceStreamer<T extends Comparable<? super T>> {
    /**
     * Streams the candidates of the values that may satisfy {@code conjunction}, in {@code order}.
     *
     * <p>The stream may contain values that do not satisfy {@code conjunction}, but it must contain all the values
     * that satisfy {@code conjunction}, if {@link #isApplicableTo(Conjunction, Order)} returns true for them.
     *
     * @param conjunction  the conjunction to stream the candidates for, not null
     * @param order  the order of the values to stream, not null
     * @return the candidates of the values that may satisfy {@code conjunction}, in {@code order}
     */
    Stream<T> sourceStreamFrom(Conjunction<T> conjunction, Order order);

    /**
     * Returns true if this streamer can stream the candidates for {@code conjunction} in {@code order},
     * without missing any values that satisfy {@code conjunction}.
     *
     * <p>It returns false by default.
     *
     * @param conjunction  the conjunction to stream the candidates for, not null
     * @param order  the order of the values to stream, not null
     * @return true if this streamer can stream the candidates for {@code conjunction} in {@code order}
     */
    default boolean isApplicableTo(Conjunction<T> conjunction, Order order) {
        return false;
    }
}
