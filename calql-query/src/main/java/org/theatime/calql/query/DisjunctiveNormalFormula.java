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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A formula in Disjunctive Normal Form (DNF) is a logical formula consisting of a disjunction of conjunctions.
 * The DNF is represented as a {@link Set} of conjunctions.
 *
 * <p>Example: {@code (a1 AND a2) OR (a3 AND a4 AND a5) OR (a6) OR (a7 AND a8 AND a9)}
 *
 * <p>Duplicated conjunctions are removed when the DNF is built.
 * For example, {@code (a1 AND a2) OR (a2 AND a1)} is reduced to only one conjunction.
 *
 * <p>The order of its conjunctions does not matter to its equality, as it is a {@link Set}.
 * On the other hand, it iterates over its conjunctions in the order in which they first appear.
 *
 * <p>Note that an empty {@link DisjunctiveNormalFormula} "equals" an empty {@link Conjunction}, which means
 * {@code DisjunctiveNormalFormula.of().equals(Conjunction.of())} is {@code true} as both are empty {@link Set}s,
 * while an empty DNF logically represents {@code false}, and an empty conjunction is evaluated as {@code true}.
 *
 * @param <T> the type of values that the atoms in this DNF are evaluated on
 * @see <a href="https://en.wikipedia.org/wiki/Disjunctive_normal_form">Disjunctive normal form</a>
 */
public final class DisjunctiveNormalFormula<T extends Comparable<? super T>> extends AbstractSet<Conjunction<T>> {
    private DisjunctiveNormalFormula(final Collection<Conjunction<T>> conjunctions) {
        Objects.requireNonNull(conjunctions, "conjunctions");

        // LinkedHashSet removes duplicated conjunctions, and keeps the order in which they first appear,
        // so that iterating over the conjunctions, and toString(), are stable.
        //
        // Set.copyOf() is not used, as its iteration order is unspecified.
        final LinkedHashSet<Conjunction<T>> copiedConjunctions = new LinkedHashSet<>();
        for (final Conjunction<T> conjunction : conjunctions) {
            copiedConjunctions.add(Objects.requireNonNull(conjunction, "conjunctions contains null"));
        }
        this.conjunctions = Collections.unmodifiableSet(copiedConjunctions);
    }

    /**
     * Returns the DNF of {@code conjunctions}.
     *
     * <p>Duplicated conjunctions are removed. The DNF of no conjunctions is false.
     *
     * @param <T>  the type of values that the atoms in the DNF are evaluated on
     * @param conjunctions  the conjunctions to "or", not null, and not containing null
     * @return the DNF of {@code conjunctions}
     * @throws NullPointerException  if {@code conjunctions} is null, or contains null
     */
    public static <T extends Comparable<? super T>> DisjunctiveNormalFormula<T> of(final Collection<Conjunction<T>> conjunctions) {
        return new DisjunctiveNormalFormula<T>(conjunctions);
    }

    /**
     * Returns the DNF of {@code conjunctions}.
     *
     * <p>Duplicated conjunctions are removed. The DNF of no conjunctions is false.
     *
     * @param <T>  the type of values that the atoms in the DNF are evaluated on
     * @param conjunctions  the conjunctions to "or", not null, and not containing null
     * @return the DNF of {@code conjunctions}
     * @throws NullPointerException  if {@code conjunctions} is null, or contains null
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> DisjunctiveNormalFormula<T> of(final Conjunction<T>... conjunctions) {
        return of(Arrays.asList(conjunctions));
    }

    /**
     * Returns a new DNF of the conjunctions of this DNF and {@code additionalConjunctions}.
     *
     * <p>This DNF is not changed. Duplicated conjunctions are removed, and the conjunctions of this DNF come first.
     *
     * @param additionalConjunctions  the conjunctions to add, not null, and not containing null
     * @return a new DNF of the conjunctions of this DNF and {@code additionalConjunctions}
     * @throws NullPointerException  if {@code additionalConjunctions} is null, or contains null
     */
    public DisjunctiveNormalFormula<T> with(final Collection<Conjunction<T>> additionalConjunctions) {
        final ArrayList<Conjunction<T>> newConjunctions = new ArrayList<>(this.conjunctions);
        newConjunctions.addAll(additionalConjunctions);
        return new DisjunctiveNormalFormula<T>(newConjunctions);
    }

    /**
     * Returns a new DNF of the conjunctions of this DNF and {@code additionalConjunctions}.
     *
     * <p>This DNF is not changed. Duplicated conjunctions are removed, and the conjunctions of this DNF come first.
     *
     * @param additionalConjunctions  the conjunctions to add, not null, and not containing null
     * @return a new DNF of the conjunctions of this DNF and {@code additionalConjunctions}
     * @throws NullPointerException  if {@code additionalConjunctions} is null, or contains null
     */
    @SafeVarargs
    public final DisjunctiveNormalFormula<T> with(final Conjunction<T>... additionalConjunctions) {
        return this.with(Arrays.asList(additionalConjunctions));
    }

    @Override
    public Iterator<Conjunction<T>> iterator() {
        return this.conjunctions.iterator();
    }

    @Override
    public int size() {
        return this.conjunctions.size();
    }

    @Override
    public boolean contains(final Object object) {
        return this.conjunctions.contains(object);
    }

    @Override
    public String toString() {
        return this.conjunctions.stream().map(Object::toString).collect(Collectors.joining(" OR ", "(", ")"));
    }

    private final Set<Conjunction<T>> conjunctions;
}
