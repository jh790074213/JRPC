package version3_1.client;


import version3_1.client.proxy.ClientProxy;
import version3_1.comment.domain.po.User;
import version3_1.comment.service.UserService;

/**
 * @Author JH
 * @Date 2024/8/9 20:07
 * @Version 1.0
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        // 创建代理对象进行消息的发送和接收，指定ip，端口号
        ClientProxy clientProxy = new ClientProxy();
        UserService userService = clientProxy.createProxy(UserService.class);
        int count=100;
        while(count > 0){
            User user = userService.findUserById(1L);
            System.out.println("查询的用户信息为：" + user.toString());
            // Thread.sleep(1000);
            User u = User.builder().id(100L).userName("jh").sex(true).build();
            Long userId = userService.insertUser(u);
            System.out.println("插入成功用户id为: " + userId);
            count--;
            Thread.sleep(1000);
        }

    }
}
