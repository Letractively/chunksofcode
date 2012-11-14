package com.myapp.consumptionanalysis.web;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

public final class Util
{
    private Util() {
    }

    public static void addIfVisible(AjaxRequestTarget target, Component component) {
        if (component.isVisible()) {
            target.add(component);
        }
    }
}
