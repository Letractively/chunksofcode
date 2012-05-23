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
        var jahrBox = document.getElementById("queryJahrSelBox");
        var monatBox = document.getElementById("queryMonatSelBox");
        var tagBox = document.getElementById("queryTagSelBox");

        switch (selection) {
          case "Jahr":
            jahrBox.disabled = false;
            monatBox.disabled = true;
            tagBox.disabled = true;
            break;
          case "Monat":
            jahrBox.disabled = false;
            monatBox.disabled = false;
            tagBox.disabled = true;
            break;
          case "Tag":
            jahrBox.disabled = false;
            monatBox.disabled = false;
            tagBox.disabled = false;
            break;
          case "Gesamt":
            jahrBox.disabled = true;
            monatBox.disabled = true;
            tagBox.disabled = true;
            break;
        }
      }
    </script>
  </head>
  <body>


  <h2>Energieverbrauch Diagramm Demo (php/mysql)</h2>
  Bitte wählen Sie einen Ansichtstyp, eine oder mehrere Datenquellen und einen Zeitraum.<br/>
  Beim Klick auf Abfragen werden die Daten aus den gewählten Quellen selektiert.</br>
  Aus den Daten wird dann eine Grafik generiert und angezeigt.<br/>
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
