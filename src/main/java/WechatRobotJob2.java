import org.apache.http.client.utils.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wistbean on 2018/6/5.
 * 定时任务 每隔一段时间提醒喝水
 */
public class WechatRobotJob2 implements Job {
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("----------定时提醒----------");
        WechatGroupRobotImpl wechatGroupRobot =  WechatGroupRobotImpl.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String msg = "现在是：" + dateFormat.format(new Date())+ " ,一天八杯水，别忘了喝水，放松下自己哦~";
        wechatGroupRobot.sendMsg(msg,wechatGroupRobot.sendToGroup);
    }
}
