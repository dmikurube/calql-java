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
 * A formula in Negation Normal Form (NNF), which consists only of "and"s, "or"s, and atoms.
 *
 * <p>It has no "not", as the negation of an atom is always an atom. See {@link Atom#negate()}.
 *
 * @param <T> the type of values that this formula is evaluated on
 * @see <a href="https://en.wikipedia.org/wiki/Negation_normal_form">Negation normal form</a>
 */
public abstract class NegationNormalFormula<T extends Comparable<? super T>> extends Formula<T> {
    /**
     * Sole constructor. (For invocation by subclass constructors, typically implicit.)
     */
    protected NegationNormalFormula() {
    }

    @Override
    public final NegationNormalFormula<T> toNegationNormalForm() {
        return this;
    }

    /**
     * Expands this Negation Normal Form (NNF) to a Disjunctive Normal Form (DNF).
     *
     * <p>Note that the DNF can grow exponentially larger than this NNF, and building it can exhaust time and memory.
     * The DNF is built by distributing "and"s over "or"s, and the number of conjunctions in the DNF is at most:
     *
     * <ul>
     * <li>one for an atom,
     * <li>the sum of the numbers for its operands for an "or", and
     * <li>the product of the numbers for its operands for an "and".
     * </ul>
     *
     * <p>The expanded DNF can have fewer conjunctions, as duplicated atoms in a conjunction, and duplicated conjunctions,
     * are removed. The duplicates in the DNF of an "and" are removed only after all the combinations of its operands
     * are built, so removing them does not save the time and memory to build the combinations.
     * See {@link Conjunction} and {@link DisjunctiveNormalFormula}.
     *
     * <p>It grows exponentially when an "and" has many operands of two or more conjunctions each, typically "or"s.
     * For example, {@code (a1 OR a2) AND (a3 OR a4) AND ... AND (a39 OR a40)}, an "and" of 20 "or"s of 2 atoms each,
     * has 2^20 = 1,048,576 conjunctions in DNF.
     *
     * <p>Such an "and" of "or"s also comes from negating an "or" of "and"s. For example, the negation of
     * {@code (a1 AND a2) OR (a3 AND a4) OR ... OR (a39 AND a40)} is
     * {@code (NOT a1 OR NOT a2) AND (NOT a3 OR NOT a4) AND ... AND (NOT a39 OR NOT a40)} in NNF, which has the same
     * number of conjunctions in DNF.
     *
     * <p>On the other hand, the number grows only linearly in the size of this NNF if every "and" has at most one
     * operand of two or more conjunctions.
     *
     * <p>Note that this {@link NegationNormalFormula} instance does not cache the expanded DNF in it.
     *
     * @return the DNF of this NNF
     */
    public abstract DisjunctiveNormalFormula<T> expandToDisjunctiveNormalForm();

    /**
     * Returns a range that contains all the values that this Negation Normal Form (NNF) should match.
     *
     * <p>Note that the range may also contain values that this NNF does not match. It is to narrow down
     * the values to search, and every value in the range should still be tested by this NNF afterwards.
     *
     * <p>It is computed from the ranges of the atoms without expanding it to DNF.
     *
     * @return a range that contains all the values that this NNF should match
     */
    public abstract Range<T> range();

    /**
     * Returns the negation of this Negation Normal Form (NNF), which is still an NNF.
     *
     * <p>It is the same as {@link #negateInNegationNormalForm()}, which takes time linear in the size of this NNF,
     * unlike {@link Formula#negate()} that just wraps a formula with {@link Not}.
     *
     * @return the negation of this NNF in NNF
     */
    @Override
    public NegationNormalFormula<T> negate() {
        // Possible optimization: it builds a new NNF every time, in time linear in the size of this NNF. The negation
        // could be kept in this instance, or the negation could keep this NNF so that negating it again returns this
        // instance, at the cost of memory.
        return this.negateInNegationNormalForm();
    }
}
