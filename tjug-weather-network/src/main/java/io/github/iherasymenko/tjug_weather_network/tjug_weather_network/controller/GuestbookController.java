package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.controller;

import io.github.iherasymenko.tjug_weather_network.tjug_weather_network.service.Guestbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GuestbookController {

    static final int MAX_NAME_LENGTH = 50;
    static final int MAX_MESSAGE_LENGTH = 500;

    private final Guestbook guestbook;

    public GuestbookController(Guestbook guestbook) {
        this.guestbook = guestbook;
    }

    @GetMapping("/guestbook")
    public String guestbook(Model model) {
        model.addAttribute("entries", guestbook.entries());
        return "guestbook";
    }

    @PostMapping("/guestbook")
    public String sign(@RequestParam(defaultValue = "") String name,
                       @RequestParam(defaultValue = "") String message,
                       Model model) {
        String error = validate(name.strip(), message.strip());
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("name", name);
            model.addAttribute("message", message);
            model.addAttribute("entries", guestbook.entries());
            return "guestbook";
        }
        guestbook.sign(name.strip(), message.strip());
        return "redirect:/guestbook";
    }

    private static String validate(String name, String message) {
        if (name.isEmpty() || message.isEmpty()) {
            return "Please enter your name and a message.";
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return "Your name can be at most " + MAX_NAME_LENGTH + " characters long.";
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            return "Your message can be at most " + MAX_MESSAGE_LENGTH + " characters long.";
        }
        return null;
    }

}
