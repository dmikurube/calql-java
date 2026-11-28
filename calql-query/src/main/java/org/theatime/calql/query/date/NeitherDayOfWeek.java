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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * An atom that tests whether the day of week of a date is none of the given days of week,
 * such as {@code dayOfWeek not in [SATURDAY, SUNDAY]}.
 *
 * <p>Its negation is {@link EitherDayOfWeek} of the same days of week.
 */
public final class NeitherDayOfWeek extends MembershipDateAtom<DayOfWeek> {
    private NeitherDayOfWeek(final Set<DayOfWeek> daysOfWeek) {
        super(daysOfWeek, true);
    }

    /**
     * Returns an atom that tests whether the day of week of a date is not {@code dayOfWeek},
     * such as {@code dayOfWeek <> SATURDAY}.
     *
     * @param dayOfWeek  the day of week, not null
     * @return the atom
     * @throws NullPointerException  if {@code dayOfWeek} is null
     */
    public static NeitherDayOfWeek of(final DayOfWeek dayOfWeek) {
        return new NeitherDayOfWeek(Set.of(dayOfWeek));
    }

    /**
     * Returns an atom that tests whether the day of week of a date is none of {@code daysOfWeek},
     * such as {@code dayOfWeek not in [SATURDAY, SUNDAY]}.
     *
     * <p>Duplicated days of week are removed.
     *
     * @param daysOfWeek  the days of week, not null, and not containing null
     * @return the atom
     * @throws NullPointerException  if {@code daysOfWeek} is null, or contains null
     */
    public static NeitherDayOfWeek of(final Collection<DayOfWeek> daysOfWeek) {
        return new NeitherDayOfWeek(Set.copyOf(daysOfWeek));
    }

    /**
     * Returns an atom that tests whether the day of week of a date is none of {@code daysOfWeek},
     * such as {@code dayOfWeek not in [SATURDAY, SUNDAY]}.
     *
     * <p>Duplicated days of week are removed.
     *
     * @param daysOfWeek  the days of week, not null, and not containing null
     * @return the atom
     * @throws NullPointerException  if {@code daysOfWeek} is null, or contains null
     */
    public static NeitherDayOfWeek of(final DayOfWeek... daysOfWeek) {
        return of(Arrays.asList(daysOfWeek));
    }

    /**
     * Returns the days of week that this atom tests the day of week of a date against.
     *
     * <p>A date matches this atom if its day of week is none of them.
     *
     * @return the days of week, an unmodifiable set in no particular order
     */
    public Set<DayOfWeek> daysOfWeek() {
        return this.values();
    }

    @Override
    public DateAtom negate() {
        return EitherDayOfWeek.of(this.values());
    }

    @Override
    String columnName() {
        return "dayOfWeek";
    }

    @Override
    DayOfWeek columnValueOf(final LocalDate date) {
        return date.getDayOfWeek();
    }
}
