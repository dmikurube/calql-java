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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An "and" operator in negation normal form.
 */
public final class NegationNormalAnd<T extends Comparable<? super T>> extends NegationNormalCompound<T> {
    private NegationNormalAnd(final ArrayList<NegationNormalFormula<T>> negationNormalFormulae) {
        this.negationNormalFormulae = Collections.unmodifiableList(negationNormalFormulae);
    }

    public static <T extends Comparable<? super T>> NegationNormalFormula<T> of(final Collection<NegationNormalFormula<T>> negationNormalFormulae) {
        return new NegationNormalAnd<T>(new ArrayList<>(negationNormalFormulae));
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> NegationNormalFormula<T> of(final NegationNormalFormula<T>... negationNormalFormulae) {
        return of(Arrays.asList(negationNormalFormulae));
    }

    /**
     * Tests whether non-null {@code target} satisfies all the formulae in this "and".
     *
     * <p>It returns true if this "and" has no formulae, as the empty conjunction is true.
     *
     * @param target  the value to test, not null
     * @return true if {@code target} satisfies all the formulae
     * @throws NullPointerException  if {@code target} is null
     */
    @Override
    public boolean test(final T target) {
        Objects.requireNonNull(target, "target");
        for (final NegationNormalFormula<T> formula : this.negationNormalFormulae) {
            if (!formula.test(target)) {
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
     * <p>Ex. {@code (f1 AND f2) OR (f3 AND f4 AND f5) OR (f6) OR (f7 AND f8 AND f9)}
     *
     * <p>Note that the number of conjunctions in the DNF of an "and" is the product of the numbers for its formulae,
     * which can grow exponentially. See {@link NegationNormalFormula#expandToDisjunctiveNormalForm()} for when it grows.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Disjunctive_normal_form">Disjunctive normal form</a>
     * @see <a href="https://github.com/aimacode/aima-java/blob/aima3e-v1.9.1/aima-core/src/main/java/aima/core/logic/propositional/visitors/ConvertToDNF.java">Example from Artificial Intelligence: A Modern Approach</a>
     */
    @Override
    public DisjunctiveNormalFormula<T> expandToDisjunctiveNormalForm() {
        return distributeToDisjunctiveNormalForm(this.negationNormalFormulae);
    }

    /**
     * Returns a range that contains all the values that this "and" should match.
     *
     * <p>It is the intersection of the ranges of its formulae, or the whole range if this "and" has no formulae.
     *
     * @return a range that contains all the values that this "and" should match
     */
    @Override
    public Range<T> range() {
        Range<T> range = Range.whole();
        for (final NegationNormalFormula<T> formula : this.negationNormalFormulae) {
            range = range.intersection(formula.range());
        }
        return range;
    }

    @Override
    public NegationNormalFormula<T> negateInNegationNormalForm() {
        return NegationNormalOr.of(
                this.negationNormalFormulae.stream().map(f -> f.negateInNegationNormalForm()).collect(Collectors.toList()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(NegationNormalAnd.class, this.negationNormalFormulae);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        if (otherObject instanceof NegationNormalAnd) {
            final NegationNormalAnd<?> other = (NegationNormalAnd<?>) otherObject;
            return Objects.equals(this.negationNormalFormulae, other.negationNormalFormulae);
        }

        return false;
    }

    @Override
    public String toString() {
        return this.negationNormalFormulae.stream().map(Object::toString).collect(Collectors.joining(" AND ", "(", ")"));
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
                // A false formula makes the whole "and" false, which has no conjunctions. Return early without
                // distributing the other formulae, which could be exponentially many before reaching the false one.
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

    private final List<NegationNormalFormula<T>> negationNormalFormulae;
}
