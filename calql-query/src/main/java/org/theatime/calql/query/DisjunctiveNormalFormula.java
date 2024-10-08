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

package org.theatime.calql.query;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A formula in Disjunctive Normal Form (DNF) is a logical formula consisting of a disjunction of conjunctions.
 *
 * <p>Ex. {@code (f1 AND f2) OR (f3 AND f4 AND f5) OR (f6) OR (f7 AND f8 AND f9)}
 *
 * @see <a href="https://en.wikipedia.org/wiki/Disjunctive_normal_form">Disjunctive normal form</a>
 */
public final class DisjunctiveNormalFormula<T extends Comparable<T>> extends AbstractList<Conjunction<T>> {
    private DisjunctiveNormalFormula(final ArrayList<Conjunction<T>> conjunctions) {
        this.conjunctions = Collections.unmodifiableList(conjunctions);
    }

    public static <T extends Comparable<T>> DisjunctiveNormalFormula<T> of(final Collection<Conjunction<T>> conjunctions) {
        return new DisjunctiveNormalFormula<T>(new ArrayList<Conjunction<T>>(conjunctions));
    }

    @SafeVarargs
    public static <T extends Comparable<T>> DisjunctiveNormalFormula<T> of(final Conjunction<T>... conjunctions) {
        return of(Arrays.asList(conjunctions));
    }

    public DisjunctiveNormalFormula<T> with(final Collection<Conjunction<T>> additionalConjunctions) {
        final ArrayList<Conjunction<T>> newConjunctions = new ArrayList<>(this.conjunctions);
        newConjunctions.addAll(conjunctions);
        return new DisjunctiveNormalFormula<T>(newConjunctions);
    }

    @SafeVarargs
    public final DisjunctiveNormalFormula<T> with(final Conjunction<T>... additionalConjunctions) {
        return this.with(Arrays.asList(additionalConjunctions));
    }

    @Override
    public int size() {
        return this.conjunctions.size();
    }

    @Override
    public Conjunction<T> get(final int index) {
        return this.conjunctions.get(index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(DisjunctiveNormalFormula.class, this.conjunctions);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == DisjunctiveNormalFormula.class && this.conjunctions.equals(((DisjunctiveNormalFormula) obj).conjunctions);
    }

    @Override
    public String toString() {
        return this.conjunctions.stream().map(Object::toString).collect(Collectors.joining(" OR ", "(", ")"));
    }

    private final List<Conjunction<T>> conjunctions;
}
