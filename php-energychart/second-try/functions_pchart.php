<?php
    
    /**
     * create a datasource that will represent 
     * the average of the given values
     * 
     * @return a pData instance that holds the average
     * @param $values the data
     */
    function create_average_dataset($values, $y_axis_name) {
        // calculate average (store in a separate dataset):
        $AvgData = new pData();
        $sum = 0;
        
        for ($i = 0; $i < sizeof($values); $i++) {
            $sum += $values[$i];
        }
        
        $sum = $sum / sizeof($values);
        $sum = preg_replace("/([.,]\\d\\d)\\d*/", "\\1", "".$sum);
        $label_average = "Mittel (".$sum." ".$y_axis_name.")";
        
        for ($i = 0; $i < sizeof($values); $i++) {
            $AvgData -> AddPoint($sum, $label_average);
        }
        
        $AvgData -> AddSerie($label_average);
        return $AvgData;
    }
    
    function create_dataset($values,
                            $labels,
				            $value_series_name, 
				            $x_axis_name, 
				            $y_axis_name) {
        // setup graphic data source:
        $DataSet = new pData();
        
        // add the kwh-array as data series:
        $DataSet -> AddPoint($values, $value_series_name);
        $DataSet -> AddSerie($value_series_name);
        
        
        // set the "Tag" values as labels for the x axis:
        $DataSet -> AddPoint($labels, "x-axis-labels");
        $DataSet -> SetAbsciseLabelSerie("x-axis-labels");
        
        // label axes:
        $DataSet -> SetXAxisName($x_axis_name);
        $DataSet -> SetYAxisName($y_axis_name);
        return $DataSet;
    }
    
    /**
     * renders a line chart for the given data, using the given labels,
     * and displays an average line in the background.
     * 
     */
    function drawChart($labels, 
		               $values, 
		               $chart_height, 
		               $chart_width, 
		               $chart_title, 
		               $targetFile,
			           $value_series_name, 
			           $x_axis_name, 
			           $y_axis_name) {
        // setup graphic data source:
        $DataSet = create_dataset(
            $values, 
            $labels,
            $value_series_name, 
            $x_axis_name, 
            $y_axis_name
        );
        
        // calculate average (store in a separate dataset):
        $AverageDataSet = create_average_dataset($values, $y_axis_name);
        
        // initialize graphics:
        $Chart = new pChart($chart_width, $chart_height);
        $Chart -> setFontProperties("Fonts/tahoma.ttf", 8);
        $Chart -> setGraphArea(70, 40, 680, 190);  
        $Chart -> drawGraphArea(255,255,255, false); //stripes
        
        $Chart -> drawScale(
            $DataSet -> GetData(),
            $DataSet -> GetDataDescription(),
            SCALE_START0, // (display empty area below data)
            // SCALE_NORMAL, // (hide empty area below data)
            150,150,150,
            true,
            0
        );  
        $Chart -> drawGrid(4,false,200,200,200,50);
        
        // draw the data /////////////////////////////
        
        // draw the average line:
        $Chart -> setColorPalette(0,255,0,0);  //red
        $Chart -> drawLineGraph(
            $AverageDataSet -> GetData(), 
            $AverageDataSet -> GetDataDescription()
        );
        
        // draw the line graph:
        $Chart -> setColorPalette(0,0,0,0);  //black
        $Chart -> drawLineGraph(
            $DataSet -> GetData(), 
            $DataSet -> GetDataDescription()
        );
        
        // draw the dots at the data points
        $Chart -> setColorPalette(0,0,0,0);  //black
        $Chart -> drawPlotGraph(
            $DataSet -> GetData(), 
            $DataSet -> GetDataDescription(), 
            2, // big radius
            1, // small radius
            255,255,255 // white background
        );
        
        
        // Finish the graph /////////////////////////////
        
        // draw average legend on the left bottom edge of the picture
        $Chart -> setFontProperties("Fonts/tahoma.ttf",8);   
        $Chart -> drawLegend(10, $chart_height-20, $DataSet -> GetDataDescription(), 255,255,255);   
        
        // draw average legend on the left bottom edge of the picture
        $Chart -> setColorPalette(0,255,0,0);          // red
        $Chart -> drawLegend(
            150,                                       // x
            $chart_height-20,                          // y
            $AverageDataSet -> GetDataDescription(),
            255,255,255                                // white background
        );   
        $Chart -> setFontProperties("Fonts/tahoma.ttf",10);   

        $Chart -> drawTitle(60,22,$chart_title,50,50,50,585);
        
        // draw a black border around the picture:
        $Chart -> drawRectangle(0, 0, $chart_width-1, $chart_height-1, 0,0,0);
        $Chart -> Render($targetFile);
    }
?>