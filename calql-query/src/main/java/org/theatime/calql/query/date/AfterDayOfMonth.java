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

public final class AfterDayOfMonth extends OrderingDateAtom {
    private AfterDayOfMonth(final int dayOfMonth, final boolean inclusive) {
        super(dayOfMonth, inclusive, true);
    }

    public static AfterDayOfMonth of(final int dayOfMonth) {
        return new AfterDayOfMonth(dayOfMonth, false);
    }

    public static AfterDayOfMonth of(final int dayOfMonth, final boolean inclusive) {
        return new AfterDayOfMonth(dayOfMonth, inclusive);
    }

    public static AfterDayOfMonth orEqualTo(final int dayOfMonth) {
        return new AfterDayOfMonth(dayOfMonth, true);
    }

    @Override
    public DateAtom negate() {
        return BeforeDayOfMonth.of(this.value(), !this.inclusive());
    }

    @Override
    protected String columnName() {
        return "dayOfMonth";
    }

    @Override
    protected int columnValueOf(final LocalDate date) {
        return date.getDayOfMonth();
    }
}
