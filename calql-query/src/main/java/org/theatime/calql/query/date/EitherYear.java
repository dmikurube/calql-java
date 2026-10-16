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
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public final class EitherYear extends MembershipDateAtom<Integer> {
    private EitherYear(final Set<Integer> years, final boolean includes) {
        super(years, includes);
    }

    static EitherYear of(final Collection<Integer> years, final boolean includes) {
        return new EitherYear(Set.copyOf(years), includes);
    }

    public static EitherYear of(final int year) {
        return new EitherYear(Set.of(year), true);
    }

    public static EitherYear notOf(final int year) {
        return new EitherYear(Set.of(year), false);
    }

    public static EitherYear of(final Collection<Integer> years) {
        return of(years, true);
    }

    public static EitherYear notOf(final Collection<Integer> years) {
        return of(years, false);
    }

    public static EitherYear of(final int... years) {
        return of(Arrays.stream(years).boxed().collect(Collectors.toSet()));
    }

    public static EitherYear notOf(final int... years) {
        return notOf(Arrays.stream(years).boxed().collect(Collectors.toSet()));
    }

    @Override
    public Optional<LocalDate> earliest() {
        if (!this.includes()) {
            return Optional.empty();
        }
        if (this.values().isEmpty()) {
            // Nothing matches. Return an earliest date after the latest date in the same way as EitherDate.
            return Optional.of(LocalDate.MAX);
        }
        return Optional.of(LocalDate.of(Collections.min(this.values()), 1, 1));
    }

    @Override
    public Optional<LocalDate> latest() {
        if (!this.includes()) {
            return Optional.empty();
        }
        if (this.values().isEmpty()) {
            // Nothing matches. Return a latest date before the earliest date in the same way as EitherDate.
            return Optional.of(LocalDate.MIN);
        }
        return Optional.of(LocalDate.of(Collections.max(this.values()), 12, 31));
    }

    @Override
    public DateAtom negate() {
        return new EitherYear(this.values(), !this.includes());
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
