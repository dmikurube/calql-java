calql-java
===========

A query language to look through calendar dates.

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
