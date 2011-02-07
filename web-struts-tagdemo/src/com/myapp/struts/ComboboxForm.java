package com.myapp.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author andre
 */
@SuppressWarnings("serial")
public class ComboboxForm extends ActionForm {
    private static final String[] EMPTY = {};

    private String car = "per pedes";
    private String[] girls = null;

    public ComboboxForm() {super();}

    public void setCar(String car)   {this.car = car;}
    public void setGirls(String[] g) {girls = g;}

    public String getCar()           {return car;}
    public String[] getGirls()       {return girls;}

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        car = "per pedes";
        girls = EMPTY;
    }

    public static class Car {
        private int price;
        private String name;
        public Car(String name, int price) {
            this.price = price;
            this.name = name;
        }
        public String getName()            {return name;}
        public int    getPrice()           {return price;}
        public void   setName(String name) {this.name = name;}
        public void   setPrice(int price)  {this.price = price;}
        @Override
        public String toString()           {return name+" (EUR "+price+")";}
    }
}
