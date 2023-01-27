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
 * An "and" operator in negation normal form.
 */
public final class NegationNormalAnd extends NegationNormalCompound {
    private NegationNormalAnd(final ArrayList<NegationNormalFormula> negationNormalFormulae) {
        this.negationNormalFormulae = Collections.unmodifiableList(negationNormalFormulae);
        this.disjunctiveNormalForm = distributeToDisjunctiveNormalForm(negationNormalFormulae);
    }

    public static NegationNormalFormula of(final Collection<NegationNormalFormula> negationNormalFormulae) {
        return new NegationNormalAnd(new ArrayList<>(negationNormalFormulae));
    }

    public static NegationNormalFormula of(final NegationNormalFormula... negationNormalFormulae) {
        return of(Arrays.asList(negationNormalFormulae));
    }

    /**
     * Gets a Disjunctive Normal Form (DNF) of this Negation Normal Form (NNF).
     *
     * <p>CalQL converts a given WHERE clause in a given query to DNF to process the WHERE clause easily and mechanically.
     *
     * <p>Ex. {@code (f1 AND f2) OR (f3 AND f4 AND f5) OR (f6) OR (f7 AND f8 AND f9)}
     *
     * @see <a href="https://en.wikipedia.org/wiki/Disjunctive_normal_form">Disjunctive normal form</a>
     * @see <a href="https://github.com/aimacode/aima-java/blob/aima3e-v1.9.1/aima-core/src/main/java/aima/core/logic/propositional/visitors/ConvertToDNF.java">Example from Artificial Intelligence: A Modern Approach</a>
     */
    @Override
    public DisjunctiveNormalFormula getDisjunctiveNormalForm() {
        return this.disjunctiveNormalForm;
    }

    @Override
    public NegationNormalFormula negateInNegationNormalForm() {
        return NegationNormalOr.of(
                this.negationNormalFormulae.stream().map(f -> f.negateInNegationNormalForm()).collect(Collectors.toList()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(NegationNormalAnd.class, this.negationNormalFormulae);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == NegationNormalAnd.class
                && this.negationNormalFormulae.equals(((NegationNormalAnd) obj).negationNormalFormulae);
    }

    @Override
    public String toString() {
        return this.negationNormalFormulae.stream().map(Object::toString).collect(Collectors.joining(" AND ", "(", ")"));
    }

    private static DisjunctiveNormalFormula distributeToDisjunctiveNormalForm(final List<NegationNormalFormula> negationNormalFormulae) {
        final ArrayList<Conjunction> conjunctions = new ArrayList<>();
        iter(0,
                negationNormalFormulae.stream().map(NegationNormalFormula::getDisjunctiveNormalForm).collect(Collectors.toList()),
                new ArrayList<>(),
                conjunctions);
        return DisjunctiveNormalFormula.of(conjunctions);
    }

    private static void iter(
            final int index,
            final List<DisjunctiveNormalFormula> dnfs,
            final ArrayList<Atom> visitingConjunction,
            final ArrayList<Conjunction> built) {
        final DisjunctiveNormalFormula dnf = dnfs.get(index);

        System.out.printf(">> %d / %s\n", index, visitingConjunction);
        final int sizeOfVisitingConjunction = visitingConjunction.size();
        for (final Conjunction conjunction : dnf) {
            System.out.printf(">>>> %s\n", conjunction);
            visitingConjunction.addAll(conjunction);
            System.out.println(">>>>>> !");
            System.out.printf(">>>>>> %s\n", visitingConjunction);
            if (index + 1 < dnfs.size()) {
                iter(index + 1, dnfs, visitingConjunction, built);
            } else {
                built.add(Conjunction.of(visitingConjunction));
            }
            visitingConjunction.subList(sizeOfVisitingConjunction, visitingConjunction.size()).clear();
        }
    }

    private final List<NegationNormalFormula> negationNormalFormulae;

    private final DisjunctiveNormalFormula disjunctiveNormalForm;
}
