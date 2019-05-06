package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import projekti.domain.models.validation.AccountModel;
import projekti.services.AccountService;

import javax.validation.Valid;

/**
 * Controller for routes that do not need authentication except '/home' which is used to redirect user to his own wall after he has logged in.
 */
@Controller
public class DefaultController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public String helloWorld() {
        return "index";
    }

    /**
     * Custom end point for login.
     *
     * @return template name.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Checks the users nickname from database and then reroutes him to his own wall page.
     *
     * @return redirection where to go.
     */
    @GetMapping("/home")
    public String homeRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "redirect:/old-face/" + this.accountService.findByUsername(auth.getName()).getNickname();
    }

    /**
     * Custom end point for registration.
     *
     * @return template name.
     */
    @GetMapping("/register")
    public String register(@ModelAttribute AccountModel accountModel) {
        return "register";
    }

    /**
     * Custom end point for registration.
     * Validates the users input in registration form and returns appropriate error messages if needed.
     * Otherwise creates the new user.
     *
     * @see AccountModel
     *
     * @return template name.
     */
    @PostMapping("/register")
    public String createAccount(@Valid @ModelAttribute AccountModel accountModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "register";
        }

        this.accountService.create(accountModel);
        return "redirect:/login";
    }
}
