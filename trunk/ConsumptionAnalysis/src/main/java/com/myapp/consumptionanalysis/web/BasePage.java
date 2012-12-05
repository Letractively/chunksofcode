package com.myapp.consumptionanalysis.web;



import java.io.Serializable;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.myapp.consumptionanalysis.config.ConfigRepository;

@SuppressWarnings("serial")
public class BasePage extends WebPage
{

    public BasePage() {
        super();
    }

    public BasePage(IModel<?> model) {
        super(model);
    }

    public BasePage(PageParameters parameters) {
        super(parameters);
    }

    protected ConfigRepository getConfigRepo() {
        WicketApplication wicketApp = (WicketApplication) getApplication();
        ConfigRepository repo = wicketApp.getGlobalConfigRepo();
        return repo;
    }

    protected <T extends Serializable> StringResourceModel substitute(String key, T c) {
        StringResourceModel m = new StringResourceModel(key, this, new Model<T>(c));
        return m;
    }

}
