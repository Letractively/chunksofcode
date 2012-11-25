$( 
    function () { 
        
        /* Definition of raw data of every series: */
        
        /* data for column 'kWh verbraucht' */
        var series_VALUE_1_KWHVERBRAUCHT = []; 
        series_VALUE_1_KWHVERBRAUCHT.push([1328050800000, 184.0]); 
        series_VALUE_1_KWHVERBRAUCHT.push([1328054400000, 184.0]); 
        series_VALUE_1_KWHVERBRAUCHT.push([1328058000000, 184.0]); 
        series_VALUE_1_KWHVERBRAUCHT.push([1328061600000, 184.0]); 
        series_VALUE_1_KWHVERBRAUCHT.push([1328065200000, 184.0]); 
        series_VALUE_1_KWHVERBRAUCHT.push([1328068800000, 184.0]); 
        
        /* data for column 'Preis in €' */
        var series_VALUE_2_PREISIN = []; 
        series_VALUE_2_PREISIN.push([1328050800000, 23.880000000000003]); 
        series_VALUE_2_PREISIN.push([1328054400000, 23.880000000000003]); 
        series_VALUE_2_PREISIN.push([1328058000000, 23.880000000000003]); 
        series_VALUE_2_PREISIN.push([1328061600000, 23.880000000000003]); 
        series_VALUE_2_PREISIN.push([1328065200000, 23.880000000000003]); 
        series_VALUE_2_PREISIN.push([1328068800000, 23.880000000000003]); 

        
        /* Definition of the dataseries with labels */
        $$$JS_VAR_DATA_SERIES$$$ = [
            { 
                label: 'kWh verbraucht = -0000.00', 
                data: series_VALUE_1_KWHVERBRAUCHT
            } , 
            { 
                label: 'Preis in € = -0000.00', 
                data: series_VALUE_2_PREISIN
            } 
        ]; 
        
        /* Definition of the chart options */
        $$$JS_VAR_OPTIONS$$$ = { 
            xaxis: {
                mode: 'time' 
            }, series: {
                lines: { show: true }
            },
            crosshair: { mode: 'x' },
            grid: { hoverable: true, autoHighlight: false }
        }; 
        
        /* Definition of where chart is rendered: */
        $$$JS_VAR_PLACEHOLDER_DIV$$$ = $('#$$$PLACEHOLDER_DOM_ID$$$'); 
        
        /* Start the rendering engine:  */
        chart0_PlotObj = $.plot(
            $$$JS_VAR_PLACEHOLDER_DIV$$$, 
            $$$JS_VAR_DATA_SERIES$$$, 
            $$$JS_VAR_OPTIONS$$$
        ); 
        
        /* Register fancy onmouseover event handler  */
        var legends = $('#$$$PLACEHOLDER_DOM_ID$$$ .legendLabel');
        legends.each(function () {
            /* fix the widths so they don't jump around */
            $(this).css('width', $(this).width());
        });
        
        var updateLegendTimeout = null;
        var latestPosition = null;
        
        function updateLegend() {
            updateLegendTimeout = null;
            var pos = latestPosition;
            var axes = chart0_PlotObj.getAxes();
            
//            console.log('pos:   ('+pos.x+'), ('+pos.y+')');
//            console.log('xaxes: ('+axes.xaxis.min+'), ('+axes.xaxis.max+')');
//            console.log('yaxes: ('+axes.yaxis.min+'), ('+axes.yaxis.max+')');
            
            if (pos.x < axes.xaxis.min || pos.x > axes.xaxis.max
             || pos.y < axes.yaxis.min || pos.y > axes.yaxis.max) {
                console.log('out of bounds!');
                return;
            }
            
            var i, j, dataset = chart0_PlotObj.getData();
            
            for (i = 0; i < dataset.length; ++i) {
                var series = dataset[i];
                
                
                /* find the nearest points, x-wise */
                for (j = 0; j < series.data.length; ++j) {
                    if (series.data[j][0] > pos.x) {
                        break;
                    }
                }
                
                /* now interpolate */
                var y, p1 = series.data[j - 1], p2 = series.data[j];
                if (p1 == null) {
                    y = p2[1];
                } else if (p2 == null) {
                    y = p1[1];
                } else {
                    y = p1[1] + (p2[1] - p1[1]) * (pos.x - p1[0]) / (p2[0] - p1[0]);
                }
                
                legends.eq(i).text(series.label.replace(/=.*/, '= ' + y.toFixed(2)));
            }
        }
        
        $$$JS_VAR_PLACEHOLDER_DIV$$$.bind('plothover', function(event, pos, item) { 
            latestPosition = pos;
            if (!updateLegendTimeout) {
                updateLegendTimeout = setTimeout(updateLegend, 50);
            } 
        });
    }
); 