/**
 * Created by wistbean on 2018/5/30.
 * 群机器人接口
 */
public interface WechatGroupRobotInterface {


    /**
     * 获取UUID
     * @return
     */
    String getUUID();


    /**
     * 显示二维码
     * @param uuid
     */
    void showQrImage(String uuid);


    /**
     * 登录状态
     * @return
     */
    String loginState();

    /**
     * 登录
     */
    boolean login();

    /**
     * 微信初始化
     * @return
     */
    boolean initWechat();

    /**
     * 加载联系人
     */
    boolean loadContact();

    /**
     * 监听消息
     */
    void syncMsg();

    /**
     * 发送消息
     */
    void sendMsg(String content, String to);

    /**
     * 微信状态栏通知
     * @return
     */
    boolean wxStatusNotify();


}
