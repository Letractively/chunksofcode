


-- month view, sum of throughput and price, two tables
select 
    daycol as "Tag",
    monthcol as "Monat",
    yearcol as "Jahr",
    sum(value1col) as "Verbrauch", 
    sum(value2col) as "Preis"
from
    (
        (
            select 
                YEAR(STR_TO_DATE(L.Datum,'%d.%m.%Y')) as yearcol,
                MONTH(STR_TO_DATE(L.Datum,'%d.%m.%Y')) as monthcol,
                DAY(STR_TO_DATE(L.Datum,'%d.%m.%Y')) as daycol,
                sum(L.kWh_pro15min) as value1col,
                sum(L.kWh_Preis) as value2col
            from 
                fm_control.fmcontrol_kw_leistungb L
            where 
                YEAR(STR_TO_DATE(L.Datum,'%d.%m.%Y')) = 2012
                and MONTH(STR_TO_DATE(L.Datum,'%d.%m.%Y')) = 2
            group by 
                DAY(STR_TO_DATE(L.Datum,'%d.%m.%Y')),
                YEAR(STR_TO_DATE(L.Datum,'%d.%m.%Y')),
                MONTH(STR_TO_DATE(L.Datum,'%d.%m.%Y'))
        )
        union
        (
            select 
                YEAR(STR_TO_DATE(L.Datum,'%d.%m.%Y')) as yearcol,
                MONTH(STR_TO_DATE(L.Datum,'%d.%m.%Y')) as monthcol,
                DAY(STR_TO_DATE(L.Datum,'%d.%m.%Y')) as daycol,
                sum(L.kWh_pro15min) as value1col,
                0 as value2col
            from 
                fm_control.fmcontrol_kw_leistung L
            where 
                YEAR(STR_TO_DATE(L.Datum,'%d.%m.%Y')) = 2012
                and MONTH(STR_TO_DATE(L.Datum,'%d.%m.%Y')) = 2
            group by 
                DAY(STR_TO_DATE(L.Datum,'%d.%m.%Y')),
                YEAR(STR_TO_DATE(L.Datum,'%d.%m.%Y')),
                MONTH(STR_TO_DATE(L.Datum,'%d.%m.%Y'))
        )
    ) as foo
group by
    daycol,
    yearcol,
    monthcol
order by
    daycol,
    yearcol,
    monthcol
;
