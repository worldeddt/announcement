package api.announcement.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micrometer.common.lang.NonNullFields;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.validation.FieldError;

import java.util.List;


@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerExceptionResponse<T> {
    private String code;
    private String reason;
    private T details;

    public ServerExceptionResponse(String code, String reason, T details) {
        this.code = code;
        this.reason = reason;
        this.details = details;
    }

    public static ServerExceptionResponse<Void> of(String code, String reason) {
        return new ServerExceptionResponse<>(code, reason, null);
    }

    public static ServerExceptionResponse<List<String>> of(
            String code, String reason, List<String> details
    ) {
        return new ServerExceptionResponse<>(code, reason, details);
    }

    public static ServerExceptionResponse<List<String>> of(
            ErrorCode errorCode, List<String> details
    ) {
        return new ServerExceptionResponse<>(errorCode.getCode(), errorCode.getReason(), details);
    }
}
