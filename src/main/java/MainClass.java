import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Created by wistbean on 2018/6/2.
 */
public class MainClass {

    private static final Logger logger = LoggerFactory.getLogger(MainClass.class);

    public static void main(String[] args) throws InterruptedException {

       WechatGroupRobotImpl wechatGroupRobot =  WechatGroupRobotImpl.getInstance();
       String uuid = wechatGroupRobot.getUUID();
       wechatGroupRobot.showQrImage(uuid);

       while(!wechatGroupRobot.loginState().equals("200")){
           Thread.sleep(2000);
       }

       if(!wechatGroupRobot.login()){
           logger.info("微信登录失败");
           return;
       }

       logger.info("[*] 微信登录成功");

        if(!wechatGroupRobot.initWechat()){
            logger.info("[*] 微信初始化失败");
            return;
        }

        logger.info("[*] 微信初始化成功");

        if(!wechatGroupRobot.wxStatusNotify()){
            logger.info("[*] 开启状态通知失败");
            return;
        }

        logger.info("[*] 开启状态通知成功");

        if(!wechatGroupRobot.loadContact()){
            logger.info("[*] 获取联系人失败");
            return;
        }

        logger.info("[*] 获取联系人成功");
        logger.info("[*] 共有 %d 位联系人", wechatGroupRobot.ContactList.size());

        // 监听消息
        wechatGroupRobot.syncMsg();

        //定时任务
        cronTrigger();

    }

    private static void cronTrigger()  {
        JobDetail job = JobBuilder.newJob(WechatRobotJob.class).withIdentity("job1", "group1").build();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 6 ? * *");
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").withSchedule(cronScheduleBuilder).build();

        JobDetail job2 = JobBuilder.newJob(WechatRobotJob2.class).withIdentity("job2", "group2").build();
        CronScheduleBuilder cronScheduleBuilder2 = CronScheduleBuilder.cronSchedule("0 10 10,15,18,21 * * ?");
        Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("trigger2", "group2").withSchedule(cronScheduleBuilder2).build();

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            scheduler = schedulerFactory.getScheduler();
            scheduler.scheduleJob(job, trigger);
            scheduler.scheduleJob(job2, trigger2);
            scheduler.start();

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
