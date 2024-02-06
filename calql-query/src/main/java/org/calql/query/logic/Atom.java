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

import java.util.Optional;
import java.util.function.Predicate;

public abstract class Atom<T extends Comparable<T>> extends NegationNormalFormula<T> implements Predicate<T> {
    protected Atom() {
        this.disjunctiveNormalForm = DisjunctiveNormalFormula.<T>of(Conjunction.<T>of(this));
    }

    @Override
    public final NegationNormalFormula<T> negateInNegationNormalForm() {
        return this.negate();
    }

    @Override
    public final DisjunctiveNormalFormula<T> getDisjunctiveNormalForm() {
        return this.disjunctiveNormalForm;
    }

    public abstract Class<? extends T> unit();

    public abstract Optional<? extends T> earliest();

    public abstract Optional<? extends T> latest();

    public abstract Optional<? extends T> unique();

    public abstract Atom<T> negate();

    private final DisjunctiveNormalFormula<T> disjunctiveNormalForm;
}
