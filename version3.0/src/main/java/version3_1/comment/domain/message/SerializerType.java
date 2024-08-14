package version3_1.comment.domain.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author JH
 * @Date 2024/8/13 20:06
 * @Version 1.0
 */
@AllArgsConstructor
@Getter
public enum SerializerType {
    JDKSerializer(0),JSONSerializer(1);
    private final int code;
}
