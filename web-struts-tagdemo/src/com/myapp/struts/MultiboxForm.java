package com.myapp.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;

@SuppressWarnings("serial")
public class MultiboxForm extends ActionForm {
    private static final String[] EMPTY = {};
    private String[] persons = EMPTY;

    public MultiboxForm() {super();}

    // invoked when the user clicked on cancel in the form
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        persons = EMPTY;
    }

    public String[] getPersons()        {return persons;}
    public void setPersons(String[] p)  {persons = p;}
}
