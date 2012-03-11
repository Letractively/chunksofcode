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
	include("my_pchart_copy/pData.class.php");
	include("my_pchart_copy/pChart.class.php");
	include("my_pchart_copy/pCache.class.php");

	// declare utility function library includes:
	include "php_lib/ViewDefinition.class.php";
	include "php_lib/Configuration.class.php";
	include "php_lib/HtmlUtil.class.php";
	include "php_lib/SqlGenerator.class.php";
	include "php_lib/PChartUtil.class.php";

	// run the page scripts:
	include 'init.php';
	include 'form.php';
	include 'main.php';

?>
</html>