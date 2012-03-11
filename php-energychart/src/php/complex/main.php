<?php
/*
 * this script will execute the core logic for the demo application.
 * (c) Andre Ragg 2011
 */


$viewDef = $_REQUEST["at.aquadome.prototype.viewDefinition"];

// generate sql statement:
$sqlGen = new SqlGenerator($viewDef);
$paramData = $sqlGen -> statementParameters();
HtmlUtil :: html_dump_query($sqlGen);

// create database connection and setup sql statement:
$db = new PDO(
    "mysql:dbname=".Configuration::db_schema.";host=".Configuration::db_host.";"
    , Configuration::db_username , Configuration::db_password
);
$pStmt = $db -> prepare($sqlGen -> createSqlStatement());

// execute statement:
if ( ! $pStmt -> execute($paramData)) {
    HtmlUtil :: html_error("Abfrage fehlgeschlagen!");
    if (Configuration :: debug_mode == true) {
    	HtmlUtil :: html_debug("<br/>Error description:<br/>\n");
    	var_dump($pStmt -> errorInfo());
    }
    return;
}

// fetch data into arrays:
$label_array = array();
$value_array = array();
$pStmt -> bindColumn("label", $label);
$pStmt -> bindColumn("value", $value);
$rowCount = 0;
for ($i = 0; $pStmt -> fetch(); $i++, $rowCount++) {
    $label_array[$i] = $label;
    $value_array[$i] = $value;
}
HtmlUtil :: html_debug($rowCount." rows fetched.");
if (0 >= $rowCount) {
	HtmlUtil :: html_error("Keine Daten gefunden!<br/>\n".
	           "Bitte versuchen Sie einen anderen Zeitpunkt.\n");
    return;
}

// draw chart:
$pchartUtil = new PChartUtil($viewDef);
$output_file = $pchartUtil -> draw_chart($label_array, $value_array);
HtmlUtil :: html_debug("image was rendered to: ".$output_file);

// embed the rendered picture:
echo "\n\n<!-- embed image file: -->\n";
echo "<img src='".$output_file."' />\n\n";

HtmlUtil :: render_table($label_array, $value_array);
?>
