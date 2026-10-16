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
import java.util.Optional;

public final class AfterYear extends OrderingDateAtom {
    private AfterYear(final int year, final boolean inclusive) {
        super(year, inclusive, true);
    }

    public static AfterYear of(final int year) {
        return new AfterYear(year, false);
    }

    public static AfterYear of(final int year, final boolean inclusive) {
        return new AfterYear(year, inclusive);
    }

    public static AfterYear orEqualTo(final int year) {
        return new AfterYear(year, true);
    }

    @Override
    public Optional<LocalDate> earliest() {
        if (this.inclusive()) {
            return Optional.of(LocalDate.of(this.value(), 1, 1));
        } else {
            return Optional.of(LocalDate.of(this.value() + 1, 1, 1));
        }
    }

    @Override
    public DateAtom negate() {
        return BeforeYear.of(this.value(), !this.inclusive());
    }

    @Override
    String columnName() {
        return "year";
    }

    @Override
    int columnValueOf(final LocalDate date) {
        return date.getYear();
    }
}
