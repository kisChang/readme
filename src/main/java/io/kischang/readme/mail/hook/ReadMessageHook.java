package io.kischang.readme.mail.hook;

import io.kischang.readme.app.utils.HttpUtils;
import io.kischang.readme.app.utils.RSSReaderUtil;
import io.kischang.readme.base.biz.UserApiBiz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MultipartImpl;
import org.apache.james.protocols.smtp.MailEnvelope;
import org.apache.james.protocols.smtp.SMTPSession;
import org.apache.james.protocols.smtp.hook.HookResult;
import org.apache.james.protocols.smtp.hook.HookReturnCode;
import org.apache.james.protocols.smtp.hook.MessageHook;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ReadMessageHook implements MessageHook {

    @Resource
    private UserApiBiz userApiBiz;

    private static final Set<String> safeMails = Set.of(
            "qq.com"
            , "foxmail.com"
            , "gmail.com"
            , "163.com"
            , "126.com"
            , "sina.com"
            , "sina.cn"
            , "inbox.com"
            , "live.com"
            , "outlook.com"
            , "189.cn"
            , "aliyun.com"
            );

    @Override
    public HookResult onMessage(SMTPSession smtpSession, MailEnvelope mailEnvelope) {
        log.info("mail from={} to={} size={}", mailEnvelope.getSender(), mailEnvelope.getRecipients(), mailEnvelope.getSize());

        // 处理消息体
        Message message;
        String mailContent;
        try {
            DefaultMessageBuilder defaultMessageBuilder = new DefaultMessageBuilder();
            message = defaultMessageBuilder.parseMessage(mailEnvelope.getMessageInputStream());
            mailContent = getMailContent(message);
        } catch (IOException e) {
            // 处理失败了，怎么处理还得考虑 TODO 暂未通过
            return HookResult.builder()
                    .hookReturnCode(HookReturnCode.declined())
                    .smtpReturnCode("DECLINED")
                    .smtpDescription("Mail Parse Error")
                    .build();
        }

        //1. 检查是否为信任邮箱
        // from 发信人 // mailEnvelope.getSender()
        if (mailEnvelope.getMaybeSender().isNullSender()) {
            //没有发信人，丢弃
            return HookResult.OK;
        }
        String domain = mailEnvelope.getMaybeSender().get()
                .getDomain().asString();
        if (!safeMails.contains(domain)) {
            //非信任邮箱源，丢弃
            return HookResult.OK;
        }
        String fullByMail = mailEnvelope.getMaybeSender().get().asString();

        //2. 正式处理
        userApiBiz.recvMailWorker(mailEnvelope.getRecipients(), fullByMail
                , message.getSubject(), mailContent);
        return HookResult.OK;
    }

    private String getMailContent(Message message) throws IOException {
        if (message.getBody() instanceof TextBody) {
            TextBody textBody = (TextBody) message.getBody();
            return IOUtils.toString(textBody.getReader());
        }
        if (message.getBody() instanceof BinaryBody) {
            BinaryBody binaryBody = (BinaryBody) message.getBody();
            return IOUtils.toString(binaryBody.getInputStream());
        }
        if (message.getBody() instanceof MessageImpl) {
            MessageImpl messageBody = (MessageImpl) message.getBody();
            return messageBody.getSubject();
        }
        if (message.getBody() instanceof MultipartImpl) {
            MultipartImpl multipart = (MultipartImpl) message.getBody();
            StringBuilder content = new StringBuilder();
            for (Entity bodyPart : multipart.getBodyParts()) {
                if (bodyPart.getBody() instanceof TextBody) {
                    //只处理文字内容
                    TextBody textBody = (TextBody) bodyPart.getBody();
                    content.append(IOUtils.toString(textBody.getReader()));
                }
            }
            return content.toString();
        }
        //TODO mail content 处理支持
        return "[not Support ToString]" + message.getBody().toString();
    }

    @Override
    public void init(org.apache.commons.configuration2.Configuration config) throws ConfigurationException {
    }

    @Override
    public void destroy() {
    }

}
