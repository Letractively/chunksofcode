
// quick hack to produce some testdata for 
// table leistung_kw
// author: andre ragg 2011



///////////////// DEFINITION OF FUNCTIONS AND CLASSES: //////////////////////////////




def final long globalStart = System.currentTimeMillis()

private def String formatDouble(double d) {
    double rounded = new Double(0.01D * Math.round(d * 100))
    return Double.toString(rounded).replaceAll("(?<=\\.\\d\\d)\\d*", "")
}


/**
  * encapsulate a test data engine for the table
  */
def class Fmcontrol_kw_leistung {

    private def Date date
    private def float kwIst
    private def int kwhGesamt
    
    // some more randomness:
    private Random random = new Random()
    private def float dailySalt
    private def float weeklySalt
    private def float monthlySalt
    
    def Fmcontrol_kw_leistung() {
        Calendar temp = Calendar.getInstance()
        temp.time = new Date(0L)
        temp.set(Calendar.YEAR,         2001)
        temp.set(Calendar.DAY_OF_MONTH,    1)
        temp.set(Calendar.MONTH,           8) // zero-based
        temp.set(Calendar.HOUR_OF_DAY,     0)
        temp.set(Calendar.SECOND,          6)
        date = temp.time
        kwIst = kwIstRandomValue()
        kwhGesamt = 2000 + getKwhPro15Min()
    }
    def void increment15min() {
        Calendar cal = Calendar.getInstance()
        cal.time = date
        int oldYear = cal.get(Calendar.YEAR)
        int oldMonth = cal.get(Calendar.MONTH)
        int oldWeek = cal.get(Calendar.WEEK_OF_YEAR)
        int oldDay = cal.get(Calendar.DAY_OF_MONTH)
        
        date.time += (15L * 60 * 1000)
        cal.time = date
        
        int newYear = cal.get(Calendar.YEAR)
        int newMonth = cal.get(Calendar.MONTH)
        int newWeek = cal.get(Calendar.WEEK_OF_YEAR)
        int newDay = cal.get(Calendar.DAY_OF_MONTH)
        
        if (oldYear != newYear) {
            monthlySalt = random.nextFloat() * 120.0F - 60.0F
        }
        if (oldMonth != newMonth) {
            monthlySalt = random.nextFloat() * 90.0F - 45.0F
        }
        if (oldWeek != newWeek) {
            weeklySalt = random.nextFloat() * 60.0F - 30.0F
        }
        if (oldDay != newDay) {
            dailySalt = random.nextFloat() * 30.0F - 15.0F
        }
        
        kwIst = kwIstRandomValue()
        kwhGesamt += getKwhPro15Min()
        
    }
    private def float kwIstRandomValue() {
        Calendar cal = Calendar.getInstance()
        cal.time = date
        
        int minimum
        int maximumRandom
                
        switch (cal.get(Calendar.MONTH)) {
        // hot months:
            case Calendar.JUNE :      
            case Calendar.JULY :      
            case Calendar.AUGUST :    
            case Calendar.SEPTEMBER : 
                minimum = 900
                maximumRandom = 900
            break;
            
        // normal months:
            case Calendar.MARCH :     
            case Calendar.APRIL :     
            case Calendar.MAY :       
            case Calendar.OCTOBER :   
                minimum = 1200
                maximumRandom = 1500
            break;
            
        // cold months:
            case Calendar.NOVEMBER :  
            case Calendar.DECEMBER :  
            case Calendar.JANUARY :   
            case Calendar.FEBRUARY :  
                minimum = 1500
                maximumRandom = 1500
            break;
        }
        
        int hour = cal.get(Calendar.HOUR_OF_DAY)
        
        if (hour > 10 && hour < 17) { // sunny daytime
            minimum -= 300
        }
        
        int random = random.nextInt(maximumRandom) + minimum
        float f = random
        f /= 10.0
        // System.err.println "$date : salt: ${monthlySalt+weeklySalt+dailySalt}" 
        // System.err.println "wert vorher: $f"
        f += monthlySalt
        f += weeklySalt
        f += dailySalt
        // System.err.println "wert nachher: $f"
        // System.err.println()
        
        return f
    }
    
    
    def int getNrViertelSt() {
        Calendar cal = Calendar.getInstance()
        cal.time = date
        int minutesSinceMidnight = 
             cal.get(Calendar.HOUR_OF_DAY) * 60 + 
             cal.get(Calendar.MINUTE)
        return (minutesSinceMidnight / 15) + 1
    }
    def int getIdViertelst() {
        StringBuilder bui = new StringBuilder()
        bui.append(fmt("yyyyMMdd", date))
        bui.append(getNrViertelSt())
        return Integer.parseInt(bui.toString())
    }    
    def String getTarif() {
        Calendar cal = Calendar.getInstance()
        cal.time = date
        // assuming the range of 'Nacht' is from 22:00 to 5:00
        int hour = cal.get(Calendar.HOUR_OF_DAY)
        if (hour < 5 || hour > 22) {
            return 'Nacht'
        }
        return 'Tag'
    }
    
    def Date getDate()       { return date }
    def String getDatum()    { return fmt("yyyy-MM-dd", date) }
    def String getZeit()     { return fmt("HH:mm:ss", date) }
    def int getKwhPro15Min() { return Math.round(kwIst/4f) }
    def int getKwhGesamt()   { return kwhGesamt }
    def int getKwMax()       { return 330 }
    def int getAnzahlAbsch() { return 0 }
    def float getKwhIst()    { return kwIst }
    
    private def String fmt(String pattern, Date dateObj) {
        return new java.text.SimpleDateFormat(pattern).format(dateObj)
    }
}

