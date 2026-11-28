/*
 * Copyright 2021-2026 Dai MIKURUBE
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
 * An atom that tests whether the day of month of a date is one of the given days of month,
 * such as {@code dayOfMonth in [1, 15]}.
 *
 * <p>Its negation is {@link NeitherDayOfMonth} of the same days of month.
 */
public final class EitherDayOfMonth extends MembershipDateAtom<Integer> {
    private EitherDayOfMonth(final Set<Integer> daysOfMonth) {
        super(daysOfMonth, false);
    }

    /**
     * Returns an atom that tests whether the day of month of a date is {@code dayOfMonth},
     * such as {@code dayOfMonth = 1}.
     *
     * @param dayOfMonth  the day of month
     * @return the atom
     */
    public static EitherDayOfMonth of(final int dayOfMonth) {
        return new EitherDayOfMonth(Set.of(dayOfMonth));
    }

    /**
     * Returns an atom that tests whether the day of month of a date is one of {@code daysOfMonth},
     * such as {@code dayOfMonth in [1, 15]}.
     *
     * <p>Duplicated days of month are removed.
     *
     * @param daysOfMonth  the days of month, not null, and not containing null
     * @return the atom
     * @throws NullPointerException  if {@code daysOfMonth} is null, or contains null
     */
    public static EitherDayOfMonth of(final Collection<Integer> daysOfMonth) {
        return new EitherDayOfMonth(Set.copyOf(daysOfMonth));
    }

    /**
     * Returns an atom that tests whether the day of month of a date is one of {@code daysOfMonth},
     * such as {@code dayOfMonth in [1, 15]}.
     *
     * <p>Duplicated days of month are removed.
     *
     * @param daysOfMonth  the days of month, not null
     * @return the atom
     * @throws NullPointerException  if {@code daysOfMonth} is null
     */
    public static EitherDayOfMonth of(final int... daysOfMonth) {
        return of(Arrays.stream(daysOfMonth).boxed().collect(Collectors.toSet()));
    }

    /**
     * Returns the days of month that this atom tests the day of month of a date against.
     *
     * <p>A date matches this atom if its day of month is one of them.
     *
     * @return the days of month, an unmodifiable set in no particular order
     */
    public Set<Integer> daysOfMonth() {
        return this.values();
    }

    @Override
    public DateAtom negate() {
        return NeitherDayOfMonth.of(this.values());
    }

    @Override
    String columnName() {
        return "dayOfMonth";
    }

    @Override
    Integer columnValueOf(final LocalDate date) {
        return date.getDayOfMonth();
    }
}
