package com.myapp.consumptionanalysis.web.querypage.emptyplaceholders;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.myapp.consumptionanalysis.config.Table;
import com.myapp.consumptionanalysis.web.querypage.DisplayQueryPage;

@SuppressWarnings({ "serial" })
public class ColumnInfoPanel extends Panel
{
    @SuppressWarnings({ "unused" })
    private static Logger log = Logger.getLogger(ColumnInfoPanel.class);


    public ColumnInfoPanel(String id, DisplayQueryPage queryPage2) {
        super(id);

        Table anyTable = queryPage2.getConfig().getDatasource().getTables().get(0);
        StringBuilder msg = new StringBuilder();

        for (Iterator<Integer> i = anyTable.getValueColumnExpr().keySet().iterator(); i.hasNext();) {
            String valueColumnLabel = anyTable.getValueColumnLabel(i.next());
            msg.append(valueColumnLabel);

            if (i.hasNext()) {
                msg.append(", ");
            }
        }

        add(new Label("columnInfo", msg.toString()));
        setOutputMarkupId(true); // avoid js errors
    }
}
