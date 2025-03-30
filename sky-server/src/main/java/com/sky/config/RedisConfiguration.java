package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean    //目标是生成Redis模版对象redisTemplate，并注入IOC容器当中，用于操作Redis数据库
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){    //从IOC容器中自动注入连接工厂对象
        log.info("开始创建redis模版对象...");
        RedisTemplate redisTemplate = new RedisTemplate();
        // 设置redis的连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置redis key的序列化器，保证Java操作Redis时传入的字符串key不乱码（乱码也不影响，只是看不懂）
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
