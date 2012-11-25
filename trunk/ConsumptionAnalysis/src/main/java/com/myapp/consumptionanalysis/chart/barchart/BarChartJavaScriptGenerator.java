package com.myapp.consumptionanalysis.chart.barchart;

import static com.myapp.consumptionanalysis.web.chart.test.BarChartPanel.PLACEHOLDER_DOM_ID;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.myapp.consumptionanalysis.config.Config;
import com.myapp.consumptionanalysis.config.Constants.GroupByType;
import com.myapp.consumptionanalysis.config.DataSelectionConfig;
import com.myapp.consumptionanalysis.config.Table;
import com.myapp.consumptionanalysis.sql.DataRow;
import com.myapp.consumptionanalysis.sql.ResultSetHolder;
import com.myapp.consumptionanalysis.util.StringUtils;

public class BarChartJavaScriptGenerator
{
    private static final Logger log = Logger.getLogger(BarChartJavaScriptGenerator.class);
    private static final String NL = System.getProperty("line.separator");

    private static final String JS_VAR_DATA_SERIES = "chart0_RawData";
    private static final String JS_VAR_OPTIONS = "chart0_Options";
    private static final String JS_VAR_PLACEHOLDER_DIV = "chart0_PlaceHolder";
    private static final String JS_VAR_PLOT_OBJECT = "chart0_PlotObj";


    private StringBuilder bui;
    private Config config;
    private int indent = 0;
    private String indentString = "    ";

    public String generateJavaScript(Config config2) {
        synchronized (this) {
            this.config = config2;
            bui = new StringBuilder();
            indent = 0;

            bui.append("$( ");

            indent++;
            nl();
            bui.append("function () { ");

            indent++;
            impl();
            indent--;

            nl();
            bui.append("}");
            indent--;
            nl();
            bui.append("); ");

            if (log.isTraceEnabled()) {
                bui.append(NL);
                bui.append("/*    CONFIG DEBUG STRING START " + NL);
                bui.append(config.getDebugString() + NL);
                bui.append("      CONFIG DEBUG STRING END " + NL);
                bui.append("*/" + NL);
                bui.append(NL);
            }

            String result = bui.toString();
            bui = null;
            return result;
        }
    }

    private void impl() {
        BarChartDataSelector selector = new BarChartDataSelector(config);
        ResultSetHolder selectData;

        try {
            selectData = selector.selectData();
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO: exception handling
        }

        nl();
        comment("Definition of raw data of every series:", true);
        printRawData(selectData);
        nl();

        comment("Definition of the dataseries with labels", true);
        createDataSeriesArrayDefinition();
        nl();

        comment("Definition of the chart options", true);
        createOptionsDefinition();
        nl();

        comment("Definition of where chart is rendered:", true);
        createDivSelector();
        nl();

        comment("Start the rendering engine: ", true);
        printPlotFunctionCall();
        nl();

        comment("Register fancy onmouseover event handler ", true);
        appendOnHoverLabelUpdate();
    }

    private void printPlotFunctionCall() {
        nl();
        bui.append(JS_VAR_PLOT_OBJECT);
        bui.append(" = $.plot(");
        indent++;
        nl();
        bui.append(JS_VAR_PLACEHOLDER_DIV);
        bui.append(", ");
        nl();
        bui.append(JS_VAR_DATA_SERIES);
        bui.append(", ");
        nl();
        bui.append(JS_VAR_OPTIONS);
        indent--;
        nl();
        bui.append("); ");
    }


