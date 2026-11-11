package com.brewery;

import com.brewery.app.AppConfig;
import com.brewery.app.BeerApplication;
import com.brewery.xml.XmlProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Entry point of the application. */
public final class Main {

    static final int EXIT_OK = 0;
    static final int EXIT_INVALID_DOCUMENT = 1;
    static final int EXIT_ERROR = 2;

    private static final Logger LOG = LogManager.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args);
        LOG.info("Finished with exit code {}", exitCode);
    }

    /**
     * Runs the application and reports the outcome as an exit code instead of
     * terminating the JVM, which keeps the method testable.
     */
    static int run(String... args) {
        try {
            AppConfig config = AppConfig.fromArgs(args);
            LOG.info("Starting with {}", config);
            return new BeerApplication().run(config) ? EXIT_OK : EXIT_INVALID_DOCUMENT;
        } catch (IllegalArgumentException e) {
            LOG.error("{}. {}", e.getMessage(), AppConfig.USAGE);
            return EXIT_ERROR;
        } catch (XmlProcessingException e) {
            LOG.error(e.getMessage());
            LOG.debug("Details of the failure", e);
            return EXIT_ERROR;
        }
    }
}
