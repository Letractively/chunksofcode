package com.myapp.struts;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author andre
 */
public class ComboboxProcessAction extends Action{

    @Override
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest req,
                                 HttpServletResponse rsp) throws Exception {
        System.out.println("ComboboxProcessAction executing...");
        System.out.println("selected car:   "+ ((ComboboxForm)form).getCar());
        System.out.println("selected girls: "+
                Arrays.deepToString(((ComboboxForm)form).getGirls()));
        System.out.println("-----------------------------------");

        // when user clicked on "reset" button in the form
        if(isCancelled(req))
            return mapping.findForward("welcome");

        return mapping.findForward("success");
    }

}
