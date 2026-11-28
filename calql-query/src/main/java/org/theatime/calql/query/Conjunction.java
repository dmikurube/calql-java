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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A logical conjunction is the "and" of a set of atoms. The conjunction is represented as a {@link Set} of atoms.
 *
 * <p>Ex. {@code f1 AND f2 AND f3 AND f4}
 *
 * <p>Duplicated atoms are removed when the conjunction is built.
 *
 * <p>The order of its atoms does not matter to its equality, as it is a {@link Set}.
 * On the other hand, it iterates over its atoms in the order in which they first appear.
 *
 * <p>Note that an empty {@link Conjunction} "equals" an empty {@link DisjunctiveNormalFormula}, which means
 * {@code Conjunction.of().equals(DisjunctiveNormalFormula.of())} is {@code true} as both are empty {@link Set}s,
 * while an empty conjunction is evaluated as {@code true}, and an empty DNF logically represents {@code false}.
 */
public final class Conjunction<T extends Comparable<? super T>> extends AbstractSet<Atom<T>> implements Predicate<T> {
    private Conjunction(final Collection<Atom<T>> atoms) {
        Objects.requireNonNull(atoms, "atoms");

        // LinkedHashSet removes duplicated atoms, and keeps the order in which they first appear,
        // so that iterating over the atoms, and toString(), are stable.
        //
        // Set.copyOf() is not used, as its iteration order is unspecified.
        final LinkedHashSet<Atom<T>> copiedAtoms = new LinkedHashSet<>();
        for (final Atom<T> atom : atoms) {
            copiedAtoms.add(Objects.requireNonNull(atom, "atoms contains null"));
        }
        this.atoms = Collections.unmodifiableSet(copiedAtoms);

        Range<T> range = Range.whole();
        for (final Atom<T> atom : this.atoms) {
            range = range.intersection(atom.range());
        }
        this.range = range;
    }

    public static <T extends Comparable<? super T>> Conjunction<T> of(final Collection<Atom<T>> atoms) {
        return new Conjunction<T>(atoms);
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> Conjunction<T> of(final Atom<T>... atoms) {
        return of(Arrays.asList(atoms));
    }

    public Conjunction<T> with(final Collection<Atom<T>> additionalAtoms) {
        final ArrayList<Atom<T>> newAtoms = new ArrayList<>(this.atoms);
        newAtoms.addAll(additionalAtoms);
        return new Conjunction<T>(newAtoms);
    }

    @SafeVarargs
    public final Conjunction<T> with(final Atom<T>... additionalAtoms) {
        return this.with(Arrays.asList(additionalAtoms));
    }

    public Stream<T> streamBy(
            final SourceStreamer<T> sourceStreamer,
            final Order order) {
        // Generate the efficient "source" stream based on Conjunction (this, itself) -- sourceStreamFrom(this)
        // Finalize the "source" stream with filtering by Conjunction (this, itself) -- filter(this)
        return sourceStreamer.sourceStreamFrom(this, order).filter(this);
    }

    /**
     * Returns a range that contains all the values that this conjunction should match.
     *
     * <p>It is the intersection of the ranges of its atoms. Note that it may also contain values that this conjunction
     * does not match, like {@link Atom#range()}.
     *
     * @return a range that contains all the values that this conjunction should match
     */
    public Range<T> range() {
        return this.range;
    }

    @Override
    public Iterator<Atom<T>> iterator() {
        return this.atoms.iterator();
    }

    @Override
    public int size() {
        return this.atoms.size();
    }

    @Override
    public boolean contains(final Object object) {
        return this.atoms.contains(object);
    }

    /**
     * Tests whether non-null {@code target} satisfies all the atoms in this conjunction.
     *
     * <p>It returns true if this conjunction has no atoms, as the empty conjunction is true.
     *
     * @param target  the value to test, not null
     * @return true if {@code target} satisfies all the atoms
     * @throws NullPointerException  if {@code target} is null
     */
    @Override
    public boolean test(final T target) {
        Objects.requireNonNull(target, "target");
        for (final Atom<T> atom : this.atoms) {
            if (!atom.test(target)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return this.atoms.stream().map(Object::toString).collect(Collectors.joining(" AND ", "(", ")"));
    }

    private final Set<Atom<T>> atoms;

    private final Range<T> range;
}
