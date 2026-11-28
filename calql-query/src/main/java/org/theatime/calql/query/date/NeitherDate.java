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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * An atom that tests whether a date is none of the given dates,
 * such as {@code date not in [2024-01-01, 2024-12-31]}.
 *
 * <p>Its negation is {@link EitherDate} of the same dates.
 */
public final class NeitherDate extends MembershipDateAtom<LocalDate> {
    private NeitherDate(final Set<LocalDate> dates) {
        super(dates, true);
    }

    public static NeitherDate of(final int year, final int month, final int dayOfMonth) {
        return of(LocalDate.of(year, month, dayOfMonth));
    }

    public static NeitherDate of(final LocalDate date) {
        return new NeitherDate(Set.of(date));
    }

    public static NeitherDate of(final Collection<LocalDate> dates) {
        return new NeitherDate(Set.copyOf(dates));
    }

    public static NeitherDate of(final LocalDate... dates) {
        return of(Arrays.asList(dates));
    }

    public Set<LocalDate> dates() {
        return this.values();
    }

    @Override
    public DateAtom negate() {
        return EitherDate.of(this.values());
    }

    @Override
    String columnName() {
        return "date";
    }

    @Override
    LocalDate columnValueOf(final LocalDate date) {
        return date;
    }
}
