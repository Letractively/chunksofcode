<?php
/*
 * this file will draw the formular and fill
 * it with the request parameters or default
 * values.
 * (c) Andre Ragg 2011
 */


// TODO: control the inputs below with javascript on value changed!

$viewDef = $_REQUEST["at.aquadome.prototype.viewDefinition"];

// show error message and exit when no data is available:
if (sizeof($viewDef -> getAvailableYears()) <= 0) {
    HtmlUtil :: html_error("Keine Daten in der Datenbank!");
    exit();
}

?>


<!-- render the http form to request a new query -->
<form method="get">

<!-- view type radiobuttons: -->
Ansichtstyp:
<?php
    HtmlUtil :: render_radiobutton_input(
        "queryTyp",
        $viewDef -> getAvailableViewTypes(),
        $viewDef -> getViewType()
    );
?>
<br/>
<!-- end of the viewtype radio buttons -->


<!-- year selectbox: -->
Jahr:
<?php
    HtmlUtil :: render_options_input(
        "queryJahr",
        $viewDef -> getAvailableYears(),
        $viewDef -> getYear()
    );
?>
<!-- end of year selectbox -->


<!-- month selectbox: -->
Monat:
<?php
    HtmlUtil :: render_options_input(
        "queryMonat",
        $viewDef -> getAvailableMonths(),
        $viewDef -> getMonth()
    );
?>
<!-- end of month selectbox -->


<!-- day selectbox: -->
Tag:
<?php
    HtmlUtil :: render_options_input(
        "queryTag",
        $viewDef -> getAvailableDays(),
        $viewDef -> getDay()
    );
?>
<!-- end of day selectbox -->


<br/>
<button onclick="this.form.submit(); return false;">Abfragen</button>

</form>
<!-- end of http form -->

