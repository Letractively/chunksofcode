package com.myapp.consumptionanalysis.web.querypage.datetime;



import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.kendo.button.AjaxButton;
import com.myapp.consumptionanalysis.config.DataSelectionConfig;
import com.myapp.consumptionanalysis.web.querypage.DisplayQueryPage;

@SuppressWarnings("serial")
public abstract class BoundsPickerPanel extends Panel
{
    private static Logger log = Logger.getLogger(BoundsPickerPanel.class);

    private final String checkboxId;

    protected final DisplayQueryPage queryPage;

    private AjaxCheckBox setupEnabledCheckbox;
    private AjaxButton applyButton;
    

    public BoundsPickerPanel(String id, DisplayQueryPage queryPage2, String checkboxId) {
        super(id);
        this.checkboxId = checkboxId;
        queryPage = queryPage2;

        applyButton = setupAjaxButton();
        add(applyButton);

        setupEnabledCheckbox = setupAjaxCheckbox();
        add(setupEnabledCheckbox);
        
        initCustomComponents();
        Boolean modelObject = setupEnabledCheckbox.getModelObject();
        ajaxCheckboxChanged(modelObject);
        
        applyButton.setEnabled(modelObject);
    }


    protected abstract void initCustomComponents();


    private AjaxButton setupAjaxButton() {
        AjaxButton jenson = new AjaxButton("applyButton", Model.of("update")) {

            private final AttributeAppender disabler = AttributeModifier.append("class",
                                                                                "k-state-disabled");

            @Override
            protected void onInitialize() {
                super.onInitialize();
                removeDisabledModifier();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                log.debug("ajax button clicked");
                target.add(queryPage.getComponentsToUpdateAfterAjax());

                List<Component> toRefresh = ajaxButtonWasClicked();
                for (Component c : toRefresh) {
                    target.add(c);
                }
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                removeDisabledModifier();

                if (! isEnabled()) {
                    this.add(disabler);
                    log.trace("add modifier: " + disabler.getAttribute());
                }
            }

            private void removeDisabledModifier() {
                for (Object o : getBehaviors(disabler.getClass())) {
                    if (o == disabler) {
                        AttributeModifier am = (AttributeModifier) o;
                        remove(disabler);
                        log.trace("remove modifier: " + am.getAttribute());
                        return;
                    }
                }
            }
        };

        return jenson;
    }

    protected AjaxCheckBox setupAjaxCheckbox() {
        boolean checkedInitially = isAjaxCheckboxCheckedInitially();

        AjaxCheckBox box = new AjaxCheckBox(checkboxId,
                                            Model.of(Boolean.valueOf(checkedInitially))) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                log.debug("box before rendering. value=" + getModelObject());
            }

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                log.debug("box updated. value=" + getModelObject());

                boolean enabledFlag = getModelObject().booleanValue();
                if (enabledFlag) {
                    applyEndValueToConfig();
                    applyStartValueToConfig();
                } else {
                    unsetStartValueInConfig();
                    unsetEndValueInConfig();
                }
                
                applyButton.setEnabled(enabledFlag);
                List<Component> toRefresh = ajaxCheckboxChanged(enabledFlag);
                
                for (Component c : toRefresh) {
                    target.add(c);
                }

                target.add(queryPage.getComponentsToUpdateAfterAjax());
                target.add(applyButton);
            }
        };
        return (box);
    }

    
    protected abstract boolean isAjaxCheckboxCheckedInitially();

    protected abstract List<Component> ajaxButtonWasClicked();
    protected abstract List<Component> ajaxCheckboxChanged(boolean enabled);

    protected abstract void applyEndValueToConfig();
    protected abstract void applyStartValueToConfig();

    protected abstract void unsetStartValueInConfig();
    protected abstract void unsetEndValueInConfig();



    protected DataSelectionConfig dataSrcCfg() {
        return queryPage.getConfig().getSelectionConfig();
    }
}
