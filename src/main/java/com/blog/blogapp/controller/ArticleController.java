package com.blog.blogapp.controller;

import com.blog.blogapp.exception.NotFoundException;
import com.blog.blogapp.model.Article;
import com.blog.blogapp.model.User;
import com.blog.blogapp.service.ArticleService;
import com.blog.blogapp.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;

    public ArticleController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping
    public String index(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false, value = "query") String query,
            @RequestParam(required = false, value = "page") Integer page,
            @RequestParam(required = false, value = "size") Integer size
    ) {

        //to make this proper non-blocking we need completable futures,
        //in spring webflux its a Mono - 0..1 elements
        //Flux - 0..N
        if (query == null) {
            model.addAttribute("articles",
                    articleService.getAll(getPageable(page, size)));
        } else {
            model.addAttribute("articles", articleService.search(query,
                    getPageable(page, size)));
        }

        return "article/index";
    }

    @GetMapping("/show/{link}")
    public String getPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String link,
            Model model
    ) {
        Optional<Article> article = articleService.getByLink(link);
        if (article.isPresent()) {
            model.addAttribute("article", article.get());
        } else {
            throwNotFoundException(link);
        }

        return "article/show";
    }

    @GetMapping("/new")
    public String newPost() {
        return "article/create";
    }

    @GetMapping("/edit/{id}")
    public String editPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            Model model
    ) {
        Optional<Article> article = articleService.getById(id);
        if (article.isPresent()) {
            model.addAttribute("article", article.get());
        } else {
            return throwNotFoundException(id);
        }

        return "article/create";
    }

    private String throwNotFoundException(@PathVariable String id) {
        throw new NotFoundException("Article Not Found for " + id);
    }

    @PostMapping("/delete/{id}")
    public String deletePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            Model model
    ) {
        articleService.deleteById(id);

        model.addAttribute("message", "Article with id " + id + " deleted successfully!");
        model.addAttribute("articles", articleService.getAll(PageRequest.of(0, 10)));

        return "article/index";
    }

    @PostMapping
    public String savePost(
            @AuthenticationPrincipal UserDetails userDetails,
            Article article,
            Model model
    ) {
        if (article.getId() == null || article.getId().length() == 0) {
            User user = userService.getByUsername(userDetails.getUsername());
            article.setAuthor(user);
        } else {
            Optional<Article> optionalArticle = articleService.getById(article.getId());
            optionalArticle.ifPresent(value -> article.setAuthor(value.getAuthor()));
        }
        articleService.save(article);

        return "redirect:/article/show/" + article.getLink();
    }

    @GetMapping("/rest")
    @ResponseBody
    public Page<Article> articlesRest(@RequestParam(required = false, value = "page") Integer page,
                                      @RequestParam(required = false, value = "size") Integer size) {
        return articleService.getAll(getPageable(page, size));
    }

    private Pageable getPageable(Integer page, Integer size) {
        if (page == null || size == null) {
            return PageRequest.of(0, 20);
        }

        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    }

}
