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
 * An atom that tests whether the month of a date is one of the given months,
 * such as {@code month in [1, 2]}.
 *
 * <p>Its negation is {@link NeitherMonth} of the same months.
 */
public final class EitherMonth extends MembershipDateAtom<Integer> {
    private EitherMonth(final Set<Integer> months) {
        super(months, false);
    }

    public static EitherMonth of(final int month) {
        return new EitherMonth(Set.of(month));
    }

    public static EitherMonth of(final Collection<Integer> months) {
        return new EitherMonth(Set.copyOf(months));
    }

    public static EitherMonth of(final int... months) {
        return of(Arrays.stream(months).boxed().collect(Collectors.toSet()));
    }

    /**
     * Returns the months that this atom tests the month of a date against.
     *
     * <p>A date matches this atom if its month is one of them.
     *
     * @return the months, an unmodifiable set in no particular order
     */
    public Set<Integer> months() {
        return this.values();
    }

    @Override
    public DateAtom negate() {
        return NeitherMonth.of(this.values());
    }

    @Override
    String columnName() {
        return "month";
    }

    @Override
    Integer columnValueOf(final LocalDate date) {
        return date.getMonthValue();
    }
}
