package com.manywho.services.saml.health;

import com.manywho.sdk.services.health.HealthHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;

public class SamlHealthHandler implements HealthHandler {
    private final JedisPool jedisPool;

    @Inject
    public SamlHealthHandler(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public boolean isHealthy() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
        }

        return true;
    }
}
