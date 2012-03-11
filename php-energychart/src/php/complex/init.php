<?
/*
 * this file will initialize the parameters used for the database query
 * if they were set in the http request parameters.
 * (c) Andre Ragg 2011
 */
?>
<?php
	$viewDef = new ViewDefinition();

	// display the validation errors:
	if (sizeof($viewDef -> getErrors()) >= 0) {
		foreach ($viewDef -> getErrors() as $key => $value) {
			HtmlUtil :: html_error($value);
		}
	}

	// show debug messages:
	HtmlUtil :: html_debug("Gew�hlte Ansicht: ".$viewDef -> getViewType() );
	HtmlUtil :: html_debug("Gew�hlte Jahr: "   .$viewDef -> getYear()     );
	HtmlUtil :: html_debug("Gew�hlte Monat: "  .$viewDef -> getMonth()    );
	HtmlUtil :: html_debug("Gew�hlte Tag: "    .$viewDef -> getDay()      );

	// save the viewDefinition in the request for further usage:
	$_REQUEST["at.aquadome.prototype.viewDefinition"] = $viewDef;
?>
