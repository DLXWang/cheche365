package com.cheche365.cheche.web.app.config;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.message.IPFilterMessage;
import com.cheche365.cheche.core.message.PartnerOrderMessage;
import com.cheche365.cheche.core.message.TMLoginUserMessage;
import com.cheche365.cheche.core.repository.QuotePhotoRepository;
import com.cheche365.cheche.core.repository.UserImgRepository;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.core.service.image.ImgUploadService;
import com.cheche365.cheche.core.service.image.QuotePhoneUploadService;
import com.cheche365.cheche.core.util.ProfileProperties;
import com.cheche365.cheche.parser.service.ReferencedThirdPartyHandlerService;
import com.cheche365.cheche.rest.jsonfilter.AlipayCharacterEncodingFilter;
import com.cheche365.cheche.rest.jsonfilter.FilteringJackson2HttpMessageConverter;
import com.cheche365.cheche.rest.listener.RedisIpFilterListener;
import com.cheche365.cheche.rest.listener.RedisSyncOrderListener;
import com.cheche365.cheche.rest.listener.RedisUserLoginListener;
import com.cheche365.cheche.rest.serverpush.SPQuotesService;
import com.cheche365.cheche.rest.session.MyRedisHttpSessionConfiguration;
import com.cheche365.cheche.rest.web.session.PersistentSessionStrategy;
import com.cheche365.cheche.web.filter.BodyReadFilter;
import com.cheche365.cheche.web.version.APIVersionMappingHandlerMapping;
import feign.RequestTemplate;
import feign.codec.Encoder;
import groovy.json.JsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.cpr.MetaBroadcaster;
import org.atmosphere.plugin.redis.RedisBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mobile.device.DeviceResolverRequestFilter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static com.cheche365.cheche.core.model.QuoteSource.Enum.REFERENCED_7;
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._INSURANCE_COMPANY_RULE_PICC_MAPPINGS;
import static java.util.concurrent.Executors.newCachedThreadPool;


/**
 * Web App配置
 */
@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@EnableSpringDataWebSupport
@Import(MyRedisHttpSessionConfiguration.class)
@ComponentScan({
    // 主要业务
    "com.cheche365.cheche.rest",
    "com.cheche365.cheche.web.app.config",
    "com.cheche365.cheche.webarchive.app.config",
    "com.cheche365.cheche.marketing.app.config",

    // 功能依赖
    "com.cheche365.cheche.core.app.config",
    "com.cheche365.cheche.sms.client.app.config",
    "com.cheche365.cheche.ccint.app.config",

    // 合作方
    "com.cheche365.cheche.partner.config.app",
    "com.cheche365.cheche.partner.config",

    // 支付接口
    "com.cheche365.cheche.alipay.app.config",
    "com.cheche365.cheche.unionpay.app.config",
	"com.cheche365.cheche.externalpayment.config",

    // 微信（含支付接口）
    "com.cheche365.cheche.wechat.app.config",

    // 钱包
    "com.cheche365.cheche.wallet.app.config",

    //联动优势支付
    "com.cheche365.cheche.soopay.app.config",

    // 自主parser
    "com.cheche365.cheche.chinalife.app.config",
    "com.cheche365.cheche.cpic.app.config",
    "com.cheche365.cheche.picc.client.app.config",
    "com.cheche365.cheche.pingan.app.config",
    "com.cheche365.cheche.pinganuk.app.config",
    "com.cheche365.cheche.sinosig.app.config",
    "com.cheche365.cheche.cpicuk.client.app.config",
    "com.cheche365.cheche.piccuk.client.app.config",

    // 协作API
    "com.cheche365.cheche.answern.app.config",
    "com.cheche365.cheche.baoxian.app.config",
    "com.cheche365.cheche.bihu.app.config",
	"com.cheche365.cheche.zhongan.app.config",
    "com.cheche365.cheche.sinosafe.app.config",
    "com.cheche365.cheche.botpy.client.app.config",
    "com.cheche365.cheche.taikang.app.config",
    "com.cheche365.cheche.huanong.config",
    "com.cheche365.cheche.aibao.app.config",
    // 微服务
    "com.cheche365.springcmp.app.config",
    "com.cheche365.cheche.manage.common.app.config",
    "com.cheche365.pushmessage.api.app.config",

    // 辅助测试（只在非生产环境生效，其他环境不去compile mock项目）
    "com.cheche365.cheche.mock.app.config"

})
@ImportResource({
    "classpath:META-INF/spring/web-context.xml"
})
public class WebConfig implements ServletContextInitializer {

