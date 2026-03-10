package project.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class HtmlParserService {

    public String searchInWeb(String courseName) {
        try {
            String searchUrl = "https://html.duckduckgo.com/html/?q=" + courseName + " технологія опис";
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();

            Element result = doc.select(".result__snippet").first();

            if (result != null) {
                return result.text();
            }
        } catch (Exception e) {
            System.err.println("Помилка парсингу пошуку: " + e.getMessage());
        }
        return "Не вдалося знайти актуальну технічну довідку в мережі.";
    }
}
