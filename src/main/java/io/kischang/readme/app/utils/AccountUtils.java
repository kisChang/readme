package io.kischang.readme.app.utils;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class AccountUtils {

    private static SimpleDateFormat datetimeFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str));
    }

    //邮箱正则
    public static final Pattern EmailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    //密码正则
    public static final Pattern PasswordPattern = Pattern.compile("^[0-9a-zA-Z!@#$%^&*()-=_.]{6,20}$");
    //账号正则
    public static final Pattern UsernamePattern = Pattern.compile("^[0-9a-zA-Z-=_.]{6,20}$");
    //昵称正则
    public static final Pattern NicknamePattern = Pattern.compile("^[0-9a-zA-Z!@#$%^&*()-=_.\\u4e00-\\u9fa5]{1,20}$");
    //个性签名正则
    public static final Pattern SignaturePattern = Pattern.compile("^[0-9a-zA-Z！￥，。？×!@#$%^&*()-=_.\\u4e00-\\u9fa5]{0,50}$");
    //包含数字
    public static final Pattern NumPattern = Pattern.compile("[\\d]+");
    //包含字符
    public static final Pattern WordPattern = Pattern.compile("[a-zA-Z]+");
    //包含特殊符号
    public static final Pattern SymbolPattern = Pattern.compile("[!@#$%^&*()_.]+");
    //包含特殊符号
    public static final Pattern PhonePattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");

    public static boolean checkPhone(String phone) {
        return !isEmpty(phone) && PhonePattern.matcher(phone).matches();
    }

    public static boolean checkEmail(String email) {
        return !isEmpty(email) && EmailPattern.matcher(email).matches();
    }

    /**
     * 检测昵称是否符合规则（格式要求：1-20位字母、!@#$%^&*()_.、数字和汉字的组合）
     *
     * @param nickname 昵称
     * @return true符合 false不符合
     */
    public static boolean checkNickname(String nickname) {
        return NicknamePattern.matcher(nickname).matches();
    }

    /**
     * 检测帐号名是否符合规则（格式要求：6-20位字母和数字的组合）
     *
     * @param username 账号
     * @return true符合 false不符合
     */
    public static boolean checkUsername(String username) {
        return UsernamePattern.matcher(username).matches();
    }

    /**
     * 检查密码是否符合规定的格式（格式要求：^[0-9a-zA-Z!@#$%^&*()_.]{6,20}）
     *
     * @param password 密码
     * @return true符合 false不符合
     */
    public static boolean checkPassword(String password) {
        if (PasswordPattern.matcher(password).matches()) {   //必须符合基础密码正则
            int con = 0;
            //且必须包含数字、字符、符号中的两种
            if (NumPattern.matcher(password).find()) {
                con++;
            }
            if (WordPattern.matcher(password).find()) {
                con++;
            }
            if (SymbolPattern.matcher(password).find()) {
                con++;
            }
            if (con >= 2) {
                return true;
            }
        }
        return false;
    }


    public static boolean checkSignature(String signature) {
        if (signature == null || "".equals(signature)){
            return true;
        }
        return SignaturePattern.matcher(signature).matches();
    }

}
