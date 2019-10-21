package com.ppdai.platform.das.codegen.config;


import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 增加方法注入，将含有 @CurrentUser 注解的方法参数注入当前登录用户
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String UNKNOWN = "unknown";
    public static final Long UNKNOWNID = 0L;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(LoginUser.class) && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        LoginUser user = (LoginUser) webRequest.getAttribute("currentUser", RequestAttributes.SCOPE_SESSION);
        if (user == null) {
            return LoginUser.builder()
                    .id(UNKNOWNID)
                    .userNo(UNKNOWN)
                    .userName(UNKNOWN)
                    .userEmail(UNKNOWN)
                    .build();
        }
        return user;
        //throw new MissingServletRequestPartException("currentUser");
    }
}
