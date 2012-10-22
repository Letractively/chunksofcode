


-- day view, sum of throughput and price, two tables
select 
    yearcol as "Jahr",
    monthcol as "Monat",
    daycol as "Tag",
    hourcol as "Stunde",
    sum(value1col) as "Verbrauch", 
    sum(value2col) as "Preis"
from
    (
        (
            select 
                YEAR(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) as yearcol,
                MONTH(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) as monthcol,
                DAY(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) as daycol,
                HOUR(t1.Zeit) as hourcol,
                sum(t1.kWh_pro15min) as value1col,
                0 as value2col
            from 
                fm_control.fmcontrol_kw_leistung t1
            where
                YEAR(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) <= 2012
                and YEAR(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) >= 2012
                and MONTH(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) <= 5
                and MONTH(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) >= 5
                and DAY(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) <= 20
                and DAY(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) >= 20
                and HOUR(t1.Zeit) >= 8
                and HOUR(t1.Zeit) <= 20
            group by 
                YEAR(STR_TO_DATE(t1.Datum,'%d.%m.%Y')),
                MONTH(STR_TO_DATE(t1.Datum,'%d.%m.%Y')),
                HOUR(t1.Zeit)
        )
        union all
        (
            select 
                YEAR(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) as yearcol,
                MONTH(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) as monthcol,
                DAY(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) as daycol,
                HOUR(t2.Zeit) as hourcol,
                0 as value1col,
                sum(t2.kWh_Preis) as value2col
            from 
                fm_control.fmcontrol_kw_leistungb t2
            where
                YEAR(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) <= 2012
                and YEAR(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) >= 2012
                and MONTH(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) <= 5
                and MONTH(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) >= 5
                and DAY(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) <= 20
                and DAY(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) >= 20
                and HOUR(t2.Zeit) >= 8
                and HOUR(t2.Zeit) <= 20
            group by 
                YEAR(STR_TO_DATE(t2.Datum,'%d.%m.%Y')),
                MONTH(STR_TO_DATE(t2.Datum,'%d.%m.%Y')),
                HOUR(t2.Zeit)
        )
    ) as foo
group by
    yearcol,
    monthcol,
    daycol,
    hourcol
order by
    yearcol,
    monthcol,
    daycol,
    hourcol
;
