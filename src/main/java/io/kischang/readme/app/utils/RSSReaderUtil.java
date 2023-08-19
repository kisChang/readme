package io.kischang.readme.app.utils;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.Data;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RSSReaderUtil {

    public static List<NewsArticle> read(String feedUrl) throws IOException, FeedException {
        URL feedSource = new URL(feedUrl);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        Iterator<SyndEntry> itr = feed.getEntries().iterator();
        List<NewsArticle> results = new ArrayList<>();
        while (itr.hasNext()) {
            SyndEntry syndEntry = itr.next();
            results.add(mapToArticle(syndEntry));
        }

        return results;
    }

    private static NewsArticle mapToArticle(SyndEntry syndEntry) {
        NewsArticle newsArticle = new NewsArticle();
        newsArticle.setUri(syndEntry.getUri());
        newsArticle.setTitle(syndEntry.getTitle());
        newsArticle.setPublishedDate(ReadmeUtils.dateTime(syndEntry.getUpdatedDate()));
        newsArticle.setImgUrl("");
        newsArticle.setLink(syndEntry.getLink());

        StringBuilder sb = new StringBuilder();
        for (SyndContent content : syndEntry.getContents()) {
            sb.append(content.getValue());
        }
        newsArticle.setContent(sb.toString());
        return newsArticle;
    }

    public static String parseHtmlContent(String content) {
        Document dirty = Jsoup.parseBodyFragment(content, "");
        Cleaner cleaner = new Cleaner(Safelist.relaxed());
        Document clean = cleaner.clean(dirty);
        // 处理所有图片，转为base64
        for (Element img : clean.select("img")) {
            String src = img.attr("src");
            if (src.startsWith("http")){
                // 转为base64
                String tmp = HttpUtils.getImgBase64(src);
                if (tmp != null) {
                    img.attr("src", tmp);
                }
            }
        }
        clean.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        clean.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        return clean.body().html();
    }

    @Data
    public static class NewsArticle {
        private String uri;
        private String title;
        private String link;
        private String imgUrl;
        private String publishedDate;
        private String content;
        private List<String> categories;

    }

}