    private final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    RedisUserLoginListener redisUserLoginListener;

    @Autowired
    RedisSyncOrderListener redisSyncOrderListener;

    @Autowired
    RedisIpFilterListener redisIpFilterListener;

    /**
     * 所有参考服务都用这个bean<br>
     * 人保自己也要包装一层人造服务，以实现在参考报价方式下报价失败时根据规则给出人造报价
     * @param _appContextHolder 这没用，加这个是为了等待QuoteSource静态常量正确地初始化完毕
     * @param piccService 人保服务（委派）
     * @param services 真实服务列表
     * @return 被真实、人造和参考服务包装后的保险公司服务
     */
    @Bean
    public IThirdPartyHandlerService thirdPartyHandlerAndRefPiccService(
            ApplicationContextHolder _appContextHolder, // <-- 绝对不许删，看上面javadoc
            @Qualifier("piccThirdPartyHandlerService") IThirdPartyHandlerService piccService,
            List<IThirdPartyHandlerService> services
    ) {
        return new ReferencedThirdPartyHandlerService(
                REFERENCED_7, piccService, services,
                _INSURANCE_COMPANY_RULE_PICC_MAPPINGS
        );
    }

    @Bean
    public IConfigService fileBasedConfigService(Environment env) {
        return new FileBasedConfigService(env);
    }

    @Bean
    public ExecutorService quotingExecutorService() {
        return newCachedThreadPool(new BasicThreadFactory.Builder().namingPattern("quoting-%d").build());
    }

    @Bean
    @Order(4)
    public ImgUploadService quotePhoneUploadService(UserImgRepository userImgRepository,
                                                    QuotePhotoRepository quotePhotoRepository,
                                                    @Qualifier("ccintService") IOCRService ocrService,
                                                    IAutoVehicleLicenseService autoVehicleLicenseService,
                                                    SupplementInfoService supplementInfoService) {
        return new QuotePhoneUploadService(userImgRepository, quotePhotoRepository, ocrService, autoVehicleLicenseService, supplementInfoService);
    }

    @Bean
    public Filter deviceResolverRequestFilter() {
        return new DeviceResolverRequestFilter();
    }

