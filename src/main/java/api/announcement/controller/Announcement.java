package api.announcement.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/announcement")
public class Announcement {


    @GetMapping("/{id}")
    public String getAnnouncement(@PathVariable int id) {
        return String.valueOf(id);
    }
}
