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
import org.theatime.calql.query.Range;

/**
 * An atom that tests whether the year of a date is greater than the given year,
 * such as {@code year > 2020}, or greater than or equal to it, such as {@code year >= 2020}.
 *
 * <p>Its negation is {@link BeforeYear} of the same year with the opposite inclusiveness,
 * such as {@code year <= 2020} for {@code year > 2020}.
 */
public final class AfterYear extends OrderingDateAtom {
    private AfterYear(final int year, final boolean inclusive) {
        super(year, inclusive, true);
    }

    /**
     * Returns an atom that tests whether the year of a date is greater than {@code year},
     * such as {@code year > 2020}.
     *
     * @param year  the year to compare the year of a date with
     * @return the atom
     */
    public static AfterYear of(final int year) {
        return new AfterYear(year, false);
    }

    /**
     * Returns an atom that tests whether the year of a date is greater than {@code year},
     * or also equal to it if {@code inclusive} is true.
     *
     * @param year  the year to compare the year of a date with
     * @param inclusive  true to match also the dates whose year equals {@code year}
     * @return the atom
     */
    public static AfterYear of(final int year, final boolean inclusive) {
        return new AfterYear(year, inclusive);
    }

    /**
     * Returns an atom that tests whether the year of a date is greater than or equal to {@code year},
     * such as {@code year >= 2020}.
     *
     * @param year  the year to compare the year of a date with
     * @return the atom
     */
    public static AfterYear orEqualTo(final int year) {
        return new AfterYear(year, true);
    }

    /**
     * Returns the year that this atom compares the year of a date with.
     *
     * <p>A date matches this atom if its year is greater than it, or if its year equals it and
     * {@link #isInclusive()} is true.
     *
     * @return the year
     */
    public int year() {
        return this.value();
    }

    @Override
    public Range<LocalDate> range() {
        if (this.isInclusive()) {
            return Range.atLeast(LocalDate.of(this.value(), 1, 1));
        } else {
            return Range.atLeast(LocalDate.of(this.value() + 1, 1, 1));
        }
    }

    @Override
    public DateAtom negate() {
        return BeforeYear.of(this.value(), !this.isInclusive());
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
