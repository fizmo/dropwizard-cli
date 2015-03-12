package org.fizmo.dropwizard.cli;

import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CheckCommandTest {
    private static class MyApplication extends CliApplication<CliConfiguration> {
        @Override
        public void run(CliConfiguration configuration, CliEnvironment environment) throws Exception {
        }
    }

    private final MyApplication application = new MyApplication();
    private final CliCheckCommand<CliConfiguration> command = new CliCheckCommand<>(application);

    @SuppressWarnings("unchecked")
    private final CliBootstrap<CliConfiguration> bootstrap = mock(CliBootstrap.class);
    private final Namespace namespace = mock(Namespace.class);
    private final CliConfiguration configuration = mock(CliConfiguration.class);

    @Test
    public void hasAName() throws Exception {
        assertThat(command.getName())
                .isEqualTo("check");
    }

    @Test
    public void hasADescription() throws Exception {
        assertThat(command.getDescription())
                .isEqualTo("Parses and validates the configuration file");
    }

    @Test
    public void doesNotInteractWithAnything() throws Exception {
        command.run(bootstrap, namespace, configuration);

        verifyZeroInteractions(bootstrap, namespace, configuration);
    }
}
