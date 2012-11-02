package com.plankenauer.fmcontrol.web;



import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.kendo.button.AjaxButton;
import com.plankenauer.fmcontrol.config.DataSelectionConfig;

@SuppressWarnings("serial")
public abstract class BoundsPickerPanel extends Panel
{
    private static Logger log = Logger.getLogger(BoundsPickerPanel.class);



    protected final DisplayQueryPage queryPage;

    private AjaxCheckBox setupEnabledCheckbox;
    private AjaxButton applyButton;

    public BoundsPickerPanel(String id, DisplayQueryPage queryPage2) {
        super(id);
        queryPage = queryPage2;
        initComponents();

        applyButton = setupAjaxButton();
        add(applyButton);

        setupEnabledCheckbox = setupAjaxCheckbox();
        add(setupEnabledCheckbox);
    }


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
                applyStartValue();
                applyEndValue();

                addItemsToUpdate(target);
                target.add(queryPage.querySqlDebugLabel);
                target.add(queryPage.feedback);
                target.add(this);
                valuesHaveChanged(target);
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
        boolean isConstrained = isConstrainedByConfig();
        setComponentsEnabledImpl(isConstrained);

        AjaxCheckBox box = new AjaxCheckBox(getCheckboxId(),
                                            Model.of(Boolean.valueOf(isConstrained))) {
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
                    applyEndValue();
                    applyStartValue();
                    info("Zeitraum: " + queryPage.getDateInfoString());
                } else {
                    unsetStartValue();
                    unsetEndValue();
                    info("Zeitraumbegrenzung deaktiviert.");
                }
                setComponentsEnabledImpl(enabledFlag);
                addItemsToUpdate(target);

                target.add(queryPage.querySqlDebugLabel);
                target.add(queryPage.feedback);
                target.add(applyButton);
            }
        };
        return (box);
    }

    private void setComponentsEnabledImpl(boolean enabled) {
        setComponentsEnabled(enabled);
        applyButton.setEnabled(enabled);
    }

    protected abstract void initComponents();

    protected abstract String getCheckboxId();

    protected abstract boolean isConstrainedByConfig();

    protected abstract void setComponentsEnabled(boolean enabled);

    protected abstract void applyEndValue();

    protected abstract void applyStartValue();

    protected abstract void unsetStartValue();

    protected abstract void unsetEndValue();

    protected abstract void addItemsToUpdate(AjaxRequestTarget target);

    protected abstract void valuesHaveChanged(AjaxRequestTarget target);


    protected Calendar cfgEnd() {
        return dataSrcCfg().getDateBoundsEnd();
    }

    protected Calendar cfgStart() {
        return dataSrcCfg().getDateBoundsStart();
    }

    protected DataSelectionConfig dataSrcCfg() {
        return queryPage.config.getDatasource();
    }
}
