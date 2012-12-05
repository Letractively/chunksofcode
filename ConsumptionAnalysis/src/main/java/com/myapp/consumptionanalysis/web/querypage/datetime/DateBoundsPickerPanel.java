package com.myapp.consumptionanalysis.web.querypage.datetime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.Options;
import com.googlecode.wicket.jquery.ui.kendo.datetime.DatePicker;
import com.myapp.consumptionanalysis.config.Constants;
import com.myapp.consumptionanalysis.util.DateUtils;
import com.myapp.consumptionanalysis.web.querypage.DisplayQueryPage;

@SuppressWarnings("serial")
public class DateBoundsPickerPanel extends BoundsPickerPanel
{

    private static Logger log = Logger.getLogger(DateBoundsPickerPanel.class);

    private static final Options opts;
    static {
        Options o = new Options();
        o.set("format", Options.asString(Constants.YYYY_MM_DD));
        o.set("culture", "'de-DE'");
        opts = o;
    }


    private static class MyDatePicker extends DatePicker
    {

        public MyDatePicker(String id, IModel<Date> model) {
            super(id, model, Constants.YYYY_MM_DD, opts);
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
        Date oldStart = cfgStart();

        Date end = dateEndPicker.getModelObject();
        Date oldEnd = cfgEnd();

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
                msg.append(start == null ? "-" : DateUtils.formatDate(start));
                msg.append(" ");

                applyStartValueToConfig();
            }

            if (dateBoundValuesDifferent(end, oldEnd)) {
                if (msg.length() > len) {
                    msg.append(", ");
                }
                msg.append("Ende: ");
                msg.append(end == null ? "keins" : DateUtils.formatDate(end));

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
        boolean startConstraint = null != dataSrcCfg().getDateBoundsStartDate();
        boolean endConstraint = null != dataSrcCfg().getDateBoundsEndDate();
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
            Date newDate = DateUtils.normalizeDateBoundDate(newval);
            Date oldDate = DateUtils.normalizeDateBoundDate(oldval);
            different = newDate.getTime() != oldDate.getTime();
        }
        log.debug("comparinson: " + (different ? "different" : "equal"));
        return different;
    }


    private Date cfgEnd() {
        return dataSrcCfg().getDateBoundsEndDate();
    }

    private Date cfgStart() {
        return dataSrcCfg().getDateBoundsStartDate();
    }

    private Model<Date> createDateBoundsModel(Date cal) {
        return Model.of(cal);
    }

    private static String valid(Date start, Date end) {
        String result = null;
        if (start != null && end != null && start.after(end)) {
            log.debug("start    = " + start);
            log.debug("end      = " + end);
            log.debug("result   = " + result);
            result = "Das Startdatum muss vor dem Enddatum sein!";
        }
        return result;
    }
}
