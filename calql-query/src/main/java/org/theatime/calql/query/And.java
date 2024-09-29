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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An "and" operator, which may not be in negation normal form.
 */
public final class And<T extends Comparable<T>> extends Compound<T> {
    private And(final ArrayList<Formula<T>> formulae) {
        this.formulae = Collections.unmodifiableList(formulae);
    }

    public static <T extends Comparable<T>> Formula<T> of(final Collection<Formula<T>> formulae) {
        return new And<T>(new ArrayList<>(formulae));
    }

    @SafeVarargs
    public static <T extends Comparable<T>> Formula<T> of(final Formula<T>... formulae) {
        return of(Arrays.asList(formulae));
    }

    @Override
    public NegationNormalFormula<T> toNegationNormalForm() {
        return NegationNormalAnd.of(this.formulae.stream().map(f -> f.toNegationNormalForm()).collect(Collectors.toList()));
    }

    @Override
    public NegationNormalFormula<T> negateInNegationNormalForm() {
        return NegationNormalOr.of(this.formulae.stream().map(f -> f.negateInNegationNormalForm()).collect(Collectors.toList()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(And.class, this.formulae);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == And.class && this.formulae.equals(((And) obj).formulae);
    }

    @Override
    public String toString() {
        return this.formulae.stream().map(Object::toString).collect(Collectors.joining(" and ", "[", "]"));
    }

    private final List<Formula<T>> formulae;
}
