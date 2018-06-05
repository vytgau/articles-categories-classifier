package classifier.crawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LinksSpider {

    private final int CONNECTION_TIMEOUT = 60_000;  // miliseconds
    private final int MAX_PAGES = 50; // max number of pages to crawl

    private List<String> links = new ArrayList<>();
    private String fromDate;
    private String toDate;
    private String channel;

    public LinksSpider(Channels channel, String fromDate, String toDate) {
        this.channel = channel.getChannel();
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * finds the last page in the archive
     */
    private int findLastPage(Document doc) {
        Elements pages = doc.select(".paging a");
        String page = "0";

        // get second to last anchor tag text
        if (pages.size() >= 2) {
            page = pages.get(pages.size() - 2).text();
        }

        return Integer.valueOf(page);
    }

    /**
     * finds links to articles
     */
    private List<String> findLinks(Document doc) {
        List<String> links = new ArrayList<>();
        Elements headlineTitles = doc.select(".headline-title");

        headlineTitles.stream()
                .forEach(element -> links.add(element.select("a").first().attributes().get("href")));

        return links;
    }

    /**
     * Returns url in correct format
     */
    private String getUrl(String fromDate, String toDate, String channel, String page) {
        return "https://www.delfi.lt/archive/index.php?fromd="
                + fromDate + "&tod="
                + toDate + "&channel="
                + channel + "&category=0&query=&page=" + page;
    }

    public List<String> crawl() throws IOException {
        String url = getUrl(fromDate, toDate, channel, "1");
        Document doc = Jsoup.connect(url).timeout(CONNECTION_TIMEOUT).get();
        int lastPage = findLastPage(doc);

        // maximum number of pages to crawl
        if (lastPage > MAX_PAGES) {
            lastPage = MAX_PAGES;
        }

        links.addAll(findLinks(doc));

        for (int i = 2; i <= lastPage; i++) {
            String nextUrl = getUrl(fromDate, toDate, channel, String.valueOf(i));
            try {
                doc = Jsoup.connect(nextUrl).timeout(CONNECTION_TIMEOUT).get();
                links.addAll(findLinks(doc));
            } catch (Exception e) {
                System.out.println("exception occured");
            }
            System.out.println(nextUrl);
        }

        return links;
    }

}
