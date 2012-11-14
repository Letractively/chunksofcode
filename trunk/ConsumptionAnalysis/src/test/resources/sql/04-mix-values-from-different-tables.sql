


-- hole den preis aus leistungB und dazu den kWh_pro15min wert aus leistung:
select 
    monthcol as "Monat",
    yearcol as "Jahr",
    sum(value1col) as "Verbrauch", 
    sum(value2col) as "Preis"
from
    (
        (
            select 
                YEAR(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) as yearcol,
                MONTH(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) as monthcol,
                0 as value1col,
                sum(t1.kWh_Preis) as value2col
            from 
                fm_control.fmcontrol_kw_leistungb t1
            where 
                YEAR(STR_TO_DATE(t1.Datum,'%d.%m.%Y')) = 2012
            group by 
                MONTH(STR_TO_DATE(t1.Datum,'%d.%m.%Y'))
        )
        union
        (
            select 
                YEAR(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) as yearcol,
                MONTH(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) as monthcol,
                sum(t2.kWh_pro15min) as value1col,
                0 as value2col
            from 
                test.foo t2
            where 
                YEAR(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) <= 2012
                and
                YEAR(STR_TO_DATE(t2.Datum,'%d.%m.%Y')) >= 2012
            group by 
                MONTH(STR_TO_DATE(t2.Datum,'%d.%m.%Y'))
        )
    ) as foo
group by
    yearcol,
    monthcol
order by
    yearcol,
    monthcol
;
