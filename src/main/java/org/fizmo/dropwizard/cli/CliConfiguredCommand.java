package org.fizmo.dropwizard.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.util.Generics;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import javax.validation.Validator;
import java.io.IOException;

public abstract class CliConfiguredCommand<T extends CliConfiguration> extends CliCommand {
    private boolean asynchronous;

    private T configuration;

    protected CliConfiguredCommand(String name, String description) {
        super(name, description);
    }

    /**
     * Returns the {@link Class} of the configuration type.
     *
     * @return the {@link Class} of the configuration type
     */
    protected Class<T> getConfigurationClass() {
        return Generics.getTypeParameter(getClass(), CliConfiguration.class);
    }

    /**
     * Configure the command's {@link net.sourceforge.argparse4j.inf.Subparser}. <p><strong> N.B.: if you override this method, you
     * <em>must</em> call {@code super.override(subparser)} in order to preserve the configuration
     * file parameter in the subparser. </strong></p>
     *
     * @param subparser the {@link net.sourceforge.argparse4j.inf.Subparser} specific to the command
     */
    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("file").nargs("?").help("application configuration file");
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void run(CliBootstrap<?> bootstrap, Namespace namespace) throws Exception {
        configuration = parseConfiguration(((CliBootstrap<T>)bootstrap).getConfigurationFactoryFactory(),
                bootstrap.getConfigurationSourceProvider(),
                bootstrap.getValidatorFactory().getValidator(),
                namespace.getString("file"),
                getConfigurationClass(),
                bootstrap.getObjectMapper());

        try {
            if (configuration != null) {
                configuration.getLoggingFactory().configure(bootstrap.getMetricRegistry(),
                        bootstrap.getApplication().getName());
            }

            run((CliBootstrap<T>) bootstrap, namespace, configuration);
        } finally {
            if (!asynchronous) {
                cleanup();
            }
        }
    }

    protected void cleanupAsynchronously() {
        this.asynchronous = true;
    }

    protected void cleanup() {
        if (configuration != null) {
            configuration.getLoggingFactory().stop();
        }
    }

    /**
     * Runs the command with the given {@link CliBootstrap} and {@link CliConfiguration}.
     *
     * @param bootstrap     the bootstrap bootstrap
     * @param namespace     the parsed command line namespace
     * @param configuration the configuration object
     * @throws Exception if something goes wrong
     */
    protected abstract void run(CliBootstrap<T> bootstrap,
                                Namespace namespace,
                                T configuration) throws Exception;

    private T parseConfiguration(ConfigurationFactoryFactory<T> configurationFactoryFactory,
                                 ConfigurationSourceProvider provider,
                                 Validator validator,
                                 String path,
                                 Class<T> klass,
                                 ObjectMapper objectMapper) throws IOException, ConfigurationException {
        final ConfigurationFactory<T> configurationFactory = configurationFactoryFactory.create(klass, validator, objectMapper, "dw");
        if (path != null) {
            return configurationFactory.build(provider, path);
        }
        return configurationFactory.build();
    }

}
