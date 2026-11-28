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
 * An atomic formula, which has no formulae in it, such as {@code month = 6}.
 *
 * <p>Its negation is always an atom, so that a formula in Negation Normal Form (NNF) never needs "not".
 * See {@link #negate()}.
 *
 * @param <T> the type of values that this atom is evaluated on
 */
public abstract class Atom<T extends Comparable<? super T>> extends NegationNormalFormula<T> {
    /**
     * Sole constructor. (For invocation by subclass constructors, typically implicit.)
     */
    protected Atom() {
    }

    @Override
    public final NegationNormalFormula<T> negateInNegationNormalForm() {
        return this.negate();
    }

    /**
     * Expands this atom to a Disjunctive Normal Form (DNF), which is a disjunction of only one conjunction of this atom.
     *
     * @return the DNF of this atom
     */
    @Override
    public final DisjunctiveNormalFormula<T> expandToDisjunctiveNormalForm() {
        return DisjunctiveNormalFormula.<T>of(Conjunction.<T>of(this));
    }

    /**
     * Returns the class of values that this atom is evaluated on,
     * such as {@link java.time.LocalDate} for the date atoms.
     *
     * <p>It declares the space of values that this atom belongs to,
     * so that atoms of different spaces can be told apart when spaces other than dates are added.
     *
     * @return the class of values that this atom is evaluated on
     */
    public abstract Class<? extends T> unit();

    /**
     * Returns a range that contains all the values that this atom should match.
     *
     * <p>Note that the range may also contain values that this atom does not match. It is to narrow down the values
     * to search, and every value in the range is still tested by this atom afterwards.
     *
     * <p>It returns {@link Range#whole()} if this atom cannot narrow them down.
     *
     * @return a range that contains all the values that this atom should match
     */
    @Override
    public abstract Range<T> range();

    /**
     * Returns an atom that negates this atom.
     *
     * <p>The negation of an atom is always an atom, and negating it twice returns an atom equal to the original.
     * It keeps the negation of a formula in Negation Normal Form (NNF) from growing.
     *
     * @return the negated atom
     */
    @Override
    public abstract Atom<T> negate();
}
