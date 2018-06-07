import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.http.HttpRequest;
import blade.kit.http.HttpRequestException;
import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import com.google.gson.Gson;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import util.*;
import vo.TuLin;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by wistbean on 2018/5/30.
 */
public class WechatGroupRobotImpl implements WechatGroupRobotInterface{

    private static final Logger logger = LoggerFactory.getLogger(WechatGroupRobotImpl.class);
    private String base_uri, redirect_uri, webpush_url = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";
    private String skey, synckey, wxsid, wxuin, pass_ticket, deviceId;
    private JSONObject SyncKey, User, BaseRequest;
    // 微信联系人列表，可聊天的联系人列表
    private JSONArray MemberList;
    public JSONArray ContactList;

    //发送给指定的群
    public String sendToGroup;

    // 微信特殊账号
    private List<String> SpecialUsers = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail", "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend", "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin", "weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts", "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm", "notification_messages");

    /**
     * 是否登录
     */
    private boolean isLogin;
    private String uuid;

    private String cookie;

    private volatile static WechatGroupRobotImpl instance = null;
    private WechatGroupRobotImpl(){}

    public static WechatGroupRobotImpl getInstance()
    {
        if(instance == null)
        {
            synchronized (WechatGroupRobotImpl.class)
            {
                if(instance==null)
                {
                    instance = new WechatGroupRobotImpl();
                }
            }
        }

        return instance;
    }


    public boolean login() {

        if(isLogin){
            logger.info("已经登录");
            return true;
        }
        this.isLogin = true;

//        String res = HttpClientUtil.doGet(this.redirect_uri);
        HttpRequest request = HttpRequest.get(this.redirect_uri);
        String res = request.body();
        this.cookie = CookieUtil.getCookie(request);

        request.disconnect();
        if(StringUtil.isEmpty(res)){
            return false;
        }

        this.skey = MatcherUtil.match("<skey>(\\S+)</skey>", res);
        this.wxsid = MatcherUtil.match("<wxsid>(\\S+)</wxsid>", res);
        this.wxuin = MatcherUtil.match("<wxuin>(\\S+)</wxuin>", res);
        this.pass_ticket = MatcherUtil.match("<pass_ticket>(\\S+)</pass_ticket>", res);

        this.BaseRequest = new JSONObject();
        BaseRequest.put("Uin", this.wxuin);
        BaseRequest.put("Sid", this.wxsid);
        BaseRequest.put("Skey", this.skey);
        BaseRequest.put("DeviceID", this.deviceId);

        return true;
    }

