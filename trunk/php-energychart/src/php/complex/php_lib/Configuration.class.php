<?php
/**
 * this file is used to define constants and
 * configruation parameters for the application.
 * (c) Andre Ragg 2011
 */
class Configuration {

	// define database parameters, used by all connections
	// during script exection:
	const db_host="localhost";
	const db_username="andre";
	const db_password="";
	const db_schema="andre";

	// if this flag is set to true, the resulting data
	// will also be rendered as a html table:
	const show_result_table = true;

	// enable this to print debug elements on the page
	// SET FALSE IN PRODUCTION ENVIRONMENT !
	const debug_mode = false;

	// the chart image will be rendered with this dimensions:
	const chart_height = 230;
	const chart_width = 700;

	// the chart image files will be stored in this
	// filesystem directory. make sure it is existing and the apache webserver process
	// has write access to it.
	// TODO: implement absolute directory files (solve the <img src="?" /> problem)
	// currently this directory is relative to the deployed php files
	const chart_image_target = "./rendered_images"; // XXX: needs to end with a "/" character
}

?>
