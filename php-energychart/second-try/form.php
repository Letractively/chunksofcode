<?
/*
 * this file will draw the formular and fill 
 * it with the request parameters or default 
 * values. 
 */
?>

<!-- render the http form to request a new query -->
<form method="get">

<!-- view type radiobuttons: -->
<?php    
    $viewModeOptions = array("Gesamt","Jahr", "Monat", "Tag");
    
    if ($selectedViewType == null) {
        $selectedViewType = $viewModeOptions[0]; // use as default
    }
    if (! in_array($selectedViewType, $viewModeOptions)) {      
    	echo "<font color='red'>FEHLER: Ungültiger Ansichts-Typ: '".$selectedViewType."'!</font><br>\n";
        $selectedViewType = $viewModeOptions[0]; // use as default
    }
    
    echo "\nAnsichts-Typ:<br/>\n";
    
    for ($i = 0; $i < sizeof($viewModeOptions); $i++) {
    	$val = $viewModeOptions[$i];
        // TODO: control the inputs below with javascript on value changed!
    	echo "<input type='radio' name='queryTyp' value='".$val."'";
    	if ($val == $selectedViewType) { echo " checked='checked'"; }
    	echo "/>".$val."\n";
    }
?>
<!-- end of the viewtype radio buttons -->

<br/>

<!-- year selectbox: -->
<?php
    
	echo "<select name='queryJahr'>\n";
	// query available years from database:
	mysql_connect($db_host, $db_username, $db_password);
	mysql_select_db($db_schema);
	$sql = "SELECT distinct YEAR(datum) ".
           "FROM fmcontrol_kw_leistung ".
           "ORDER BY 1 desc";
	$result = mysql_query($sql);
	
	// show error message when query result was empty:
	if (mysql_affected_rows() <= 0) {
		echo "</select><br/>\n";
		if ($debug_mode) {
			echo "<font color='green'>DEBUG: sql=".$sql."</font><br/>\n";
		}
	    echo "<font color='red'>FEHLER: Keine Daten gefunden!</font><br/>\n";
	    return;
	} else {
		// render the result in a table:
	    while ($row = mysql_fetch_array($result, MYSQL_NUM)) {
	        $jahr = $row[0];
	        if ($selectedYear == null) { // use most recent year as default
	        	$selectedYear = $jahr;
	        }
	        
	        // render a year option value:
	        echo "  <option value=".$jahr;
	        if ($jahr == $selectedYear) { echo " selected='selected'"; }
	        echo ">".$jahr."</option>\n";
	    }
	}
	echo "</select>\n";
?>
<!-- end of year selectbox -->

<!-- month selectbox: -->
<?php
	if ($selectedMonth == null) { // use current month as default
	    $selectedMonth = date("n");
	}
	echo "<select name='queryMonat'>\n";
	for ($i = 1; $i <= 12; $i++) {
	    echo "  <option value='".$i."'";
	    if ($i == $selectedMonth) { echo " selected='selected'"; }
	    echo ">";
	    echo $i;
	    echo "</option>\n";
	}
	echo "</select>\n";
?>
<!-- end of month selectbox -->

<!-- day selectbox: -->
<?php
    if ($selectedDay == null) { // use current day as default
        $selectedDay = date("j"); // day of month
    }
    echo "<select name='queryTag'>\n";
    for ($i = 1; $i <= 31; $i++) {
        echo "  <option value='".$i."'";
        if ($i == $selectedDay) { echo " selected='selected'"; }
        echo ">";
        echo $i;
        echo "</option>\n";
    }
    echo "</select>\n";
?>
<!-- end of day selectbox -->

<button onclick="this.form.submit(); return false;">Abfragen</button>

</form>
<!-- end of http form -->

