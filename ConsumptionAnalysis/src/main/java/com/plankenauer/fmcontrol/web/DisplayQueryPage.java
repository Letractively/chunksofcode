package com.plankenauer.fmcontrol.web;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.plankenauer.fmcontrol.config.Config;
import com.plankenauer.fmcontrol.config.ConfigRepository.ParseResultHolder;
import com.plankenauer.fmcontrol.config.UiSettings;
import com.plankenauer.fmcontrol.util.StringUtils;

public class DisplayQueryPage extends BasePage implements Constants
{

    private static final long serialVersionUID = - 6532890623799310807L;

    protected static final Logger log = Logger.getLogger(DisplayQueryPage.class);

    final Config config;
    final JQueryFeedbackPanel feedback;
    final Label querySqlDebugLabel;


    public DisplayQueryPage(PageParameters parameters) {
        super(parameters);
        log.debug("url parameters: " + parameters.toString());

        // we need this to get the jqueryfeedbackpanel thing working
        getSession().bind();

        String project = parameters.get(URL_PARAM_PROJECT).toString();
        String configFileName = parameters.get(URL_PARAM_CONFIG).toString();
        ParseResultHolder h = getConfigRepo().parseConfigHolder(project, configFileName);
        config = h.getConfig();
        log.debug("conf from params: " + config.getName());

        this.add(new Label("pageTitle", substitute("page.title", config)));
        this.add(new Label("queryName", substitute("query.name", config)));
        this.add(new Label("queryTitle", substitute("query.title", config)));
        this.add(new Label("queryDescription", substitute("query.description", config)));

        querySqlDebugLabel = createSqlDebugLabel();
        this.add(querySqlDebugLabel);


        feedback = new JQueryFeedbackPanel("feedback");
        feedback.setMaxMessages(3);
        feedback.setOutputMarkupId(true);
        this.add(feedback);


        BookmarkablePageLink<Object> link = createReloadLink(project, configFileName);
        add(link);


        BookmarkablePageLink<Object> link2 = createTopLink();
        add(link2);


        final Form<Date> form = new Form<Date>("form");
        form.setOutputMarkupId(true);
        add(form);

        Component picker = setupDateBoundsPicker();
        form.add(picker);

        Component dayTimePicker = setupDayTimeBoundsPicker();
        form.add(dayTimePicker);

        Component tablePicker = setupTablePicker();
        form.add(tablePicker);

        Component columnPicker = setupColumnPicker();
        form.add(columnPicker);

        Component groupByPicker = setupGroupbyPicker();
        form.add(groupByPicker);
    }


    private BookmarkablePageLink<Object> createTopLink() {
        BookmarkablePageLink<Object> link2 = new BookmarkablePageLink<>("topLink",
                                                                        HomePage.class);
        link2.add(new Label("topLinkName", "Zur Übersicht"));
        return link2;
    }


    private BookmarkablePageLink<Object> createReloadLink(String project,
                                                          String configFileName) {
        PageParameters p = new PageParameters();
        p.add(URL_PARAM_PROJECT, project);
        p.add(URL_PARAM_CONFIG, configFileName);
        BookmarkablePageLink<Object> link = new BookmarkablePageLink<>("reloadLink",
                                                                       DisplayQueryPage.class,
                                                                       p);
        link.add(new Label("reloadLinkName", "Konfiguration neu laden"));
        return link;
    }


    private Label createSqlDebugLabel() {
        Label l = null;

        UiSettings settings = config.getUiSettings();
        if (settings.isShowSqlDebug()) {
            l = new Label("querySql", Model.of("")) {
                private static final long serialVersionUID = - 1149693350235310645L;

                @Override
                protected void onBeforeRender() {
                    super.onBeforeRender();
                    String createSqlQuery = config.createSqlQuery();
                    setDefaultModelObject(createSqlQuery);
                }
            };
        } else {
            l = new Label("querySql", "foo");
            l.setVisible(false);
        }
        l.setOutputMarkupId(true);
        return l;
    }

    final String getDayTimeInfoString() {
        StringBuilder bui = new StringBuilder();
        bui.append("Tageszeit: ");
        Calendar start = config.getDatasource().getDayTimeBoundsStart();
        Calendar end = config.getDatasource().getDayTimeBoundsStart();

        if (start == null && end == null) {
            bui.append("Alles");
        } else {
            if (start == null) {
                bui.append("00:00:00");
            } else {
                bui.append(StringUtils.formatDayTime(start.getTime()));
            }
            bui.append(" bis ");
            if (end == null) {
                bui.append("23:59:59");
            } else {
                bui.append(StringUtils.formatDayTime(end.getTime()));
            }
        }
        return bui.toString();
    }

    final String getDateInfoString() {
        StringBuilder bui = new StringBuilder();
        bui.append("Zeitraum: ");
        Calendar start = config.getDatasource().getDateBoundsStart();
        Calendar end = config.getDatasource().getDateBoundsEnd();

        if (start == null && end == null) {
            bui.append("Alles");
        } else {
            if (start == null) {
                bui.append("Beginn der Aufzeichnungen");
            } else {
                bui.append(StringUtils.formatDate(start.getTime()));
            }
            bui.append(" bis ");
            if (end == null) {
                bui.append("Ende der Aufzeichnungen");
            } else {
                bui.append(StringUtils.formatDate(end.getTime()));
            }
        }
        return bui.toString();
    }

    private Component setupDateBoundsPicker() {
        if (! config.getUiSettings().isDateBoundsChangeable()) {
            // only show a info string containing the date bounds
            Label label = new Label("dateBoundsPicker", getDateInfoString());
            label.setOutputMarkupId(true);
            return label;
        }

        BoundsPickerPanel picker = new DateBoundsPickerPanel("dateBoundsPicker", this);
        return picker;
    }

    private Component setupDayTimeBoundsPicker() {
        if (! config.getUiSettings().isDaytimeBoundsChangeable()) {
            // only show a info string containing the daytime bounds
            Label label = new Label("dayTimeBoundsPicker", getDayTimeInfoString());
            label.setOutputMarkupId(true);
            return label;
        }

        BoundsPickerPanel picker = new DayTimeBoundsPickerPanel("dayTimeBoundsPicker",
                                                                this);
        return picker;
    }

    private Component setupTablePicker() {
        if (! config.getUiSettings().isTableSelectionChangeable()
                || config.getDatasource().getTables().size() <= 1) {
            Label label = new Label("tablePicker");
            label.setOutputMarkupId(true);
            label.setVisible(false);
            return label;
        }

        TablePickerPanel picker = new TablePickerPanel("tablePicker", this);
        return picker;
    }

    private Component setupColumnPicker() {
        if (! config.getUiSettings().isColumnsSelectionChangeable()
                || config.getDatasource().getTables().get(0).getValueColumnExpr().size() <= 1) {
            Label label = new Label("columnPicker");
            label.setOutputMarkupId(true);
            label.setVisible(false);
            return label;
        }

        ColumnPickerPanel picker = new ColumnPickerPanel("columnPicker", this);
        return picker;
    }

    private Component setupGroupbyPicker() {
        if (! config.getUiSettings().isGroupbyChangeable()) {
            Label label = new Label("groupbyPicker");
            label.setVisible(false);
            label.setOutputMarkupId(true);
            return label;
        }

        GroupbyPickerPanel picker = new GroupbyPickerPanel("groupbyPicker", this);
        return picker;
    }
}
