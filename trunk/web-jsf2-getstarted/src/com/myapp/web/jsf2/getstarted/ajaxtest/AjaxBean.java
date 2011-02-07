package com.myapp.web.jsf2.getstarted.ajaxtest;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.validation.constraints.Size;

@ManagedBean
@SessionScoped
public class AjaxBean implements Serializable {

    private static final long serialVersionUID = 8793542285999432966L;

    @Size(min=2, max=12)
    private String firstName;

    @Size(min=2, max=12)
    private String lastName;

    public String getFirstName() {
        System.out.println("AjaxBean.getFirstName()");
        return firstName;
    }

    public String getLastName() {
        System.out.println("AjaxBean.getLastName()");
        return lastName;
    }

    public void setFirstName(String firstName) {
        System.out.println("AjaxBean.setFirstName()");
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        System.out.println("AjaxBean.setLastName()");
        this.lastName = lastName;
    }

    public Date getServerTime() {
        System.out.println("AjaxBean.getServerTime()");
        return new Date();
    }


}
