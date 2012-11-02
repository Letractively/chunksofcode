package com.plankenauer.fmcontrol.web;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.kendo.datetime.TimePicker;

@SuppressWarnings("serial")
public class DayTimeBoundsPickerPanel extends BoundsPickerPanel
{
    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(DayTimeBoundsPickerPanel.class);

    private TimePicker timeStartPicker;
    private TimePicker timeEndPicker;



    public DayTimeBoundsPickerPanel(String id, DisplayQueryPage queryPage) {
        super(id, queryPage);
    }



    @Override
    protected void initComponents() {
        timeStartPicker = createStartPicker();
        timeEndPicker = createEndPicker();

        add(timeStartPicker);
        add(timeEndPicker);
    }


    private TimePicker createEndPicker() {
        Calendar c = dataSrcCfg().getDayTimeBoundsEnd();

        Model<Date> model;
        if (c != null) {
            model = Model.of(c.getTime());
        } else {
            model = Model.of((Date) null);
        }
        TimePicker timePicker = new TimePicker("dayTimeEndPicker", model);
        return timePicker;
    }

    private TimePicker createStartPicker() {
        Calendar c = dataSrcCfg().getDayTimeBoundsStart();

        Model<Date> model;
        if (c != null) {
            model = Model.of(c.getTime());
        } else {
            model = Model.of((Date) null);
        }
        TimePicker timePicker = new TimePicker("dayTimeStartPicker", model);
        return timePicker;
    }

    @Override
    protected String getCheckboxId() {
        return "dayTimeBoundsEnabled";
    }

    @Override
    protected boolean isConstrainedByConfig() {
        boolean startConstraint = null != dataSrcCfg().getDayTimeBoundsStart();
        boolean endConstraint = null != dataSrcCfg().getDayTimeBoundsEnd();
        return startConstraint || endConstraint;
    }

    @Override
    protected void setComponentsEnabled(boolean enabled) {
        timeStartPicker.setEnabled(enabled);
        timeEndPicker.setEnabled(enabled);
    }

    @Override
    protected void applyEndValue() {
        Date date = timeEndPicker.getModelObject();
        dataSrcCfg().setDayTimeBoundsEnd(date);
    }

    @Override
    protected void applyStartValue() {
        Date date = timeStartPicker.getModelObject();
        dataSrcCfg().setDayTimeBoundsStart(date);
    }

    @Override
    protected void unsetStartValue() {
        dataSrcCfg().setDayTimeBoundsStart(null);
    }

    @Override
    protected void unsetEndValue() {
        dataSrcCfg().setDayTimeBoundsEnd(null);
    }

    @Override
    protected void addItemsToUpdate(AjaxRequestTarget target) {
        target.add(timeStartPicker);
        target.add(timeEndPicker);
    }



    @Override
    protected void valuesHaveChanged(AjaxRequestTarget target) {
        Date start = timeStartPicker.getModelObject();
        Date end = timeEndPicker.getModelObject();

        String error = validateValue(start, end);

        if (error != null) {
            error(error);
            Calendar oldStart = dataSrcCfg().getDayTimeBoundsStart();
            if (oldStart == null) {
                timeStartPicker.setModelObject(null);
            } else {
                timeStartPicker.setModelObject(oldStart.getTime());
            }
            Calendar oldEnd = dataSrcCfg().getDayTimeBoundsEnd();
            if (oldEnd == null) {
                timeEndPicker.setModelObject(null);
            } else {
                timeEndPicker.setModelObject(oldEnd.getTime());
            }
        } else {
            applyStartValue();
            applyEndValue();
            target.add(queryPage.querySqlDebugLabel);
        }

        target.add(timeStartPicker);
        target.add(timeEndPicker);
    }

    private String validateValue(Date start, Date end) {
        if (start == null || end == null) {
            return null;
        }

        Calendar s = Calendar.getInstance();
        s.setTime(start);
        s.set(Calendar.YEAR, 2000);
        s.set(Calendar.MONTH, 1);
        s.set(Calendar.DAY_OF_MONTH, 1);

        Calendar e = Calendar.getInstance();
        e.setTime(end);
        e.set(Calendar.YEAR, 2000);
        e.set(Calendar.MONTH, 1);
        e.set(Calendar.DAY_OF_MONTH, 1);

        if (s.getTime().before(e.getTime())) {
            return null;
        }

        return "Der Start muss vor dem Ende liegen.";
    }

}
