package com.example.crawlers.controller;

import com.example.crawlers.model.Article;
import com.example.crawlers.model.Category;
import com.example.crawlers.service.ArticleService;
import com.example.crawlers.service.CategoryService;
import com.example.crawlers.utils.CrawlerUtils;
import com.example.crawlers.utils.FileUtils;
import com.example.crawlers.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class CrawlerController {
    @Value("${site.url}")
    private String siteUrl;

    @Value("${site.domain}")
    private String siteDomain;

    @Value("${download.path}")
    private String downloadPath;

    @Resource
    private FileUtils fileUtils;

    private final CategoryService categoryService;
    private final ArticleService articleService;

    @Autowired
    public CrawlerController(CategoryService categoryService, ArticleService articleService) {
        this.categoryService = categoryService;
        this.articleService = articleService;
    }


    public void crawlerFrontPage(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements aTags = document.getElementsByTag("a");
        aTags.stream()
                .filter(a -> a.hasAttr("href"))
                .map(a->{
                    String href = StringUtils.trim(a.attr("href"));
                    if (StringUtils.isBlank(href)){
                        System.out.println("[Blank]"+a.text());
                    }else if (!StringUtils.isStartWith(href,"/")){
                        System.out.println("[outLink]"+a.text() + "[href:"+href+"]" );
                    }else {
                        return href;
                    }
                    return null;
                })
                .filter(StringUtils::isNotBlank)
                .forEach(href -> {
                    crawler(href);
                });
//        Element classifyDiv = document.getElementsByClass("classifyDIV").first();
    }

    public String crawler(String url)  {
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Element classifyDiv = document.getElementsByClass("classifyDIV").first();
        Elements aTags = classifyDiv.getElementsByTag("a");

        if (aTags.last().text().equals("正文") &&
                (!aTags.last().hasAttr("href")
                        || StringUtils.trim(aTags.last().attr("href")).equals("") ) ) {
            Article article = parseArticle(document);
            if (article != null) {
                articleService.save(article);
            }
        } else {
            Category category = parseCategory(document);
            if (category != null) {
                categoryService.save(category);
            }

            for (Element a : aTags) {
                String href = StringUtils.trim(a.attr("href"));
                if (!href.equals("")) {
                    Document childDocument = Jsoup.connect(siteDomain + href).get();
                    Element childClassifyDiv = childDocument.getElementsByClass("classifyDIV").first();
                    Elements childATags = childClassifyDiv.getElementsByTag("a");

                    if (childATags.last().text().equals("正文") && StringUtils.trim(childATags.last().attr("href")).equals("")) {
                        Article article = parseArticle(childDocument);
                        if (article != null) {
                            articleService.save(article);
                        }
                    } else {
                        Category childCategory = parseCategory(childDocument);
                        if (childCategory != null) {
                            categoryService.save(childCategory);
                        }
                    }
                }
            }
        }

        return "index";
    }

    private Category parseCategory(Document document) {
        Element classifyDiv = document.getElementsByClass("classifyDIV").first();
        Elements aTags = classifyDiv.getElementsByTag("a");
        if (aTags.size() < 2) {
            return null;
        }

        Category category = new Category();
        category.setName(StringUtils.trim(aTags.last().text()));
        String href = StringUtils.trim(aTags.last().attr("href"));
        category.setUrl(href.startsWith("/") ? href.substring(1) : href);
        category.setType("1");

        Element parentElement = aTags.get(aTags.size() - 2);
        if (parentElement.tagName().equals("a")) {
            category.setParentName(StringUtils.trim(parentElement.text()));
            String parentHref = StringUtils.trim(parentElement.attr("href"));
            Category parentCategory = categoryService.findByUrl(parentHref.startsWith("/") ? parentHref.substring(1) : parentHref);
            if (parentCategory != null) {
                category.setParentId(parentCategory.getId());
            }
        }

        return category;
    }

    private Article parseArticle(Document document) {
        Element txmdDiv = document.getElementsByClass("txmd").first();
        Element myInfoDiv = document.getElementsByClass("my-info").first();
        Element basicDiv = document.getElementsByClass("basic").first();

        if (txmdDiv == null || myInfoDiv == null || basicDiv == null) {
            return null;
        }

        Article article = new Article();
        article.setTitle(StringUtils.trim(txmdDiv.text()));
        article.setPublishDate(CrawlerUtils.parseDate(StringUtils.trim(myInfoDiv.text())));
        article.setContent(basicDiv.html());
        article.setCategoryId(parseArticleCategory(document));

        List<String> filePaths = new ArrayList<>();
        Elements imgTags = basicDiv.getElementsByTag("img");
        for (Element img : imgTags) {
            String src = StringUtils.trim(img.attr("src"));
            if (StringUtils.isNotBlank(src)) {
                String filePath = downloadFile(src);
                if (StringUtils.isNotBlank(filePath)) {
                    filePaths.add(filePath);
                    img.attr("src", filePath);
                }
            }
        }

        Elements aTags = basicDiv.getElementsByTag("a");
        for (Element a : aTags) {
            String href = StringUtils.trim(a.attr("href"));
            if (StringUtils.isNotBlank(href)) {
                String filePath = downloadFile(href);
                if (StringUtils.isNotBlank(filePath)) {
                    filePaths.add(filePath);
                    a.attr("href", filePath);
                }
            }
        }

        articleService.saveArticleFiles(article.getId(), filePaths);

        return article;
    }

    private Long parseArticleCategory(Document document) {
        Elements aTags = document.getElementsByClass("classifyDIV").first().getElementsByTag("a");
        for (int i = aTags.size() - 1; i >= 0; i--) {
            Element a = aTags.get(i);
            if (a.text().equals("正文")) {
                String url = StringUtils.trim(a.attr("href"));
                if (!url.startsWith("/")) {
                    url = "/" + url;
                }
                Category category = categoryService.findByUrl(url);
                return category != null ? category.getId() : null;
            }
        }
        return null;
    }

    private String downloadFile(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        String fileName = url.substring(url.lastIndexOf("/") + 1);
        String filePath = downloadPath + "/" + CrawlerUtils.getFolderName(url);
        File file = new File(filePath + "/" + fileName);

        if (file.exists()) {
            return file.getAbsolutePath();
        }

        try {
            byte[] bytes = FileUtils.downloadFile(url);
            if (bytes == null) {
                return null;
            }
            FileUtils.saveFile(bytes, filePath, fileName);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
