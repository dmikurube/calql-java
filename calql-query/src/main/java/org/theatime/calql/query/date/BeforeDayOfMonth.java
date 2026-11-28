/*
 * Copyright 2022-2026 Dai MIKURUBE
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

/**
 * An atom that tests whether the day of month of a date is less than the given day of month,
 * such as {@code dayOfMonth < 15}, or less than or equal to it, such as {@code dayOfMonth <= 15}.
 *
 * <p>Its negation is {@link AfterDayOfMonth} of the same day of month with the opposite inclusiveness,
 * such as {@code dayOfMonth >= 15} for {@code dayOfMonth < 15}.
 */
public final class BeforeDayOfMonth extends OrderingDateAtom {
    private BeforeDayOfMonth(final int dayOfMonth, final boolean inclusive) {
        super(dayOfMonth, inclusive, false);
    }

    /**
     * Returns an atom that tests whether the day of month of a date is less than {@code dayOfMonth},
     * such as {@code dayOfMonth < 15}.
     *
     * @param dayOfMonth  the day of month to compare the day of month of a date with
     * @return the atom
     */
    public static BeforeDayOfMonth of(final int dayOfMonth) {
        return new BeforeDayOfMonth(dayOfMonth, false);
    }

    /**
     * Returns an atom that tests whether the day of month of a date is less than {@code dayOfMonth},
     * or also equal to it if {@code inclusive} is true.
     *
     * @param dayOfMonth  the day of month to compare the day of month of a date with
     * @param inclusive  true to match also the dates whose day of month equals {@code dayOfMonth}
     * @return the atom
     */
    public static BeforeDayOfMonth of(final int dayOfMonth, final boolean inclusive) {
        return new BeforeDayOfMonth(dayOfMonth, inclusive);
    }

    /**
     * Returns an atom that tests whether the day of month of a date is less than or equal to {@code dayOfMonth},
     * such as {@code dayOfMonth <= 15}.
     *
     * @param dayOfMonth  the day of month to compare the day of month of a date with
     * @return the atom
     */
    public static BeforeDayOfMonth orEqualTo(final int dayOfMonth) {
        return new BeforeDayOfMonth(dayOfMonth, true);
    }

    /**
     * Returns the day of month that this atom compares the day of month of a date with.
     *
     * <p>A date matches this atom if its day of month is less than it, or if its day of month equals it and
     * {@link #isInclusive()} is true.
     *
     * @return the day of month
     */
    public int dayOfMonth() {
        return this.value();
    }

    @Override
    public DateAtom negate() {
        return AfterDayOfMonth.of(this.value(), !this.isInclusive());
    }

    @Override
    String columnName() {
        return "dayOfMonth";
    }

    @Override
    int columnValueOf(final LocalDate date) {
        return date.getDayOfMonth();
    }
}
