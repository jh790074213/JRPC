package version4_1.client;


import lombok.extern.slf4j.Slf4j;
import version4_1.client.proxy.ClientProxy;
import version4_1.comment.domain.po.User;
import version4_1.comment.service.UserService;

/**
 * @Author JH
 * @Date 2024/8/9 20:07
 * @Version 4.0
 */
@Slf4j
public class Client {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy=new ClientProxy();
        UserService proxy=clientProxy.createProxy(UserService.class);
        for(int i = 0; i < 120; i++) {
            Long i1 = (long) i;
            if (i%30==0) {
                Thread.sleep(10000);
            }
            new Thread(()->{
                try{
                    User user = proxy.findUserById(i1);
                    System.out.println("从服务端得到的user="+user.toString());
                    Long id = proxy.insertUser(User.builder().id(i1).userName("User" + i1).sex(true).build());
                    System.out.println("向服务端插入user的id"+id);
                } catch (NullPointerException e){
                    log.error("user为空 :" + e);
                }
            },"Thread:"+i).start();
        }
    }
}
