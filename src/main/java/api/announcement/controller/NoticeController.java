package api.announcement.controller;


import api.announcement.controller.dto.NoticeRequestDto;
import api.announcement.controller.dto.NoticeResponseDto;
import api.announcement.services.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<Page<NoticeResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "createdAt", direction= Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok().body(
            noticeService.getNotices(pageable)
        );
    }
}