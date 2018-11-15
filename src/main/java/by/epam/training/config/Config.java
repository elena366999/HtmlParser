package by.epam.training.config;

import by.epam.training.resolver.CustomHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
public class Config extends WebMvcConfigurerAdapter {

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CustomHandlerMethodArgumentResolver());
    }

    @PostConstruct
    public void prioritizeCustomArgumentMethodHandlers() {
        List<HandlerMethodArgumentResolver> argumentResolvers =
                new ArrayList<>(Objects.requireNonNull(adapter.getArgumentResolvers()));
        List<HandlerMethodArgumentResolver> customResolvers =
                adapter.getCustomArgumentResolvers();
        argumentResolvers.removeAll(Objects.requireNonNull(customResolvers));
        argumentResolvers.addAll(0, customResolvers);
        adapter.setArgumentResolvers(argumentResolvers);
    }

}
