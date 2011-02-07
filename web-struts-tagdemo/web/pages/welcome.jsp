<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>

<html:html xhtml="true">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>struts showcase project</title>
        <html:base/>
    </head>
    <body>
        <h1>Welcome to Struts Simple Showcases</h1>
        <p>
            andres struts showcase project<br/>
            choose a showcase:
        </p>
        <ul>
            <html:link action="/prepareMultibox" >
                &lt;html:multibox&gt; - tag (Checkboxes)
            </html:link>
        </ul>
        <ul>
            <html:link action="/prepareCombobox" >
                &lt;html:select&gt; - tag (Combobox)
            </html:link>
        </ul>
    </body>
</html:html>
