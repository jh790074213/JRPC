package version1_1.comment.service.impl;

import version1_1.comment.domain.po.User;
import version1_1.comment.service.UserService;

import java.util.Random;
import java.util.UUID;

/**
 * @Author JH
 * @Date 2024/8/9 20:09
 * @Version 1.0
 */

public class UserServiceImpl implements UserService {
    @Override
    public User findUserById(Long id) {
        System.out.println("客户端查询了"+id+"的用户");
        // 模拟从数据库中取用户的行为
        Random random = new Random();
        User user = User.builder().userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean()).build();
        return user;
    }

    @Override
    public Long insertUser(User user) {
        System.out.println("插入数据成功"+user.getUserName());
        return user.getId();
    }
}
