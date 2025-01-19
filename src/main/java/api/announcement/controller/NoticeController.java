package api.announcement.controller;


import api.announcement.controller.dto.NoticeRequestDto;
import api.announcement.controller.dto.NoticeResponseDto;
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
    public ResponseEntity<NoticeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok().body(noticeService.getNoticeById(id));
    }

    @PostMapping("")
    public ResponseEntity<NoticeRequestDto> post(@RequestBody NoticeRequestDto noticeRequestDto) {
        noticeService.createNotice(noticeRequestDto);
        return ResponseEntity.ok().body(new NoticeRequestDto());
    }

}
