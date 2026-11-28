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

    /**
     * Returns an atom that tests whether a date is not the date of {@code year}, {@code month}, and {@code dayOfMonth},
     * such as {@code date <> 2024-01-01}.
     *
     * @param year  the year of the date
     * @param month  the month of the date, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month of the date, from 1 to 31
     * @return the atom
     * @throws java.time.DateTimeException  if the value of any field is out of range,
     *   or if the day of month is invalid for the month and the year
     */
    public static NeitherDate of(final int year, final int month, final int dayOfMonth) {
        return of(LocalDate.of(year, month, dayOfMonth));
    }

    /**
     * Returns an atom that tests whether a date is not {@code date}, such as {@code date <> 2024-01-01}.
     *
     * @param date  the date, not null
     * @return the atom
     * @throws NullPointerException  if {@code date} is null
     */
    public static NeitherDate of(final LocalDate date) {
        return new NeitherDate(Set.of(date));
    }

    /**
     * Returns an atom that tests whether a date is none of {@code dates},
     * such as {@code date not in [2024-01-01, 2024-12-31]}.
     *
     * <p>Duplicated dates are removed.
     *
     * @param dates  the dates, not null, and not containing null
     * @return the atom
     * @throws NullPointerException  if {@code dates} is null, or contains null
     */
    public static NeitherDate of(final Collection<LocalDate> dates) {
        return new NeitherDate(Set.copyOf(dates));
    }

    /**
     * Returns an atom that tests whether a date is none of {@code dates},
     * such as {@code date not in [2024-01-01, 2024-12-31]}.
     *
     * <p>Duplicated dates are removed.
     *
     * @param dates  the dates, not null, and not containing null
     * @return the atom
     * @throws NullPointerException  if {@code dates} is null, or contains null
     */
    public static NeitherDate of(final LocalDate... dates) {
        return of(Arrays.asList(dates));
    }

    /**
     * Returns the dates that this atom tests a date against.
     *
     * <p>A date matches this atom if it is none of them.
     *
     * @return the dates, an unmodifiable set in no particular order
     */
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
