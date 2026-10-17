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

public abstract class NegationNormalFormula<T extends Comparable<? super T>> extends Formula<T> {
    @Override
    public final NegationNormalFormula<T> toNegationNormalForm() {
        return this;
    }

    /**
     * Expands this Negation Normal Form (NNF) to a Disjunctive Normal Form (DNF).
     *
     * <p>Note that the DNF can grow exponentially larger than this NNF, and building it can exhaust time and memory.
     * The DNF is built by distributing "and"s over "or"s, and the number of conjunctions in the DNF is:
     *
     * <ul>
     * <li>one for an atom,
     * <li>the sum of the numbers for its formulae for an "or", and
     * <li>the product of the numbers for its formulae for an "and".
     * </ul>
     *
     * <p>It grows exponentially when an "and" has many formulae of two or more conjunctions each, typically "or"s.
     * For example, {@code (f1 OR f2) AND (f3 OR f4) AND ... AND (f39 OR f40)}, an "and" of 20 "or"s of 2 atoms each,
     * has 2^20 = 1,048,576 conjunctions in DNF.
     *
     * <p>Such an "and" of "or"s also comes from negating an "or" of "and"s. For example, the negation of
     * {@code (f1 AND f2) OR (f3 AND f4) OR ... OR (f39 AND f40)} is
     * {@code (NOT f1 OR NOT f2) AND (NOT f3 OR NOT f4) AND ... AND (NOT f39 OR NOT f40)} in NNF, which has the same
     * number of conjunctions in DNF.
     *
     * <p>On the other hand, the number grows only linearly in the size of this NNF if every "and" has at most one
     * formula of two or more conjunctions.
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
}
