calql-java
===========

Imagine a virtual table like the one below.

| proleptic_gregorian_date | year | month_of_year | day_of_year | day_of_month | day_of_week | julian_day_number |
| ------------------------ | ---- | ------------- | ----------- | ------------ | ----------- | ----------------- |
|               1970-01-01 | 1970 |             1 |           1 |            1 |    Thursday |           2440588 |
|               1970-01-02 | 1970 |             1 |           2 |            2 |      Friday |           2440589 |
|               1970-01-03 | 1970 |             1 |           3 |            3 |    Saturday |           2440590 |
|                      ... |  ... |           ... |         ... |          ... |         ... |               ... |
|               2024-09-29 | 2024 |             9 |         273 |           29 |      Sunday |           2460583 |
|               2024-09-30 | 2024 |             9 |         274 |           30 |      Monday |           2460584 |
|               2024-10-01 | 2024 |            10 |         275 |            1 |     Tuesday |           2460585 |
|                      ... |  ... |           ... |         ... |          ... |         ... |               ... |

This is a query (language) to such a table.

Intented design by examples
----------------------------

The default `dates` table and its default columns would be based on the [proleptic Gregorian calendar](https://en.wikipedia.org/wiki/Proleptic_Gregorian_calendar) and [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601).

```sql
-- The day of week of 2023-02-27.
select weekday from dates where year = 2023 and month = 2 and day = 27;
1  -- 1 represents Monday in ISO 8601.

select weekday_en from dates where year = 2023 and month = 2 and day = 27;
"Monday"

select weekday_en_abbr from dates where year = 2023 and month = 2 and day = 27;
"Mon"

-- The first Wednesdays of each month in 2023.
select month,day from dates where year = 2023 and day >= 1 and day <= 7 and weekday = 3;
1,4
2,1
3,1
4,5
5,3
6,7
7,5
8,2
9,6
10,4
11,1
12,6
```
