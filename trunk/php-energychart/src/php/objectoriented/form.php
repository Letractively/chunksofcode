<?php
	/*
	 * this file will draw the formular and fill
	 * it with the request parameters or default
	 * values.
	 * (c) Andre Ragg 2011
	 */


	// TODO: control the inputs below with javascript on value changed!

	$viewDef = $_REQUEST["com.myapp.energychart.viewDefinition"];

?>


<!-- render the http form to request a new query -->
<form method="get">

    <!-- view type radiobuttons: -->
    ViewType:
<?php HtmlUtil :: render_radiobutton_input("queryTyp",
            $viewDef -> getAvailableViewTypes(), $viewDef -> getViewType());
?>
    <br/>
    <!-- end of the viewtype radio buttons -->

    <!-- data source checkboxes: -->
    Database:
<?php HtmlUtil :: render_checkboxes_input(
           $viewDef -> getAvailableDatabases(), $viewDef -> getDatabases());
?>
    <br/>
    <!-- end of data source checkboxes -->

    <!-- year selectbox: -->
    Year:
<?php HtmlUtil :: render_options_input("queryYear",
                    $viewDef -> getAvailableYears(), $viewDef -> getYear());
?>
    <!-- end of year selectbox -->

    <!-- month selectbox: -->
    Month:
<?php HtmlUtil :: render_options_input("queryMonth",
                  $viewDef -> getAvailableMonths(), $viewDef -> getMonth());
?>
    <!-- end of month selectbox -->

    <!-- day selectbox: -->
    Day:
<?php HtmlUtil :: render_options_input( "queryDay",
                      $viewDef -> getAvailableDays(), $viewDef -> getDay());
?>
    <!-- end of day selectbox -->


<br/>
<button onclick="this.form.submit(); return false;">Query</button>

</form>
<!-- end of http form -->

