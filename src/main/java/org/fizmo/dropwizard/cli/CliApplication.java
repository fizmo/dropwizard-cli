package org.fizmo.dropwizard.cli;

import io.dropwizard.util.Generics;

public abstract class CliApplication<T extends CliConfiguration> {

    public void initialize(CliBootstrap<T> bootstrap) {

    }

    /**
     * Returns the {@link Class} of the configuration class type parameter.
     *
     * @return the configuration class
     * @see io.dropwizard.util.Generics#getTypeParameter(Class, Class)
     */
    public final Class<T> getConfigurationClass() {
        return Generics.getTypeParameter(getClass(), CliConfiguration.class);
    }

    /**
     * Returns the name of the application.
     *
     * @return the application's name
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract void run(T configuration, CliEnvironment environment) throws Exception;

    public void run(String... arguments) throws Exception {
        final CliBootstrap<T> bootstrap = new CliBootstrap<T>(this);
    }

}
