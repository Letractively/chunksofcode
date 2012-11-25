package com.myapp.consumptionanalysis.web.querypage;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.INamedParameters.NamedPair;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import com.myapp.consumptionanalysis.config.ConfigException;
import com.myapp.consumptionanalysis.config.ConfigRepository.ParseResultHolder;
import com.myapp.consumptionanalysis.web.HomePage;

@SuppressWarnings("serial")
public class ErrorPage extends WebPage
{

    private static final String NL = System.getProperty("line.separator");

    public ErrorPage(ParseResultHolder h, PageParameters paramsErroneous) {
        super(new PageParameters());

        ConfigException error = h.getError();
        String msg = "(keine Fehlermeldung gefunden)";
        String stacktrace = "(keine Fehlerverfolgung gefunden)";
        String configDump = "(kein Abbild der Config gefunden)";
        String configFilePath = "(kein Pfad der Config gefunden)";
        String validationErrors = "(Keine Konfigurations Fehler bekannt.)";
        String pageParemeterDebug = "(Keine HTTP Parameter übergeben.)";

        if (error != null) {
            msg = error.getMessage();
            stacktrace = stacktraceToString(error);

            if (error.getDebugString() != null) {
                configDump = error.getDebugString();
            }
            if (error.getConfigFilePath() != null) {
                configFilePath = error.getConfigFilePath();
            }
            String estr = error.getErrorString();
            if (estr != null) {
                validationErrors = estr;
            }
        }

        if (paramsErroneous != null) {
            StringBuilder bui = new StringBuilder();
            int indexedCount = paramsErroneous.getIndexedCount();
            for (int i = 0; i < indexedCount; i++) {
                bui.append(i);
                bui.append(" - ");
                StringValue obj = paramsErroneous.get(i);
                bui.append(obj.toOptionalString());
                bui.append(NL);
            }
            bui.append(NL);

            List<NamedPair> allNamed = paramsErroneous.getAllNamed();
            for (NamedPair namedPair : allNamed) {
                bui.append(namedPair.getKey());
                bui.append(" - ");
                bui.append(namedPair.getValue());
            }
            pageParemeterDebug = bui.toString();
            bui.append(NL);
        }

        add(new Label("windowTitle", "Fehler"));
        add(new Label("pageTitle", "Ein Fehler ist aufgetreten..."));


        add(new Label("errorMessageTitle", "Fehlermeldung"));
        add(new Label("errorMessage", msg));
        add(new Label("configFilePath", configFilePath));


        add(new Label("validationErrorsTitle", "Config Warnungen"));
        add(new Label("validationErrors", validationErrors));


        add(new Label("configDumpTitle", "Config Debug Info"));
        add(new Label("configDump", configDump));


        add(new Label("stackTraceTitle", "Fehlerverfolgung"));
        add(new Label("stackTrace", stacktrace));


        add(new Label("pageParametersTitle", "Übergebene HTTP Parameter"));
        add(new Label("pageParameters", pageParemeterDebug));

        BookmarkablePageLink<Object> link2;
        link2 = new BookmarkablePageLink<>("topLink", HomePage.class);
        link2.add(new Label("topLinkName", "Zur Übersicht"));
        add(link2);
    }

    private static String stacktraceToString(Throwable t) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PrintWriter pw = new PrintWriter(baos);
        t.printStackTrace(pw);
        pw.close();

        String string = baos.toString();
        return string;
    }
}
