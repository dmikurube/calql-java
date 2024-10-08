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

package org.theatime.calql.query.date;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Optional;
import org.theatime.calql.query.Atom;

public abstract class DateAtom extends Atom<ChronoLocalDate> {
    @Override
    public final Class<LocalDate> unit() {
        return LocalDate.class;
    }

    @Override
    public Optional<LocalDate> earliest() {
        return Optional.<LocalDate>empty();
    }

    @Override
    public Optional<LocalDate> latest() {
        return Optional.<LocalDate>empty();
    }

    @Override
    public Optional<LocalDate> unique() {
        return Optional.<LocalDate>empty();
    }
}
