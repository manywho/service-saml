package com.manywho.services.saml;

import com.google.inject.AbstractModule;
import com.manywho.services.saml.factories.JedisPoolFactory;
import com.manywho.services.saml.factories.JwtServiceFactory;
import com.manywho.services.saml.services.JwtService;
import redis.clients.jedis.JedisPool;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractModule {
    @Override
    protected void configure() {
        bind(JwtService.class).toProvider(JwtServiceFactory.class).in(Singleton.class);
        bind(JedisPool.class).toProvider(JedisPoolFactory.class).in(Singleton.class);
    }
}
