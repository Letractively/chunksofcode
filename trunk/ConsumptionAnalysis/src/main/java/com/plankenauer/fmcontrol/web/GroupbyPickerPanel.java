package com.plankenauer.fmcontrol.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.kendo.dropdown.AjaxDropDownList;
import com.plankenauer.fmcontrol.config.Constants.GroupByType;


@SuppressWarnings("serial")
public class GroupbyPickerPanel extends Panel
{

    private static final List<String> allTypeNames;
    private static final Map<String, GroupByType> nameToTypeMap;

    static {
        List<String> l = new ArrayList<>();
        Map<String, GroupByType> m = new HashMap<>();
        
        for (GroupByType type : GroupByType.values()) {
            String name = getName(type);
            l.add(name);
            m.put(name, type);
        }
        
        allTypeNames = Collections.unmodifiableList(l);
        nameToTypeMap = Collections.unmodifiableMap(m);
    }

    private final DisplayQueryPage queryPage;


    public GroupbyPickerPanel(String id, DisplayQueryPage queryPage2) {
        super(id);
        this.queryPage = queryPage2;

        GroupByType initial = queryPage.config.getDatasource().getGroupBy();
        Model<String> initialValue = Model.of(getName(initial));

        AjaxDropDownList<String> dropDownList;
        dropDownList = new AjaxDropDownList<String>("groupbyDropdown",
                                                    initialValue,
                                                    allTypeNames) {
            @Override
            public void onSelectionChanged(AjaxRequestTarget target, Form<?> form) {
                super.onSelectionChanged(target, form);
                String choice = this.getModelObject();

                if (choice != null) {
                    info("Gruppierung geändert: " + choice);
                    GroupByType gb = nameToTypeMap.get(choice);
                    if (gb == null) {
                        throw new RuntimeException("choice=" + choice);
                    }
                    queryPage.config.getDatasource().setGroupBy(gb);
                }

                target.add(queryPage.feedback);
                target.add(queryPage.querySqlDebugLabel);
            }
        };

        this.add(dropDownList);
    }



    private static String getName(GroupByType type) {
        String name = null;
        switch (type) {
            case TOTAL:
                name = "Gesamt";
                break;
            case YEAR:
                name = "Jahr";
                break;
            case MONTH:
                name = "Monat";
                break;
            case DAY:
                name = "Tag";
                break;
            case HOUR:
                name = "Stunde";
                break;
            default:
                throw new RuntimeException("" + type);
        }
        return name;
    }

}
