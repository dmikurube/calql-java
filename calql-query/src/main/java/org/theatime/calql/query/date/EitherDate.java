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
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public final class EitherDate extends MembershipDateAtom<LocalDate> {
    private EitherDate(final Set<LocalDate> dates, final boolean includes) {
        super(dates, includes);
    }

    static EitherDate of(final Collection<LocalDate> dates, final boolean includes) {
        return new EitherDate(Set.copyOf(dates), includes);
    }

    public static EitherDate of(final int year, final int month, final int dayOfMonth) {
        return of(LocalDate.of(year, month, dayOfMonth));
    }

    public static EitherDate notOf(final int year, final int month, final int dayOfMonth) {
        return notOf(LocalDate.of(year, month, dayOfMonth));
    }

    public static EitherDate of(final LocalDate date) {
        return new EitherDate(Set.of(date), true);
    }

    public static EitherDate notOf(final LocalDate date) {
        return new EitherDate(Set.of(date), false);
    }

    public static EitherDate of(final Collection<LocalDate> dates) {
        return of(dates, true);
    }

    public static EitherDate notOf(final Collection<LocalDate> dates) {
        return of(dates, false);
    }

    public static EitherDate of(final LocalDate... dates) {
        return of(Set.of(dates), true);
    }

    public static EitherDate notOf(final LocalDate... dates) {
        return of(Set.of(dates), false);
    }

    public Set<LocalDate> dates() {
        return this.values();
    }

    @Override
    public Optional<LocalDate> earliest() {
        if (!this.includes()) {
            return Optional.empty();
        }
        if (this.values().isEmpty()) {
            // Nothing matches. Return an earliest date after the latest date.
            return Optional.of(LocalDate.MAX);
        }
        return Optional.of(Collections.min(this.values()));
    }

    @Override
    public Optional<LocalDate> latest() {
        if (!this.includes()) {
            return Optional.empty();
        }
        if (this.values().isEmpty()) {
            // Nothing matches. Return a latest date before the earliest date.
            return Optional.of(LocalDate.MIN);
        }
        return Optional.of(Collections.max(this.values()));
    }

    @Override
    public Optional<LocalDate> unique() {
        if (!this.includes()) {
            return Optional.empty();
        }
        if (this.values().size() == 1) {
            return Optional.of(this.values().iterator().next());
        }
        return Optional.empty();
    }

    @Override
    public DateAtom negate() {
        return new EitherDate(this.values(), !this.includes());
    }

    @Override
    String columnName() {
        return "date";
    }

    @Override
    LocalDate columnValueOf(final LocalDate date) {
        return date;
    }
}
