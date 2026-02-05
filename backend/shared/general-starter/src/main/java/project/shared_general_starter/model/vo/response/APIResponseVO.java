package project.shared_general_starter.model.vo.response;

import jakarta.annotation.Nullable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APIResponseVO<T> {
    private boolean success;
    private String message;
    private T data;

    public static <D> APIResponseVO<D> success(@Nullable D data){
        return APIResponseVO.<D>builder()
            .success(true)
            .message(null)
            .data(data)
            .build();
    }

    public static APIResponseVO<Object> error(String message){
        return APIResponseVO.builder()
            .success(false)
            .message(message)
            .data(null)
            .build();
    }
}
