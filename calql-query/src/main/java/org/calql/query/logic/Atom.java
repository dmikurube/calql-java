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

public abstract class Atom<T> extends NegationNormalFormula implements Predicate<T> {
    protected Atom() {
        this.disjunctiveNormalForm = DisjunctiveNormalFormula.of(Conjunction.of(this));
    }

    @Override
    public final NegationNormalFormula negateInNegationNormalForm() {
        return this.negate();
    }

    @Override
    public final DisjunctiveNormalFormula getDisjunctiveNormalForm() {
        return this.disjunctiveNormalForm;
    }

    public abstract Class<T> unit();

    public abstract Optional<T> earliest();

    public abstract Optional<T> latest();

    public abstract Optional<T> unique();

    public abstract Atom<T> negate();

    private final DisjunctiveNormalFormula disjunctiveNormalForm;
}
