<?php
/*
 * this file is used as root file that will include the contents of the
 * website components. every http request will start at this file.
 * (c) Andre Ragg 2011
 */
?>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <script type="text/javascript">
      function refreshForm(selection) {
        var jahrBox = document.getElementById("queryYearSelBox");
        var monatBox = document.getElementById("queryMonthSelBox");
        var tagBox = document.getElementById("queryDaySelBox");

        switch (selection) {
          case "Year":
            jahrBox.disabled = false;
            monatBox.disabled = true;
            tagBox.disabled = true;
            break;
          case "Month":
            jahrBox.disabled = false;
            monatBox.disabled = false;
            tagBox.disabled = true;
            break;
          case "Day":
            jahrBox.disabled = false;
            monatBox.disabled = false;
            tagBox.disabled = false;
            break;
          case "Everything":
            jahrBox.disabled = true;
            monatBox.disabled = true;
            tagBox.disabled = true;
            break;
        }
      }
    </script>
  </head>
  <body>


  <h2>Energy Consumption Graph Demo (php/mysql)</h2>
   Please select a view type, one or more data sources and a period. <br/>
   By clicking on the query button data will be selected and a chart will be generated. </ Br>
  <hr/>


<?php

    // declare external libraries:

    include("lib/pchart/pData.class.php");
    include("lib/pchart/pChart.class.php");
    include("lib/pchart/pCache.class.php");


    // declare utility function library includes:

    include "Configuration.class.php";

    include "lib/custom/ViewDefinition.class.php";
    include "lib/custom/HtmlUtil.class.php";
    include "lib/custom/SqlGenerator.class.php";
    include "lib/custom/PChartUtil.class.php";


    // run the page scripts:
    include 'init.php';
    include 'form.php';
    include 'main.php';

    $viewDef = $_REQUEST["com.myapp.energychart.viewDefinition"];
?>

    <script type="text/javascript">
      // window.onload = function () { alert("old onload"); };
      var oldWindowOnloadFunc = window.onload;
      window.onload = function() {
        if (typeof oldWindowOnloadFunc == "function") {
          oldWindowOnloadFunc();
        }
        
        // alert("new onload");
        refreshForm("<?php echo $viewDef -> getViewType(); ?>");
      }  
    </script>
  </body>
</html>
