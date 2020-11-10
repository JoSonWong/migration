package com.bestarmedia.migration.config;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.bestarmedia.migration.repository.mongo.vod", mongoTemplateRef = "vodMongoTemplate")
public class VodMongoTemplate {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    @Qualifier("vodMongoProperties")
    private MongoProperties vodMongoProperties;

    @Primary
    @Bean(name = "vodMongo")
    public MongoTemplate vodMongoTemplate() throws Exception {
        MongoDbFactory factory = vodFactory(this.vodMongoProperties);
        MongoMappingContext mongoMappingContext = new MongoMappingContext();
        mongoMappingContext.setApplicationContext(appContext);
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(factory), mongoMappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(factory, converter);
    }

    @Bean
    @Primary
    public MongoDbFactory vodFactory(MongoProperties mongoProperties) throws Exception {
        ServerAddress serverAddress = new ServerAddress(mongoProperties.getUri());
        return new SimpleMongoDbFactory(new MongoClient(serverAddress), mongoProperties.getDatabase());
    }


//    @Bean
//    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, BeanFactory beanFactory) {
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
//        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
//        try {
//            mappingConverter.setCustomConversions(beanFactory.getBean(MongoCustomConversions.class));
//        } catch (NoSuchBeanDefinitionException ignore) {
//            ignore.printStackTrace();
//        }
//        // Don't save _class to mongo
//        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
//        return mappingConverter;
//    }

//    @Bean
//    public MongoMappingContext mongoMappingContext() throws ClassNotFoundException {
//        MongoMappingContext mappingContext = new MongoMappingContext();
////        mappingContext.setInitialEntitySet(getInitialEntitySet());
////        mappingContext.setSimpleTypeHolder(customConversions().getSimpleTypeHolder());
////        mappingContext.setFieldNamingStrategy(fieldNamingStrategy());
//        return mappingContext;
//    }

//    @Bean
//    public MappingMongoConverter mappingMongoConverter() throws Exception {
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(songFactory(this.mongoProperties));
//        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
//        converter.setCustomConversions(customConversions());
//        return converter;
//    }

//    @Bean
//    public CustomConversions customConversions() {
//        return new CustomConversions(Collections.emptyList());
//    }
//
//    protected Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {
//        String basePackage = getMappingBasePackage();
//        Set<Class<?>> initialEntitySet = new HashSet<Class<?>>();
//        if (StringUtils.hasText(basePackage)) {
//            ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
//                    false);
//            componentProvider.addIncludeFilter(new AnnotationTypeFilter(Document.class));
//            componentProvider.addIncludeFilter(new AnnotationTypeFilter(Persistent.class));
//
//            for (BeanDefinition candidate : componentProvider.findCandidateComponents(basePackage)) {
//                initialEntitySet.add(ClassUtils.forName(candidate.getBeanClassName(),
//                        AbstractMongoConfiguration.class.getClassLoader()));
//            }
//        }
//
//        return initialEntitySet;
//
//    }
//
//    protected String getMappingBasePackage() {
//        Package mappingBasePackage = getClass().getPackage();
//        return mappingBasePackage == null ? null : mappingBasePackage.getName();
//    }
//
//    protected FieldNamingStrategy fieldNamingStrategy() {
//        return abbreviateFieldNames() ? new CamelCaseAbbreviatingFieldNamingStrategy()
//                : PropertyNameFieldNamingStrategy.INSTANCE;
//    }
//
//    protected boolean abbreviateFieldNames() {
//        return false;
//    }
}
