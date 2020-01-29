package com.manywho.services.saml.test;

import com.fiftyonred.mock_jedis.MockJedisPool;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.inject.Module;
import com.google.inject.*;
import com.google.inject.util.Modules;
import com.manywho.sdk.api.jackson.ObjectMapperFactory;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.sdk.services.ServiceApplicationModule;
import com.manywho.sdk.services.controllers.DefaultActionController;
import com.manywho.sdk.services.controllers.DefaultDescribeController;
import com.manywho.sdk.services.controllers.DefaultFileController;
import com.manywho.sdk.services.identity.AuthorizationEncoder;
import com.manywho.sdk.services.providers.AuthenticatedWhoProvider;
import com.manywho.services.saml.ApplicationBinder;
import com.manywho.services.saml.security.SecurityConfiguration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.plugins.guice.RequestScoped;
import org.junit.Before;
import org.mockito.Mockito;
import org.reflections.Reflections;
import redis.clients.jedis.JedisPool;

import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static org.mockito.Mockito.when;

public class SamlServiceFunctionalTest {

    protected Dispatcher dispatcher;
    protected Injector injector;
    protected JedisPool jedisPool;
    protected JedisPool mockJedisPool;
    protected RunClient runClient;
    protected UriInfo uriInfo;
    protected HttpHeaders httpHeadersTest;
    private AuthenticatedWhoProvider authenticatedWhoProvider;
    private SecurityConfiguration securityConfiguration;

    @Before
    public void setUp() {
        jedisPool = Mockito.mock(JedisPool.class);
        mockJedisPool= new MockJedisPool(new GenericObjectPoolConfig(), "localhost");
        runClient = Mockito.mock(RunClient.class);
        uriInfo = Mockito.mock(UriInfo.class);
        httpHeadersTest = Mockito.mock(HttpHeaders.class);
        authenticatedWhoProvider = new AuthenticatedWhoProvider(httpHeadersTest, new AuthorizationEncoder(ObjectMapperFactory.create()));
        securityConfiguration = Mockito.mock(SecurityConfiguration.class);

        when(securityConfiguration.getSecret()).thenReturn("secret");

        final List<Module> modules = Lists.newArrayList();

        modules.add(new ServiceApplicationModule("com.manywho.services.saml", true));
        modules.add(new ApplicationBinder());

        this.injector = Guice.createInjector(
                Modules.override(modules).with(createTestModule())
        );

        this.dispatcher = MockDispatcherFactory.createDispatcher();

        dispatcher.getRegistry().addSingletonResource(injector.getInstance(DefaultActionController.class));
        dispatcher.getRegistry().addSingletonResource(injector.getInstance(DefaultFileController.class));
        dispatcher.getRegistry().addSingletonResource(injector.getInstance(DefaultDescribeController.class));

        Reflections reflections = injector.getInstance(Reflections.class);

        addInstances(dispatcher, reflections.getTypesAnnotatedWith(Path.class));
        addInstances(dispatcher, reflections.getTypesAnnotatedWith(Provider.class));
    }

    private void addInstances(Dispatcher dispatcher, Set<Class<?>> classes) {
        String servicePackage = "com.manywho.services.saml";

        // Only create instances of classes that are in the service or in the SDK
        classes.stream()
                .filter(c -> c.getPackage().getName().startsWith(servicePackage) || c.getPackage().getName().startsWith("com.manywho.sdk.services"))
                .forEach(c -> dispatcher.getProviderFactory().register(injector.getInstance(c)));
    }

    protected static InputStream getFile(String fileResourcePath) {
        try {
            return Resources.getResource(fileResourcePath).openStream();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load the test file", e);
        }
    }

    protected static String getJsonFormatFileContent(String filePath) {
        return new Scanner(getFile(filePath)).useDelimiter("\\Z").next();
    }

    protected Module createTestModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(JedisPool.class).toProvider(() -> mockJedisPool);
                bind(RunClient.class).toProvider(() -> runClient);
                bind(UriInfo.class).toProvider(() -> uriInfo);
                bind(SecurityConfiguration.class).toProvider(() -> securityConfiguration);
                bind(AuthenticatedWhoProvider.class).toProvider(() -> authenticatedWhoProvider);
                bind(HttpHeaders.class).toProvider(() -> httpHeadersTest);

                bindScope(RequestScoped.class, new Scope() {
                    @Override
                    public <T> com.google.inject.Provider<T> scope(Key<T> key, com.google.inject.Provider<T> unscoped) {
                        return (com.google.inject.Provider<T>) authenticatedWhoProvider;
                    }
                });
            }
        };
    }
}
