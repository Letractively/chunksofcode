<?php
/**
 * encapsulates routines for creating a chart image
 *
 * (c) Andre Ragg 2011
 */
class PChartUtil {

	private $viewDef;
	private $pChartObj;


	public function __construct(ViewDefinition $viewDef) {
        $this -> viewDef = $viewDef;
	}


    /**
     * renders a line chart for the given data, using the given labels,
     * and displays an average line in the background.
     *
     */
    public function draw_chart($labels, $values) {
        $this -> initPChartObj();

        // setup main graph data source:
        $DataSet = $this -> create_dataset($values, $labels);
        // calculate average graph data source (store in a separate dataset):
        $AverageDataSet =  $this -> create_average_dataset($values);

        // initialize graphics, draw background and scales:
        $this -> drawChartScale($DataSet);

        // draw the data graphs:
        $this -> drawBackgroundFunctionGraph($AverageDataSet);
        $this -> drawMainFunctionGraph($DataSet);

        // draw legend on the left bottom edge of the picture
        $this -> drawLabels($DataSet, $AverageDataSet);

        // set up output file name and render image to file:
        $output_directory =
            Configuration :: chart_image_target."/".$this -> calc_output_dir();
        if ( ! is_dir($output_directory)) {
            mkdir($output_directory, true);
        }
        $output_file = $output_directory."/".$this -> calc_output_filename();
        $this -> pChartObj -> Render($output_file);
        return $output_file;
    }


    /**
     * create a datasource that will represent
     * the average of the given values
     *
     * @return a pData instance that holds the average
     */
    private function create_average_dataset($values) {

        // calculate average (store in a separate dataset):
        $AvgData = new pData();
        $sum = 0;

        for ($i = 0; $i < sizeof($values); $i++) {
            $sum += $values[$i];
        }

        $sum = $sum / sizeof($values);
        $sum = preg_replace("/([.,]\\d\\d)\\d*/", "\\1", "".$sum);

        // XXX assuming the y axis name is the unit of the data values
        $label_average = "Mittel (".$sum." ".$this -> calc_y_axis_name().")";

        for ($i = 0; $i < sizeof($values); $i++) {
            $AvgData -> AddPoint($sum, $label_average);
        }

        $AvgData -> AddSerie($label_average);
        return $AvgData;
    }


    /**
     * create a datasource that will represent
     * the data for the value data
     *
     * @return a pData instance that holds the average
     */
    private function create_dataset($values, $labels) {
        $value_series_name = $this -> calc_value_series_name();

        // setup graphic data source:
        $DataSet = new pData();

        // add the kwh-array as data series:
        $DataSet -> AddPoint($values, $value_series_name);
        $DataSet -> AddSerie($value_series_name);


        // set the "Tag" values as labels for the x axis:
        $DataSet -> AddPoint($labels, "x-axis-labels");
        $DataSet -> SetAbsciseLabelSerie("x-axis-labels");

        // label axes:
        $DataSet -> SetXAxisName($this -> calc_x_axis_name());
        $DataSet -> SetYAxisName($this -> calc_y_axis_name());
        return $DataSet;
    }


    ///////////// chart rendering routines ////////////////////


	private function initPChartObj() {
        $chart_width = Configuration :: chart_width;
        $chart_height = Configuration :: chart_height;

        $this -> pChartObj = new pChart($chart_width, $chart_height);
        $this -> pChartObj -> setFontProperties("Fonts/tahoma.ttf", 8);

        $this -> pChartObj -> setGraphArea(
            70, 40,
            $chart_width - 20,
            $chart_height - 40
        );

        $this -> pChartObj -> drawGraphArea(255,255,255, true); //stripes
	}

	private function drawChartScale(pData $dataSet) {
        $this -> pChartObj -> drawScale(
            $dataSet -> GetData(),
            $dataSet -> GetDataDescription(),
            SCALE_START0, // (display empty area below data)
            // SCALE_NORMAL, // (hide empty area below data)
            150,150,150,
            true,
            0
        );

        $this -> pChartObj -> drawGrid(4,false,200,200,200,50);
	}

