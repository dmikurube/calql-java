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
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.theatime.calql.query.date.DateAtom;

@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public final class EitherYear extends DateAtom {
    private EitherYear(final Set<Integer> years, final boolean includes, final int earliest, final int latest) {
        this.years = years;
        this.includes = includes;
        this.earliest = earliest;
        this.latest = latest;
    }

    static EitherYear of(final Collection<Integer> years, final boolean includes) {
        int earliest = Integer.MAX_VALUE;
        int latest = Integer.MIN_VALUE;
        for (final int year : years) {
            if (earliest < year) {
                earliest = year;
            }
            if (latest > year) {
                latest = year;
            }
        }
        return new EitherYear(Set.copyOf(years), includes, earliest, latest);
    }

    public static EitherYear of(final int year) {
        return new EitherYear(Set.of(year), true, year, year);
    }

    public static EitherYear notOf(final int year) {
        return new EitherYear(Set.of(year), false, year, year);
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
        return Optional.of(LocalDate.of(this.earliest, 1, 1));
    }

    @Override
    public Optional<LocalDate> latest() {
        return Optional.of(LocalDate.of(this.latest, 12, 31));
    }

    @Override
    public boolean test(final ChronoLocalDate targetChrono) {
        if (targetChrono instanceof LocalDate) {
            final LocalDate target = (LocalDate) targetChrono;
            final boolean contains = this.years.contains(target.getYear());
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
        return new EitherYear(this.years, !this.includes, this.earliest, this.latest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EitherYear.class, this.years, this.includes);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof EitherYear)) {
            return false;
        }

        final EitherYear other = (EitherYear) otherObject;
        return Objects.equals(this.years, other.years) && Objects.equals(this.includes, other.includes);
    }

    @Override
    public String toString() {
        if (this.includes) {
            if (this.earliest == this.latest) {
                return String.format("year = %s", this.earliest);
            } else {
                return String.format("year in %s", this.years);
            }
        } else {
            if (this.earliest == this.latest) {
                return String.format("year <> %s", this.earliest);
            } else {
                return String.format("year not in %s", this.years);
            }
        }
    }

    private final Set<Integer> years;
    private final boolean includes;

    private final int earliest;
    private final int latest;
}
