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

/**
 * A compound formula, which may not be in Negation Normal Form (NNF), such as {@link And}, {@link Or}, and {@link Not}.
 *
 * <p>Its {@link #toNegationNormalForm()} converts it to a formula in NNF,
 * such as {@link NegationNormalAnd} for {@link And}.
 *
 * @param <T> the type of values that this formula is evaluated on
 */
public abstract class Compound<T extends Comparable<? super T>> extends Formula<T> {
    /**
     * Sole constructor. (For invocation by subclass constructors, typically implicit.)
     */
    protected Compound() {
    }
}
