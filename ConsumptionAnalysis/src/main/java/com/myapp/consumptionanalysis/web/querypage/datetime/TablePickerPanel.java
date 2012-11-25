package com.myapp.consumptionanalysis.web.querypage.datetime;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.googlecode.wicket.jquery.ui.interaction.Selectable;
import com.myapp.consumptionanalysis.config.Table;
import com.myapp.consumptionanalysis.config.UiSettings;
import com.myapp.consumptionanalysis.web.querypage.DisplayQueryPage;

@SuppressWarnings("serial")
public class TablePickerPanel extends Panel
{


    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(TablePickerPanel.class);
    private final DisplayQueryPage queryPage;



    public TablePickerPanel(String id, DisplayQueryPage queryPage) {
        super(id);
        this.queryPage = queryPage;

        initComponents();
    }

    private void initComponents() {
        final List<Table> tables = queryPage.getConfig().getSelectionConfig().getTables();
        final UiSettings uiSettings = queryPage.getConfig().getUiSettings();

        final Selectable<Table> selectable = new Selectable<Table>("selectable", tables) {
            @Override
            protected void onSelect(AjaxRequestTarget target, List<Table> items) {
                uiSettings.removeAllChosenTables();

                if (items.isEmpty()) { // set all
                    for (Table t : queryPage.getConfig().getSelectionConfig().getTables()) {
                        uiSettings.addChosenTable(t);
                    }
                    List<Table> selectedItems = getSelectedItems();
                    if (selectedItems != null) {
                        selectedItems.clear();
                        selectedItems.addAll(queryPage.getConfig().getSelectionConfig().getTables());
                    }
                    info("Alle Tabellen wurden ausgewählt.");
                    target.add(this);
                    
                } else {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Tabellen Auswahl: ");
                    for (Iterator<Table> iterator = items.iterator(); iterator.hasNext();) {
                        Table t = iterator.next();

                        uiSettings.addChosenTable(t);
                        msg.append(t.getAlias());

                        if (iterator.hasNext()) {
                            msg.append(", ");
                        }
                    }
                    info(msg.toString());
                }

                target.add(queryPage.getComponentsToUpdateAfterAjax());
            }
        };

        this.add(selectable);

        selectable.add(new ListView<Table>("items", tables) {
            @Override
            protected void populateItem(ListItem<Table> item) {
                final Table modelObject = item.getModelObject();
                Label label = new Label("item", modelObject.getAlias());
                item.add(label);
            }
        });

    }

}
