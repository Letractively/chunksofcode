<?php

final class ViewDefinition {

    private $availableViewTypes_a;
    private $availableYears_a;
    private $availableMonths_a;
    private $availableDays_a;
    private $availableDatabases_a;

    private $viewType_s;
    private $year_i;
    private $month_i;
    private $day_i;
    private $databases_a;

    private $errors_a;

    //////////////////  init  ///////////////////

    function __construct() {
        $this -> availableViewTypes_a = array("Everything","Year","Month","Day");
        $this -> availableYears_a = ViewDefinition :: fetchAvailableYears();
        $this -> availableMonths_a = array(1,2,3,4,5,6,7,8,9,10,11,12);
        $this -> availableDays_a = array(
                 1,  2,  3,  4,  5,  6,  7,  8,  9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            30, 31
        );
        $this -> errors_a = array();
        $this -> availableDatabases_a = array();
        foreach (Configuration :: getDatabases() as $key => $value) {
            array_push($this -> availableDatabases_a, $key);
        }
        $this -> databases_a = array();

        $this -> viewType_s = null;
        $this -> year_i = -1;
        $this -> month_i = -1;
        $this -> day_i = -1;

        $this -> initFromHttpRequest();
        $this -> correctHttpState();
    }
    private function initFromHttpRequest() {
        if (isset($_REQUEST["queryTyp"])) {
            $this -> setViewType($_REQUEST["queryTyp"]);
        }
        if (isset($_REQUEST["queryYear"])) {
            $this -> setYear($_REQUEST["queryYear"]);
        }
        if (isset($_REQUEST["queryMonth"])) {
            $this -> setMonth($_REQUEST["queryMonth"]);
        }
        if (isset($_REQUEST["queryDay"])) {
            $this -> setDay($_REQUEST["queryDay"]);
        }

        $allDbs = $this -> availableDatabases_a;
        for ($i = 0, $len = sizeof($allDbs); $i < $len; $i++) {
            $db = "".$allDbs[$i];
            if (isset($_REQUEST[$db])) {
                if (! array_search($db, $this -> databases_a)) {
                    array_push($this -> databases_a, $db);
                }
            }
        }
    }
    private function correctHttpState() {
        $vtypes = $this -> availableViewTypes_a;
        $years = $this -> availableYears_a;
        $months = $this -> availableMonths_a;
        $days = $this -> availableDays_a;

        if (! in_array($this -> viewType_s, $vtypes)) {
            $this -> viewType_s = $vtypes[3];  // fallback to month view
        }
        if (! in_array($this -> year_i, $years)) {
            $this -> year_i = $years[0]; // fallback to most recent year
            if (is_int($years[1])) {
                $this -> year_i = $years[0]; // minus one, if available
            }
        }
        if (! in_array($this -> month_i, $months)) {
            $this -> month_i = date("n"); // fallback to current month
        }
        if (! in_array($this -> day_i, $days)) {
            $this -> day_i = date("j"); // fallback to current day
        }

        if (sizeof($this -> databases_a) <= 0) {
            array_push($this -> databases_a, // fallback to first database
                       $this -> availableDatabases_a[0]);
        }
    }
    private static function fetchAvailableYears() {
        // setup a connection to the database:
        mysql_connect(
            Configuration :: db_host,
            Configuration :: db_username,
            Configuration :: db_password
        );
        mysql_select_db(Configuration :: db_schema);

         // query available years from database:
         $sql = "SELECT distinct YEAR(Zeit) FROM ";
         $sql .= Configuration :: tableA;
         $sql .= " ORDER BY 1 desc";
         $result = mysql_query($sql);      // XXX hardcoded table name
         if (! $result) {
         	echo "query: ".$sql;
            echo "ERROR: ".mysql_error();
         }
         $years = array();

         for ($i = 0; ; $i++) {
            if ($row = $row = mysql_fetch_array($result, MYSQL_NUM)) {
                $years[$i] = $row[0];
                continue;
            }
            break;
         }

         return $years;
    }

    //////////////////  getters  ///////////////////

    public function getViewType()  {  return $this -> viewType_s;  }
    public function getYear()      {  return $this -> year_i;      }
    public function getMonth()     {  return $this -> month_i;     }
    public function getDay()       {  return $this -> day_i;       }
    public function getErrors()    {  return $this -> errors_a;    }
    public function getDatabases() {  return $this -> databases_a; }

    public function getAvailableDatabases() {
        return array_slice($this -> availableDatabases_a, 0);
    }
    public function getAvailableMonths() {
        return array_slice($this -> availableMonths_a, 0);
    }
    public function getAvailableDays() {
        return array_slice($this -> availableDays_a, 0);
    }
    public function getAvailableYears() {
        return array_slice($this -> availableYears_a, 0);
    }
    public function getSelectedDate() {
        $selectedDate = strtotime(
            $this -> year_i ."-". $this -> month_i ."-". $this -> day_i
        );
        return $selectedDate;
    }
    public function getAvailableViewTypes() {
        return array_slice($this -> availableViewTypes_a, 0);
    }

    //////////////////  setters  ///////////////////

    private function setViewType($viewType) {
        if (in_array($viewType, $this -> availableViewTypes_a)) {
            $this -> viewType_s = $viewType;
        } else {
            $this -> errors_a["viewtype"] = "Invalid View Type: ".$viewType;
        }
    }
    private function setYear($year_i) {
        $i = 0 + $year_i;
        if (is_int($i)) {
            $this -> year_i = $i;
        } else {
            $this -> errors_a["year"] = "Invalid Year: ".$year_i;
        }
    }
    private function setMonth($month_i) {
        $i = 0 + $month_i;
        if (is_int($i) && $i < 13 && $i > 0) {
            $this -> month_i = $month_i;
        } else {
            $this -> errors_a["month"] = "Invalid Month: ".$month_i;
        }
    }
    private function setDatabase($tables_a) {

    }
    private function setDay($day_i) {
        $i = 0 + $day_i;
        if (is_int($i) && $i > 0 && $i < 32) {
            $this -> day_i = $i;
        } else {
            $this -> errors_a["day"] = "Invalid Day: ".$day_i;
        }
    }
}

?>
