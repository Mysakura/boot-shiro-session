package com.example.demo.auth;

import com.example.demo.common.entity.User;
import com.example.demo.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: boot-shiro
 * @description:
 * @author: 001977
 * @create: 2018-07-12 13:03
 */
public class PermissionRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Object principal = principalCollection.getPrimaryPrincipal();
        User user = (User) principal;
        Set<String> roles = new HashSet<>();
        roles.add("user");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roles);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken uToken = (UsernamePasswordToken) authenticationToken;

        String username = uToken.getUsername();
        String password = String.valueOf(uToken.getPassword());

        User user = userService.login(new User(username,password));

        if(user == null){
            throw new AuthenticationException("用户名密码不存在");
        }
        //认证的实体信息
        Object principal = user;
        //从数据库获取的密码
        Object hashedCredentials = user.getPassword();
        //盐值
        ByteSource credentialsSalt = ByteSource.Util.bytes(user.getUsername());
        //当前Realm对象的name，调用父类的getName方法
        String realmName = getName();

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, hashedCredentials, credentialsSalt, realmName);

        return info;
    }
}
