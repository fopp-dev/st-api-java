package com.xc.utils.email;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.xc.pojo.User;
import com.xc.pojo.UserRecharge;
import com.xc.utils.PropertiesUtil;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendHTMLMail {
    private static final Logger log = LoggerFactory.getLogger(SendHTMLMail.class);

    public static void send(User user, UserRecharge userRecharge, String emailToken, String host, String emailTo) {
        SAXReader reader = new SAXReader();
        Document document = null;

        String path = SendHTMLMail.class.getResource("/").getPath().replaceAll("%20", " ");

        String html_path = path + "email/auth.html";

        File file = new File(html_path);

        try {
            document = reader.read(file);
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();

            Element time = getNodes(root, "id", "time");
            Calendar calendar = Calendar.getInstance();
            time.setText(calendar
                    .get(1) + "-" + (calendar.get(2) + 1) + "-" + calendar
                    .get(5) + " " + calendar.get(10) + ":" + calendar
                    .get(12) + ":" + calendar.get(13));

            Element noticeName = getNodes(root, "id", "noticeName");
            noticeName.setText(user.getRealName());

            Element userid = getNodes(root, "id", "userid");
            userid.setText(user.getId() + "");
            Element realname = getNodes(root, "id", "realname");
            realname.setText((user.getAccountType().intValue() == 0) ? ("正式用户 - " + user
                    .getRealName()) : ("模拟用户 - " + user
                    .getRealName()));
            Element amt = getNodes(root, "id", "amt");
            amt.setText(userRecharge.getPayAmt().toString());
            Element ordersn = getNodes(root, "id", "ordersn");
            ordersn.setText(userRecharge.getOrderSn());

            String authurl = PropertiesUtil.getProperty("site.email.auth.url");

            String emailUrl = host + authurl + "?token=" + emailToken + "&orderSn=" + userRecharge.getOrderSn() + "&state=";

            String successUrl = emailUrl + '\001';
            Element dosuccessurl = getNodes(root, "id", "dosuccessurl");
            dosuccessurl.setText(successUrl);
            Element dosuccess = getNodes(root, "id", "dosuccess");
            dosuccess.setAttributeValue("href", successUrl);

            String failUrl = emailUrl + '\002';
            Element dofailurl = getNodes(root, "id", "dofailurl");
            dofailurl.setText(failUrl);
            Element dofail = getNodes(root, "id", "dofail");
            dofail.setAttributeValue("href", failUrl);

            String cancelUrl = emailUrl + '\003';
            Element docancelurl = getNodes(root, "id", "docancelurl");
            docancelurl.setText(cancelUrl);
            Element docancel = getNodes(root, "id", "docancel");
            docancel.setAttributeValue("href", cancelUrl);

            FileWriter fwriter = new FileWriter(path + "email/temp.html");
            XMLWriter writer = new XMLWriter(fwriter);
            writer.write(document);
            writer.flush();

            FileReader in = new FileReader(path + "email/temp.html");
            char[] buff = new char[10240];
            in.read(buff);
            String str = new String(buff);

            str = str.replaceAll("\000", "");
            (new MailSender.Builder(str.toString(), emailTo)).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Element getNodes(Element node, String attrName, String attrValue) {
        List<Attribute> listAttr = node.attributes();
        for (Attribute attr : listAttr) {
            String name = attr.getName();
            String value = attr.getValue();

            if (attrName.equals(name) && attrValue.equals(value)) {
                return node;
            }
        }

        List<Element> listElement = node.elements();
        for (Element e : listElement) {
            Element temp = getNodes(e, attrName, attrValue);

            if (temp != null) {
                return temp;
            }
        }
        return null;
    }

    public static void sample(String code,String email) throws Exception {
        try {
            // 如果是除杭州region外的其它region（如新加坡region）， 需要做如下处理, 这个居然是通过工单文过来的!!!
            DefaultProfile.addEndpoint("dm.ap-southeast-1.aliyuncs.com", "ap-southeast-1", "Dm", "dm.ap-southeast-1.aliyuncs.com");
        } catch (ClientException e) {
             e.printStackTrace();
             throw  e;
        }

        IClientProfile profile = DefaultProfile.getProfile("ap-southeast-1", "LTAI4GByc8pMZkotgBwnDZyM", "msX7Yaba90dalbBulTRerIW8YogAlw");
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setAccountName("push@push.jiayuhongkong.com");
//            request.setAccountName("push@push.bitpartner.com");
            request.setAddressType(1);
            request.setTagName("更改密码验证");
            request.setReplyToAddress(true);
            request.setAddressType(1);
            request.setToAddress(email);
            request.setSubject("验证码");
            request.setHtmlBody("【嘉誉投资】本次的验证码:"+code+" ，5分钟有效。");
            request.setMethod(MethodType.POST);
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
        } catch (ServerException e) {
            e.printStackTrace();
            throw e;
        } catch (ClientException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public static void regSample(Integer id,String email) throws Exception {
        try {
            // 如果是除杭州region外的其它region（如新加坡region）， 需要做如下处理, 这个居然是通过工单文过来的!!!
            DefaultProfile.addEndpoint("dm.ap-southeast-1.aliyuncs.com", "ap-southeast-1", "Dm", "dm.ap-southeast-1.aliyuncs.com");
        } catch (ClientException e) {
            e.printStackTrace();
            throw  e;
        }

        IClientProfile profile = DefaultProfile.getProfile("ap-southeast-1", "LTAI4GByc8pMZkotgBwnDZyM", "msX7Yaba90dalbBulTRerIW8YogAlw");
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setAccountName("push@push.jiayuhongkong.com");
//            request.setAccountName("push@push.bitpartner.com");
            request.setAddressType(1);
            request.setTagName("用户注册");
            request.setReplyToAddress(true);
            request.setAddressType(1);
            request.setToAddress(email);
            request.setSubject("注册信息");
            request.setHtmlBody("尊敬的用户你好，您在【嘉誉投资】网站注册已经成功。你的注册邮箱为："+email+",网站用户id为："+id+
                    "。您可以使用邮箱和用户id登录本网站，请妥善保管您的用户信息，祝您使用愉快。");
            request.setMethod(MethodType.POST);
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
        } catch (ServerException e) {
            e.printStackTrace();
            throw e;
        } catch (ClientException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public static void main(String[] args){
        try {
            sample("1111","840074185@qq.com");
        }catch ( Exception e){
            e.printStackTrace();
        }
    }
}
