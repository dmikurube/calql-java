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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An "and" operator in negation normal form.
 *
 * <p>The order and duplicates of its operands matter to its equality, unlike {@link Conjunction}.
 * A formula in negation normal form keeps the structure of the original formula as written.
 *
 * @param <T> the type of values that this formula is evaluated on
 */
public final class NegationNormalAnd<T extends Comparable<? super T>> extends NegationNormalCompound<T> {
    private NegationNormalAnd(final Collection<NegationNormalFormula<T>> operands) {
        // List.copyOf() rejects null elements, so that a null operand fails early here.
        this.operands = List.copyOf(operands);
    }

    /**
     * Returns the "and" of {@code operands} in negation normal form.
     *
     * <p>The "and" of no operands is true.
     *
     * @param <T>  the type of values that the formula is evaluated on
     * @param operands  the formulae to "and", not null, and not containing null
     * @return the "and" of {@code operands}
     * @throws NullPointerException  if {@code operands} is null, or contains null
     */
    public static <T extends Comparable<? super T>> NegationNormalFormula<T> of(final Collection<NegationNormalFormula<T>> operands) {
        return new NegationNormalAnd<T>(operands);
    }

    /**
     * Returns the "and" of {@code operands} in negation normal form.
     *
     * <p>The "and" of no operands is true.
     *
     * @param <T>  the type of values that the formula is evaluated on
     * @param operands  the formulae to "and", not null, and not containing null
     * @return the "and" of {@code operands}
     * @throws NullPointerException  if {@code operands} is null, or contains null
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> NegationNormalFormula<T> of(final NegationNormalFormula<T>... operands) {
        return of(Arrays.asList(operands));
    }

    @Override
    public List<NegationNormalFormula<T>> operands() {
        return this.operands;
    }

    /**
     * Tests whether non-null {@code target} satisfies all the operands of this "and".
     *
     * <p>It returns true if this "and" has no operands, as the empty conjunction is true.
     *
     * @param target  the value to test, not null
     * @return true if {@code target} satisfies all the operands
     * @throws NullPointerException  if {@code target} is null
     */
    @Override
    public boolean test(final T target) {
        Objects.requireNonNull(target, "target");
        // Possible optimization: testing the operands that are cheap and likely to be false first would return false
        // earlier. Atoms of the same column could also be merged, such as "month in (1, 2) and month in (2, 3)" into
        // "month = 2", and an "and" that contains an empty "or" is always false. They are left to an optimizer, as they
        // would change the structure as written.
        for (final NegationNormalFormula<T> operand : this.operands) {
            if (!operand.test(target)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Expands this Negation Normal Form (NNF) to a Disjunctive Normal Form (DNF).
     *
     * <p>CalQL converts a given WHERE clause in a given query to DNF to process the WHERE clause easily and mechanically.
     *
     * <p>For example, {@code (a1 OR a2) AND a3} is expanded to {@code (a1 AND a3) OR (a2 AND a3)}.
     *
     * <p>Note that the number of conjunctions in the DNF of an "and" is at most the product of the numbers for its
     * operands, which can grow exponentially.
     * See {@link NegationNormalFormula#expandToDisjunctiveNormalForm()} for when it grows.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Disjunctive_normal_form">Disjunctive normal form</a>
     * @see <a href="https://github.com/aimacode/aima-java/blob/aima3e-v1.9.1/aima-core/src/main/java/aima/core/logic/propositional/visitors/ConvertToDNF.java">Example from Artificial Intelligence: A Modern Approach</a>
     */
    @Override
    public DisjunctiveNormalFormula<T> expandToDisjunctiveNormalForm() {
        return distributeToDisjunctiveNormalForm(this.operands);
    }

    /**
     * Returns a range that contains all the values that this "and" should match.
     *
     * <p>It is the intersection of the ranges of its operands, or the whole range if this "and" has no operands.
     *
     * @return a range that contains all the values that this "and" should match
     */
    @Override
    public Range<T> range() {
        Range<T> range = Range.whole();
        for (final NegationNormalFormula<T> operand : this.operands) {
            range = range.intersection(operand.range());
        }
        return range;
    }

    @Override
    public NegationNormalFormula<T> negateInNegationNormalForm() {
        return NegationNormalOr.of(
                this.operands.stream().map(f -> f.negateInNegationNormalForm()).collect(Collectors.toList()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(NegationNormalAnd.class, this.operands);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        if (otherObject instanceof NegationNormalAnd) {
            final NegationNormalAnd<?> other = (NegationNormalAnd<?>) otherObject;
            return Objects.equals(this.operands, other.operands);
        }

        return false;
    }

    @Override
    public String toString() {
        return this.operands.stream().map(Object::toString).collect(Collectors.joining(" AND ", "(", ")"));
    }

    private static <T extends Comparable<? super T>> DisjunctiveNormalFormula<T> distributeToDisjunctiveNormalForm(final List<NegationNormalFormula<T>> negationNormalFormulae) {
        if (negationNormalFormulae.isEmpty()) {
            // The empty "and" is true, which is a disjunction of the empty conjunction.
            return DisjunctiveNormalFormula.<T>of(Conjunction.<T>of());
        }

        final List<DisjunctiveNormalFormula<T>> dnfs =
                negationNormalFormulae.stream().map(NegationNormalFormula<T>::expandToDisjunctiveNormalForm).collect(Collectors.toList());
        for (final DisjunctiveNormalFormula<T> dnf : dnfs) {
            if (dnf.isEmpty()) {
                // A false operand makes the whole "and" false, which has no conjunctions. Return early without
                // distributing the other operands, which could be exponentially many before reaching the false one.
                return DisjunctiveNormalFormula.<T>of();
            }
        }

        final ArrayList<Conjunction<T>> conjunctions = new ArrayList<>();
        iter(0, dnfs, new ArrayList<Atom<T>>(), conjunctions);
        return DisjunctiveNormalFormula.<T>of(conjunctions);
    }

    private static <T extends Comparable<? super T>> void iter(
            final int index,
            final List<DisjunctiveNormalFormula<T>> dnfs,
            final ArrayList<Atom<T>> visitingConjunction,
            final ArrayList<Conjunction<T>> built) {
        final DisjunctiveNormalFormula<T> dnf = dnfs.get(index);

        final int sizeOfVisitingConjunction = visitingConjunction.size();
        for (final Conjunction<T> conjunction : dnf) {
            visitingConjunction.addAll(conjunction);
            if (index + 1 < dnfs.size()) {
                iter(index + 1, dnfs, visitingConjunction, built);
            } else {
                built.add(Conjunction.of(visitingConjunction));
            }
            visitingConjunction.subList(sizeOfVisitingConjunction, visitingConjunction.size()).clear();
        }
    }

    // NegationNormalAnd preserves its operands in a List of NegationNormalFormula, not in a Set like Conjunction.
    //
    // This is an intentional design choice. A formula in negation normal form is expected to be optimized later.
    // The equality and de-duplication behavior of Set could conflict with potential optimizations.
    // Normalizing the structure, including removing the duplicates, is left to optimizers,
    // like reordering and merging the operands noted in test().
    //
    // Even if it used a Set, it would not deliver the full benefits of the Set.
    // For example, "(f1 AND f2) AND f3" and "f1 AND (f2 AND f3)" would not be normalized to the same form by Set.
    // Building a Set would also cost hashCode() of the operands every time an NNF is built, which traverses each subtree.
    private final List<NegationNormalFormula<T>> operands;
}
