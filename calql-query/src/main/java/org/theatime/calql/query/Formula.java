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

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A logical formula over values of type {@code T}, such as {@link java.time.LocalDate}.
 *
 * @param <T> the type of values that this formula is evaluated on. It is bounded by
 *   {@code Comparable<? super T>}, not by {@code Comparable<T>}, so that types comparable
 *   through their supertype can be used. For example, {@link java.time.LocalDate} implements
 *   {@code Comparable<ChronoLocalDate>}, not {@code Comparable<LocalDate>}.
 */
public abstract class Formula<T extends Comparable<? super T>> implements Predicate<T> {
    /**
     * Tests whether non-null {@code target} satisfies this formula.
     *
     * <p>{@link Formula} implements {@link Predicate}, so that a formula can be used directly where a {@link Predicate}
     * is needed, such as {@link java.util.stream.Stream#filter(Predicate)}. Its {@link #and(Formula)},
     * {@link #or(Formula)}, and {@link #negate()} return a formula, not just a {@link Predicate}.
     *
     * @param target  the value to test, not null
     * @return true if {@code target} satisfies this formula
     * @throws NullPointerException  if {@code target} is null
     */
    @Override
    public abstract boolean test(T target);

    /**
     * Returns the "and" of this formula and non-null {@code other}, which is still a formula.
     *
     * <p>It overloads {@link Predicate#and(Predicate)}, which returns a {@link Predicate} that is no longer a formula.
     * Note that {@link Predicate#and(Predicate)} is still called if {@code other} is not statically typed as
     * a {@link Formula}, such as a lambda expression, or a formula held in a variable of type {@link Predicate}.
     *
     * @param other  the formula to "and" with this formula, not null
     * @return the "and" of this formula and {@code other}, same as {@code And.of(this, other)}
     * @throws NullPointerException  if {@code other} is null
     */
    public Formula<T> and(final Formula<T> other) {
        Objects.requireNonNull(other, "other");
        // Possible optimization: chaining and() nests the "and"s, such as And(And(a, b), c), which is not equal to
        // And.of(a, b, c), and makes the recursion deeper. It could flatten the formulae that are And, but it would
        // change the structure as written, and copying the formulae every time would make chaining n formulae O(n^2).
        // An overload for NegationNormalFormula could also return a NegationNormalAnd, to save converting it to
        // negation normal form later, at the cost of another public method.
        return And.of(this, other);
    }

    /**
     * Returns the "or" of this formula and non-null {@code other}, which is still a formula.
     *
     * <p>It overloads {@link Predicate#or(Predicate)}, which returns a {@link Predicate} that is no longer a formula.
     * Note that {@link Predicate#or(Predicate)} is still called if {@code other} is not statically typed as
     * a {@link Formula}, such as a lambda expression, or a formula held in a variable of type {@link Predicate}.
     *
     * @param other  the formula to "or" with this formula, not null
     * @return the "or" of this formula and {@code other}, same as {@code Or.of(this, other)}
     * @throws NullPointerException  if {@code other} is null
     */
    public Formula<T> or(final Formula<T> other) {
        Objects.requireNonNull(other, "other");
        // Possible optimization: chaining or() nests the "or"s, such as Or(Or(a, b), c), which is not equal to
        // Or.of(a, b, c), and makes the recursion deeper. It could flatten the formulae that are Or, but it would
        // change the structure as written, and copying the formulae every time would make chaining n formulae O(n^2).
        // An overload for NegationNormalFormula could also return a NegationNormalOr, to save converting it to
        // negation normal form later, at the cost of another public method.
        return Or.of(this, other);
    }

    /**
     * Returns the negation of this formula, which is still a formula.
     *
     * <p>It overrides {@link Predicate#negate()}, which would return a {@link Predicate} that is no longer a formula.
     * It wraps this formula as is with {@code Not.of(this)}, unlike {@link #negateInNegationNormalForm()}.
     *
     * @return the negation of this formula, same as {@code Not.of(this)}
     */
    @Override
    public Formula<T> negate() {
        // Possible optimization: Not could override it to return its formula, so that negating a formula twice returns
        // the original formula, like Atom#negate(). It is not done yet, as it would change the structure as written,
        // from Not(Not(f)) to f.
        return Not.of(this);
    }

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
