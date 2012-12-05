package com.myapp.consumptionanalysis.web.querypage.datetime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.Options;
import com.googlecode.wicket.jquery.ui.kendo.datetime.TimePicker;
import com.myapp.consumptionanalysis.util.DateUtils;
import com.myapp.consumptionanalysis.web.querypage.DisplayQueryPage;

@SuppressWarnings("serial")
public class DayTimeBoundsPickerPanel extends BoundsPickerPanel
{

    private static Logger log = Logger.getLogger(DayTimeBoundsPickerPanel.class);


    private static final Options opts;
    static {
        Options o = new Options();
        o.set("culture", "'de-DE'");
        opts = o;
    }


    private static class MyTimePicker extends TimePicker
    {
        public MyTimePicker(String id, Model<Date> model) {
            super(id, model, "HH:mm", opts);
            setOutputMarkupId(true);
        }
    }


    private TimePicker timeStartPicker;
    private TimePicker timeEndPicker;


    public DayTimeBoundsPickerPanel(String id, DisplayQueryPage queryPage) {
        super(id, queryPage, "dayTimeBoundsEnabled");
    }


    @Override
    protected void initCustomComponents() {
        Model<Date> startModel = Model.of(dataSrcCfg().getDayTimeBoundsStartDate());
        Model<Date> endModel = Model.of(dataSrcCfg().getDayTimeBoundsEndDate());

        timeStartPicker = new MyTimePicker("dayTimeStartPicker", startModel);
        timeEndPicker = new MyTimePicker("dayTimeEndPicker", endModel);

        add(timeStartPicker);
        add(timeEndPicker);
    }

    @Override
    protected boolean isAjaxCheckboxCheckedInitially() {
        boolean startConstraint = null != dataSrcCfg().getDayTimeBoundsStartDate();
        boolean endConstraint = null != dataSrcCfg().getDayTimeBoundsEndDate();
        return startConstraint || endConstraint;
    }

    @Override
    protected List<Component> ajaxCheckboxChanged(boolean enabled) {
        timeStartPicker.setEnabled(enabled);
        timeEndPicker.setEnabled(enabled);

        if (enabled) {
            info("Tageszeitbegrenzung aktiv.");
        } else {
            info("Tageszeitbegrenzung aus.");
        }

        List<Component> needAsyncUpdate = new ArrayList<>();
        needAsyncUpdate.add(timeStartPicker);
        needAsyncUpdate.add(timeEndPicker);
        return needAsyncUpdate;
    }

    @Override
    protected void applyEndValueToConfig() {
        Date date = timeEndPicker.getModelObject();
        dataSrcCfg().setDayTimeBoundsEnd(date);
    }

    @Override
    protected void applyStartValueToConfig() {
        Date date = timeStartPicker.getModelObject();
        dataSrcCfg().setDayTimeBoundsStart(date);
    }

    @Override
    protected void unsetStartValueInConfig() {
        dataSrcCfg().setDayTimeBoundsStart(null);
    }

    @Override
    protected void unsetEndValueInConfig() {
        dataSrcCfg().setDayTimeBoundsEnd(null);
    }

    @Override
    protected List<Component> ajaxButtonWasClicked() {
        List<Component> needAsyncUpdate = new ArrayList<>();
        Date start = timeStartPicker.getModelObject();
        Date oldStart = dataSrcCfg().getDayTimeBoundsStartDate();

        Date end = timeEndPicker.getModelObject();
        Date oldEnd = dataSrcCfg().getDayTimeBoundsEndDate();

        String error = validateValue(start, end);

        if (error != null) {
            error(error);
            timeStartPicker.setModelObject(oldStart);
            timeEndPicker.setModelObject(oldEnd);
            needAsyncUpdate.add(timeStartPicker);
            needAsyncUpdate.add(timeEndPicker);

        } else {
            StringBuilder msg = new StringBuilder("Tageszeitbegrenzung - ");
            final int len = msg.length();

            if (dayTimesAreDifferent(start, oldStart)) {
                msg.append("Beginn: ");
                msg.append(start == null ? "-" : DateUtils.formatDayTime(start));
                msg.append(" ");

                applyStartValueToConfig();
            }

            if (dayTimesAreDifferent(end, oldEnd)) {
                if (msg.length() > len) {
                    msg.append(", ");
                }
                msg.append("Ende: ");
                msg.append(end == null ? "-" : DateUtils.formatDayTime(end));

                applyEndValueToConfig();
            }

            if (msg.length() > len) {
                info(msg.toString());
            }
        }
        return needAsyncUpdate;
    }



    // helper methods



    private static boolean dayTimesAreDifferent(Date start, Date oldStart) {
        log.debug("old date: " + start);
        log.debug("new date: " + oldStart);

        boolean different;
        if (start == null || oldStart == null) {
            different = start != oldStart;
        } else {
            Date newDate = DateUtils.normalizeDayTimeDate(start);
            Date oldDate = DateUtils.normalizeDayTimeDate(oldStart);
            different = newDate.getTime() != oldDate.getTime();
        }
        log.debug("comparinson: " + (different ? "different" : "equal"));
        return different;
    }



    private String validateValue(Date start, Date end) {
        if (start == null || end == null) {
            return null;
        }

        start = DateUtils.normalizeDayTimeDate(start);
        end = DateUtils.normalizeDayTimeDate(end);

        if (start.after(end)) {
            return "Das Ende darf nicht vor dem Start liegen.";
        }
        return null;
    }
}
