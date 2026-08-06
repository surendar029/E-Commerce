package dev.project.userservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class TestController {

    private final List<String> names = new ArrayList<>(
            List.of("John", "Doe", "Jane", "Sam"));

    @GetMapping("/test")
    public String hello() {
        return "Hello Boss!";
    }

    @PostMapping("/addname/{name}")
    public String addName(@PathVariable String name) {
        names.add(name);
        return "Name Added " + names;
    }

    @GetMapping("/getnames")
    public List<String> getNames() {
        return names;
    }

    @DeleteMapping("/deletename/{name}")
    public String deleteName(@PathVariable String name) {
        names.remove(name);
        return "Name Deleted " + names;
    }
}