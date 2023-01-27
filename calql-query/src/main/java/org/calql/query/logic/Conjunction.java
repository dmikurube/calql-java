/*
 * Copyright 2021-2023 Dai MIKURUBE
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
import java.util.stream.Collectors;

/**
 * A logical conjuction is the "and" of a set of atoms.
 *
 * <p>Ex. {@code f1 AND f2 AND f3 AND f4}
 */
public final class Conjunction extends AbstractList<Atom> {
    private Conjunction(final ArrayList<Atom> atoms) {
        this.atoms = Collections.unmodifiableList(atoms);
    }

    public static Conjunction of(final Collection<Atom> atoms) {
        return new Conjunction(new ArrayList<>(atoms));
    }

    public static Conjunction of(final Atom... atoms) {
        return of(Arrays.asList(atoms));
    }

    public Conjunction with(final Collection<Atom> additionalAtoms) {
        final ArrayList<Atom> newAtoms = new ArrayList<>(this.atoms);
        newAtoms.addAll(atoms);
        return new Conjunction(newAtoms);
    }

    public Conjunction with(final Atom... additionalAtoms) {
        return this.with(Arrays.asList(additionalAtoms));
    }

    public boolean isTriviallyUnique() {
        return false;
    }

    public boolean isTriviallyFinite() {
        return false;
    }

    @Override
    public int size() {
        return this.atoms.size();
    }

    @Override
    public Atom get(final int index) {
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

    private final List<Atom> atoms;
}
