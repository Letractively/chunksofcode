package com.plankenauer.fmcontrol.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.plankenauer.fmcontrol.config.ConfigRepository.ParseResultHolder;

@SuppressWarnings("serial")
public class HomePage extends BasePage implements Constants
{
    
    private static Logger log = Logger.getLogger(HomePage.class);

    public HomePage(final PageParameters parameters) {
        super(parameters);
        log.debug("ENTERING constructor");

        List<ParseResultHolder> everything = getConfigRepo().parseEverything();

        add(new ListView<ParseResultHolder>("queryList", everything) {

            @Override
            protected void populateItem(ListItem<ParseResultHolder> item) {
                ParseResultHolder object = item.getModel().getObject();
                item.add(new Label("queryProject", object.getProject()));
                Label fileNameLabel = new Label("queryName", object.getFileName());
                item.add(fileNameLabel);

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
                
                fileNameLabel.setVisible(erroneous);
                item.add(new Label("queryErrors", label).setVisible(erroneous));

                
                
                PageParameters p = new PageParameters();
                p.add(URL_PARAM_PROJECT, object.getProject());
                p.add(URL_PARAM_CONFIG, object.getFileName());

                BookmarkablePageLink<Object> link = new BookmarkablePageLink<>("queryLink",
                                                                               DisplayQueryPage.class,
                                                                               p);
                link.add(new Label("queryLinkName", object.getFileName()));
                link.setVisible(! erroneous);
                item.add(link);
            }
        });

        log.debug("EXITING constructor");
    }
}
