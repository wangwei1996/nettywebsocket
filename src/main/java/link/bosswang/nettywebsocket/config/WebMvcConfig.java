package link.bosswang.nettywebsocket.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * 项目配置
 * 定义Spring Boot的视图解析器，和静态资源处理器,文件上传解析器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 定义视图解析器
     *
     * @return 视图解析器
     */
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/webapp/pages/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    /**
     * 静态资源映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/webapp/resources/");
    }

    /**
     * 定义文件上传解析器
     *
     * @return 文件上传解析器
     */
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");
        //推迟文件解析，在uploadAction中捕获文件大小异常
        multipartResolver.setResolveLazily(true);
        multipartResolver.setMaxInMemorySize(40960);
        //上传文件大小 50M
        multipartResolver.setMaxUploadSize(50 * 1024 * 1024);

        return multipartResolver;
    }

}
