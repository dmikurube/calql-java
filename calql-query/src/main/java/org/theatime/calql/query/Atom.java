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

import java.util.Optional;
import java.util.function.Predicate;

public abstract class Atom<T extends Comparable<? super T>> extends NegationNormalFormula<T> implements Predicate<T> {
    @Override
    public final NegationNormalFormula<T> negateInNegationNormalForm() {
        return this.negate();
    }

    @Override
    public final DisjunctiveNormalFormula<T> getDisjunctiveNormalForm() {
        // TODO: Cache?
        return DisjunctiveNormalFormula.<T>of(Conjunction.<T>of(this));
    }

    public abstract Class<? extends T> unit();

    public abstract Optional<T> earliest();

    public abstract Optional<T> latest();

    public abstract Optional<T> unique();

    /**
     * Returns an atom that negates this atom.
     *
     * <p>The negation of an atom is always an atom, and negating it twice returns an atom equal to the original.
     * It keeps the negation of a formula in Negation Normal Form (NNF) from growing.
     *
     * @return the negated atom
     */
    public abstract Atom<T> negate();
}
