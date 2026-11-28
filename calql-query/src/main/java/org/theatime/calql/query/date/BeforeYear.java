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
 * An atom that tests whether the year of a date is less than the given year,
 * such as {@code year < 2020}, or less than or equal to it, such as {@code year <= 2020}.
 *
 * <p>Its negation is {@link AfterYear} of the same year with the opposite inclusiveness,
 * such as {@code year >= 2020} for {@code year < 2020}.
 */
public final class BeforeYear extends OrderingDateAtom {
    private BeforeYear(final int year, final boolean inclusive) {
        super(year, inclusive, false);
    }

    /**
     * Returns an atom that tests whether the year of a date is less than {@code year},
     * such as {@code year < 2020}.
     *
     * @param year  the year to compare the year of a date with
     * @return the atom
     */
    public static BeforeYear of(final int year) {
        return new BeforeYear(year, false);
    }

    /**
     * Returns an atom that tests whether the year of a date is less than {@code year},
     * or also equal to it if {@code inclusive} is true.
     *
     * @param year  the year to compare the year of a date with
     * @param inclusive  true to match also the dates whose year equals {@code year}
     * @return the atom
     */
    public static BeforeYear of(final int year, final boolean inclusive) {
        return new BeforeYear(year, inclusive);
    }

    /**
     * Returns an atom that tests whether the year of a date is less than or equal to {@code year},
     * such as {@code year <= 2020}.
     *
     * @param year  the year to compare the year of a date with
     * @return the atom
     */
    public static BeforeYear orEqualTo(final int year) {
        return new BeforeYear(year, true);
    }

    /**
     * Returns the year that this atom compares the year of a date with.
     *
     * <p>A date matches this atom if its year is less than it, or if its year equals it and
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
            return Range.atMost(LocalDate.of(this.value(), 12, 31));
        } else {
            return Range.atMost(LocalDate.of(this.value() - 1, 12, 31));
        }
    }

    @Override
    public DateAtom negate() {
        return AfterYear.of(this.value(), !this.isInclusive());
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
