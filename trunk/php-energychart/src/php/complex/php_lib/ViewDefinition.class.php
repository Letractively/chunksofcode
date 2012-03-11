<?php

class ViewDefinition {

    private $availableViewTypes = array("Gesamt", "Jahr", "Monat", "Tag");
    private $viewType_s = -1;

    private $availableYears_a;
    private $year_i = -1;

    private $availableMonths_a = array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    private $month_i = -1;

    private $availableDays_a = array(
             1,  2,  3,  4,  5,  6,  7,  8,  9,
        10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
        20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
        30, 31
    );
    private $day_i = -1;

    private $errors_a;

    //////////////////  init  ///////////////////

    function __construct() {
        $this -> viewType_s = null;
        $this -> year_i = -1;
        $this -> month_i = -1;
        $this -> day_i = -1;
        $this -> errors_a = array();

        $this -> availableYears_a = ViewDefinition :: fetchAvailableYears();

        $this -> initFromHttpRequest();
        $this -> correctHttpState();
    }

    private function initFromHttpRequest() {
        if (isset($_REQUEST["queryTyp"])) {
            $this -> setViewType($_REQUEST["queryTyp"]);
        }
        if (isset($_REQUEST["queryJahr"])) {
            $this -> setYear($_REQUEST["queryJahr"]);
        }
        if (isset($_REQUEST["queryMonat"])) {
            $this -> setMonth($_REQUEST["queryMonat"]);
        }
        if (isset($_REQUEST["queryTag"])) {
            $this -> setDay($_REQUEST["queryTag"]);
        }
    }

    private function correctHttpState() {
        $vtypes = $this -> availableViewTypes;
        $years = $this -> availableYears_a;
        $months = $this -> availableMonths_a;
        $days = $this -> availableDays_a;

        if (! in_array($this -> viewType_s, $vtypes)) {
            $this -> viewType_s = $vtypes[0];  // fallback to JahresAnsicht
        }
        if (! in_array($this -> year_i, $years)) {
            $this -> year_i = $years[0]; // use most recent year as default
        }
        if (! in_array($this -> month_i, $months)) {
            $this -> month_i = date("n"); // use current month as default
        }
        if (! in_array($this -> day_i, $days)) {
            $this -> day_i = date("j"); // use current day as default
        }
    }

    //////////////////  setters  ///////////////////

    private function setViewType($viewType) {
        if (ViewDefinition :: checkViewType($viewType)) {
            $this -> viewType_s = $viewType;
        } else {
            $this -> errors_a["viewtype"] = "Ungültiger Ansichtstyp: ".$viewType;
        }
    }
    private function setYear($year_i) {
        $i = 0 + $year_i;
        if (is_int($i)) {
            $this -> year_i = $i;
        } else {
            $this -> errors_a["year"] = "Ungültiges Jahr: ".$year_i;
        }
    }
    private function setMonth($month_i) {
        $i = 0 + $month_i;
        if (is_int($i) && $i < 13 && $i > 0) {
            $this -> month_i = $month_i;
        } else {
            $this -> errors_a["month"] = "Ungültiges Monat: ".$month_i;
        }
    }
    private function setDay($day_i) {
        $i = 0 + $day_i;
        if (is_int($i) && $i > 0 && $i < 32) {
            $this -> day_i = $i;
        } else {
            $this -> errors_a["day"] = "Ungültiger Tag: ".$day_i;
        }
    }

    //////////////////  getter  ///////////////////

    public function getViewType() { return $this -> viewType_s; }
    public function getYear() {     return $this -> year_i;     }
    public function getMonth() {    return $this -> month_i;    }
    public function getDay() {      return $this -> day_i;      }
    public function getErrors() {   return $this -> errors_a;   }

    //////////////////  static stuff  ///////////////////

    public function getAvailableViewTypes() {
        return array_slice($this -> availableViewTypes, 0);
    }

    public function getAvailableMonths() {
        return array_slice($this -> availableMonths_a, 0);
    }

    public function getAvailableDays() {
        return array_slice($this -> availableDays_a, 0);
    }

    private function checkViewType($vt) {
        return in_array($vt, $this -> availableViewTypes);
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

    private static function fetchAvailableYears() {
        // setup a connection to the database:
        mysql_connect(
            Configuration :: db_host,
            Configuration :: db_username,
            Configuration :: db_password
        );
        mysql_select_db(Configuration :: db_schema);

         // query available years from database:
         $sql = "SELECT distinct YEAR(datum) FROM leistung_kw ORDER BY 1 desc";
         $result = mysql_query($sql);      // XXX hardcoded table name
         if (! $result) {
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
}

?>
