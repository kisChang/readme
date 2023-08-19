package io.kischang.readme.mail;

import io.kischang.readme.mail.tests.RecordingMetricFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.james.protocols.api.Protocol;
import org.apache.james.protocols.api.handler.ProtocolHandler;
import org.apache.james.protocols.netty.NettyServer;
import org.apache.james.protocols.smtp.SMTPConfigurationImpl;
import org.apache.james.protocols.smtp.SMTPProtocol;
import org.apache.james.protocols.smtp.SMTPProtocolHandlerChain;
import org.springframework.lang.NonNull;

import java.net.InetSocketAddress;
import java.util.Collection;

@Slf4j
public class ReadMailServer {

    private final SmtpServerProperties properties;

    private final Collection<ProtocolHandler> handlers;

    private NettyServer smtpServer;

    public ReadMailServer(@NonNull SmtpServerProperties properties, Collection<ProtocolHandler> handlers) {
        this.properties = properties;
        this.handlers = handlers;
    }

    public void start() throws Exception {
        SMTPConfigurationImpl smtpConfiguration = new SMTPConfigurationImpl();

        SMTPProtocolHandlerChain chain = new SMTPProtocolHandlerChain(new RecordingMetricFactory());
        chain.addAll(0, handlers);
        chain.wireExtensibleHandlers();

        Protocol smtpProtocol = new SMTPProtocol(chain, smtpConfiguration);

        smtpServer = new NettyServer.Factory()
                .protocol(smtpProtocol)
                .build();
        smtpServer.setListenAddresses(new InetSocketAddress(properties.getSmtpPort()));
        smtpServer.setTimeout(properties.getTimeout());
        smtpServer.bind();
        log.info("SMTP Server started on port: {}", properties.getSmtpPort());
    }


    public void stop() {
        smtpServer.unbind();
        log.info("MAIL Server stopped on smtp port: {}", properties.getSmtpPort());
    }
}