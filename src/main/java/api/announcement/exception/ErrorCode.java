package api.announcement.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    IN_VALID_ARGUMENT(HttpStatus.BAD_REQUEST, "NO_0011", "find invalid argument"),
    NOT_UPDATE_ROLE_OF_NOTICE(HttpStatus.BAD_REQUEST, "NO_0006", "is not update role of notice"),
    NOT_DELETE_ROLE_OF_NOTICE(HttpStatus.BAD_REQUEST, "NO_0005", "is not delete role of notice"),
    NOT_CREATE_ROLE_OF_NOTICE(HttpStatus.BAD_REQUEST, "NO_0004", "is not create role of notice"),
    NOT_FOUND_ATTACHMENT(HttpStatus.NOT_FOUND, "NO_0003", "not found attachment"),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "NO_0002", "not found user"),
    NOT_FOUND_NOTICE(HttpStatus.NOT_FOUND, "NO_0001", "not found notice");

    private final HttpStatus httpStatus;
    private final String code;
    private final String reason;

    public NoticeExeption build() {
        return new NoticeExeption(httpStatus, code, reason);
    }

    public NoticeExeption build(Object ...args) {
        return new NoticeExeption(httpStatus, code, reason.formatted(args));
    }
}
