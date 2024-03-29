/*
 * Copyright 2023-2024 Dai MIKURUBE
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

package org.calql.query.date;

import java.time.LocalDate;
import java.util.stream.Stream;
import org.calql.query.logic.Conjunction;
import org.junit.jupiter.api.Test;

public class TestSimpleLocalDateStreamBuilder {
    @Test
    public void dump() {
        final Stream<LocalDate> stream = SimpleLocalDateStreamBuilder.builder().fromConjunction(Conjunction.of(AfterYear.orEqualTo(1970))).build(DateOrder.FROM_EARLIEST_TO_LATEST);
        stream.limit(100).forEach(date -> System.out.println(date.toString()));
    }
}
