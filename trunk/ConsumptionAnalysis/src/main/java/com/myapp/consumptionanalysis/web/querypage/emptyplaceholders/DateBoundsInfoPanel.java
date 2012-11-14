package com.myapp.consumptionanalysis.web.querypage.emptyplaceholders;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.myapp.consumptionanalysis.config.Config;
import com.myapp.consumptionanalysis.web.querypage.DisplayQueryPage;

@SuppressWarnings({ "serial" })
public class DateBoundsInfoPanel extends Panel
{
    @SuppressWarnings({ "unused" })
    private static Logger log = Logger.getLogger(DateBoundsInfoPanel.class);

    public DateBoundsInfoPanel(String id, DisplayQueryPage queryPage2) {
        super(id);

        Config config = queryPage2.getConfig();
        String text = config.getDatasource().getDateBoundsString();

        add(new Label("dateBoundsInfoLabel", text));
        setOutputMarkupId(true); // avoid js errors
    }
}
