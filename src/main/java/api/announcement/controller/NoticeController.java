package api.announcement.controller;


import api.announcement.entities.Notice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {


    @GetMapping("/{id}")
    public String getById(@PathVariable Long id) {
        return String.valueOf(id);
    }


    @PostMapping("")
    public String post(@RequestBody Notice notice) {

    }
}
