<?php
/*
 * this file will initialize the parameters used for the database query
 * if they were set in the http request parameters.
 * (c) Andre Ragg 2011
 */
?>
<?php
	$viewDef = new ViewDefinition();

	// show debug messages:
        HtmlUtil :: html_debug("chosen Ansicht: "  .$viewDef -> getViewType() );
        HtmlUtil :: html_debug("chosen Jahr: "     .$viewDef -> getYear()     );
        HtmlUtil :: html_debug("chosen Monat: "    .$viewDef -> getMonth()    );
        HtmlUtil :: html_debug("chosen Tag: "      .$viewDef -> getDay()      );
        HtmlUtil :: html_debug("chosen Databases: ".sizeof($viewDef -> getDatabases()));

	// show error message and exit when no data is available:
	if (sizeof($viewDef -> getAvailableYears()) <= 0) {
	    HtmlUtil :: html_error("Keine Daten in der Datenbank!");
	    exit();
	}

	// save the viewDefinition in the request for further usage:
	$_REQUEST["com.myapp.energychart.viewDefinition"] = $viewDef;
?>
