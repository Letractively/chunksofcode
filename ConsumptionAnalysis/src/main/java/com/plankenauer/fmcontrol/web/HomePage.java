package com.plankenauer.fmcontrol.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.plankenauer.fmcontrol.config.ConfigRepository;
import com.plankenauer.fmcontrol.config.ConfigRepository.ParseResultHolder;

@SuppressWarnings("serial")
public class HomePage extends WebPage
{

    private static Logger log = Logger.getLogger(HomePage.class);
    
    public HomePage(final PageParameters parameters) {
        super(parameters);
        log.debug("ENTERING constructor");
        
        WicketApplication wicketApp = (WicketApplication) getApplication();
        ConfigRepository repo = wicketApp.getGlobalConfigRepo();
        List<ParseResultHolder> everything = repo.parseEverything();

        add(new PropertyListView<ParseResultHolder>("queryList", everything) {

            @Override
            protected void populateItem(ListItem<ParseResultHolder> item) {
                ParseResultHolder object = item.getModel().getObject();
                item.add(new Label("queryProject", object.getProject()));
                item.add(new Label("queryName", object.getFileName()));
                
                String label;
                
                if (object.getConfig() != null) {
                    label = object.getConfig().getTitle();
                } else {
                    label = "Error!";
                }
                
                item.add(new Label("queryStatus", label));

                label = "foo";
                boolean erroneous = object.getError() != null;
                if (erroneous) {
                    label = object.getError().getErrorString();
                }
                item.add(new Label("queryErrors", label).setVisible(erroneous));
            }
        });



        add(new Label("version", getApplication().getFrameworkSettings().getVersion()));

        // TODO Add your page's components here

        log.debug("EXITING constructor");
    }
}
