<?php

    /**
     * renders the header of the table 
     */
    function render_table_header() {
        echo "<!-- render the result table: -->\n";
        echo "<table border='1'>\n";
        echo   "  ";
        echo   "<tr>";
        echo     "<th>label</th>";
        echo     "<th>wert</th>";
        echo   "<tr>\n";
    }

    /** 
     * renders a row of the result
     */
    function render_table_row($datum, $kwh_total) {
        echo "  ";
        echo "<tr>";
        echo   "<td>".$datum."</td>";
        echo   "<td>".$kwh_total."</td>";
        echo "</tr>\n";
    }           
    
    /** 
     * renders the bottom of the html result table
     */
    function render_table_bottom() {
        echo "</table>\n";
        echo "<!-- end of result table -->\n\n";
    }
    
    function render_table($stmt) {
    	render_table_header();
        $stmt -> bind_result($name, $val);
        for ($i = 0; $stmt -> fetch(); $i++) {
            render_table_row($name, $val);
        }
        render_table_bottom();
    }
?>