package com.xl.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

   @Autowired
    JedisPool jedisPool;

   public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
       Jedis jedis = null;
       try {
           jedis = jedisPool.getResource();
           key = prefix.getPrefix() + key;
           String str = jedis.get(key);
           return stringToBean(str,clazz);
       }finally {
           release(jedis);
       }
   }

   public <T> boolean set(KeyPrefix prefix,String key,T value){
       Jedis jedis = null;
       try {
           jedis = jedisPool.getResource();
           key = prefix.getPrefix() + key;
           String str = beanToString(value);
           if(str == null || str.length()<=0)
               return false;
           int expire = prefix.expireSeconds();
           if(expire <= 0){
               jedis.set(key,str);
           }else {
               jedis.setex(key,expire,str);
           }
           return true;
       }finally {
           release(jedis);
       }
   }

    public boolean exists(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            key = prefix.getPrefix() + key;
            return jedis.exists(key);
        }finally {
            release(jedis);
        }
    }

    public long del(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            key = prefix.getPrefix() + key;
            return jedis.del(key);
        }finally {
            release(jedis);
        }
    }

    public long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            key = prefix.getPrefix() + key;
            return jedis.incr(key);
        }finally {
            release(jedis);
        }
    }

    public long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            key = prefix.getPrefix() + key;
            return jedis.decr(key);
        }finally {
            release(jedis);
        }
    }


   public  static <T> T stringToBean(String str,Class<T> clazz){
       if(str == null || str.length() <= 0 || clazz == null)
           return null;
       if(clazz == int.class || clazz == Integer.class)
           return (T)Integer.valueOf(str);
       else if(clazz == long.class || clazz == Long.class)
           return (T)Long.valueOf(str);
       else if(clazz == String.class)
           return (T)str;
       else
           return JSON.toJavaObject(JSON.parseObject(str),clazz);
   }

   public  static <T> String beanToString(T value){
       if(value == null)
           return null;
       Class<?> clazz = value.getClass();
       if(clazz == int.class || clazz == Integer.class)
           return "" + value;
       else if(clazz == long.class || clazz == Long.class)
           return "" + value;
       else if(clazz == String.class)
           return "" + value;
       else
           return JSON.toJSONString(value);
   }

    public void release(Jedis jedis){
        if(jedis!=null)
            jedis.close();
    }

}
