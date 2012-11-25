package com.myapp.consumptionanalysis.web.querypage.emptyplaceholders;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.myapp.consumptionanalysis.web.querypage.DisplayQueryPage;

@SuppressWarnings({ "serial" })
public class GroupByInfoPanel extends Panel
{
    @SuppressWarnings({ "unused" })
    private static Logger log = Logger.getLogger(GroupByInfoPanel.class);

    public GroupByInfoPanel(String id, DisplayQueryPage queryPage2) {
        super(id);

        String text= queryPage2.getConfig().getSelectionConfig().getGroupByString();
        add(new Label("groupByInfo", text));
        setOutputMarkupId(true); // avoid js errors
    }
}
