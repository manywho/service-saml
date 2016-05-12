package com.manywho.services.saml.managers;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;

public class CacheManager {
    public final static String REDIS_KEY_USER_GROUPS = "service:saml:user:%s:groups";

    private JedisPool jedisPool;

    private ObjectMapper objectMapper;

    @Inject
    public CacheManager(JedisPool jedisPool, ObjectMapper objectMapper) {
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
    }

    public ArrayList getUserGroups(String userId) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_KEY_USER_GROUPS, userId));

            if (StringUtils.isNotEmpty(json)) {
               return objectMapper.readValue(json, ArrayList.class);
            }
        }

        return new ArrayList<>();
    }

    public void removeUserGroups(String userId) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(String.format(REDIS_KEY_USER_GROUPS, userId));
        }
    }

    public void saveUserGroups(String userid, ArrayList<String> groups) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(String.format(REDIS_KEY_USER_GROUPS, userid), objectMapper.writeValueAsString(groups));
        }
    }
}
