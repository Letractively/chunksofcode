package com.plankenauer.fmcontrol.web;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.Options;
import com.plankenauer.fmcontrol.util.StringUtils;

@SuppressWarnings("serial")
public class DateBoundsPickerPanel extends BoundsPickerPanel
{

    private abstract class MyDatePicker extends
                                       com.googlecode.wicket.jquery.ui.kendo.datetime.DatePicker
    {



        public MyDatePicker(String id, IModel<Date> model) {
            super(id,
                  model,
                  StringUtils.YYYY_MM_DD,
                  new Options("format", Options.asString(StringUtils.YYYY_MM_DD)));

            setOutputMarkupId(true);
        }

        // Note: not a wicket method
        public void onValueChanged(AjaxRequestTarget target) {
            Date date = getModelObject();
            String error = validateValue(date);

            if (error != null) {
                error(error);
                Date old = getOldValue();
                setModelObject(old);
                target.add(this);
            } else {
                applyValue(date);
                target.add(queryPage.querySqlDebugLabel);
            }

            target.add(queryPage.feedback);
        }

        protected abstract Date getOldValue();

        protected abstract void applyValue(Date value);

        protected abstract String validateValue(Date value);
    }


    private final class MyStartDatePicker extends MyDatePicker
    {
        private MyStartDatePicker(String id, IModel<Date> model) {
            super(id, model);
        }

        @Override
        protected Date getOldValue() {
            Date old = value(cfgStart());
            return old;
        }

        @Override
        protected void applyValue(Date date) {
            if (date == null) {
                unsetStartValue();
                info("Zeitraumbegrenzung - Startdatum entfernt.");
            } else {
                dataSrcCfg().setDateBoundsStart(date);
                info("Zeitraumbegrenzung - Startdatum gesetzt: "
                        + StringUtils.formatDate(date));
            }
        }

        @Override
        protected String validateValue(Date value) {
            String error = DateBoundsPickerPanel.valid(getModelObject(), cfgEnd());
            return error;
        }
    }


    private final class MyEndDatePicker extends MyDatePicker
    {
        private MyEndDatePicker(String id, IModel<Date> model) {
            super(id, model);
        }

        @Override
        protected Date getOldValue() {
            Date old = value(cfgEnd());
            return old;
        }

        @Override
        protected void applyValue(Date date) {
            if (date == null) {
                dataSrcCfg().setDateBoundsEnd((Long) null);
                info("Zeitraumbegrenzung - Enddatum entfernt.");
            } else {
                dataSrcCfg().setDateBoundsEnd(date);
                info("Zeitraumbegrenzung - Enddatum gesetzt: "
                        + StringUtils.formatDate(date));
            }
        }

        @Override
        protected String validateValue(Date value) {
            String error = DateBoundsPickerPanel.valid(cfgStart(), getModelObject());
            return error;
        }
    }

    private static Logger log = Logger.getLogger(DateBoundsPickerPanel.class);


    private MyDatePicker dateStartPicker;
    private MyDatePicker dateEndPicker;


    public DateBoundsPickerPanel(String id, DisplayQueryPage queryPage) {
        super(id, queryPage);
    }

    protected void initComponents() {
        dateStartPicker = setupStartPicker();
        dateEndPicker = setupEndPicker();

        add(dateStartPicker);
        add(dateEndPicker);
    }

    @Override
    protected void valuesHaveChanged(AjaxRequestTarget target) {
        dateStartPicker.onValueChanged(target);
        dateEndPicker.onValueChanged(target);
    }

    @Override
    protected boolean isConstrainedByConfig() {
        boolean startConstraint = null != dataSrcCfg().getDateBoundsStart();
        boolean endConstraint = null != dataSrcCfg().getDateBoundsEnd();
        return startConstraint || endConstraint;
    }

    @Override
    protected void setComponentsEnabled(boolean enabled) {
        dateStartPicker.setEnabled(enabled);
        dateEndPicker.setEnabled(enabled);
    }

    @Override
    protected void applyEndValue() {
        dataSrcCfg().setDateBoundsEnd(dateEndPicker.getModelObject());
    }

    @Override
    protected void applyStartValue() {
        dataSrcCfg().setDateBoundsStart(dateStartPicker.getModelObject());
    }

    @Override
    protected void unsetEndValue() {
        dataSrcCfg().setDateBoundsEnd((Date) null);
    }

    @Override
    protected void unsetStartValue() {
        dataSrcCfg().setDateBoundsStart((Date) null);
    }

    @Override
    protected void addItemsToUpdate(AjaxRequestTarget target) {
        target.add(dateStartPicker);
        target.add(dateEndPicker);
    }

    @Override
    protected String getCheckboxId() {
        return "dateBoundsEnabled";
    }


    protected MyDatePicker setupStartPicker() {
        Model<Date> startModel = createConfigStartModel();
        MyDatePicker startPicker = new MyStartDatePicker("dateStartPicker", startModel);
        return (startPicker);
    }



    protected MyDatePicker setupEndPicker() {
        Model<Date> endModel = createConfigEndModel();
        MyDatePicker endPicker = new MyEndDatePicker("dateEndPicker", endModel);
        return (endPicker);
    }



    // helper methods



    private Model<Date> createConfigEndModel() {
        Calendar endOfConfig = cfgEnd();

        if (endOfConfig == null) {
            return Model.of((Date) null);
        }

        return Model.of(endOfConfig.getTime());
    }


    private Model<Date> createConfigStartModel() {
        Calendar endOfConfig = cfgStart();

        if (endOfConfig == null) {
            return Model.of((Date) null);
        }

        return Model.of(endOfConfig.getTime());
    }


    private static String valid(Date start, Calendar end) {
        return valid(start, end != null ? end.getTime() : null);
    }

    private static String valid(Calendar start, Date end) {
        return valid(start != null ? start.getTime() : null, end);
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

    private static Date value(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return cal.getTime();
    }
}
