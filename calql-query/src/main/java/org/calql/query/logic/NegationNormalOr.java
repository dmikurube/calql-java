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
public final class NegationNormalOr extends NegationNormalCompound {
    private NegationNormalOr(final ArrayList<NegationNormalFormula> negationNormalFormulae) {
        this.negationNormalFormulae = Collections.unmodifiableList(negationNormalFormulae);
        this.disjunctiveNormalForm = toDisjunctiveNormalForm(negationNormalFormulae);
    }

    public static NegationNormalFormula of(final Collection<NegationNormalFormula> negationNormalFormulae) {
        return new NegationNormalOr(new ArrayList<>(negationNormalFormulae));
    }

    public static NegationNormalFormula of(final NegationNormalFormula... negationNormalFormulae) {
        return of(Arrays.asList(negationNormalFormulae));
    }

    @Override
    public NegationNormalFormula negateInNegationNormalForm() {
        return NegationNormalAnd.of(this.negationNormalFormulae.stream().map(f -> f.negateInNegationNormalForm()).collect(Collectors.toList()));
    }

    @Override
    public DisjunctiveNormalFormula getDisjunctiveNormalForm() {
        return this.disjunctiveNormalForm;
    }

    @Override
    public int hashCode() {
        return Objects.hash(NegationNormalOr.class, this.negationNormalFormulae);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == NegationNormalOr.class && this.negationNormalFormulae.equals(((NegationNormalOr) obj).negationNormalFormulae);
    }

    @Override
    public String toString() {
        return this.negationNormalFormulae.stream().map(Object::toString).collect(Collectors.joining(" OR ", "(", ")"));
    }

    private static DisjunctiveNormalFormula toDisjunctiveNormalForm(final List<NegationNormalFormula> negationNormalFormulae) {
        final ArrayList<Conjunction> conjunctions = new ArrayList<>();
        System.out.print(">> ");
        System.out.println(negationNormalFormulae);
        final List<DisjunctiveNormalFormula> dnfs = negationNormalFormulae.stream().map(NegationNormalFormula::getDisjunctiveNormalForm).collect(Collectors.toList());
        System.out.print(")) ");
        System.out.println(dnfs);
        for (final DisjunctiveNormalFormula dnf : dnfs) {
            if (dnf == null) {
                System.out.println("!!!");
            }
            for (final Conjunction conjunction : dnf) {
                conjunctions.add(conjunction);
            }
        }
        return DisjunctiveNormalFormula.of(conjunctions);
    }

    private final List<NegationNormalFormula> negationNormalFormulae;

    private final DisjunctiveNormalFormula disjunctiveNormalForm;
}
