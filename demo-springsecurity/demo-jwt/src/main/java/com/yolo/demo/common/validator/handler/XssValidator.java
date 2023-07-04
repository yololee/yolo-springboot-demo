package com.yolo.demo.common.validator.handler;


import cn.hutool.core.util.StrUtil;
import com.yolo.demo.common.validator.annotation.Xss;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 自定义xss校验注解实现
 *
 * @author ruoyi
 */
@Slf4j
public class XssValidator implements ConstraintValidator<Xss, String>{
//    private static final String HTML_PATTERN = "<(\\S*?)[^>]*>.*?|<.*? />";

    /**
     * xss脚本正则
     */
    private final static Pattern[] SCRIPT_PATTERNS = {
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isBlank(value)) {
            return true;
        }
        return cleanXss(value);
    }

    /**
     * 清除xss
     */
    public Boolean cleanXss(String src) {
        String temp = src;
        // 校验xss脚本
        for (Pattern pattern : SCRIPT_PATTERNS) {
            temp = pattern.matcher(temp).replaceAll("");
        }
        // 校验xss特殊字符
        temp = temp.replaceAll("\0|\n|\r", "");
        temp = temp.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        if (!temp.equals(src)) {
            log.error("xss攻击检查：参数含有非法攻击字符，已禁止继续访问！！");
            log.error("原始输入信息-->" + temp);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}