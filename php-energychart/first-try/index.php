<html>

<?php 
echo "<font color='green'>hello world, greetings from andre!</font><br/>";

$db_host="localhost";
$db_username="root";
$db_password="";
$db_schema="test";


// check if queryJahr is set in request parameters
// (if not, it will be initialized while rendering
// the selectbox element below.)
$selectedYear = null;
if (isset($_REQUEST["queryJahr"])) {
    $selectedYear=$_REQUEST["queryJahr"];
    echo "<font color='green'>Ausgewähltes Jahr: ".$selectedYear."</font><br/>";	
}
?>

<!-- render the form to select the parameters: -->
<form method="get">

    <!-- render the year selectbox: -->
    <select name="queryJahr">
        <?php 
        // query available years from database:
	    mysql_connect($db_host, $db_username, $db_password);
	    mysql_select_db($db_schema);
        $result=mysql_query("SELECT distinct YEAR(datum) ".
                            "FROM fmcontrol_kw_leistung ".
                            "ORDER BY 1 desc;");

        // show error message when query result was empty:
        if (mysql_affected_rows() <= 0) {
        	echo "</select><br/>\n<font color='red'>".
	            "FEHLER: Keine Daten gefunden!".
	            "</font>";
        } else {
            while ($row = mysql_fetch_array($result, MYSQL_NUM)) {
                $jahr = $row[0];
                // use most recent year as default value if not set in request:
                if (! $selectedYear) { $selectedYear = $jahr; }
                
                // render a year option value:
                echo "<option value=".$jahr;
                if ($jahr == $selectedYear) { echo " selected='selected'"; }
                echo ">".$jahr."</option>\n";
            }
        }
        ?>
    </select>
    
    <!-- render the Aktualisieren button: -->
    <button onclick="this.form.submit(); return false;">Aktualisieren</button>
</form>


<?php
// determine which year is currently selected:
$dbLink = new mysqli($db_host, $db_username, $db_password, $db_schema);

if (mysqli_connect_errno()) {
	echo "\n<font color='red'>".
       "FEHLER: Verbindung mit der Datenbank fehlgeschlagen!".
       "Details: ".mysqli_connect_errno().
	   "</font>";
}

$sql =
   "SELECT ".
        "YEAR(datum) as jahr, ".
        "MONTH(datum) as monat, ".
        "(SUM(kw_ist) / 4) as kwh_total, ".   // FIXME: was ist richtiger ??
     // "SUM(kwh_pro15min) as kwh_total, ".   // FIXME: was ist richtiger ??
        "MAX(kw_ist) as max_kw, ".
        "MIN(kw_ist) as min_kw, ".
        "AVG(kw_ist) as avg_kw \n".
    "FROM ".
        "fmcontrol_kw_leistung \n".
    "WHERE ".
        "YEAR(datum) = ? \n". // 1
    "GROUP BY ".
        "YEAR(datum), ".
        "MONTH(datum) \n".
    "ORDER BY ".
        "YEAR(datum), ".
        "MONTH(datum);";

if ($stmt = $dbLink -> prepare($sql)) {
    $stmt -> bind_param("i", $selectedYear);
    $stmt -> execute();
    $stmt -> bind_result($jahr, $monat, $kwh_total, $max_kw, $min_kw, $avg_kw);
            
    // render result of the query into html table:
    echo "<!-- results of the query -->\n";
    echo "<table border='1'>\n";
    echo "<tr>";
    echo "<th>jahr</th>";
    echo "<th>monat</th>";
    echo "<th>kwh total</th>";
    echo "<th>kw leistungsspitze</th>";
    echo "<th>kw leistungsminimum</th>";
    echo "<th>kw leistungsdurchschnitt</th>";
    echo "<tr>\n";
        
    $atLeastOneRowFound = false;
    
    while($stmt -> fetch()) {
    	$atLeastOneRowFound = true;
        echo "<tr>";
        echo "<td>".$jahr."</td>";
        echo "<td>".$monat."</td>";
        echo "<td>".$kwh_total."</td>";
        echo "<td>".$max_kw."</td>";
        echo "<td>".$min_kw."</td>";
        echo "<td>".$avg_kw."</td>";
        echo "</tr>\n";
    }
    echo "</table>\n";
    $dbLink->close();
    
    if (! $atLeastOneRowFound) {
    	echo "</select><br/>\n<font color='red'>".
            "FEHLER: Keine Daten gefunden!".
            "</font>";
    }
        
} else { // error during preparing statement
    echo "<font color='red'>FEHLER: Abfrage fehlgeschlagen!</font>";
    $dbLink->close();
}

// display query for easy debugging:
echo "<font color='green'><br/><pre>*** SQL-CODE ***:\n".$sql."</pre><br/></font>";
?>


</html>