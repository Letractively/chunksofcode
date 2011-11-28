<?
/* 
 * this file will build the query and fetch the results into 
 * a html table. 
 */
?>
<?php

    // declare utility function library:
    include 'functions_html.php';
    include 'functions_pchart.php';
    include 'functions_sql.php';
    
    // declare usage of pChart library:
    include("my_pchart_copy/pData.class.php");
    include("my_pchart_copy/pChart.class.php");
    include("my_pchart_copy/pCache.class.php");
    
    // create database connection:
	$dbLink = new mysqli($db_host, $db_username, $db_password, $db_schema);
	
	if (mysqli_connect_errno()) {
		echo "<font color='red'>FEHLER: Verbindung mit der Datenbank fehlgeschlagen! Details: ".mysqli_connect_errno()."</font>\n";
		return;
	}
	
	// generate sql statement: ///////////////////////////////
	$sql = createSqlStatement($selectedViewType);
    
    if ($debug_mode == true) { // display query for easy debugging:
        echo "<!-- display query: -->\n";
        echo "<br/>\n<font color='green'>\n<pre>\nDEBUG: *** SQL-CODE ***\n".$sql."\n  </pre>\n</font>\n";
        echo "<!-- end of query -->\n\n";
    }
    
	// execute statement:
	if (! ($stmt = $dbLink -> prepare($sql))) { // error during preparing statement
        echo "<font color='red'>FEHLER: Abfrage fehlgeschlagen!</font>\n</html>";
        $dbLink -> close();
        return;
    }

    // bind parameters to statement:
    bindStatementVariables(
	    $stmt, 
	    $selectedViewType, 
	    $selectedYear, $selectedMonth, $selectedDay
    );

	$stmt -> execute();
    $stmt -> store_result();
    $dbLink -> close();
    
    // check how many rows are in resultset:
    $rows_in_result = $stmt -> num_rows();
    if ($debug_mode == true) {
        echo "<font color='green'>DEBUG: ".$rows_in_result." rows fetched.</font><br/>\n";
    }
    if (0 == $rows_in_result) {
        echo "<br/>\n<font color='red'>Keine Daten gefunden!</font>\n";
        echo "<br/>\nBitte versuchen Sie einen anderen Zeitpunkt.\n";
        return;
    }
    
    if ($show_result_table == true) { // render the table:
        render_table($stmt);
        $stmt -> data_seek(0);
    }
// < ?php
//     $var="User', email='test";
//     $a=new PDO("mysql:host=localhost;dbname=database;","root","");
//     $b=$a->prepare("UPDATE `users` SET user=:var");
//     $b->bindParam(":var",$var);
//     $b->execute();
// ? >
    new PDO();
    // store data into arrays:
    $stmt -> bind_result($label, $value);
    $label_array = array();
    $value_array = array();
	for ($i = 0; $stmt -> fetch(); $i++) {
        $label_array[$i] = $label;
        $value_array[$i] = $value;
	}
	
    // compute chart labels:
    $fooDate = strtotime($selectedYear."-".$selectedMonth."-".$selectedDay);
    switch ($selectedViewType) {
        case "Gesamt":
            $output_dir = "rendered_images/all";
            $key = "all";
            $output_file_name = "all.png";
            $chart_title = "Gesamtansicht";
            $value_series_name = "Jahresverbrauch";
            $x_axis_name = "Jahr";
            $y_axis_name = "MWh";
            break;
	    case "Jahr":
	        $output_dir = "rendered_images/yearly";
	        $key = date("Y", $fooDate);
	    	$output_file_name = date("Y", $fooDate).".png";
	        $chart_title = "Jahresansicht ".$key;
	        $value_series_name = "Monatsverbrauch";
	        $x_axis_name = "Monat";
	        $y_axis_name = "MWh";
	        break;
	    case "Monat":
	        $output_dir = "rendered_images/monthly";
	        $key = date("Y-m", $fooDate);
	        $output_file_name = $key.".png";
	        $chart_title = "Monatsansicht ".$key;
	        $value_series_name = "Tagesverbrauch";
	        $x_axis_name = "Kalendertag";
	        $y_axis_name = "MWh";
	        break;
	    case "Tag":
	        $output_dir = "rendered_images/daily";
	        $key = date("Y-m-d", $fooDate);
	        $output_file_name = $key.".png";
	        $chart_title = "Tagesansicht ".$key;
	        $value_series_name = "stündlicher Verbrauch";
	        $x_axis_name = "Stunde";
	        $y_axis_name = "kWh";
	        break;
    }
    
    // compute the target file name where the picture will be rendered to
    $output_file = $output_dir."/".$output_file_name;
    
    // create dir if not existing              
    if ( ! is_dir($output_dir)) {
        mkdir($output_dir, true);
    }
    
    // draw the chart picture file
	drawChart(
		$label_array, $value_array, 
		$chart_height, $chart_width, 
		$chart_title, 
		$output_file,
        $value_series_name, $x_axis_name, $y_axis_name
	);

    // embed the rendered picture:
    echo "\n\n<!-- embed image file: -->\n";
    echo "<img src='".$output_file."' />\n\n";
?>
