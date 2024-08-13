package version2_1.comment.service;

import version2_1.comment.domain.po.User;

/**
 * @Author JH
 * @Date 2024/8/9 20:09
 * @Version 1.0
 */
public interface UserService {
    User findUserById(Long id);
    Long insertUser(User user);
}
