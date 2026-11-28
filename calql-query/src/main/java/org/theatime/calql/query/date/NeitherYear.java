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
import java.util.stream.Collectors;

/**
 * An atom that tests whether the year of a date is none of the given years,
 * such as {@code year not in [2020, 2022]}.
 *
 * <p>Its negation is {@link EitherYear} of the same years.
 */
public final class NeitherYear extends MembershipDateAtom<Integer> {
    private NeitherYear(final Set<Integer> years) {
        super(years, true);
    }

    public static NeitherYear of(final int year) {
        return new NeitherYear(Set.of(year));
    }

    public static NeitherYear of(final Collection<Integer> years) {
        return new NeitherYear(Set.copyOf(years));
    }

    public static NeitherYear of(final int... years) {
        return of(Arrays.stream(years).boxed().collect(Collectors.toSet()));
    }

    /**
     * Returns the years that this atom tests the year of a date against.
     *
     * <p>A date matches this atom if its year is none of them.
     *
     * @return the years, an unmodifiable set in no particular order
     */
    public Set<Integer> years() {
        return this.values();
    }

    @Override
    public DateAtom negate() {
        return EitherYear.of(this.values());
    }

    @Override
    String columnName() {
        return "year";
    }

    @Override
    Integer columnValueOf(final LocalDate date) {
        return date.getYear();
    }
}
