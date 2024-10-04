/*
 * Copyright 2021-2024 Dai MIKURUBE
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
import java.time.chrono.ChronoLocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.theatime.calql.query.date.DateAtom;

@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public final class EitherDate extends DateAtom {
    private EitherDate(final Set<LocalDate> dates, final boolean includes, final LocalDate earliest, final LocalDate latest) {
        this.dates = dates;
        this.includes = includes;
        this.earliest = earliest;
        this.latest = latest;
    }

    static EitherDate of(final Collection<LocalDate> dates, final boolean includes) {
        LocalDate earliest = LocalDate.MAX;
        LocalDate latest = LocalDate.MIN;
        for (final LocalDate date : dates) {
            if (earliest.compareTo(date) > 0) {
                earliest = date;
            }
            if (latest.compareTo(date) < 0) {
                latest = date;
            }
        }
        return new EitherDate(Set.copyOf(dates), includes, earliest, latest);
    }

    public static EitherDate of(final int year, final int month, final int dayOfMonth) {
        return of(LocalDate.of(year, month, dayOfMonth));
    }

    public static EitherDate notOf(final int year, final int month, final int dayOfMonth) {
        return notOf(LocalDate.of(year, month, dayOfMonth));
    }

    public static EitherDate of(final LocalDate date) {
        return new EitherDate(Set.of(date), true, date, date);
    }

    public static EitherDate notOf(final LocalDate date) {
        return new EitherDate(Set.of(date), false, date, date);
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

    @Override
    public Optional<LocalDate> earliest() {
        return Optional.of(this.earliest);
    }

    @Override
    public Optional<LocalDate> latest() {
        return Optional.of(this.latest);
    }

    @Override
    public Optional<LocalDate> unique() {
        if (earliest.equals(latest)) {
            return Optional.of(this.earliest);
        }
        return Optional.empty();
    }

    @Override
    public boolean test(final ChronoLocalDate targetChrono) {
        if (targetChrono instanceof LocalDate) {
            final LocalDate target = (LocalDate) targetChrono;
            final boolean contains = this.dates.contains(target);
            if (this.includes) {
                return contains;
            } else {
                return !contains;
            }
        }
        return false;
    }

    /**
     * Negates this formula.
     *
     * <p>It negates the formula in Negation Normal Form (NNF).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Negation_normal_form">Negation normal form</a>
     *
     * @return the negated formula in Negation Normal Form (NNF)
     */
    @Override
    public DateAtom negate() {
        return new EitherDate(this.dates, this.includes, this.earliest, this.latest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EitherDate.class, this.dates, this.includes);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof EitherDate)) {
            return false;
        }

        final EitherDate other = (EitherDate) otherObject;
        return Objects.equals(this.dates, other.dates) && Objects.equals(this.includes, other.includes);
    }

    @Override
    public String toString() {
        if (this.includes) {
            if (this.earliest == this.latest) {
                return String.format("date = %s", this.earliest);
            } else {
                return String.format("date in %s", this.dates);
            }
        } else {
            if (this.earliest == this.latest) {
                return String.format("date <> %s", this.earliest);
            } else {
                return String.format("date not in %s", this.dates);
            }
        }
    }

    private final Set<LocalDate> dates;
    private final boolean includes;

    private final LocalDate earliest;
    private final LocalDate latest;
}
