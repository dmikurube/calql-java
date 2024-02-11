/*
 * Copyright 2024 Dai MIKURUBE
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

/**
 * Defines orders of dates.
 *
 * <p>They are defined as {@code static} constant variables, not as {@code enum}, so that
 * more parameterized orders can be defined, such as "ordered by month", "ordered by day of
 * week", and else.
 */
public class DateOrder {
    private DateOrder() {
        // No instantiation.
    }

    public static final DateOrder FROM_EARLIEST_TO_LATEST = new DateOrder();

    public static final DateOrder FROM_LATEST_TO_EARLIEST = new DateOrder();
}
