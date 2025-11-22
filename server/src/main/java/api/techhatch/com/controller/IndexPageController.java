package api.techhatch.com.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexPageController {
    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/error")
    public String error(Model model, HttpSession session){
        var ex = (Exception) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (ex != null) {
            model.addAttribute("errorMessage", ex.fillInStackTrace());
        }

        return "error";
    }


    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("name", "Sanjay");
        return "test";
    }

}