    @Bean
    @Order(value = 0)
    public FilterRegistrationBean sessionRepositoryFilterRegistration(SessionRepositoryFilter springSessionRepositoryFilter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new DelegatingFilterProxy(springSessionRepositoryFilter));
        filterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));
        return filterRegistrationBean;
    }


    @Bean
    public FilterRegistrationBean alipayEncodingFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new DelegatingFilterProxy(new AlipayCharacterEncodingFilter()));
        filterRegistrationBean.setUrlPatterns(Collections.singletonList("/web/alipay/channels/gateway"));
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean bodyReadFilterBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/api/callback/pingpp/*","/api/public/*"));
        filterRegistrationBean.setFilter(new BodyReadFilter());
        return filterRegistrationBean;
    }

    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }



    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        APIVersionMappingHandlerMapping handlerMapping = new APIVersionMappingHandlerMapping();
        handlerMapping.setOrder(0);
        handlerMapping.setRemoveSemicolonContent(false);
        return handlerMapping;
    }

    @Bean
    PersistentSessionStrategy sessionStrategy() {
        return new PersistentSessionStrategy();
    }


    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(5);
        pool.setMaxPoolSize(10);
        pool.setWaitForTasksToCompleteOnShutdown(false);
        pool.setDaemon(true);
        return pool;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        //一共为4M
        return new MultipartConfigElement(null, 1024 * 1024 * 10, 1024 * 1024 * 10, 0);
    }

    @Configuration
    @AutoConfigureAfter(WebConfig.class)
    public static class WebSecurityConfigurerAdapterAutoConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.httpBasic().disable();
            http.csrf().disable();
            http.anonymous();
            http.headers().frameOptions().disable();
            http.headers().cacheControl().disable();
        }

    }

    @Configuration
    @AutoConfigureAfter(WebConfig.class)
    public static class WebMvcConfigurerAdapterAutoConfig extends WebMvcConfigurerAdapter {


        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {  //以下converts的顺序不要变，否则与微信交互会有问题
            StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
            stringConverter.setWriteAcceptCharset(false);

            converters.add(stringConverter);
            converters.add(new FilteringJackson2HttpMessageConverter());

            super.configureMessageConverters(converters);
        }

        public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
            if(null != configurer){
                configurer.enable();
            }
        }

    }

    //=====================================atmosphere related==============================================


    @Bean
    public AtmosphereServlet atmosphereServlet() {
        return new AtmosphereServlet();
    }

    @Bean
    public AtmosphereFramework atmosphereFramework() {
        return atmosphereServlet().framework();
    }

    @Bean
    public MetaBroadcaster metaBroadcaster() {
        AtmosphereFramework framework = atmosphereFramework();
        return framework.metaBroadcaster();
    }


    @Bean
    public RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        container.addMessageListener(new MessageListenerAdapter(this.redisUserLoginListener),
            new ChannelTopic(TMLoginUserMessage.QUEUE_NAME));
        container.addMessageListener(new MessageListenerAdapter(this.redisSyncOrderListener),
            new ChannelTopic(PartnerOrderMessage.QUEUE_NAME));
        container.addMessageListener(new MessageListenerAdapter(this.redisIpFilterListener),
                new ChannelTopic(IPFilterMessage.QUEUE_NAME));
        return container;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        //forbid to list directory
        servletContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        configureAtmosphere(atmosphereServlet(), servletContext);
    }

    private void configureAtmosphere(AtmosphereServlet servlet, final ServletContext servletContext) {
        ServletRegistration.Dynamic atmosphereServlet = servletContext.addServlet("atmosphereServlet", servlet);
        final Properties parameters = new Properties();
        parameters.put(ApplicationConfig.ANNOTATION_PACKAGE, SPQuotesService.class.getPackage().getName());
        parameters.put(ApplicationConfig.BROADCASTER_CACHE, UUIDBroadcasterCache.class.getName());
        parameters.put(ApplicationConfig.BROADCASTER_SHARABLE_THREAD_POOLS, "true");
        parameters.put(ApplicationConfig.BROADCASTER_MESSAGE_PROCESSING_THREADPOOL_MAXSIZE, "10");
        parameters.put(ApplicationConfig.BROADCASTER_ASYNC_WRITE_THREADPOOL_MAXSIZE, "10");
        parameters.put(ApplicationConfig.BROADCASTER_CLASS, "com.cheche365.cheche.web.app.config.WebBroadcaster");
        parameters.put(ApplicationConfig.CLIENT_HEARTBEAT_INTERVAL_IN_SECONDS, "10");

        Properties properties = new Properties();
        try {
            properties.load(ProfileProperties.class.getResourceAsStream("/META-INF/spring/redis.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        properties.putAll(System.getProperties());
        ProfileProperties profileProperties = new ProfileProperties(properties);
        String host = profileProperties.getProperty("redis_host", "localhost");
        String port = profileProperties.getProperty("redis_port", "6379");
        String password = profileProperties.getProperty("redis_password", "");
        parameters.put(RedisBroadcaster.class.getName() + ".server", "http://" + host + ":" + port);
        if (StringUtils.isNotBlank(password)) {
            parameters.put(RedisBroadcaster.class.getName() + ".authorization", password);
        }
        logger.debug("Atmosphere redis broadcaster connect to {}", parameters.get(RedisBroadcaster.class.getName() + ".server"));

        try {
            servlet.init(new ServletConfig() {
                @Override
                public String getServletName() {
                    return "atmosphereServlet";
                }

                @Override
                public ServletContext getServletContext() {
                    return servletContext;
                }

                @Override
                public String getInitParameter(String s) {
                    return parameters.getProperty(s);
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return (Enumeration) parameters.keys();
                }
            });
        } catch (ServletException e) {
            e.printStackTrace();
        }
        servletContext.addListener(new org.atmosphere.cpr.SessionSupport());
        atmosphereServlet.addMapping("/sp/*");
        atmosphereServlet.setLoadOnStartup(0);
        atmosphereServlet.setAsyncSupported(true);
    }

    //===========================================atmosphere end=================================================


}

