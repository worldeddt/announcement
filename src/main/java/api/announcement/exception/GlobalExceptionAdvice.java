package api.announcement.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static api.announcement.exception.ErrorCode.IN_VALID_ARGUMENT;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(NoticeExeption.class)
    ResponseEntity<ServerExceptionResponse> handleFanException(NoticeExeption e) {
        log.error("Notice exception : Code = {}, Reason = {}", e.getCode(), e.getReason());
        return ResponseEntity.status(e.getHttpStatus().value())
                        .body(ServerExceptionResponse.of(
                                e.getCode(), e.getReason()
                        ));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ServerExceptionResponse> handleException(Exception e) {
        log.error("unhandled exception : {} ", e.getMessage());
        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
        ).body(
                ServerExceptionResponse.of(
                        String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        e.getMessage()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServerExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> list = fieldErrors.stream().map(GlobalExceptionAdvice::getFieldErrorParsing).toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ServerExceptionResponse.of(
                        IN_VALID_ARGUMENT,
                        list
                )
        );
    }

    private static String getFieldErrorParsing(FieldError fieldError) {
        final var field = fieldError.getField();
        final var rejectedValue = fieldError.getRejectedValue();
        final var defaultMessage = fieldError.getDefaultMessage();
        return getResultMessage(field, rejectedValue, defaultMessage);
    }

    private static String getResultMessage(String field, Object rejectedValue, String defaultMessage) {
        return String.format("field: %s, value: %s, message: %s", field, rejectedValue, defaultMessage);
    }

}