package com.example.demo.conf;

import com.example.demo.auth.PermissionRealm;
import com.example.demo.common.entity.User;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {

    @Bean
    public RedisConfig redisConfig(){
        return new RedisConfig();
    }

    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();     // crazycake 实现
        redisManager.setHost(redisConfig().getHost());
        redisManager.setTimeout(redisConfig().getTimeout());
        return redisManager;
    }

    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator(){
        return new JavaUuidSessionIdGenerator();
    }

    @Bean
    public RedisSessionDAO sessionDAO(){
        RedisSessionDAO sessionDAO = new RedisSessionDAO(); // crazycake 实现
        sessionDAO.setRedisManager(redisManager());
        sessionDAO.setSessionIdGenerator(sessionIdGenerator()); //  Session ID 生成器
        return sessionDAO;
    }

    @Bean
    public SimpleCookie cookie(){
        SimpleCookie cookie = new SimpleCookie("SHAREJSESSIONID"); //  cookie的name,对应的默认是 JSESSIONID
        cookie.setHttpOnly(true);
        cookie.setPath("/");        //  path为 / 用于多个系统共享JSESSIONID
        return cookie;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(redisConfig().getTimeout());    // 设置session超时
        sessionManager.setDeleteInvalidSessions(true);      // 删除无效session
        sessionManager.setSessionIdCookie(cookie());            // 设置JSESSIONID
        sessionManager.setSessionDAO(sessionDAO());         // 设置sessionDAO
        return sessionManager;
    }

    /**
     * 1. 配置SecurityManager
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());  // 设置realm
        securityManager.setSessionManager(sessionManager());    // 设置sessionManager
//        securityManager.setCacheManager(redisCacheManager()); // 配置缓存的话，退出登录的时候crazycake会报错，要求放在session里面的实体类必须有个id标识
        return securityManager;
    }

    /**
     * 2. 配置缓存
     * @return
     */
//    @Bean
//    public CacheManager cacheManager(){
//        EhCacheManager ehCacheManager = new EhCacheManager();
//        ehCacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
//        return ehCacheManager;
//    }

    @Bean
    public RedisCacheManager redisCacheManager(){
        RedisCacheManager cacheManager = new RedisCacheManager();   // crazycake 实现
        cacheManager.setRedisManager(redisManager());
        return cacheManager;
    }

    /**
     * 3. 配置Realm
     * @return
     */
    @Bean
    public AuthorizingRealm realm(){
        PermissionRealm realm = new PermissionRealm();
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        // 指定加密算法
        matcher.setHashAlgorithmName("MD5");
        // 指定加密次数
        matcher.setHashIterations(10);
        // 指定这个就不会报错
        matcher.setStoredCredentialsHexEncoded(true);
        realm.setCredentialsMatcher(matcher);
        return realm;
    }

    /**
     * 4. 配置LifecycleBeanPostProcessor，可以来自动的调用配置在Spring IOC容器中 Shiro Bean 的生命周期方法
     * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 5. 启用IOC容器中使用Shiro的注解，但是必须配置第四步才可以使用
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        return new DefaultAdvisorAutoProxyCreator();
    }

    /**
     * 6. 配置ShiroFilter
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(){
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        // 静态资源
        map.put("/css/**", "anon");
        map.put("/js/**", "anon");

        // 公共路径
        map.put("/login", "anon");
        map.put("/register", "anon");
        //map.put("/*", "anon");

        // 登出,项目中没有/logout路径,因为shiro是过滤器,而SpringMVC是Servlet,Shiro会先执行
        map.put("/logout", "logout");

        // 授权
        map.put("/user/**", "authc,roles[user]");
        map.put("/admin/**", "authc,roles[admin]");

        // everything else requires authentication:
        map.put("/**", "authc");

        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 配置SecurityManager
        factoryBean.setSecurityManager(securityManager());
        // 配置权限路径
        factoryBean.setFilterChainDefinitionMap(map);
        // 配置登录url
        factoryBean.setLoginUrl("/");
        // 配置无权限路径
        factoryBean.setUnauthorizedUrl("/unauthorized");
        return factoryBean;
    }

    /**
     * 配置RedisTemplate，充当数据库服务
     * @return
     */
    @Bean
    public RedisTemplate<String,User> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String,User> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));
        return redisTemplate;
    }

}
