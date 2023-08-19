package io.kischang.readme.mail.hook;

import lombok.extern.slf4j.Slf4j;
import org.apache.james.core.Username;
import org.apache.james.protocols.api.OidcSASLConfiguration;
import org.apache.james.protocols.smtp.SMTPSession;
import org.apache.james.protocols.smtp.hook.AuthHook;
import org.apache.james.protocols.smtp.hook.HookResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReadAuthHook implements AuthHook {

    @Override
    public HookResult doAuth(SMTPSession smtpSession, Username username, String password) {
        log.info("MAIL AUTH, username:{}  password:{}", username, password);
        return HookResult.OK;
    }

    @Override
    public HookResult doSasl(SMTPSession smtpSession, OidcSASLConfiguration oidcSASLConfiguration, String initialResponse) {
        return HookResult.OK;
    }

}
