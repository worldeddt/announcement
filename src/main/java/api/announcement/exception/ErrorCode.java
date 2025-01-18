package api.announcement.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    IN_VALID_ARGUMENT(HttpStatus.BAD_REQUEST, "NO_0011", "find invalid argument"),
    NOT_FOUND_INSTRUCTOR(HttpStatus.NOT_FOUND, "NO_0010", "instructor not found"),
    NOT_FOUND_VENUE(HttpStatus.NOT_FOUND, "NO_0009", "venue not found"),
    RESOURCE_IS_LOCKED(HttpStatus.BAD_REQUEST, "NO_0009", "Resource is locked"),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NO_0008", "application not found"),
    ATTENDER_DUPLICATED(HttpStatus.BAD_REQUEST, "NO_0007", "duplicate attender"),
    LECTURE_IS_FULL(HttpStatus.BAD_REQUEST, "NO_0006", "lecture full"),
    INVALID_ATTENDER_ID(HttpStatus.BAD_REQUEST, "NO_0005", "invalid attender id"),
    INVALID_INSTRUCTOR_ID(HttpStatus.BAD_REQUEST, "NO_0004", "invalid instructor id"),
    INVALID_VENUE_ID(HttpStatus.BAD_REQUEST, "NO_0003", "invalid venue id"),
    OVER_SEAT_COUNT_REGISTERING(HttpStatus.BAD_REQUEST, "NO_0002", "over seat registering"),
    NOT_FOUND_LECTURE(HttpStatus.NOT_FOUND, "NO_0001", "not found lecture");

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
