package org.fizmo.dropwizard.cli;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.logging.LoggingFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class CliConfiguration {

    @Valid
    @NotNull
    private LoggingFactory logging = new LoggingFactory();

    /**
     * Returns the logging-specific section of the configuration file.
     *
     * @return logging-specific configuration parameters
     */
    @JsonProperty("logging")
    public LoggingFactory getLoggingFactory() {
        return logging;
    }

}
