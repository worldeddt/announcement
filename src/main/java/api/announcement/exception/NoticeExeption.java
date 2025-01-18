package api.announcement.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class NoticeExeption extends RuntimeException{
    private HttpStatus httpStatus;
    private String code;
    private String reason;
}
