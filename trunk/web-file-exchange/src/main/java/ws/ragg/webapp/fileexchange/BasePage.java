package ws.ragg.webapp.fileexchange;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.StringResourceModel;

@SuppressWarnings("serial")
abstract class BasePage extends WebPage {
    
    BasePage() {
        StringResourceModel title = new StringResourceModel("page.title", this, null);
        add(new Label("page.title", title));
        
        String wicketVersion = getApplication().getFrameworkSettings().getVersion();
        add(new Label("wicket.version", wicketVersion));
        
        String javaVersion = System.getProperty("java.version");
        add(new Label("java.version", javaVersion));
    }
}
