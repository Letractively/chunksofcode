<?
/*
 * this file is used as root file that will include the contents of the
 * website components. every http request will start at this file.
 * (c) Andre Ragg 2011
 */
?>
<html>
<?php

    // declare external libraries:

	include("lib/pchart/pData.class.php");
	include("lib/pchart/pChart.class.php");
	include("lib/pchart/pCache.class.php");


	// declare utility function library includes:

    include "Configuration.class.php";

	include "lib/custom/ViewDefinition.class.php";
	include "lib/custom/HtmlUtil.class.php";
	include "lib/custom/SqlGenerator.class.php";
	include "lib/custom/PChartUtil.class.php";


	// run the page scripts:
	include 'init.php';
	include 'form.php';
	include 'main.php';

?>
</html>