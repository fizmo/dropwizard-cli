package org.fizmo.dropwizard.cli;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.validation.valuehandling.OptionalValidatedValueUnwrapper;
import org.hibernate.validator.HibernateValidator;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.lang.management.ManagementFactory;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CliBootstrap<T extends CliConfiguration> {
    private final CliApplication<T> application;
    private final ObjectMapper objectMapper;
    private final List<CliCommand> commands;
    private final MetricRegistry metricRegistry;

    private ConfigurationSourceProvider configurationSourceProvider;
    private ConfigurationFactoryFactory<T> configurationFactoryFactory;
    private ClassLoader classLoader;
    private ValidatorFactory validatorFactory;

    public CliBootstrap(CliApplication<T> application) {
        this.application = application;
        this.objectMapper = Jackson.newObjectMapper();
        this.commands = Lists.newArrayList();
        this.metricRegistry = new MetricRegistry();
        this.validatorFactory = Validation
                .byProvider(HibernateValidator.class)
                .configure()
                .addValidatedValueHandler(new OptionalValidatedValueUnwrapper())
                .buildValidatorFactory();

        getMetricRegistry().register("jvm.attribute", new JvmAttributeGaugeSet());
        getMetricRegistry().register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory
                .getPlatformMBeanServer()));
        getMetricRegistry().register("jvm.classloader", new ClassLoadingGaugeSet());
        getMetricRegistry().register("jvm.filedescriptor", new FileDescriptorRatioGauge());
        getMetricRegistry().register("jvm.gc", new GarbageCollectorMetricSet());
        getMetricRegistry().register("jvm.memory", new MemoryUsageGaugeSet());
        getMetricRegistry().register("jvm.threads", new ThreadStatesGaugeSet());

        JmxReporter.forRegistry(metricRegistry).build().start();

        this.configurationSourceProvider = new FileConfigurationSourceProvider();
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.configurationFactoryFactory = new DefaultConfigurationFactoryFactory<>();
    }

    /**
     * Returns the bootstrap's {@link CliApplication}.
     */
    public CliApplication<T> getApplication() {
        return application;
    }

    /**
     * Returns the bootstrap's {@link ConfigurationSourceProvider}.
     */
    public ConfigurationSourceProvider getConfigurationSourceProvider() {
        return configurationSourceProvider;
    }

    /**
     * Sets the bootstrap's {@link ConfigurationSourceProvider}.
     */
    public void setConfigurationSourceProvider(ConfigurationSourceProvider provider) {
        this.configurationSourceProvider = checkNotNull(provider);
    }

    /**
     * Returns the bootstrap's class loader.
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Sets the bootstrap's class loader.
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Adds the given command to the bootstrap.
     *
     * @param command a {@link CliCommand}
     */
    public void addCommand(CliCommand command) {
        commands.add(command);
    }

    /**
     * Adds the given command to the bootstrap.
     *
     * @param command a {@link CliConfiguredCommand}
     */
    public void addCommand(CliConfiguredCommand<T> command) {
        commands.add(command);
    }

    /**
     * Returns the bootstrap's {@link com.fasterxml.jackson.databind.ObjectMapper}.
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Returns the application's commands.
     */
    public ImmutableList<CliCommand> getCommands() {
        return ImmutableList.copyOf(commands);
    }

    /**
     * Returns the application's metrics.
     */
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    /**
     * Returns the application's validator factory.
     */
    public ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public ConfigurationFactoryFactory<T> getConfigurationFactoryFactory() {
        return configurationFactoryFactory;
    }

    public void setConfigurationFactoryFactory(ConfigurationFactoryFactory<T> configurationFactoryFactory) {
        this.configurationFactoryFactory = configurationFactoryFactory;
    }
}