    private function drawBackgroundFunctionGraph(pData $backgroundDataSet) {
        // draw the average line:
         $this -> pChartObj -> setColorPalette(0,255,0,0);  //red
         $this -> pChartObj -> drawLineGraph(
            $backgroundDataSet -> GetData(),
            $backgroundDataSet -> GetDataDescription()
        );
    }

	private function drawMainFunctionGraph(pData $dataSet) {
        // draw the line graph:
        $this -> pChartObj -> setColorPalette(0,0,0,0);  //black
        $this -> pChartObj -> drawLineGraph(
            $dataSet -> GetData(),
            $dataSet -> GetDataDescription()
        );

        // draw the dots at the data points
        $this -> pChartObj -> setColorPalette(0,0,0,0);  //black
        $this -> pChartObj -> drawPlotGraph(
            $dataSet -> GetData(),
            $dataSet -> GetDataDescription(),
            2, // big radius
            1, // small radius
            255,255,255 // white background
        );
	}

	private function drawLabels(pData $DataSet, pData $AverageDataSet) {
        $chart_width = Configuration :: chart_width;
        $chart_height = Configuration :: chart_height;

        $this -> pChartObj -> setFontProperties("Fonts/tahoma.ttf",8);
        $this -> pChartObj -> drawLegend(
            10,
            $chart_height - 20,
            $DataSet -> GetDataDescription(),
            255,255,255
        );

        // draw a chart title:
        $this -> pChartObj -> setFontProperties("Fonts/tahoma.ttf",10);
        $this -> pChartObj -> drawTitle(
            60,22,
            $this -> calc_chart_title(),
            50,50,50,
            585
        );

        // draw average legend on the left bottom edge of the picture
        $this -> pChartObj -> setColorPalette(0,255,0,0);          // red
        $this -> pChartObj -> setFontProperties("Fonts/tahoma.ttf",8);
        $this -> pChartObj -> drawLegend(
            150,                                       // x
            $chart_height - 20,                        // y
            $AverageDataSet -> GetDataDescription(),
            255,255,255                                // white background
        );

        // draw a black border around the picture:
        $this -> pChartObj -> drawRectangle(
            0,0,
            $chart_width - 1, $chart_height - 1,
            0,0,0
        );
	}



	//////// configuration the labels used in the chart ////////////


	private function calc_output_dir() {
	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt": return "all";
	        case "Jahr":   return "yearly";
	        case "Monat":  return "monthly";
	        case "Tag":    return "daily";
	    }
	}

	private function calc_output_filename() {
		$date = $this -> viewDef -> getSelectedDate();

	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt": return "all.png";
	        case "Jahr":   return date("Y", $date).".png";
	        case "Monat":  return date("Y-m", $date).".png";
	        case "Tag":    return date("Y-m-d", $date).".png";
	    }
	}

	private function calc_chart_title() {
        $date = $this -> viewDef -> getSelectedDate();

	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt": return "Gesamtansicht";
	        case "Jahr":   return "Jahresansicht ".date("Y", $date);
	        case "Monat":  return "Monatsansicht ".date("Y-m", $date);
	        case "Tag":    return "Tagesansicht ".date("Y-m-d", $date);
	    }
	}

	private function calc_value_series_name() {
	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt": return "Jahresverbrauch";
	        case "Jahr":   return "Monatsverbrauch";
	        case "Monat":  return "Tagesverbrauch";
	        case "Tag":    return "stündlicher Verbrauch";
	    }
	}

	private function calc_x_axis_name() {
	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt": return "Jahr";
	        case "Jahr":   return "Monat";
	        case "Monat":  return "Kalendertag";
	        case "Tag":    return "Stunde";
	    }
	}

	private function calc_y_axis_name() {
	    switch ($this -> viewDef -> getViewType()) {
	        case "Gesamt":
	        case "Jahr":
	        case "Monat":  return "MWh";
	        case "Tag":    return "kWh";
	    }
	}
}

?>