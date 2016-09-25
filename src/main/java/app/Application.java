package app;


import util.Config;

import java.io.IOException;

/**
 * The Application interface allows the {@link app.applicationimpl.Proxy}, {@link app.applicationimpl.Client}, and {@link app.applicationimpl.Server}
 * all to have common hooks.  This promotes code reuse through the project.
 * <p>
 * Created by nathaniel on 8/25/16.
 */
public interface Application {

    /**
     * Set the configuration on the application so it knows how to behave.
     *
     * @param Config c
     */
    void setConfig(Config c);

    /**
     * Similar to the construction of Threads, this method allows for the application
     * to be started.
     * <p>
     * Since many of the extending components will be making use of networking facilities
     * there is a chance that an {@link IOException} may be thrown.
     *
     * @throws IOException
     */
    void run() throws IOException;

}
