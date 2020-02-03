package com.herokuapp.psxapi.config;


import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.InetSocketAddress;

@Configuration
@Slf4j
public class MemcachedClientConfig {

    @Bean
    @Profile("local")
    public MemcachedClient devMemCachedClient() {
        try {
            return new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
        } catch (Exception e) {
            log.info("Unable to initialize local memcached.");
        }
        return null;
    }


    @Bean
    @Profile("!local")
    public MemcachedClient memcachedClient() {
        try {
            AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"},
                    new PlainCallbackHandler(System.getenv("MEMCACHEDCLOUD_USERNAME"), System.getenv("MEMCACHEDCLOUD_PASSWORD")));
            MemcachedClient mc = new MemcachedClient(
                    new ConnectionFactoryBuilder()
                            .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                            .setAuthDescriptor(ad).build(),
                    AddrUtil.getAddresses(System.getenv("MEMCACHEDCLOUD_SERVERS")));
            return mc;
        } catch (Exception e) {
            log.info("Unable to initialize prod memcached.");
        }
        return null;
    }

}
