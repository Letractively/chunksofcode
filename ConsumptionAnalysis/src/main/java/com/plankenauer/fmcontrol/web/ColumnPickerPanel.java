package com.plankenauer.fmcontrol.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.googlecode.wicket.jquery.ui.interaction.Selectable;
import com.plankenauer.fmcontrol.config.Table;
import com.plankenauer.fmcontrol.config.UiSettings;

@SuppressWarnings("serial")
public class ColumnPickerPanel extends Panel
{

    private final DisplayQueryPage queryPage;

    public ColumnPickerPanel(String id, DisplayQueryPage queryPage) {
        super(id);
        this.queryPage = queryPage;

        initComponents();
    }

    private void initComponents() {
        final Table anyTable = queryPage.config.getDatasource().getTables().get(0);
        List<String> valueNames = new ArrayList<>();

        final Map<String, Integer> reverseMap = new HashMap<String, Integer>() {
            @Override
            public Integer put(String key, Integer value) {
                if (super.containsKey(key)) {
                    throw new RuntimeException("name " + key + " not unique in "
                            + anyTable.getValueColumnExpr());
                }
                return super.put(key, value);
            }
        };

        for (Integer valueKey : anyTable.getValueColumnExpr().keySet()) {
            String valueColumnLabel = anyTable.getValueColumnLabel(valueKey);
            valueNames.add(valueColumnLabel);
            reverseMap.put(valueColumnLabel, valueKey);
        }

        final UiSettings uiSettings = queryPage.config.getUiSettings();
        final Selectable<String> selectable = new Selectable<String>("selectable",
                                                                     valueNames) {
            @Override
            protected void onSelect(AjaxRequestTarget target, List<String> items) {
                uiSettings.removeAllChosenColumns();

                if (items.isEmpty()) { // set all
                    Iterator<Entry<String, Integer>> it = reverseMap.entrySet()
                                                                    .iterator();
                    while (it.hasNext()) {
                        Entry<String, Integer> next = it.next();
                        uiSettings.addChosenColumn(next.getValue());
                    }
                    List<String> selectedItems = getSelectedItems();
                    if (selectedItems != null) {
                        selectedItems.clear();
                        for (Integer key : anyTable.getValueColumnExpr().keySet()) {
                            selectedItems.add(anyTable.getValueColumnLabel(key));
                        }
                    }
                    info("Alle Werte wurden ausgewählt.");
                    target.add(this);

                } else {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Werte Auswahl: ");

                    for (Iterator<String> iterator = items.iterator(); iterator.hasNext();) {
                        String key = iterator.next();

                        uiSettings.addChosenColumn(reverseMap.get(key));
                        msg.append(key);

                        if (iterator.hasNext()) {
                            msg.append(", ");
                        }
                    }
                    info(msg.toString());
                }

                target.add(queryPage.feedback);
                target.add(queryPage.querySqlDebugLabel);
            }
        };

        this.add(selectable);

        selectable.add(new ListView<String>("items", valueNames) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Label("item", item.getModelObject()));
            }
        });
    }

}
