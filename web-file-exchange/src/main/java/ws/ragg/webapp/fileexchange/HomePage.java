package ws.ragg.webapp.fileexchange;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

@SuppressWarnings("serial")
public class HomePage extends BasePage {
    
    public HomePage(@SuppressWarnings("unused") PageParameters parameters) {
        add(new BookmarkablePageLink<UploadPage>("upload.link", UploadPage.class));
        add(new BookmarkablePageLink<DownloadPage>("download.link", DownloadPage.class));
    }
}
