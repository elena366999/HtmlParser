package by.epam.training.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public final class CustomHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(SkipCacheCheck.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        String skipCacheCheck = nativeWebRequest.getParameter("skipCacheCheck");

        if (skipCacheCheck != null && !skipCacheCheck.equals("true")) {
            skipCacheCheck = "false";
        }

        return Boolean.valueOf(skipCacheCheck);
    }
}
