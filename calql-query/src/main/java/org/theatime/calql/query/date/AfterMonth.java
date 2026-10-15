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

public final class AfterMonth extends OrderingDateAtom {
    private AfterMonth(final int month, final boolean inclusive) {
        super(month, inclusive, true);
    }

    public static AfterMonth of(final int month) {
        return new AfterMonth(month, false);
    }

    public static AfterMonth of(final int month, final boolean inclusive) {
        return new AfterMonth(month, inclusive);
    }

    public static AfterMonth orEqualTo(final int month) {
        return new AfterMonth(month, true);
    }

    @Override
    public DateAtom negate() {
        return BeforeMonth.of(this.value(), !this.inclusive());
    }

    @Override
    protected String columnName() {
        return "month";
    }

    @Override
    protected int columnValueOf(final LocalDate date) {
        return date.getMonthValue();
    }
}
