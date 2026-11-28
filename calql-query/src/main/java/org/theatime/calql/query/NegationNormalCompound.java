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

import java.util.List;

/**
 * A compound formula in Negation Normal Form (NNF), such as {@link NegationNormalAnd} and {@link NegationNormalOr}.
 *
 * @param <T> the type of values that this formula is evaluated on
 */
public abstract class NegationNormalCompound<T extends Comparable<? super T>> extends NegationNormalFormula<T> {
    /**
     * Sole constructor. (For invocation by subclass constructors, typically implicit.)
     */
    protected NegationNormalCompound() {
    }

    /**
     * Returns the operands of this compound formula, which are its direct children as written.
     *
     * <p>The operands are not flattened. For example, the operands of {@code (f1 AND f2) AND f3}
     * are {@code f1 AND f2} and {@code f3}.
     *
     * @return the operands, an unmodifiable list in the order as written, with duplicates if any
     */
    public abstract List<NegationNormalFormula<T>> operands();
}
