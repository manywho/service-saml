package com.manywho.services.saml.factories;

import com.manywho.services.saml.security.RedisConfiguration;

import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import javax.inject.Provider;

public class JedisPoolFactory implements Provider<JedisPool> {

    private RedisConfiguration redisConfiguration;

    @Inject
    JedisPoolFactory(RedisConfiguration redisConfiguration) {
        this.redisConfiguration = redisConfiguration;
    }

    @Override
    public JedisPool get() {
        return new JedisPool(redisConfiguration.geRedisUrl());
    }
}
