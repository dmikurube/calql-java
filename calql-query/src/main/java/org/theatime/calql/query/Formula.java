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

package org.theatime.calql.query;

/**
 * A logical formula over values of type {@code T}, such as {@link java.time.LocalDate}.
 *
 * @param <T> the type of values that this formula is evaluated on. It is bounded by
 *   {@code Comparable<? super T>}, not by {@code Comparable<T>}, so that types comparable
 *   through their supertype can be used. For example, {@link java.time.LocalDate} implements
 *   {@code Comparable<ChronoLocalDate>}, not {@code Comparable<LocalDate>}.
 */
public abstract class Formula<T extends Comparable<? super T>> {
    /**
     * Tests whether non-null {@code target} satisfies this formula.
     *
     * <p>{@link Formula} does not implement {@link java.util.function.Predicate}, because
     * {@link java.util.function.Predicate}'s {@code and()} and {@code or()} would accept any {@code Predicate},
     * and return a {@code Predicate} that is no longer a formula.
     *
     * <p>Use {@code formula::test} instead where a {@link java.util.function.Predicate} is needed.
     *
     * @param target  the value to test, not null
     * @return true if {@code target} satisfies this formula
     * @throws NullPointerException  if {@code target} is null
     */
    public abstract boolean test(T target);

    public abstract NegationNormalFormula<T> toNegationNormalForm();

    /**
     * Negates this formula.
     *
     * <p>It negates the formula in Negation Normal Form (NNF).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Negation_normal_form">Negation normal form</a>
     *
     * @return the negated formula in Negation Normal Form (NNF)
     */
    public abstract NegationNormalFormula<T> negateInNegationNormalForm();
}
