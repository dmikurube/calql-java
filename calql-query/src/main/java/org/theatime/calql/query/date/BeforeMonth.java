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
 * An atom that tests whether the month of a date is less than the given month,
 * such as {@code month < 6}, or less than or equal to it, such as {@code month <= 6}.
 *
 * <p>Its negation is {@link AfterMonth} of the same month with the opposite inclusiveness,
 * such as {@code month >= 6} for {@code month < 6}.
 */
public final class BeforeMonth extends OrderingDateAtom {
    private BeforeMonth(final int month, final boolean inclusive) {
        super(month, inclusive, false);
    }

    /**
     * Returns an atom that tests whether the month of a date is less than {@code month},
     * such as {@code month < 6}.
     *
     * @param month  the month to compare the month of a date with
     * @return the atom
     */
    public static BeforeMonth of(final int month) {
        return new BeforeMonth(month, false);
    }

    /**
     * Returns an atom that tests whether the month of a date is less than {@code month},
     * or also equal to it if {@code inclusive} is true.
     *
     * @param month  the month to compare the month of a date with
     * @param inclusive  true to match also the dates whose month equals {@code month}
     * @return the atom
     */
    public static BeforeMonth of(final int month, final boolean inclusive) {
        return new BeforeMonth(month, inclusive);
    }

    /**
     * Returns an atom that tests whether the month of a date is less than or equal to {@code month},
     * such as {@code month <= 6}.
     *
     * @param month  the month to compare the month of a date with
     * @return the atom
     */
    public static BeforeMonth orEqualTo(final int month) {
        return new BeforeMonth(month, true);
    }

    /**
     * Returns the month that this atom compares the month of a date with.
     *
     * <p>A date matches this atom if its month is less than it, or if its month equals it and
     * {@link #isInclusive()} is true.
     *
     * @return the month
     */
    public int month() {
        return this.value();
    }

    @Override
    public DateAtom negate() {
        return AfterMonth.of(this.value(), !this.isInclusive());
    }

    @Override
    String columnName() {
        return "month";
    }

    @Override
    int columnValueOf(final LocalDate date) {
        return date.getMonthValue();
    }
}
