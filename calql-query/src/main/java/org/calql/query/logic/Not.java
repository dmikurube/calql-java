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

import java.util.Objects;

/**
 * A "not" operator, which may not be in negation normal form.
 */
public final class Not extends Compound {
    public Not(final Formula formula) {
        this.formula = formula;
    }

    public static Formula of(final Formula formula) {
        return new Not(formula);
    }

    @Override
    public NegationNormalFormula toNegationNormalForm() {
        return this.formula.negateInNegationNormalForm();
    }

    @Override
    public NegationNormalFormula negateInNegationNormalForm() {
        return this.formula.toNegationNormalForm();
    }

    @Override
    public int hashCode() {
        return Objects.hash(Not.class, this.formula);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == Not.class && this.formula.equals(((Not) obj).formula);
    }

    @Override
    public String toString() {
        return String.format("not [%s]", this.formula.toString());
    }

    private final Formula formula;
}