    private void appendOnHoverLabelUpdate() {
        nl();
        bui.append("var legends = $('#" + PLACEHOLDER_DOM_ID + " .legendLabel');");
        nl();
        bui.append("legends.each(function () {");
        indent++;
        comment("fix the widths so they don't jump around", true);
        nl();
        bui.append("$(this).css('width', $(this).width());");
        indent--;
        nl();
        bui.append("});");
        nl();
        nl();
        bui.append("var updateLegendTimeout = null;");
        nl();
        bui.append("var latestPosition = null;");
        nl();
        nl();



        bui.append("function updateLegend() {"); // function updateLegend START
        indent++;
        nl();
        bui.append("updateLegendTimeout = null;");
        nl();
        bui.append("var pos = latestPosition;");
        nl();
        bui.append("var axes = " + JS_VAR_PLOT_OBJECT + ".getAxes();");
//        nl();
//        nl();
//        bui.append("console.log('pos:   ('+pos.x+'), ('+pos.y+')');");
//        nl();
//        bui.append("console.log('xaxes: ('+axes.xaxis.min+'), ('+axes.xaxis.max+')');");
//        nl();
//        bui.append("console.log('yaxes: ('+axes.yaxis.min+'), ('+axes.yaxis.max+')');");
        nl();
        nl();
        bui.append("if (pos.x < axes.xaxis.min || pos.x > axes.xaxis.max");
        nl();
        bui.append(" || pos.y < axes.yaxis.min || pos.y > axes.yaxis.max) {");
        indent++;
        nl();
        bui.append("console.log('out of bounds!');");
        nl();
        bui.append("return;");
        indent--;
        nl();
        bui.append("}");
        nl();
        nl();
        bui.append("var i, j, dataset = " + JS_VAR_PLOT_OBJECT + ".getData();");
        nl();
        nl();



        bui.append("for (i = 0; i < dataset.length; ++i) {");// foreach dataset START
        indent++;
        nl();
        bui.append("var series = dataset[i];");
        nl();
        nl();
        comment("find the nearest points, x-wise", true);
        nl();
        bui.append("for (j = 0; j < series.data.length; ++j) {");//foreach datapoint START
        indent++;
        nl();
        bui.append("if (series.data[j][0] > pos.x) {");
        indent++;
        nl();
        bui.append("break;");
        indent--;
        nl();
        bui.append("}"); // if
        indent--;
        nl();
        bui.append("}"); // foreach datapoint END
        nl();


        comment("now interpolate", true);
        nl();
        bui.append("var y, p1 = series.data[j - 1], p2 = series.data[j];");
        nl();
        bui.append("if (p1 == null) {");
        indent++;
        nl();
        bui.append("y = p2[1];");
        indent--;
        nl();
        bui.append("} else if (p2 == null) {");
        indent++;
        nl();
        bui.append("y = p1[1];");
        indent--;
        nl();
        bui.append("} else {");
        indent++;
        nl();
        bui.append("y = p1[1] + (p2[1] - p1[1]) * (pos.x - p1[0]) / (p2[0] - p1[0]);");
        indent--;
        nl();
        bui.append("}");
        nl();
        nl();
        bui.append("legends.eq(i).text(series.label.replace(/=.*/, '= ' + y.toFixed(2)));");
        indent--;
        nl();
        bui.append("}"); // foreach dataset END
        indent--;
        nl();
        bui.append("}");// function updateLegend END


        nl();
        nl();
        bui.append(JS_VAR_PLACEHOLDER_DIV);
        bui.append(".bind('plothover', function(event, pos, item) { ");// register event START
        indent++;
        nl();
        bui.append("latestPosition = pos;");
        nl();
        bui.append("if (!updateLegendTimeout) {");
        indent++;
        nl();
        bui.append("updateLegendTimeout = setTimeout(updateLegend, 50);");
        indent--;
        nl();
        bui.append("} ");
        indent--;
        nl();
        bui.append("});");// register event END
    }


    private boolean isGapDetectionNecessary() {
        DataSelectionConfig sc = config.getSelectionConfig();
        if (sc.getGroupBy() != GroupByType.HOUR) {
            return false;
        }

        if (sc.getDayTimeBoundsStartDate() != null) {
            return true;
        }
        if (sc.getDayTimeBoundsEndDate() != null) {
            return true;
        }

        return false;

    }

    private void printRawData(ResultSetHolder selectData) {
        DataSelectionConfig sc = config.getSelectionConfig();
        Table anyTbl = sc.getTables().get(0);

        final boolean detectGapsInTimeline = isGapDetectionNecessary();
        log.debug("detectGapsInTimeline=" + detectGapsInTimeline);

        for (Iterator<Integer> chosen = config.getUiSettings()
                                              .getChosenColumns()
                                              .iterator(); chosen.hasNext();) {
            final Integer valColKey = chosen.next();
            final String variableName = calcSeriesDataVarName(anyTbl, valColKey);
            final String seriesLabel = anyTbl.getValueColumnLabel(valColKey);

            nl();
            comment("data for column '" + seriesLabel + "'", true);
            nl();
            bui.append("var " + variableName + " = []; ");

            Iterator<DataRow> rowItr = selectData.iterator();
            DataRow prev = null;

            for (int i = 0; rowItr.hasNext(); i++) {
                final DataRow row = rowItr.next();
                Date currTime = row.getTimestamp();

                if (detectGapsInTimeline && prev != null) {
                    // detect gap, we need to insert a null to register a jump
                    // in the data series
                    Date prevTime = prev.getTimestamp();
                    if (gapNecessary(currTime, prevTime)) {
                        insertGap(variableName, currTime, prevTime);
                    }
                }

                nl();
                if (log.isTraceEnabled()) { // embed debug info to js code
                    String debugInfo = "row #" + StringUtils.fillWithLeadingZeros(i, 3)
                            + " - label: " + row.getLabel();
                    debugInfo = wrapInBlockComment(debugInfo, true);
                    debugInfo = debugInfo + "    ";
                    bui.append(debugInfo);
                }

                Object value = row.getValue(valColKey);
                
                // null check if there is only one value to show:
                long timeAsLong = currTime == null ? 0L : currTime.getTime();
                bui.append(variableName + ".push([" + timeAsLong + ", " + value + "]); ");

                prev = row;
            }

            if (chosen.hasNext()) {
                nl();
            }
        }
    }

