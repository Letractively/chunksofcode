<?php

function createSqlStatement($selectedViewType) {
    $sql = "SELECT "; ///////////////////////////////////
    switch ($selectedViewType) {
        case "Gesamt" :$sql .= "YEAR(datum)"; break;
    	case "Jahr" :  $sql .= "MONTH(datum)"; break;
    	case "Monat" : $sql .= "DAY(datum)";   break;
    	case "Tag" :   $sql .= "HOUR(zeit)";   break;
    	default : {
	        echo "<font color='red'>FEHLER: Kein gültiger Ansichtstyp: '".$selectedViewType."'</font>\n";
	        return "ERROR";
	    } 
    }
    $sql .= " as label, ";
    switch ($selectedViewType) {
        case "Gesamt" :$sql .= "SUM(kwh_pro15min)/1000"; break;
        case "Jahr" :  $sql .= "SUM(kwh_pro15min)/1000"; break;
        case "Monat" : $sql .= "SUM(kwh_pro15min)/1000"; break;
        case "Tag" :   $sql .= "SUM(kwh_pro15min)";      break;
    }
    $sql.= " as value \n";
    
    
    $sql.= "FROM "; ///////////////////////////////////
    $sql.=       "fmcontrol_kw_leistung ";
//    $sql.=       "(select * from fmcontrol_kw_leistung union";
//    $sql.=       " select * from fmcontrol_kw_leistung2)";
//    $sql.=       "nasenview ";
    
    
    $sql.= "WHERE "; ///////////////////////////////////
    switch ($selectedViewType) {
        case "Gesamt" : $sql .= "(1=1)"; break; // dummy
        case "Jahr" : 
            $sql .=  "YEAR(datum) = ? ";     // 1 
            break;
        case "Monat" : 
            $sql .= "YEAR(datum) = ? ";      // 1 
            $sql .= "AND MONTH(datum) = ? "; // 2
            break;
        case "Tag" :
            $sql .= "YEAR(datum) = ? ";      // 1 
        	$sql .= "AND MONTH(datum) = ? "; // 2
        	$sql .= "AND DAY(datum) = ? ";   // 3 
        	break;
    }
    $sql.= "\n";
    
    
    $sql.= "GROUP BY "; ///////////////////////////////////
    switch ($selectedViewType) {
        case "Gesamt" : 
            $sql .= "YEAR(datum)";
            break;
        case "Jahr" : 
            $sql .= "YEAR(datum)";
            $sql .= ", MONTH(datum)";
            break;
        case "Monat" : 
            $sql .= "YEAR(datum)";
            $sql .= ", MONTH(datum)";
        	$sql .= ", DAY(datum)";
            break;
        case "Tag" :
            $sql .= "YEAR(datum)";
            $sql .= ", MONTH(datum)";
        	$sql .= ", DAY(datum)";
            $sql .= ", HOUR(zeit)";
            break;
    }
    $sql.= "\n";
    
    
    $sql.= "ORDER BY "; ///////////////////////////////////
    switch ($selectedViewType) {
        case "Gesamt" : 
            $sql .= "YEAR(datum)";
            break;
        case "Jahr" : 
            $sql .= "YEAR(datum)";
            $sql .= ", MONTH(datum)";
            break;
        case "Monat" : 
            $sql .= "YEAR(datum)";
            $sql .= ", MONTH(datum)";
            $sql .= ", DAY(datum)";
            break;
        case "Tag" :
            $sql .= "YEAR(datum)";
            $sql .= ", MONTH(datum)";
            $sql .= ", DAY(datum)";
            $sql .= ", HOUR(zeit)";
            break;
    }
    return $sql;
}

function bindStatementVariables($stmt, 
                                $selectedViewType, 
                                $selectedYear, 
                                $selectedMonth, 
                                $selectedDay) {
    switch ($selectedViewType) {
    	case "Gesamt" : /* nothing to do! */ break;
        case "Jahr" : 
            $stmt -> bind_param("i", $selectedYear);
            break;
        case "Monat" : 
            $stmt -> bind_param("ii", $selectedYear, $selectedMonth);
            break;
        case "Tag" :
            $stmt -> bind_param("iii", $selectedYear, $selectedMonth, $selectedDay);
            break;
        default :
            echo "FAILURE! selectedViewType was: ".$selectedViewType;
            break;
    }
}

?>