package com.myapp.consumptionanalysis.web.querypage.emptyplaceholders;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.myapp.consumptionanalysis.config.Config;
import com.myapp.consumptionanalysis.web.querypage.DisplayQueryPage;

@SuppressWarnings({ "serial", "unused" })
public class DayTimeBoundsInfoPanel extends Panel
{


    private static Logger log = Logger.getLogger(DayTimeBoundsInfoPanel.class);
    public DayTimeBoundsInfoPanel(String id, DisplayQueryPage queryPage2) {
        super(id);

        Config config = queryPage2.getConfig();
        String text = config.getSelectionConfig().getDayTimeBoundsString();

        add(new Label("dayTimeBoundsInfo", text));
        setOutputMarkupId(true); // avoid js errors
    }
}
