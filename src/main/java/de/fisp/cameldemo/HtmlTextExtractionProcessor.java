package de.fisp.cameldemo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class HtmlTextExtractionProcessor {

    public final static void main(String[] args) throws Exception {
        HtmlTextExtractionProcessor.extract("test.html");
    }

    private static void extract(String path) throws IOException {
        File input = new File(path);
        Document doc = Jsoup.parse(input, "UTF-8");
        Elements elements = doc.select("*");
        for (Element element : elements) {
            System.out.println(element.tagName() + ": " + element.text());
        }
    }
}