    public boolean initWechat() {

        String url = this.base_uri + "/webwxinit?r=" + DateKit.getCurrentUnixTime() + "&pass_ticket=" + this.pass_ticket +
                "&skey=" + this.skey;

        JSONObject body = new JSONObject();
        body.put("BaseRequest", this.BaseRequest);

        HttpRequest request = HttpRequest.post(url)
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Cookie", this.cookie)
                .send(body.toString());

        logger.info("[*] " + request);
        String res = request.body();
        request.disconnect();

        if(StringKit.isBlank(res)){
            return false;
        }

        try {
            JSONObject jsonObject = JSON.parse(res).asObject();
            if(null != jsonObject){
                JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
                if(null != BaseResponse){
                    int ret = BaseResponse.getInt("Ret", -1);
                    if(ret == 0){
                        this.SyncKey = jsonObject.getJSONObject("SyncKey");
                        this.User = jsonObject.getJSONObject("User");

                        StringBuffer synckey = new StringBuffer();

                        JSONArray list = SyncKey.getJSONArray("List");
                        for(int i=0, len=list.size(); i<len; i++){
                            JSONObject item = list.getJSONObject(i);
                            synckey.append("|" + item.getInt("Key", 0) + "_" + item.getInt("Val", 0));
                        }
                        this.synckey = synckey.substring(1);

                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }



    public void sendMsg(String content, String to) {

        String url = this.base_uri + "/webwxsendmsg?lang=zh_CN&pass_ticket=" + this.pass_ticket;

        JSONObject body = new JSONObject();

        String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
        JSONObject Msg = new JSONObject();
        Msg.put("Type", 1);
        Msg.put("Content", content);
        Msg.put("FromUserName", User.getString("UserName"));
        Msg.put("ToUserName", to);
        Msg.put("LocalID", clientMsgId);
        Msg.put("ClientMsgId", clientMsgId);

        body.put("BaseRequest", this.BaseRequest);
        body.put("Msg", Msg);

        HttpRequest request = HttpRequest.post(url)
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Cookie", this.cookie)
                .send(body.toString());

        logger.info("[*] " + request);
        request.body();
        request.disconnect();
    }

    public boolean wxStatusNotify() {
        String url = this.base_uri + "/webwxstatusnotify?lang=zh_CN&pass_ticket=" + this.pass_ticket;

        JSONObject body = new JSONObject();
        body.put("BaseRequest", BaseRequest);
        body.put("Code", 3);
        body.put("FromUserName", this.User.getString("UserName"));
        body.put("ToUserName", this.User.getString("UserName"));
        body.put("ClientMsgId", DateKit.getCurrentUnixTime());

        HttpRequest request = HttpRequest.post(url)
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Cookie", this.cookie)
                .send(body.toString());

        logger.info("[*] " + request);
        String res = request.body();
        request.disconnect();

        if(StringKit.isBlank(res)){
            return false;
        }

        try {
            JSONObject jsonObject = JSON.parse(res).asObject();
            JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
            if(null != BaseResponse){
                int ret = BaseResponse.getInt("Ret", -1);
                return ret == 0;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public String getUUID() {

        logger.info("获取二维码UUID");
        String url = "https://login.weixin.qq.com/jslogin";

        Map requestParam = new HashMap<String,Object>();
        requestParam.put("appid","wx782c26e4c19acffb");
        requestParam.put("fun","new");
        requestParam.put("lang","zh_CN");

        String result = HttpClientUtil.doGet(url, requestParam);

        logger.info("[获取UUID返回结果]  " + result);

        if(!StringUtil.isEmpty(result)){
            String code = MatcherUtil.match("window.QRLogin.code = (\\d+);", result);
            if(null != code){
                if(code.equals("200")){
                    this.uuid = MatcherUtil.match("window.QRLogin.uuid = \"(.*)\";", result);
                    return this.uuid;
                } else {
                    logger.info("[*] 获取UUID错误，错误码: %s", code);
                }
            }
        }
        return null;
    }

    public void showQrImage(String uuid) {
        String uid  = null != uuid ? uuid : this.uuid;
        String url = "https://login.weixin.qq.com/qrcode/" + uid + "?t=webwx";
        final File output = new File("temp.jpg");

        //下载二维码
        File qrImage = HttpClientUtil.doGetImage(url);

        //控制台显示二维码
        Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hintMap.put(EncodeHintType.MARGIN, 1);
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        String  qrContent  = readQRCode(qrImage, hintMap);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 10, 10, hintMap);
            System.out.println(toAscii(bitMatrix));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public String loginState() {

        String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
        Map requestParam = new HashMap();
        requestParam.put("tip","1");
        requestParam.put("uuid",this.uuid);
        String res = HttpClientUtil.doGet(url, requestParam);

        if(null == res){
            logger.info("[*] 扫描二维码验证失败");
            return "";
        }

        String code = MatcherUtil.match("window.code=(\\d+);", res);
        if(null == code){
            logger.info("[*] 扫描二维码验证失败");
            return "";
        } else {
            if(code.equals("201")){
                logger.info("[*] 成功扫描,请在手机上点击确认以登录");
            } else if(code.equals("200")){
                logger.info("[*] 正在登录...");
                String pm = MatcherUtil.match("window.redirect_uri=\"(\\S+?)\";", res);
                String redirectHost = "wx.qq.com";
                try {
                    URL pmURL = new URL(pm);
                    redirectHost = pmURL.getHost();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String pushServer = JSUtil.getPushServer(redirectHost);
                webpush_url = "https://" + pushServer + "/cgi-bin/mmwebwx-bin";

                this.redirect_uri = pm + "&fun=new";
                logger.info("[*] redirect_uri=%s", this.redirect_uri);
                this.base_uri = this.redirect_uri.substring(0, this.redirect_uri.lastIndexOf("/"));
                logger.info("[*] base_uri=%s", this.base_uri);
            } else if(code.equals("408")){
                logger.info("[*] 登录超时");
            } else {
                logger.info("[*] 扫描code=%s", code);
            }
        }
        return code;

    }

    /**
     * 读取二维码信息
     *
     * @param filePath 文件路径
     * @param hintMap  hintMap
     * @return 二维码内容
     */
    private static String readQRCode(File filePath, Map hintMap) {
        try {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
            return qrCodeResult.getText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将二维码输出为 ASCII
     *
     * @param bitMatrix
     * @return
     */
    private static String toAscii(BitMatrix bitMatrix) {
        StringBuilder sb = new StringBuilder();
        for (int rows = 0; rows < bitMatrix.getHeight(); rows++) {
            for (int cols = 0; cols < bitMatrix.getWidth(); cols++) {
                boolean x = bitMatrix.get(rows, cols);
                if (!x) {
                    // white
                    sb.append("\033[47m  \033[0m");
                } else {
                    sb.append("\033[30m  \033[0;39m");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    public boolean loadContact() {
        String url = this.base_uri + "/webwxgetcontact?pass_ticket=" + this.pass_ticket + "&skey=" + this.skey + "&r=" + DateKit.getCurrentUnixTime();

        JSONObject body = new JSONObject();
        body.put("BaseRequest", BaseRequest);

        HttpRequest request = HttpRequest.post(url)
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Cookie", this.cookie)
                .send(body.toString());

        logger.info("[*] " + request);
        String res = request.body();
        request.disconnect();

        if(StringKit.isBlank(res)){
            return false;
        }

        try {
            JSONObject jsonObject = JSON.parse(res).asObject();
            JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
            if(null != BaseResponse){
                int ret = BaseResponse.getInt("Ret", -1);
                if(ret == 0){
                    this.MemberList = jsonObject.getJSONArray("MemberList");
                    this.ContactList = new JSONArray();
                    if(null != MemberList){
                        for(int i=0, len=MemberList.size(); i<len; i++){
                            JSONObject contact = this.MemberList.getJSONObject(i);
                            //公众号/服务号
                            if(contact.getInt("VerifyFlag", 0) == 8){
                                continue;
                            }
                            //特殊联系人
                            if(SpecialUsers.contains(contact.getString("UserName"))){
                                continue;
                            }
                            //群聊
                            if(contact.getString("UserName").indexOf("@@") != -1){
                                continue;
                            }
                            //自己
                            if(contact.getString("UserName").equals(this.User.getString("UserName"))){
                                continue;
                            }
                            ContactList.add(contact);
                        }
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void syncMsg() {
        new Thread(new Runnable() {
            public void run(){
                logger.info("[*] 进入消息监听模式 ...");
                while(true){

                    int[] arr = syncCheck();
                    logger.info("[*] retcode=%s,selector=%s", arr[0], arr[1]);

                    if(arr[0] == 0){
                        if(arr[1] == 2){
                            JSONObject data = webwxsync();
                            handleMsg(data);
                        } else if(arr[1] == 6){
                            JSONObject data = webwxsync();
                            handleMsg(data);
                        } else if(arr[1] == 3){
                        } else if(arr[1] == 0){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "listenMsgMode").start();
    }

    /**
     * 消息检查
     */
    public int[] syncCheck(){

        int[] arr = new int[2];

        String url = this.webpush_url + "/synccheck";

        JSONObject body = new JSONObject();
        body.put("BaseRequest", BaseRequest);

        HttpRequest request = HttpRequest.get(url, true,
                "r", DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5),
                "skey", this.skey,
                "uin", this.wxuin,
                "sid", this.wxsid,
                "deviceid", this.deviceId,
                "synckey", this.synckey,
                "_", System.currentTimeMillis())
                .header("Cookie", this.cookie);

        logger.info("[*] " + request);
        String res = null;
        try {
            res = request.body();
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
        request.disconnect();

        if(StringKit.isBlank(res)){
            return arr;
        }

        String retcode = MatcherUtil.match("retcode:\"(\\d+)\",", res);
        String selector = MatcherUtil.match("selector:\"(\\d+)\"}", res);
        if(null != retcode && null != selector){
            arr[0] = Integer.parseInt(retcode);
            arr[1] = Integer.parseInt(selector);
            return arr;
        }
        return arr;
    }


    /**
     * 获取最新消息
     */
    public JSONObject webwxsync(){

        String url = this.base_uri + "/webwxsync?lang=zh_CN&pass_ticket=" + this.pass_ticket
                + "&skey=" + this.skey + "&sid=" + this.wxsid + "&r=" + DateKit.getCurrentUnixTime();

        JSONObject body = new JSONObject();
        body.put("BaseRequest", BaseRequest);
        body.put("SyncKey", this.SyncKey);
        body.put("rr", DateKit.getCurrentUnixTime());

        HttpRequest request = HttpRequest.post(url)
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Cookie", this.cookie)
                .connectTimeout(30000)
                .send(body.toString());

        logger.info("[*] " + request);
        String res = request.body();
        request.disconnect();

        if(StringKit.isBlank(res)){
            return null;
        }

        JSONObject jsonObject = JSON.parse(res).asObject();
        JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
        if(null != BaseResponse){
            int ret = BaseResponse.getInt("Ret", -1);
            if(ret == 0){
                this.SyncKey = jsonObject.getJSONObject("SyncKey");

                StringBuffer synckey = new StringBuffer();
                JSONArray list = SyncKey.getJSONArray("List");
                for(int i=0, len=list.size(); i<len; i++){
                    JSONObject item = list.getJSONObject(i);
                    synckey.append("|" + item.getInt("Key", 0) + "_" + item.getInt("Val", 0));
                }
                this.synckey = synckey.substring(1);
            }
        }
        return jsonObject;
    }

    /**
     * 处理最新消息
     */
    public void handleMsg(JSONObject data){
        if(null == data){
            return;
        }

        JSONArray AddMsgList = data.getJSONArray("AddMsgList");

        for(int i=0,len=AddMsgList.size(); i<len; i++){
            JSONObject msg = AddMsgList.getJSONObject(i);
            int msgType = msg.getInt("MsgType", 0);
            String name = getUserRemarkName(msg.getString("FromUserName"));
            String content = msg.getString("Content");

            if(msgType == 51){
                logger.info("[*] 成功截获微信初始化消息");
            } else if(msgType == 1){
                if(SpecialUsers.contains(msg.getString("ToUserName"))){
                    continue;
                } else if(msg.getString("FromUserName").equals(User.getString("UserName"))){
                    continue;
                } else if (msg.getString("ToUserName").indexOf("@@") != -1) {
                    String[] peopleContent = content.split(":<br/>");
                    logger.info("|" + name + "| " + peopleContent[0] + ":\n" + peopleContent[1].replace("<br/>", "\n"));
                } else {
//                    logger.info(name + ": " + content + ": " );
                    String[] peopleContent = content.split(":<br/>");
//                    logger.info("|" + name + "| " + peopleContent[0] + ":\n" + peopleContent[1].replace("<br/>", "\n"));
                    logger.info("发送者：" + msg.getString("FromUserName"));
                    if(name.equals("wistbean和他的朋友们")){
                        if(this.sendToGroup==null)
                            this.sendToGroup = msg.getString("FromUserName");
                    }

                    if (content.contains("wistbean的小三")){

                        String sendMsg ;

                        if(peopleContent.length==2){
                            sendMsg = getMsg(peopleContent[1].replace("@wistbean的小三", ""));
                        }else{
                            sendMsg = "别随便@我，我比较娇贵~";
                        }

                        sendMsg(sendMsg , msg.getString("FromUserName"));
                    }
                }
            } else if(msgType == 3){
                //收到图片信息
            } else if(msgType == 34){
                //收到语音信息
            } else if(msgType == 42){
                //名片信息
            }
        }
    }

    /**
     * 图灵机器人获取消息回复
     * @param content
     * @return
     */
    private String getMsg(String content) {
        Map requestParam = new HashMap();
        requestParam.put("key","80a7ba9246814892ad0836b6561be745");
        requestParam.put("info",content);
        String res = HttpClientUtil.doPost("http://www.tuling123.com/openapi/api",requestParam);
        TuLin tuLin = new Gson().fromJson(res, TuLin.class);
        return tuLin.getText();

    }

    private String getUserRemarkName(String id) {
        String name = "这个人物名字未知";
        for(int i=0, len=MemberList.size(); i<len; i++){
            JSONObject member = this.MemberList.getJSONObject(i);
            if(member.getString("UserName").equals(id)){
                if(StringKit.isNotBlank(member.getString("RemarkName"))){
                    name = member.getString("RemarkName");
                } else {
                    name = member.getString("NickName");
                }
                return name;
            }
        }
        return name;
    }


}
