package org.fizmo.dropwizard.cli;

import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliCheckCommand<T extends CliConfiguration> extends CliConfiguredCommand<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CliCheckCommand.class);

    private final Class<T> configurationClass;

    public CliCheckCommand(CliApplication<T> application) {
        super("check", "Parses and validates the configuration file");
        this.configurationClass = application.getConfigurationClass();
    }

    /*
     * Since we don't subclass CheckCommand, we need a concrete reference to the configuration
     * class.
     */
    @Override
    protected Class<T> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    protected void run(CliBootstrap<T> bootstrap,
                       Namespace namespace,
                       T configuration) throws Exception {
        LOGGER.info("Configuration is OK");
    }
}
