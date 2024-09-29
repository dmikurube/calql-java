calql-java
===========

Imagine a virtual table like the one below.

|         date | year | month | day_of_year | day_of_month |  day_of_week | julian_day |
| ------------ | ---- | ----- | ----------- | ------------ | ------------ | ---------- |
|   1970-01-01 | 1970 |     1 |           1 |            1 | Thursday (4) |    2440588 |
|   1970-01-02 | 1970 |     1 |           2 |            2 |   Friday (5) |    2440589 |
|   1970-01-03 | 1970 |     1 |           3 |            3 | Saturday (6) |    2440590 |
|          ... |  ... |   ... |         ... |          ... |          ... |        ... |
|   2024-09-29 | 2024 |     9 |         273 |           29 |   Sunday (7) |    2460583 |
|   2024-09-30 | 2024 |     9 |         274 |           30 |   Monday (1) |    2460584 |
|   2024-10-01 | 2024 |    10 |         275 |            1 |  Tuesday (2) |    2460585 |
|          ... |  ... |   ... |         ... |          ... |          ... |        ... |

This is a query (language) to such a table.

The intented design by examples
--------------------------------

The default `dates` table and its default columns would be based on the [proleptic Gregorian calendar](https://en.wikipedia.org/wiki/Proleptic_Gregorian_calendar) and [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601).

```sql
-- The day of week of 2024-09-29.
select day_of_week from dates where year = 2024 and month = 9 and day_of_month = 29;
7  -- 7 represents Sunday in ISO 8601.

select day_of_week_en from dates where year = 2024 and month = 9 and day_of_month = 29;
"Sunday"

select day_of_week_en_abbr from dates where year = 2024 and month = 9 and day_of_month = 29;
"Sun"

-- The first Wednesdays of each month in 2023.
select month, day_of_month from dates where year = 2023 and day_of_month >= 1 and day_of_month <= 7 and day_of_week = 3;
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
