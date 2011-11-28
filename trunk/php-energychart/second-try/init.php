<?
/* 
 * this file will initialize the parameters used for the database query
 * if they were set in the http request parameters. 
 */
?>
<?php
    // store the selected view mode into a variable: (Jahr,Monat,Tag)
    $selectedViewType = null;
    if (isset($_REQUEST["queryTyp"])) {
        $selectedViewType = $_REQUEST["queryTyp"];
        if ($debug_mode == true) {
           echo "<font color='green'>DEBUG: Ausgew�hlte Ansicht: ".$selectedViewType."</font><br/>\n";
        }    
    }

	// store the selected jear mode into a variable:
	$selectedYear = null;
	if (isset($_REQUEST["queryJahr"])) {
	    $selectedYear=$_REQUEST["queryJahr"];
        if ($debug_mode == true) {
	       echo "<font color='green'>DEBUG: Ausgew�hltes Jahr: ".$selectedYear."</font><br/>\n";
        }    
	}
	
	// store the selected month mode into a variable:
	$selectedMonth = null;
	if (isset($_REQUEST["queryMonat"])) {
	    $selectedMonth=$_REQUEST["queryMonat"];
	    
        if ($debug_mode == true) {
        	echo "<font color='green'>DEBUG:Ausgew�hltes Monat: ".$selectedMonth."</font><br/>\n";    
        }
	}
    
    // store the selected day mode into a variable:
    $selectedDay = null;
    if (isset($_REQUEST["queryTag"])) {
        $selectedDay=$_REQUEST["queryTag"];
        
        if ($debug_mode == true) {
            echo "<font color='green'>DEBUG:Ausgew�hlter Tag: ".$selectedDay."</font><br/>\n";    
        }
    }
    
    
    if ($debug_mode == true) {
        if ($selectedViewType == null) {
            echo "<font color='green'>DEBUG: W�hle Standard Ansicht.</font><br/>\n";
        }
        if ($selectedYear == null) {
            echo "<font color='green'>DEBUG: W�hle Standard Jahr.</font><br/>\n";
        }
        if ($selectedMonth == null) {
            echo "<font color='green'>DEBUG: W�hle Standard Monat.</font><br/>\n";
        }
        if ($selectedYear == null) {
            echo "<font color='green'>DEBUG: W�hle Standard Tag.</font><br/>\n";
        }
    }
?>
