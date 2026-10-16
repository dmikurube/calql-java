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
 * An "or" operator in negation normal form.
 */
public final class NegationNormalOr<T extends Comparable<? super T>> extends NegationNormalCompound<T> {
    private NegationNormalOr(final ArrayList<NegationNormalFormula<T>> negationNormalFormulae) {
        this.negationNormalFormulae = Collections.unmodifiableList(negationNormalFormulae);
    }

    public static <T extends Comparable<? super T>> NegationNormalFormula<T> of(final Collection<NegationNormalFormula<T>> negationNormalFormulae) {
        return new NegationNormalOr<T>(new ArrayList<>(negationNormalFormulae));
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> NegationNormalFormula<T> of(final NegationNormalFormula<T>... negationNormalFormulae) {
        return of(Arrays.asList(negationNormalFormulae));
    }

    /**
     * Tests whether non-null {@code target} satisfies any of the formulae in this "or".
     *
     * <p>It returns false if this "or" has no formulae, as the empty disjunction is false.
     *
     * @param target  the value to test, not null
     * @return true if {@code target} satisfies any of the formulae
     * @throws NullPointerException  if {@code target} is null
     */
    @Override
    public boolean test(final T target) {
        Objects.requireNonNull(target, "target");
        for (final NegationNormalFormula<T> formula : this.negationNormalFormulae) {
            if (formula.test(target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NegationNormalFormula<T> negateInNegationNormalForm() {
        return NegationNormalAnd.of(this.negationNormalFormulae.stream().map(f -> f.negateInNegationNormalForm()).collect(Collectors.toList()));
    }

    /**
     * Expands this Negation Normal Form (NNF) to a Disjunctive Normal Form (DNF).
     *
     * <p>The number of conjunctions in the DNF of an "or" is the sum of the numbers for its formulae. It does not grow
     * exponentially by this "or" itself, but it can by its formulae. See
     * {@link NegationNormalFormula#expandToDisjunctiveNormalForm()} for when it grows.
     *
     * @return the DNF of this NNF
     */
    @Override
    public DisjunctiveNormalFormula<T> expandToDisjunctiveNormalForm() {
        return concatenateToDisjunctiveNormalForm(this.negationNormalFormulae);
    }

    @Override
    public int hashCode() {
        return Objects.hash(NegationNormalOr.class, this.negationNormalFormulae);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        if (otherObject instanceof NegationNormalOr) {
            final NegationNormalOr<?> other = (NegationNormalOr<?>) otherObject;
            return Objects.equals(this.negationNormalFormulae, other.negationNormalFormulae);
        }

        return false;
    }

    @Override
    public String toString() {
        return this.negationNormalFormulae.stream().map(Object::toString).collect(Collectors.joining(" OR ", "(", ")"));
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

    private final List<NegationNormalFormula<T>> negationNormalFormulae;
}
