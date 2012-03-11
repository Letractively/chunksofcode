<?php
/**
 * a set of static functions that are helpful for
 * rendering stuff to a html page
 *
 * (c) Andre Ragg 2011
 */
final class HtmlUtil {

	/**
	 * display a message in red color
	 */
	public static function html_error($msg) {
	    echo "<font color='red'>FEHLER: ".$msg."</font><br/>\n";
	}

	/**
	 * display a message in green color
	 */
	public static function html_debug($msg) {
        if (Configuration :: debug_mode != true) {
            return;
        }
		echo "<font color='green'>DEBUG: ".$msg."</font><br/>\n";
	}

	/**
	 * shows a sql query in green color
	 */
	public static function html_dump_query(SqlGenerator $sqlGen) {
        if (Configuration :: debug_mode != true) {
        	return;
        }

	    $str_replaced = "".$sqlGen -> createSqlStatement();
	    foreach ($sqlGen -> statementParameters() as $key => $value) {
	    	$str_replaced = str_replace($key, $value, $str_replaced);
	    }
        $str_replaced = trim($str_replaced);

        echo "<!-- display query: -->\n";
        echo "<font color='green'>";
        echo "<pre>\n-- DEBUG: SQL query --\n";
	    echo $str_replaced;
	    echo "</pre>\n</font>\n";
	    echo "<!-- end of query -->\n\n";
	}

	/**
	 *  render a html table
	 */
	public static function render_table($label_array, $value_array) {
		if (Configuration :: show_result_table != true) {
			return;
		}
	    echo "\n<!-- render the result table: -->\n";
	    echo "<table border='1'>\n";
	    echo   "  ";
	    echo   "<tr>";
	    echo     "<th>label</th>";
	    echo     "<th>wert</th>";
	    echo   "<tr>\n";

	    $len = sizeof($label_array);

	    for ($i = 0; $i < $len; $i++) {
	        echo "  ";
	        echo "<tr>";
	        echo   "<td>".$label_array[$i]."</td>";
	        echo   "<td>".$value_array[$i]."</td>";
	        echo "</tr>\n";
	    }

	    echo "</table>\n";
	    echo "<!-- end of result table -->\n\n";
	}

    /**
     * render a options input element
     */
	public static function render_options_input(
	                       $inputName_s, $options_a, $selectedOption = null) {
        echo "\n<select name='".$inputName_s."'>\n";

        for ($i = 0, $len = sizeof($options_a); $i < $len; $i++) {
            $opt = $options_a[$i];
            echo '  <option value="'.$opt.'"';
            if ($opt == $selectedOption) {
            	echo ' selected="1"';
            }
            echo ">".$opt."</option>\n";
        }

        echo "</select>\n";
	}

    /**
     * render a radio button input element
     */
    public static function render_radiobutton_input(
                          $inputName_s, $options_a, $selectedOption = null) {
        for ($i = 0, $len = sizeof($options_a); $i < $len; $i++) {
        	$opt = $options_a[$i];
        	echo "<input type='radio' name='".$inputName_s."' value='".$opt."'";
        	if ($opt == $selectedOption) {
        		echo " checked='1'";
        	}
        	echo "/>".$opt."\n";
        }
    }

    /**
     * render a set of checkboxes
     */
    public static function render_checkboxes_input(
                                $options_a, $selectedOptions_a = array()) {
        for ($i = 0, $len = sizeof($options_a); $i < $len; $i++) {
            $opt = $options_a[$i];
            echo "<input type='checkbox' name='".$opt."' value='true'";
            if (in_array($opt, $selectedOptions_a)) {
                echo " checked='1'";
            }
            echo ">".$opt."</input>\n";
        }
    }
}

?>