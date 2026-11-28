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
 * An atom that tests whether the month of a date is none of the given months,
 * such as {@code month not in [1, 2]}.
 *
 * <p>Its negation is {@link EitherMonth} of the same months.
 */
public final class NeitherMonth extends MembershipDateAtom<Integer> {
    private NeitherMonth(final Set<Integer> months) {
        super(months, true);
    }

    /**
     * Returns an atom that tests whether the month of a date is not {@code month},
     * such as {@code month <> 1}.
     *
     * @param month  the month
     * @return the atom
     */
    public static NeitherMonth of(final int month) {
        return new NeitherMonth(Set.of(month));
    }

    /**
     * Returns an atom that tests whether the month of a date is none of {@code months},
     * such as {@code month not in [1, 2]}.
     *
     * <p>Duplicated months are removed.
     *
     * @param months  the months, not null, and not containing null
     * @return the atom
     * @throws NullPointerException  if {@code months} is null, or contains null
     */
    public static NeitherMonth of(final Collection<Integer> months) {
        return new NeitherMonth(Set.copyOf(months));
    }

    /**
     * Returns an atom that tests whether the month of a date is none of {@code months},
     * such as {@code month not in [1, 2]}.
     *
     * <p>Duplicated months are removed.
     *
     * @param months  the months, not null
     * @return the atom
     * @throws NullPointerException  if {@code months} is null
     */
    public static NeitherMonth of(final int... months) {
        return of(Arrays.stream(months).boxed().collect(Collectors.toSet()));
    }

    /**
     * Returns the months that this atom tests the month of a date against.
     *
     * <p>A date matches this atom if its month is none of them.
     *
     * @return the months, an unmodifiable set in no particular order
     */
    public Set<Integer> months() {
        return this.values();
    }

    @Override
    public DateAtom negate() {
        return EitherMonth.of(this.values());
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
