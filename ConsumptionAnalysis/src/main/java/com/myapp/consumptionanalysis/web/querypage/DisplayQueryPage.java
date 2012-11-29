package com.myapp.consumptionanalysis.web.querypage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.myapp.consumptionanalysis.config.Config;
import com.myapp.consumptionanalysis.config.ConfigRepository.ParseResultHolder;
import com.myapp.consumptionanalysis.config.UiSettings;
import com.myapp.consumptionanalysis.util.DateUtils;
import com.myapp.consumptionanalysis.web.BasePage;
import com.myapp.consumptionanalysis.web.Constants;
import com.myapp.consumptionanalysis.web.HomePage;
import com.myapp.consumptionanalysis.web.chart.test.BarChartPanel;
import com.myapp.consumptionanalysis.web.querypage.datetime.BoundsPickerPanel;
import com.myapp.consumptionanalysis.web.querypage.datetime.ColumnPickerPanel;
import com.myapp.consumptionanalysis.web.querypage.datetime.DateBoundsPickerPanel;
import com.myapp.consumptionanalysis.web.querypage.datetime.DayTimeBoundsPickerPanel;
import com.myapp.consumptionanalysis.web.querypage.datetime.GroupByPickerPanel;
import com.myapp.consumptionanalysis.web.querypage.datetime.TablePickerPanel;
import com.myapp.consumptionanalysis.web.querypage.emptyplaceholders.ColumnInfoPanel;
import com.myapp.consumptionanalysis.web.querypage.emptyplaceholders.DateBoundsInfoPanel;
import com.myapp.consumptionanalysis.web.querypage.emptyplaceholders.DayTimeBoundsInfoPanel;
import com.myapp.consumptionanalysis.web.querypage.emptyplaceholders.GroupByInfoPanel;
import com.myapp.consumptionanalysis.web.querypage.emptyplaceholders.TableInfoPanel;

@SuppressWarnings("serial")
public class DisplayQueryPage extends BasePage implements Constants
{

    protected static final Logger log = Logger.getLogger(DisplayQueryPage.class);

    private final Config config;
    private final JQueryFeedbackPanel feedback;
    private final Label querySqlDebugLabel;
    private final BarChartPanel chartPanel;

    public Config getConfig() {
        return config;
    }

    public Component[] getComponentsToUpdateAfterAjax() {
        List<Component> components = new ArrayList<>();
        if (config.getUiSettings().isShowSqlDebug()) {
            components.add(querySqlDebugLabel);
        }
        components.add(feedback);
        components.add(chartPanel.getComponentToUpdateAfterAjax());
        Component[] array = components.toArray(new Component[components.size()]);
        return array;
    }

    public DisplayQueryPage(PageParameters parameters) {
        super(parameters);
        log.debug("url parameters: " + parameters.toString());

        // we need this to get the jqueryfeedbackpanel thing working
        getSession().bind();
        String project = parameters.get(URL_PARAM_PROJECT).toString();
        String cfgFileName = parameters.get(URL_PARAM_CONFIG).toString();
        ParseResultHolder h = getConfigRepo().parseConfigHolder(project, cfgFileName);

        config = h.getConfig();

        if (config == null) {
            querySqlDebugLabel = null;
            feedback = null;
            chartPanel = null;
            setResponsePage(new ErrorPage(h, parameters));
            return;
        }

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


        BookmarkablePageLink<Object> reloadLink = createReloadLink(project, cfgFileName);
        add(reloadLink);

        BookmarkablePageLink<Object> topLink = createTopLink();
        add(topLink);

        Form<Date> form = new Form<Date>("form");
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

        this.chartPanel = setupChartPanel();
        add(chartPanel);
    }

    private BarChartPanel setupChartPanel() {
        BarChartPanel bcp = new BarChartPanel("chartContainer", config);
        return bcp;
    }

    private BookmarkablePageLink<Object> createTopLink() {
        BookmarkablePageLink<Object> link2 = new BookmarkablePageLink<>("topLink",
                                                                        HomePage.class);
        link2.add(new Label("topLinkName", "Zur Übersicht"));
        return link2;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
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


    final String getDateInfoString() {
        StringBuilder bui = new StringBuilder();
        bui.append("Zeitraum: ");
        Date start = config.getSelectionConfig().getDateBoundsStartDate();
        Date end = config.getSelectionConfig().getDateBoundsEndDate();

        if (start == null && end == null) {
            bui.append("Alles");
        } else {
            if (start == null) {
                bui.append("Beginn der Aufzeichnungen");
            } else {
                bui.append(DateUtils.formatDate(start));
            }
            bui.append(" bis ");
            if (end == null) {
                bui.append("Ende der Aufzeichnungen");
            } else {
                bui.append(DateUtils.formatDate(end));
            }
        }
        return bui.toString();
    }

    private Component setupDateBoundsPicker() {
        if (! config.getUiSettings().isDateBoundsChangeable()) {
            DateBoundsInfoPanel dbip = new DateBoundsInfoPanel("dateBoundsPicker", this);
            return dbip;
        }

        BoundsPickerPanel picker = new DateBoundsPickerPanel("dateBoundsPicker", this);
        return picker;
    }

    private Component setupDayTimeBoundsPicker() {
        if (! config.getUiSettings().isDaytimeBoundsChangeable()) {
            DayTimeBoundsInfoPanel dtbip;
            dtbip = new DayTimeBoundsInfoPanel("dayTimeBoundsPicker", this);
            return dtbip;
        }

        BoundsPickerPanel p = new DayTimeBoundsPickerPanel("dayTimeBoundsPicker", this);
        return p;
    }

    private Component setupTablePicker() {
        if (! config.getUiSettings().isTableSelectionChangeable()
                || config.getSelectionConfig().getTables().size() <= 1) {
            TableInfoPanel tip = new TableInfoPanel("tablePicker", this);
            return tip;
        }

        TablePickerPanel picker = new TablePickerPanel("tablePicker", this);
        return picker;
    }

    private Component setupColumnPicker() {
        if (! config.getUiSettings().isColumnsSelectionChangeable()
                || config.getSelectionConfig()
                         .getTables()
                         .get(0)
                         .getValueColumnExpr()
                         .size() <= 1) {
            ColumnInfoPanel p = new ColumnInfoPanel("columnPicker", this);
            p.setOutputMarkupId(true);
            return p;
        }

        ColumnPickerPanel picker = new ColumnPickerPanel("columnPicker", this);
        return picker;
    }

    private Component setupGroupbyPicker() {
        if (! config.getUiSettings().isGroupbyChangeable()) {
            GroupByInfoPanel gbip = new GroupByInfoPanel("groupbyPicker", this);
            return gbip;
        }

        GroupByPickerPanel picker = new GroupByPickerPanel("groupbyPicker", this);
        return picker;
    }
}
