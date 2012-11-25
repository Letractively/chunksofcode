package com.myapp.consumptionanalysis.web.chart.test;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.myapp.consumptionanalysis.chart.barchart.BarChartJavaScriptGenerator;
import com.myapp.consumptionanalysis.config.Config;

@SuppressWarnings("serial")
public class BarChartPanel extends Panel
{
    public static final String PLACEHOLDER_DOM_ID = "chartPlaceholder";
    
    
    private static Logger log = Logger.getLogger(BarChartPanel.class);

    private Config config;

    private Label workaround = null;

    public BarChartPanel(String id, Config cfg) {
        super(id);
        this.config = cfg;
        
        workaround = new Label("workaround") {
            @Override
            public void renderHead(IHeaderResponse response) {
                super.renderHead(response);
                String jsCode = new BarChartJavaScriptGenerator().generateJavaScript(config);
                response.render(new JavaScriptContentHeaderItem(jsCode, null, null));
                log.debug("workaround head rendered.");
            }
        };
        workaround.setOutputMarkupId(true);
        add(workaround);
    }
    
    public Label getComponentToUpdateAfterAjax() {
        return workaround;
    }
}
