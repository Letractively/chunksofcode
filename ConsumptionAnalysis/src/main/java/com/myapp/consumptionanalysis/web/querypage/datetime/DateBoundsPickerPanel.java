package com.myapp.consumptionanalysis.web.querypage.datetime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.Options;
import com.googlecode.wicket.jquery.ui.kendo.datetime.DatePicker;
import com.myapp.consumptionanalysis.util.StringUtils;
import com.myapp.consumptionanalysis.web.querypage.DisplayQueryPage;

@SuppressWarnings("serial")
public class DateBoundsPickerPanel extends BoundsPickerPanel
{

    private static Logger log = Logger.getLogger(DateBoundsPickerPanel.class);

    private static final Options opts;
    static {
        Options o = new Options();
        o.set("format", Options.asString(StringUtils.YYYY_MM_DD));
        o.set("culture", "'de-DE'");
        opts = o;
    }


    private static class MyDatePicker extends DatePicker
    {

        public MyDatePicker(String id, IModel<Date> model) {
            super(id, model, StringUtils.YYYY_MM_DD, opts);
            setOutputMarkupId(true);
        }
    }



    private MyDatePicker dateStartPicker;
    private MyDatePicker dateEndPicker;


    public DateBoundsPickerPanel(String id, DisplayQueryPage queryPage) {
        super(id, queryPage, "dateBoundsEnabled");

    }

    @Override
    protected void initCustomComponents() {
        Model<Date> startModel = createDateBoundsModel(cfgStart());
        Model<Date> endModel = createDateBoundsModel(cfgEnd());

        dateStartPicker = new MyDatePicker("dateStartPicker", startModel);
        dateEndPicker = new MyDatePicker("dateEndPicker", endModel);

        add(dateStartPicker);
        add(dateEndPicker);
    }

    @Override
    protected List<Component> ajaxButtonWasClicked() {
        List<Component> needAsyncUpdate = new ArrayList<>();
        Date start = dateStartPicker.getModelObject();
        Date oldStart = asDate(cfgStart());

        Date end = dateEndPicker.getModelObject();
        Date oldEnd = asDate(cfgEnd());

        String error = DateBoundsPickerPanel.valid(start, end);

        if (error != null) {
            error(error);
            dateStartPicker.setModelObject(oldStart);
            dateEndPicker.setModelObject(oldEnd);
            needAsyncUpdate.add(dateStartPicker);
            needAsyncUpdate.add(dateEndPicker);

        } else {
            StringBuilder msg = new StringBuilder("Zeitraumbegrenzung - ");
            final int len = msg.length();

            if (dateBoundValuesDifferent(start, oldStart)) {
                msg.append("Beginn: ");
                msg.append(start == null ? "-" : StringUtils.formatDate(start));
                msg.append(" ");

                applyStartValueToConfig();
            }

            if (dateBoundValuesDifferent(end, oldEnd)) {
                if (msg.length() > len) {
                    msg.append(", ");
                }
                msg.append("Ende: ");
                msg.append(end == null ? "keins" : StringUtils.formatDate(end));

                applyEndValueToConfig();
            }

            if (msg.length() > len) {
                info(msg.toString());
            }
        }
        return needAsyncUpdate;
    }
    
    
    @Override
    protected boolean isAjaxCheckboxCheckedInitially() {
        boolean startConstraint = null != dataSrcCfg().getDateBoundsStart();
        boolean endConstraint = null != dataSrcCfg().getDateBoundsEnd();
        return startConstraint || endConstraint;
    }

    @Override
    protected List<Component> ajaxCheckboxChanged(boolean enabled) {
        dateStartPicker.setEnabled(enabled);
        dateEndPicker.setEnabled(enabled);
        
        if (enabled) {
            info("Zeitraumbegrenzung aktiv.");
        } else {
            info("Zeitraumbegrenzung aus.");
        }
        
        List<Component> needAsyncUpdate = new ArrayList<>();
        needAsyncUpdate.add(dateStartPicker);
        needAsyncUpdate.add(dateEndPicker);
        return needAsyncUpdate;
    }

    @Override
    protected void applyEndValueToConfig() {
        Date modelObject = dateEndPicker.getModelObject();
        dataSrcCfg().setDateBoundsEnd(modelObject);
    }

    @Override
    protected void applyStartValueToConfig() {
        Date modelObject = dateStartPicker.getModelObject();
        dataSrcCfg().setDateBoundsStart(modelObject);
    }

    @Override
    protected void unsetEndValueInConfig() {
        dataSrcCfg().setDateBoundsEnd((Date) null);
    }

    @Override
    protected void unsetStartValueInConfig() {
        dataSrcCfg().setDateBoundsStart((Date) null);
    }



    // helper methods



    static final boolean dateBoundValuesDifferent(Date oldval, Date newval) {
        log.debug("old date: " + oldval);
        log.debug("new date: " + newval);

        boolean different;
        if (oldval == null || newval == null) {
            different = newval != oldval;
        } else {
            Date newDate = normalizeDateBound(newval);
            Date oldDate = normalizeDateBound(oldval);
            different = newDate.getTime() != oldDate.getTime();
        }
        log.debug("comparinson: " + (different ? "different" : "equal"));
        return different;
    }

    private static Date normalizeDateBound(Date value) {
        Calendar newCal = Calendar.getInstance();
        newCal.setTime(value);

        int[] valsToZeroOut = { Calendar.MINUTE, Calendar.MILLISECOND,
                Calendar.HOUR_OF_DAY, Calendar.SECOND };

        for (int i : valsToZeroOut) {
            newCal.set(i, 0);
        }
        return newCal.getTime();
    }


    private Calendar cfgEnd() {
        return dataSrcCfg().getDateBoundsEnd();
    }

    private Calendar cfgStart() {
        return dataSrcCfg().getDateBoundsStart();
    }

    private Model<Date> createDateBoundsModel(Calendar cal) {
        if (cal == null) {
            return Model.of((Date) null);
        }

        return Model.of(cal.getTime());
    }

    private static String valid(Date start, Date end) {
        String result = null;
        if (start != null && end != null && ! start.before(end)) {
            log.debug("start    = " + start);
            log.debug("end      = " + end);
            log.debug("result   = " + result);
            result = "Das Startdatum muss vor dem Enddatum sein!";
        }
        return result;
    }
}
