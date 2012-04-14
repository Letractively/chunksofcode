package com.myapp.web.jsf2.getstarted.healthplan;

import javax.faces.bean.*;

@ManagedBean
public class HealthPlanBean {

    public String signup() {
        double d = Math.random();
        System.out.println("HealthPlanBean.signup(" + d + ")");
        
        return d < 0.5 ? "accepted" : "rejected";
    }
}
