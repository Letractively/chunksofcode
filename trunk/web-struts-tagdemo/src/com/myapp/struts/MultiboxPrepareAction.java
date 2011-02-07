package com.myapp.struts;

import javax.servlet.http.*;
import org.apache.struts.action.*;

/**
 * @author andre
 */
public class MultiboxPrepareAction extends Action {

    private static final String[] ALL_PERSONS =
                         {"Fritz", "Franz", "Josef", "Heinz", "Jochen"};

    public MultiboxPrepareAction() {super();}

    @Override
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest req,
                                 HttpServletResponse rsp) throws Exception {

        // add all persons to the request
        req.setAttribute("allPersons", ALL_PERSONS);

        // set intitially selected persons to form
        String[] defaultPersons = {"Fritz", "Heinz"};
        MultiboxForm mbForm = (MultiboxForm) form;
        mbForm.setPersons(defaultPersons);

        return mapping.findForward("success"); 
            // --> to /pages/multibox/multibox.jsp
    }
}
