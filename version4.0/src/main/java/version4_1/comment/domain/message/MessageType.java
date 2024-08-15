package version4_1.comment.domain.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author JH
 * @Date 2024/8/13 20:04
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    REQUEST(0),RESPONSE(1);
    private final int code;
}
