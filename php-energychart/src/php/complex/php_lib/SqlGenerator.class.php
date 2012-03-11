<?php

class SqlGenerator {

	private $viewDef;

	public function __construct(ViewDefinition $viewDef) {
		$this -> viewDef = $viewDef;
	}

	private function compute_select_clause() {
	    $select = "    ";

	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt" :$select .= "YEAR(datum)"; break;
	        case "Jahr" :  $select .= "MONTH(datum)"; break;
	        case "Monat" : $select .= "DAY(datum)";   break;
	        case "Tag" :   $select .= "HOUR(zeit)";   break;
	    }

	    $select .= " AS label, \n    ";

	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt" :$select .= "SUM(kwh_pro15min)/1000"; break;
	        case "Jahr" :  $select .= "SUM(kwh_pro15min)/1000"; break;
	        case "Monat" : $select .= "SUM(kwh_pro15min)/1000"; break;
	        case "Tag" :   $select .= "SUM(kwh_pro15min)";      break;
	    }

	    $select.= " AS value \n";
	    return $select;
	}

	private function compute_from_clause() {
	    $from = "    ";
	    $from.=       "leistung_kw \n";
	//    $from.=     "( \n";
	//    $from.= "        ( \n";
	//    $from.= "            SELECT * FROM leistung_kw \n";
	//    $from.= "            WHERE \n";
	//    $from.=              compute_where_clause($selectedViewType);
	//    $from.= "        ) \n";
	//    $from.= "        UNION \n";
	//    $from.= "        ( \n";
	//    $from.= "            SELECT * FROM leistung_kw2 \n";
	//    $from.= "            WHERE \n";
	//    $from.=              compute_where_clause($selectedViewType);
	//    $from.= "        ) \n";
	//    $from.= "   ) AS beide_tabellen_vereint \n";
	    return $from;
	}

	private function compute_where_clause() {
	    $where = "    ";

	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt" :
	            $where .= "(1=1)";
	            break;
	        case "Jahr" :
	            $where .=  "YEAR(datum) = :jahr ";
	            break;
	        case "Monat" :
	            $where .= "YEAR(datum) = :jahr \n    ";
	            $where .= "AND MONTH(datum) = :monat ";
	            break;
	        case "Tag" :
	            $where .= "YEAR(datum) = :jahr \n    ";
	            $where .= "AND MONTH(datum) = :monat \n    ";
	            $where .= "AND DAY(datum) = :tag ";
	            break;
	    }

	    $where.= "\n";
	    return $where;
	}

	private function compute_group_by_clause() {
	    $groupby = "    ";

	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt" :
	            $groupby .= "YEAR(datum)";
	            break;
	        case "Jahr" :
	            $groupby .= "YEAR(datum), \n    ";
	            $groupby .= "MONTH(datum)";
	            break;
	        case "Monat" :
	            $groupby .= "YEAR(datum), \n    ";
	            $groupby .= "MONTH(datum), \n    ";
	            $groupby .= "DAY(datum)";
	            break;
	        case "Tag" :
	            $groupby .= "YEAR(datum), \n    ";
	            $groupby .= "MONTH(datum), \n    ";
	            $groupby .= "DAY(datum), \n    ";
	            $groupby .= "HOUR(zeit)";
	            break;
	    }

	    $groupby.= "\n";
	    return $groupby;
	}

	private function compute_order_by_clause() {
	    $orderby = "    ";

	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt" :
	            $orderby .= "YEAR(datum)";
	            break;
	        case "Jahr" :
	            $orderby .= "YEAR(datum), \n    ";
	            $orderby .= "MONTH(datum)";
	            break;
	        case "Monat" :
	            $orderby .= "YEAR(datum), \n    ";
	            $orderby .= "MONTH(datum), \n    ";
	            $orderby .= "DAY(datum)";
	            break;
	        case "Tag" :
	            $orderby .= "YEAR(datum), \n    ";
	            $orderby .= "MONTH(datum), \n    ";
	            $orderby .= "DAY(datum), \n    ";
	            $orderby .= "HOUR(zeit)";
	            break;
	    }

	    $orderby.= "\n";
	    return $orderby;
	}

	public function createSqlStatement() {
	    $sql = "SELECT \n"   . $this -> compute_select_clause();
	    $sql.= "FROM \n"     . $this -> compute_from_clause();
	    $sql.= "WHERE \n"    . $this -> compute_where_clause();
	    $sql.= "GROUP BY \n" . $this -> compute_group_by_clause();
	    $sql.= "ORDER BY \n" . $this -> compute_order_by_clause();
	    return $sql;
	}

	public function statementParameters() {
        $vd = $this -> viewDef;
	    $result = array();
	    switch ($vd -> getViewType()) {
            case "Gesamt" : break;
	        case "Tag" :    $result[":tag"] =   $vd -> getDay();
	        case "Monat" :  $result[":monat"] = $vd -> getMonth();
	        case "Jahr"  :  $result[":jahr"] =  $vd -> getYear();
	    }
	    return $result;
	}
}

?>
