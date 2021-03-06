package classifier.utils;

import classifier.crawlers.Channels;
import classifier.crawlers.ContentSpider;
import classifier.crawlers.LinksSpider;

import java.io.IOException;
import java.util.List;

public class Utils {

    /**
     * Returns date in correct format
     */
    public static String getDate(int day, int month, int year) {
        String dayStr = String.valueOf(day);
        String monthStr = String.valueOf(month);
        String yearStr = String.valueOf(year);

        return dayStr + "." + monthStr + "." + yearStr;
    }

    /**
     * Gets data for training and testing
     */
    public static void gatherData() throws IOException {
        String fromDate = Utils.getDate(1, 1, 2016);
        String toDate = Utils.getDate(1, 5, 2018);

        LinksSpider linksSpiderAuto = new LinksSpider(Channels.Auto, fromDate, toDate);
        LinksSpider linksSpiderSportas = new LinksSpider(Channels.Sportas, fromDate, toDate);
        LinksSpider linksSpiderVerslas = new LinksSpider(Channels.Verslas, fromDate, toDate);
        LinksSpider linksSpiderMokslas = new LinksSpider(Channels.Mokslas, fromDate, toDate);

        List<String> linksAuto = linksSpiderAuto.crawl();
        List<String> linksSportas = linksSpiderSportas.crawl();
        List<String> linksVerslas = linksSpiderVerslas.crawl();
        List<String> linksMokslas = linksSpiderMokslas.crawl();

        ContentSpider contentSpiderAuto = new ContentSpider(linksAuto);
        ContentSpider contentSpiderSportas = new ContentSpider(linksSportas);
        ContentSpider contentSpiderVerslas = new ContentSpider(linksVerslas);
        ContentSpider contentSpiderMokslas = new ContentSpider(linksMokslas);

        List<String> contentAuto = contentSpiderAuto.crawl();
        List<String> contentSportas = contentSpiderSportas.crawl();
        List<String> contentVerslas = contentSpiderVerslas.crawl();
        List<String> contentMokslas = contentSpiderMokslas.crawl();

        Writer.write(Channels.Auto, contentAuto);
        Writer.write(Channels.Sportas, contentSportas);
        Writer.write(Channels.Verslas, contentVerslas);
        Writer.write(Channels.Mokslas, contentMokslas);
    }

}
