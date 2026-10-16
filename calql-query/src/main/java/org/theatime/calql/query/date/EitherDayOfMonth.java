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

@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public final class EitherDayOfMonth extends MembershipDateAtom<Integer> {
    private EitherDayOfMonth(final Set<Integer> daysOfMonth, final boolean includes) {
        super(daysOfMonth, includes);
    }

    static EitherDayOfMonth of(final Collection<Integer> daysOfMonth, final boolean includes) {
        return new EitherDayOfMonth(Set.copyOf(daysOfMonth), includes);
    }

    public static EitherDayOfMonth of(final int dayOfMonth) {
        return new EitherDayOfMonth(Set.of(dayOfMonth), true);
    }

    public static EitherDayOfMonth notOf(final int dayOfMonth) {
        return new EitherDayOfMonth(Set.of(dayOfMonth), false);
    }

    public static EitherDayOfMonth of(final Collection<Integer> daysOfMonth) {
        return of(daysOfMonth, true);
    }

    public static EitherDayOfMonth notOf(final Collection<Integer> daysOfMonth) {
        return of(daysOfMonth, false);
    }

    public static EitherDayOfMonth of(final int... daysOfMonth) {
        return of(Arrays.stream(daysOfMonth).boxed().collect(Collectors.toSet()));
    }

    public static EitherDayOfMonth notOf(final int... daysOfMonth) {
        return notOf(Arrays.stream(daysOfMonth).boxed().collect(Collectors.toSet()));
    }

    @Override
    public DateAtom negate() {
        return new EitherDayOfMonth(this.values(), !this.includes());
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