    private void insertGap(String variableName, Date currTime, Date prevTime) {
        comment("inserting gap, because we had a jump here.", true);

        nl();
        bui.append(variableName + ".push([" + (prevTime.getTime() + 1L) + ", null]); ");
        nl();
        bui.append(variableName + ".push([" + (currTime.getTime() - 1L) + ", null]); ");
        nl();
    }

    private boolean gapNecessary(Date current, Date prev) {
        Calendar helper = Calendar.getInstance();
        helper.setTime(prev);
        int prevDay = helper.get(Calendar.DAY_OF_MONTH);
        int prevMonth = helper.get(Calendar.MONTH);
        int prevYear = helper.get(Calendar.YEAR);

        helper.setTime(current);
        int currDay = helper.get(Calendar.DAY_OF_MONTH);
        int currMonth = helper.get(Calendar.MONTH);
        int currYear = helper.get(Calendar.YEAR);

        if (prevDay != currDay || prevMonth != currMonth || prevYear != currYear) {
            return true;
        } else {
            return false;
        }
    }

    private void comment(String text, boolean joinToOneLiner) {
        nl();
        bui.append(wrapInBlockComment(text, joinToOneLiner));
    }

    private static String wrapInBlockComment(String text, boolean joinToOneLiner) {
        text = text.replaceAll("\\*", "+");
        if (joinToOneLiner) {
            text = text.replaceAll("(?mx) \\s+", " ");
        }
        return "/* " + text + " */";
    }

    private void createDivSelector() {
        nl();
        bui.append(JS_VAR_PLACEHOLDER_DIV).append(" = ");
        bui.append("$('#");
        bui.append(PLACEHOLDER_DOM_ID);
        bui.append("'); ");
    }

    private void createOptionsDefinition() {
        nl();
        bui.append(JS_VAR_OPTIONS).append(" = { ");
        indent++;

        nl();
        bui.append("xaxis: {");
        indent++;

        nl();
        bui.append("mode: 'time' ");

        indent--;
        nl();
        bui.append("}, ");


        bui.append("series: {");
        indent++;
        nl();
        bui.append("lines: { show: true }");
        indent--;
        nl();
        bui.append("},");
        nl();
        bui.append("crosshair: { mode: 'x' },");
        nl();
        bui.append("grid: { hoverable: true, autoHighlight: false }");

        indent--;
        nl();
        bui.append("}; ");
    }

    private void createDataSeriesArrayDefinition() {
        nl();
        bui.append(JS_VAR_DATA_SERIES).append(" = [");
        indent++;

        final Table anyTbl = config.getSelectionConfig().getTables().get(0);

//        [ { label: "Foo", data: [ [10,  1], [17, -14], [30,  5] ] },
//          { label: "Bar", data: [ [11, 13], [19,  11], [30, -7] ] } ]

        Iterator<Integer> itr = config.getUiSettings().getChosenColumns().iterator();
        for (; itr.hasNext();) {
            final Integer valColKey = itr.next();

            nl();
            bui.append("{ ");
            indent++;

            nl();
            bui.append("label: '");
            bui.append(anyTbl.getValueColumnLabel(valColKey) + " = -0000.00");
            bui.append("', ");

            nl();
            bui.append("data: ");
            final String variableName = calcSeriesDataVarName(anyTbl, valColKey);
            bui.append(variableName);

            indent--;
            nl();
            bui.append("} ");


            if (itr.hasNext()) {
                bui.append(", ");
            } else {
                break;
            }
        }

        indent--;
        nl();
        bui.append("]; ");
    }

    private String calcSeriesDataVarName(Table table, Integer valColKey) {
        return "series_" + table.getOuterQueryLabel(valColKey);
    }

    private void nl() {
        bui.append(newLine());
    }

    private String newLine() {
        StringBuilder b = new StringBuilder();
        b.append(NL);
        for (int i = 0; i < indent; i++) {
            b.append(indentString);
        }
        return b.toString();
    }
}
