package com.myapp.struts;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.myapp.struts.ComboboxForm.Car;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author andre
 */
@SuppressWarnings("serial")
public class ComboboxPrepareAction extends Action{

    public static final Map<String, Car> CAR_MAP =
        Collections.unmodifiableMap(
            new HashMap<String, Car>() {{
                put("auto", new Car("auto", 25000));
                put("moped", new Car("moped", 1000));
                put("lkw", new Car("lkw", 92000));
                put("ferrari", new Car("ferrari", 250000));
    }});

    public static final List<String> GIRLS_LIST =
        Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("Lisa");add("Linda");
                add("Mona");add("Schakkeline");
                add("Petra");add("Hölga");
    }});

    public ComboboxPrepareAction() {super();}

    @Override
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest req,
                                 HttpServletResponse rsp) throws Exception {

        Map<String, Car> carMap = new HashMap<String, Car>(CAR_MAP);
        req.setAttribute("allCars", carMap);

        List<String> girls = new ArrayList<String>(GIRLS_LIST);
        req.setAttribute("allGirls", girls);

        return mapping.findForward("success");
    }

}
