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

public final class BeforeDayOfMonth extends OrderingDateAtom {
    private BeforeDayOfMonth(final int dayOfMonth, final boolean inclusive) {
        super(dayOfMonth, inclusive, false);
    }

    public static BeforeDayOfMonth of(final int dayOfMonth) {
        return new BeforeDayOfMonth(dayOfMonth, false);
    }

    public static BeforeDayOfMonth of(final int dayOfMonth, final boolean inclusive) {
        return new BeforeDayOfMonth(dayOfMonth, inclusive);
    }

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
