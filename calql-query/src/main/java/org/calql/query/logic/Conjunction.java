/*
 * Copyright 2021-2024 Dai MIKURUBE
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

package org.calql.query.logic;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A logical conjuction is the "and" of a set of atoms.
 *
 * <p>Ex. {@code f1 AND f2 AND f3 AND f4}
 */
public final class Conjunction<T extends Comparable<T>> extends AbstractList<Atom<T>> {
    private Conjunction(final ArrayList<Atom<T>> atoms) {
        this.atoms = Collections.unmodifiableList(atoms);

        T totalEarliest = null;
        T totalLatest = null;
        T totalUnique = null;
        boolean isUnique = true;

        for (final Atom<T> atom : atoms) {
            final Optional<? extends T> atomEarliest = atom.earliest();
            if (atomEarliest.isPresent() && (totalEarliest == null || totalEarliest.compareTo(atomEarliest.get()) < 0)) {
                totalEarliest = atomEarliest.get();
            }

            final Optional<? extends T> atomLatest = atom.latest();
            if (atomLatest.isPresent() && (totalLatest == null || totalLatest.compareTo(atomLatest.get()) > 0)) {
                totalLatest = atomLatest.get();
            }

            final Optional<? extends T> atomUnique = atom.unique();
            if (isUnique && atomUnique.isPresent()) {
                if (totalUnique == null) {
                    totalUnique = atomUnique.get();
                } else if (!totalUnique.equals(atomUnique.get())) {
                    isUnique = false;
                }
            }
        }

        if (totalEarliest != null && totalLatest != null && totalEarliest.compareTo(totalLatest) > 0) {
            this.earliest = Optional.empty();
            this.latest = Optional.empty();
            this.unique = Optional.empty();
        } else {
            this.earliest = Optional.ofNullable(totalEarliest);
            this.latest = Optional.ofNullable(totalLatest);
            this.unique = isUnique ? Optional.ofNullable(totalUnique) : Optional.empty();
        }
    }

    public static <T extends Comparable<T>> Conjunction<T> of(final Collection<Atom<T>> atoms) {
        return new Conjunction<T>(new ArrayList<>(atoms));
    }

    @SafeVarargs
    public static <T extends Comparable<T>> Conjunction<T> of(final Atom<T>... atoms) {
        return of(Arrays.asList(atoms));
    }

    public Conjunction<T> with(final Collection<Atom<T>> additionalAtoms) {
        final ArrayList<Atom<T>> newAtoms = new ArrayList<>(this.atoms);
        newAtoms.addAll(atoms);
        return new Conjunction<T>(newAtoms);
    }

    @SafeVarargs
    public final Conjunction<T> with(final Atom<T>... additionalAtoms) {
        return this.with(Arrays.asList(additionalAtoms));
    }

    public Optional<T> earliest() {
        return this.earliest;
    }

    public Optional<T> latest() {
        return this.latest;
    }

    public Optional<T> unique() {
        return this.unique;
    }

    @Override
    public int size() {
        return this.atoms.size();
    }

    @Override
    public Atom<T> get(final int index) {
        return this.atoms.get(index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Conjunction.class, this.atoms);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == Conjunction.class && this.atoms.equals(((Conjunction) obj).atoms);
    }

    @Override
    public String toString() {
        return this.atoms.stream().map(Object::toString).collect(Collectors.joining(" AND ", "(", ")"));
    }

    private final List<Atom<T>> atoms;

    private final Optional<T> earliest;
    private final Optional<T> latest;
    private final Optional<T> unique;
}
