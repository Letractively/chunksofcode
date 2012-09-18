<?php
/**
 * generates a sql statement to select data for a specific viewtype
 */
final class SqlGenerator {

	private $viewDef;

	public function __construct(ViewDefinition $viewDef) {
		$this -> viewDef = $viewDef;
	}

    public function createSqlStatement() {
        $sql = "SELECT \n";
        $sql.= $this -> compute_select_clause();

        $sql.= "FROM \n";
        $sql.= $this -> compute_from_clause();

        if ($this -> is_multi_table_query()) {
            $sql.= "WHERE (1=1) \n";
            $sql.= "GROUP BY label \n";
            $sql.= "ORDER BY label \n";
        } else {
            $sql.= "WHERE "      . $this -> compute_where_clause();
            $sql.= "GROUP BY \n" . $this -> compute_group_by_clause();
            $sql.= "ORDER BY \n" . $this -> compute_order_by_clause();
        }

        return $sql;
    }

    private function compute_select_clause() {
        $sql = "";
        if ($this -> is_multi_table_query()) {
            /* create a select clause that is used to select labels and values
             * from a result wrapping a unified source (when we have to
             * query in multiple tables, the label field is already calculated
             * and aliased to "label" */
            $sql = "    label, \n    SUM(value) AS value \n";
        } else {
            $sql.= $this -> compute_source_select_clause();
        }
        return $sql;
    }

    /**
     * create a select clause that is used to select directly from a table,
     * not from an united result set (where datum or hour fields, not just
     * the "label" field from the union statement)
     */
    private function compute_source_select_clause() {
        $select = "    ";

        // label
        switch ($this -> viewDef -> getViewType()) {
            case "Everything" :$select .= "YEAR(Zeit)"; break;
            case "Year" :  $select .= "MONTH(Zeit)"; break;
            case "Month" : $select .= "DAY(Zeit)";   break;
            case "Day" :   $select .= "HOUR(Zeit)";   break;
        }
        $select .= " AS label, \n    ";

        // value
        switch ($this -> viewDef -> getViewType()) {
            case "Everything" :$select .= "SUM(kwh_pro15min)/1000"; break;
            case "Year" :  $select .= "SUM(kwh_pro15min)/1000"; break;
            case "Month" : $select .= "SUM(kwh_pro15min)/1000"; break;
            case "Day" :   $select .= "SUM(kwh_pro15min)";      break;
        }
        $select.= " AS value \n";

        return $select;
    }

    public function statementParameters() {
        $vd = $this -> viewDef;
        $result = array();
        switch ($vd -> getViewType()) {
            case "Everything" : break;
            case "Day" :    $result[":tag"] =   $vd -> getDay();
            case "Month" :  $result[":monat"] = $vd -> getMonth();
            case "Year"  :  $result[":jahr"] =  $vd -> getYear();
        }
        return $result;
    }

	private function is_multi_table_query() {
        return sizeof($this -> viewDef -> getDatabases()) > 1;
	}

	private function compute_from_clause() {
        $from = "    ";
	    $src_a = $this -> viewDef -> getDatabases();
        $tableNameMap = Configuration :: getDatabases();

        if ( ! $this -> is_multi_table_query()) {
            $tableName = $tableNameMap[$src_a[0]];
            $from .= $tableName." \n";
            return $from;
        }

        $from .= "( \n";
        for ($i = 0, $length = sizeof($src_a); $i < $length; $i++) {
            $tableKey = $src_a[$i];
            $tableName = $tableNameMap[$tableKey];
            $from .= "( \n";
            $from .= " SELECT \n";
            $from .=       $this -> compute_source_select_clause();
            $from .= " FROM \n    ";
            $from .=       $tableName." \n";
            $from .= " WHERE ";
            $from .=       $this -> compute_where_clause();
            $from .= " GROUP BY \n";
            $from .=       $this -> compute_group_by_clause();
            $from .= ") \n";
            if ($i + 1 < $length) {
                $from .= "UNION ALL \n";
            }
        }
        $from .= "    ) AS foo \n";
        return $from;
	}

	private function compute_where_clause() {
	    $where = "";
        switch ($this -> viewDef -> getViewType()) {
            case "Everything" :
                $where .= "(1=1)";
                break;
            case "Year" :
                $where = "\n    ";
                $where .=  "YEAR(Zeit) = :jahr ";
                break;
            case "Month" :
                $where = "\n    ";
                $where .= "YEAR(Zeit) = :jahr \n    ";
                $where .= "AND MONTH(Zeit) = :monat ";
                break;
            case "Day" :
                $where = "\n    ";
                $where .= "YEAR(Zeit) = :jahr \n    ";
                $where .= "AND MONTH(Zeit) = :monat \n    ";
                $where .= "AND DAY(Zeit) = :tag ";
                break;
        }
	    $where.= "\n";
	    return $where;
	}

	private function compute_group_by_clause() {
	    $groupby = "    ";
	    switch ($this -> viewDef -> getViewType()) {
	        case "Everything" : $groupby .= "YEAR(Zeit)";  break;
	        case "Year" :   $groupby .= "MONTH(Zeit)"; break;
	        case "Month" :  $groupby .= "DAY(Zeit)";   break;
	        case "Day" :    $groupby .= "HOUR(Zeit)";   break;
	    }
	    $groupby.= "\n";
	    return $groupby;
	}

	private function compute_order_by_clause() {
	    $orderby = "    ";
	    switch ($this -> viewDef -> getViewType()) {
	        case "Everything" : $orderby .= "YEAR(Zeit)";  break;
	        case "Year" :   $orderby .= "MONTH(Zeit)"; break;
	        case "Month" :  $orderby .= "DAY(Zeit)";   break;
	        case "Day" :    $orderby .= "HOUR(Zeit)";   break;
	    }
	    $orderby.= "\n";
	    return $orderby;
	}
}

?>
