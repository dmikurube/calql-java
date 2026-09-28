/*
 * Copyright 2026 Dai MIKURUBE
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

package org.theatime.calql.query.date;

import java.util.Collection;
import java.util.Set;

class UnmodifiableSet {
    private UnmodifiableSet() {
        // No instantiation.
    }

    static <E> Set<E> copyOf(final Collection<? extends E> coll)
        return Set.<E>copyOf(coll);
    }

    static <E> Set<E> of() {
        return Set.<E>of();
    }

    static <E> Set<E> of(final E e1) {
        return Set.<E>of(e1);
    }

    static <E> Set<E> of(final E... elements) {
        return Set.<E>of(elements);
    }

    static <E> Set<E> of(final E e1, final E e2) {
        return Set.<E>of(e1, e2);
    }

    static <E> Set<E> of(final E e1, final E e2, final E e3) {
        return Set.<E>of(e1, e2, e3);
    }

    static <E> Set<E> of(final E e1, final E e2, final E e3, final E e4) {
        return Set.<E>of(e1, e2, e3, e4);
    }

    static <E> Set<E> of(final E e1, final E e2, final E e3, final E e4, final E e5) {
        return Set.<E>of(e1, e2, e3, e4, e5);
    }

    static <E> Set<E> of(final E e1, final E e2, final E e3, final E e4, final E e5, final E e6) {
        return Set.<E>of(e1, e2, e3, e4, e5, e6);
    }

    static <E> Set<E> of(final E e1, final E e2, final E e3, final E e4, final E e5, final E e6, final E e7) {
        return Set.<E>of(e1, e2, e3, e4, e5, e6, e7);
    }

    static <E> Set<E> of(final E e1, final E e2, final E e3, final E e4, final E e5, final E e6, final E e7, final E e8) {
        return Set.<E>of(e1, e2, e3, e4, e5, e6, e7, e8);
    }

    static <E> Set<E> of(final E e1, final E e2, final E e3, final E e4, final E e5, final E e6, final E e7, final E e8, final E e9) {
        return Set.<E>of(e1, e2, e3, e4, e5, e6, e7, e8, e9);
    }

    static <E> Set<E> of(final E e1, final E e2, final E e3, final E e4, final E e5, final E e6, final E e7, final E e8, final E e9, final E e10) {
        return Set.<E>of(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10);
    }
}
