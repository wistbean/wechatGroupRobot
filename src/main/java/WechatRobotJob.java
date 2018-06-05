import com.google.gson.Gson;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import util.HttpClientUtil;
import vo.Weather;

/**
 * Created by wistbean on 2018/6/4.
 */
public class WechatRobotJob implements Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("--------调度任务-----------");
        WechatGroupRobotImpl wechatGroupRobot =  WechatGroupRobotImpl.getInstance();
        String res = HttpClientUtil.doGet("http://v.juhe.cn/weather/index?format=2&cityname=广州&key=bad9dc30f56ed7d97a5abf84b6a4ae52");
        Gson gson = new Gson();
        Weather weather = gson.fromJson(res, Weather.class);
        Weather.ResultBean.TodayBean today = weather.getResult().getToday();
        String msg = today.getWeek() + "的" + today.getCity() + "温度：" + today.getTemperature()
                +" , 天气：" + today.getWeather() + " , " + today.getDressing_advice() + today.getWash_index() + "洗车,"
                + today.getTravel_index() + "旅游," + today.getExercise_index() + "晨练！";
        wechatGroupRobot.sendMsg(msg,wechatGroupRobot.sendToGroup);
    }
}
