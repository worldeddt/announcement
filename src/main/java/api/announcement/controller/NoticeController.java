package api.announcement.controller;


import api.announcement.controller.dto.NoticeRequestDto;
import api.announcement.entities.Notice;
import api.announcement.repositories.NoticeRepository;
import api.announcement.services.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id) {
        return String.valueOf(id);
    }


    @PostMapping("")
    public ResponseEntity<> post(@RequestBody NoticeRequestDto noticeRequestDto) {

        noticeService.createNotice(noticeRequestDto);
    }
}
