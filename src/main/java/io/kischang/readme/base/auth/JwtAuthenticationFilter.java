package io.kischang.readme.base.auth;

import io.jsonwebtoken.Claims;
import io.kischang.readme.app.utils.JacksonUtils;
import io.kischang.readme.base.bean.R;
import io.kischang.readme.user.dao.UserAccountDao;
import io.kischang.readme.user.dao.UserDetailServiceImpl;
import io.kischang.readme.user.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    @Autowired
    UserDetailServiceImpl userDetailService;
    @Autowired
    UserAccountDao sysUserService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = request.getHeader(JwtUtils.getHeader());
        // 这里如果没有jwt，继续往后走，因为后面还有鉴权管理器等去判断是否拥有身份凭证，所以是可以放行的
        // 没有jwt相当于匿名访问，若有一些接口是需要权限的，则不能访问这些接口
        if (StringUtils.isEmpty(jwt)) {
            chain.doFilter(request, response);
            return;
        }
        Claims claim = JwtUtils.getClaimsByToken(jwt);
        if (claim == null) {
            responseError(response, R.fail(403, "token 异常"));
            return;
        }
        if (JwtUtils.isTokenExpired(claim)) {
            responseError(response, R.fail(403, "token 已过期"));
            return;
        }
        String username = claim.getSubject();
        // 获取用户的权限等信息
        UserAccount sysUser = sysUserService.findByUsername(username);
        if (sysUser == null) {
            responseError(response, R.fail(403, "token 已过期"));
            return;
        }
        // 构建UsernamePasswordAuthenticationToken,这里密码为null，是因为提供了正确的JWT,实现自动登录
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(sysUser.getUsername(), sysUser.getPassword(), Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(request, response);
    }


    public static void responseError(HttpServletResponse response, R<?> ret) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            PrintWriter out = response.getWriter();
            out.write(JacksonUtils.obj2Json(ret));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}