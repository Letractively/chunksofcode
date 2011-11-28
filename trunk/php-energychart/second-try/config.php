<?
/* 
 * this file is used to define constants and
 * configruation parameters for the application.
 */
?>
<?php
	// define database parameters:
	$db_host="localhost";
	$db_username="root";
	$db_password="";
	$db_schema="test";
	
	// if this flag is set to true, the resulting data
	// will also be rendered as a html table:
	$show_result_table = true;
	
	// enable this to print debug elements on the page
	// SET FALSE IN PRODUCTION ENVIRONMENT !
	$debug_mode = true;
	
	// the graphics will be rendered with this dimension:
	$chart_height = 230;
	$chart_width = 700;
?>