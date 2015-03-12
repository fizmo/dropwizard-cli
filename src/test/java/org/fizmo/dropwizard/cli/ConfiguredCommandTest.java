package org.fizmo.dropwizard.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;
import org.mockito.Mockito;

import javax.validation.Validator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ConfiguredCommandTest {
    private static class TestCommand extends CliConfiguredCommand<CliConfiguration> {
        protected TestCommand() {
            super("test", "test");
        }

        @Override
        protected void run(CliBootstrap<CliConfiguration> bootstrap, Namespace namespace, CliConfiguration configuration) throws Exception {
            
        }        
    }
    
    private static class MyApplication extends CliApplication<CliConfiguration> {
        @Override
        public void run(CliConfiguration configuration, CliEnvironment environment) throws Exception {
        }
    }
    
    private final MyApplication application = new MyApplication();
    private final TestCommand command = new TestCommand();
    private final CliBootstrap<CliConfiguration> bootstrap = new CliBootstrap<>(application);
    private final Namespace namespace = mock(Namespace.class);
    
    @SuppressWarnings("unchecked")
    @Test
    public void canUseCustomConfigurationFactory() throws Exception {

        ConfigurationFactory<CliConfiguration> factory = Mockito.mock(ConfigurationFactory.class);
        when(factory.build()).thenReturn(null);
        

        ConfigurationFactoryFactory<CliConfiguration> factoryFactory = Mockito.mock(ConfigurationFactoryFactory.class);
        when(factoryFactory.create(any(Class.class), any(Validator.class), any(ObjectMapper.class), any(String.class))).thenReturn(factory);
        bootstrap.setConfigurationFactoryFactory(factoryFactory);
        
        command.run(bootstrap, namespace);
        
        Mockito.verify(factoryFactory).create(any(Class.class), any(Validator.class), any(ObjectMapper.class), any(String.class));
        Mockito.verify(factory).build();
    }
}
