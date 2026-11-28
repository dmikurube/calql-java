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
 * An atom that tests whether the day of month of a date is none of the given days of month,
 * such as {@code dayOfMonth not in [1, 15]}.
 *
 * <p>Its negation is {@link EitherDayOfMonth} of the same days of month.
 */
public final class NeitherDayOfMonth extends MembershipDateAtom<Integer> {
    private NeitherDayOfMonth(final Set<Integer> daysOfMonth) {
        super(daysOfMonth, true);
    }

    public static NeitherDayOfMonth of(final int dayOfMonth) {
        return new NeitherDayOfMonth(Set.of(dayOfMonth));
    }

    public static NeitherDayOfMonth of(final Collection<Integer> daysOfMonth) {
        return new NeitherDayOfMonth(Set.copyOf(daysOfMonth));
    }

    public static NeitherDayOfMonth of(final int... daysOfMonth) {
        return of(Arrays.stream(daysOfMonth).boxed().collect(Collectors.toSet()));
    }

    /**
     * Returns the days of month that this atom tests the day of month of a date against.
     *
     * <p>A date matches this atom if its day of month is none of them.
     *
     * @return the days of month, an unmodifiable set in no particular order
     */
    public Set<Integer> daysOfMonth() {
        return this.values();
    }

    @Override
    public DateAtom negate() {
        return EitherDayOfMonth.of(this.values());
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
