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

public final class BeforeYear extends OrderingDateAtom {
    private BeforeYear(final int year, final boolean inclusive) {
        super(year, inclusive, false);
    }

    public static BeforeYear of(final int year) {
        return new BeforeYear(year, false);
    }

    public static BeforeYear of(final int year, final boolean inclusive) {
        return new BeforeYear(year, inclusive);
    }

    public static BeforeYear orEqualTo(final int year) {
        return new BeforeYear(year, true);
    }

    @Override
    public Optional<LocalDate> latest() {
        if (this.inclusive()) {
            return Optional.of(LocalDate.of(this.value(), 12, 31));
        } else {
            return Optional.of(LocalDate.of(this.value() - 1, 12, 31));
        }
    }

    @Override
    public DateAtom negate() {
        return AfterYear.of(this.value(), !this.inclusive());
    }

    @Override
    protected String columnName() {
        return "year";
    }

    @Override
    protected int columnValueOf(final LocalDate date) {
        return date.getYear();
    }
}
