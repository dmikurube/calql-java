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
public final class EitherMonth extends MembershipDateAtom<Integer> {
    private EitherMonth(final Set<Integer> months, final boolean includes) {
        super(months, includes);
    }

    static EitherMonth of(final Collection<Integer> months, final boolean includes) {
        return new EitherMonth(Set.copyOf(months), includes);
    }

    public static EitherMonth of(final int month) {
        return new EitherMonth(Set.of(month), true);
    }

    public static EitherMonth notOf(final int month) {
        return new EitherMonth(Set.of(month), false);
    }

    public static EitherMonth of(final Collection<Integer> months) {
        return of(months, true);
    }

    public static EitherMonth notOf(final Collection<Integer> months) {
        return of(months, false);
    }

    public static EitherMonth of(final int... months) {
        return of(Arrays.stream(months).boxed().collect(Collectors.toSet()));
    }

    public static EitherMonth notOf(final int... months) {
        return notOf(Arrays.stream(months).boxed().collect(Collectors.toSet()));
    }

    @Override
    public DateAtom negate() {
        return new EitherMonth(this.values(), !this.includes());
    }

    @Override
    protected String columnName() {
        return "month";
    }

    @Override
    protected Integer columnValueOf(final LocalDate date) {
        return date.getMonthValue();
    }
}
