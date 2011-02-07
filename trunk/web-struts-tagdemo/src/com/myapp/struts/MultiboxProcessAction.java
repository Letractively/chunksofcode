package com.myapp.struts;

import java.util.Arrays;
import javax.servlet.http.*;
import org.apache.struts.action.*;

public class MultiboxProcessAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest req,
                                 HttpServletResponse rsp) throws Exception {
        System.out.println("MultiBoxProcessAction executing...");
        System.out.println("selected girls: "+
                Arrays.deepToString(((MultiboxForm)form).getPersons()));
        System.out.println("-----------------------------------");

        // when user clicked on "reset" button in the form
        if(isCancelled(req))
            return mapping.findForward("welcome");

        return mapping.findForward("success");
    }
}
