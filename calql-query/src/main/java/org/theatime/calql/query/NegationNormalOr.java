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
 * An "or" operator in negation normal form.
 *
 * <p>The order and duplicates of its operands matter to its equality, unlike {@link DisjunctiveNormalFormula}.
 * A formula in negation normal form keeps the structure of the original formula as written.
 *
 * @param <T> the type of values that this formula is evaluated on
 */
public final class NegationNormalOr<T extends Comparable<? super T>> extends NegationNormalCompound<T> {
    private NegationNormalOr(final Collection<NegationNormalFormula<T>> operands) {
        // List.copyOf() rejects null elements, so that a null operand fails early here.
        this.operands = List.copyOf(operands);
    }

    /**
     * Returns the "or" of {@code operands} in negation normal form.
     *
     * <p>The "or" of no operands is false.
     *
     * @param <T>  the type of values that the formula is evaluated on
     * @param operands  the formulae to "or", not null, and not containing null
     * @return the "or" of {@code operands}
     * @throws NullPointerException  if {@code operands} is null, or contains null
     */
    public static <T extends Comparable<? super T>> NegationNormalFormula<T> of(final Collection<NegationNormalFormula<T>> operands) {
        return new NegationNormalOr<T>(operands);
    }

    /**
     * Returns the "or" of {@code operands} in negation normal form.
     *
     * <p>The "or" of no operands is false.
     *
     * @param <T>  the type of values that the formula is evaluated on
     * @param operands  the formulae to "or", not null, and not containing null
     * @return the "or" of {@code operands}
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
     * Tests whether non-null {@code target} satisfies any of the operands of this "or".
     *
     * <p>It returns false if this "or" has no operands, as the empty disjunction is false.
     *
     * @param target  the value to test, not null
     * @return true if {@code target} satisfies any of the operands
     * @throws NullPointerException  if {@code target} is null
     */
    @Override
    public boolean test(final T target) {
        Objects.requireNonNull(target, "target");
        // Possible optimization: testing the operands that are cheap and likely to be true first would return true
        // earlier. Atoms of the same column could also be merged, such as "month = 1 or month = 2" into
        // "month in (1, 2)", and an "or" that contains an empty "and" is always true. They are left to an optimizer, as
        // they would change the structure as written.
        for (final NegationNormalFormula<T> operand : this.operands) {
            if (operand.test(target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NegationNormalFormula<T> negateInNegationNormalForm() {
        return NegationNormalAnd.of(this.operands.stream().map(f -> f.negateInNegationNormalForm()).collect(Collectors.toList()));
    }

    /**
     * Expands this Negation Normal Form (NNF) to a Disjunctive Normal Form (DNF).
     *
     * <p>The number of conjunctions in the DNF of an "or" is at most the sum of the numbers for its operands.
     * It does not grow exponentially by this "or" itself, but it can by its operands.
     * See {@link NegationNormalFormula#expandToDisjunctiveNormalForm()} for when it grows.
     *
     * @return the DNF of this NNF
     */
    @Override
    public DisjunctiveNormalFormula<T> expandToDisjunctiveNormalForm() {
        return concatenateToDisjunctiveNormalForm(this.operands);
    }

    /**
     * Returns a range that contains all the values that this "or" should match.
     *
     * <p>It is the span of the ranges of its operands, or the empty range if this "or" has no operands. Note that the
     * span may also contain values between the ranges, which none of its operands match. See {@link Range#span(Range)}.
     *
     * @return a range that contains all the values that this "or" should match
     */
    @Override
    public Range<T> range() {
        Range<T> range = Range.empty();
        for (final NegationNormalFormula<T> operand : this.operands) {
            range = range.span(operand.range());
        }
        return range;
    }

    @Override
    public int hashCode() {
        return Objects.hash(NegationNormalOr.class, this.operands);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        if (otherObject instanceof NegationNormalOr) {
            final NegationNormalOr<?> other = (NegationNormalOr<?>) otherObject;
            return Objects.equals(this.operands, other.operands);
        }

        return false;
    }

    @Override
    public String toString() {
        return this.operands.stream().map(Object::toString).collect(Collectors.joining(" OR ", "(", ")"));
    }

    private static <T extends Comparable<? super T>> DisjunctiveNormalFormula<T> concatenateToDisjunctiveNormalForm(final List<NegationNormalFormula<T>> negationNormalFormulae) {
        final ArrayList<Conjunction<T>> conjunctions = new ArrayList<>();
        final List<DisjunctiveNormalFormula<T>> dnfs = negationNormalFormulae.stream().map(NegationNormalFormula<T>::expandToDisjunctiveNormalForm).collect(Collectors.toList());
        for (final DisjunctiveNormalFormula<T> dnf : dnfs) {
            for (final Conjunction<T> conjunction : dnf) {
                conjunctions.add(conjunction);
            }
        }
        return DisjunctiveNormalFormula.of(conjunctions);
    }

    // NegationNormalOr preserves its operands in a List of NegationNormalFormula,
    // not in a Set like DisjunctiveNormalFormula.
    //
    // This is an intentional design choice. A formula in negation normal form is expected to be optimized later.
    // The equality and de-duplication behavior of Set could conflict with potential optimizations.
    // Normalizing the structure, including removing the duplicates, is left to optimizers,
    // like reordering and merging the operands noted in test().
    //
    // Even if it used a Set, it would not deliver the full benefits of the Set.
    // For example, "(f1 OR f2) OR f3" and "f1 OR (f2 OR f3)" would not be normalized to the same form by Set.
    // Building a Set would also cost hashCode() of the operands every time an NNF is built, which traverses each subtree.
    private final List<NegationNormalFormula<T>> operands;
}