private def int generateTestData(String databaseTable) {
    final Fmcontrol_kw_leistung obj = new Fmcontrol_kw_leistung()
    
    final String insert = 
        "insert into `$databaseTable` (" +
        "id_viertelst, tarif, datum, zeit, kw_ist, kwh_pro15min, kwh_gesamt, nr_viertelst, kw_max,anzahl_absch" +
        ") values "
     
    final StringBuilder bui = new StringBuilder(insert)
    final int createLines = 10 * 365 * 24 * 4
    final int partitionsize = 4096 // group multiple values(...) into one insert stmt to speed the thing up
    final Set<Integer> idCheck = new HashSet<Integer>(64*1024)
    final Calendar tempCal = Calendar.getInstance()
    int totalcount = 0
    final long start = System.currentTimeMillis()
    final Date now = new Date()
    
    for (int i = 0; i < createLines; i++) {
        final int id = obj.idViertelst
        
        tempCal.time = obj.date
        final int oldYear = tempCal.get(Calendar.YEAR)
        
        if (idCheck.add(id)) { // FIXME: why is this necessary? (groovy-bug?)
            bui.append("\n  (")
            bui.append(id + ", ")
            bui.append("'" + obj.tarif + "', ")
            bui.append("'" + obj.datum + "', ")
            bui.append("'" + obj.zeit + "', ")
            bui.append(obj.kwhIst + ", ")
            bui.append(obj.kwhPro15Min + ", ")
            bui.append(obj.kwhGesamt + ", ")
            bui.append(obj.nrViertelSt + ", ")
            bui.append(obj.kwMax + ", ")
            bui.append(obj.anzahlAbsch)
            bui.append(")")
            
            if ((i % partitionsize == 0 && i > 0) || (i+1) == createLines) {
                bui.append(";")
                println bui
                bui.length = 0L
                bui.append(insert)
                System.err.print "."
            } else {
                bui.append(",")
            }
            
            if (bui.length() >= 10240) {
                print bui
                bui.length = 0L
            }
            
            if (totalcount == 0) {
                System.err.print "  start with $oldYear! (every '.' stands for $partitionsize records) "
            } else if (totalcount % 20000 == 0) {
                System.err.print "\n    $totalcount rows created! "
            }
            
            totalcount++;
        }
        
        obj.increment15min()
        
        tempCal.time = obj.date
        int newYear = tempCal.get(Calendar.YEAR)
        
        if (oldYear != newYear) {
            System.err.print "\n  done with $oldYear, continue with $newYear! (${idCheck.size()} rows created) "
            idCheck.clear()
        }
        
        if (obj.date.after(now)) {            
            String remaining = bui.toString().replaceAll(',\\s*$', ';\n')
            print remaining;
            System.err.println "\n  reached current date, EXITING."
            break;
        }
    }

    System.err.println "\n  done!"
    final long end = System.currentTimeMillis()
    double duration = end - start
    double durationSeconds = duration / 1000
    double rowsPerSecond = totalcount / durationSeconds
    
    System.err.println()
    System.err.println()
    System.err.println "rows created: $totalcount"
    System.err.println "time needed: ${formatDouble(durationSeconds)} seconds."
    System.err.println "rows per second: ${formatDouble(rowsPerSecond)} rows/sec"
    System.err.println()
    
    return totalcount
}


private def void createTable(String tablename) {
    println """
    DROP TABLE IF EXISTS `$tablename`;
    CREATE TABLE `$tablename` (
      `id_viertelst` int(11) NOT NULL,
      `tarif` char(5) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
      `datum` date NOT NULL,
      `zeit` time NOT NULL,
      `kw_ist` float NOT NULL,
      `kwh_pro15min` float NOT NULL,
      `kwh_gesamt` float NOT NULL,
      `nr_viertelst` int(11) NOT NULL,
      `kw_max` int(11) NOT NULL,
      `anzahl_absch` int(11) NOT NULL,
      PRIMARY KEY (`id_viertelst`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
    create index leistung_kw_idx_datum on `$tablename` (datum);
    create index leistung_kw_idx_zeit on `$tablename` (zeit);
    """
    println()
}








///////////////// PROCEDURE START: //////////////////////////////




println "-- init script for table leistung_kw start --"
println "-- author: andre ragg ${new java.text.SimpleDateFormat('yyyy').format(new Date())} --"
println()
println "-- set database schema to 'andre' --"
println "use andre;"
println()
println "-- creating tables --"
createTable("leistung_kw")
createTable("leistung_kw2")

println """
-- create a view to combine both views leistung_kw and leistung_kw2 --
drop view if exists nasenview;
create view nasenview 
as select * from leistung_kw 
union all 
select * from leistung_kw2;
"""


// eventual performance tuning:
// println """
// alter table leistung_kw add monat int not null default year(datum);
// update leistung_kw set monat = month(datum);
// create index leistung_kw_idx_monat on leistung_kw(monat);
//
// alter table leistung_kw add jahr int;
// update fmcontrol set jahr = year(datum);
// create index leistung_kw_idx_jahr on leistung_kw(jahr);
// """



System.err.println "-- inserting data --"; println "-- inserting data --"

int globalCount = 0
globalCount += generateTestData("leistung_kw")
globalCount += generateTestData("leistung_kw2")

final long globalEnd = System.currentTimeMillis()
double duration = globalEnd - globalStart
double durationSeconds = duration / 1000
double rowsPerSecond = globalCount / durationSeconds

System.err.println()
System.err.println()
System.err.println "rows created: $globalCount"
System.err.println "time needed: ${formatDouble(durationSeconds)} seconds."
System.err.println "rows per second: ${formatDouble(rowsPerSecond)} rows/sec"
System.err.println()
