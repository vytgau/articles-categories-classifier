package classifier.crawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContentSpider {

    private final int CONNECTION_TIMEOUT = 60_000;  // miliseconds
    private List<String> links;
    private List<String> content;

    public ContentSpider(List<String> links) {
        this.links = links;
        this.content = new ArrayList<>();
    }

    public List<String> crawl() throws IOException {
        for (String url : links) {
            try {
                Document doc = Jsoup.connect(url).timeout(CONNECTION_TIMEOUT).get();
                Elements articleText = doc.select("[itemprop=\"articleBody\"]").select("p");
                content.add(articleText.text());
                System.out.println(url);
            } catch (Exception e) {
                System.out.println("Exception occured");
            }
        }
        return content;
    }

}
