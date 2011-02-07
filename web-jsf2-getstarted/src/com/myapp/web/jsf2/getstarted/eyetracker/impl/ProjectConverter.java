package com.myapp.web.jsf2.getstarted.eyetracker.impl;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.myapp.web.jsf2.getstarted.eyetracker.Project;
import com.myapp.web.jsf2.getstarted.eyetracker.dbdummy.DataBase;

public class ProjectConverter implements Converter {

    private DataBase dataBase = new DataBase();

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        System.err.println("ProjectConverter.getAsObject() string=" + arg2);
        Object projectInstance = dataBase.get(Project.class, Integer
                .parseInt(arg2));
        System.err.println("return " + projectInstance);
        return dataBase.get(Project.class, Integer.parseInt(arg2));
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        System.err.println("ProjectConverter.getAsString() obj=" + arg2);
        return arg2.toString();
    }
}
