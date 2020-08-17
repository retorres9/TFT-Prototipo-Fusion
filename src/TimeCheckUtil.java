import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 13-11-22
 * Time: 上午10:43
 * To change this template use File | Settings | File Templates.
 */
public class TimeCheckUtil {

    /**
     * 正则验证
     * @param el
     * @param value
     * @return
     */
    public static Boolean commonPattern(String el, String value){
        Pattern p = Pattern.compile(el);
        Matcher m = p.matcher(value);
        boolean b = m.matches();
        return b;
    }

    /**
     * 查找是否包含el字符
     * @param el
     * @param value
     * @return
     */
    public static Boolean commonPatternFind(String el, String value){
        Pattern p = Pattern.compile(el);
        Matcher m = p.matcher(value);
        return m.find(0);
    }


    /**
     * 检查日期
     * @param date
     * @return
     */
    public static Boolean checkDate(String date){
        String el = ".{0,}((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|" +
                "(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|" +
                "[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|" +
                "[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)).{0,}";
//        String el = ".{0,}((((1[6-9]|[2-9]\\d)\\d{2})[-|/]*(0?[13578]|1[02])[-|/]*(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})[-|/]*(0?[13456789]|1[012])[-|/]*(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})[-|/]*0?2[-|/]*(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))[-|/]*0?2[-|/]*29[-|/]*)).{0,}";
        return commonPattern(el, date);
    }

    /**
     * 检查日期时间
     * @param dateTime
     * @return
     */
    public static Boolean checkDateTime(String dateTime){
        String el = ".{0,}((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|" +
                "[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])" +
                "-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|" +
                "2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]" +
                "|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d.{0,}";
        return commonPattern(el, dateTime);
    }

    /**
     * 检查时间
     * @param time
     * @return
     */
    public static Boolean checkTime(String time){
        String el = ".{0,}(20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d.{0,}";
        return commonPattern(el, time);
    }

    /**
     * 检查星期
     * @param week
     * @return
     */
    public static Boolean checkWeek(String week){
        String el = "([下|本|上]+周)|(周[一|二|三|四|五|六|日|天]+)|(星期[一|二|三|四|五|六|七|日|天]+)";
        return commonPatternFind(el, week);
    }

    /**
     * 检查其他
     * @param others
     * @return
     */
    public static Boolean checkOthers(String others){
        String el = "([前|昨|明|后]+天)";
        return commonPatternFind(el, others);
    }


    public static void main(String[] args){
        String date = "星期天一2013-2-下3下去";
//        String date = "星期天一2007229下去";
//        String date = "星期天一2013/12/下122`下13下去";
        System.out.println(checkDate(date));
        String time = "星期天一12:23:23下去";
        System.out.println(checkTime(time));
        String dateTime = "星期天一2013-12-13 12:23:23111下去";
        System.out.println(checkDateTime(dateTime));
        String week = "星期天一111下去";
        System.out.println(checkWeek(week));
        String others = "后天11下去";
        System.out.println(checkOthers(others));

    }

}