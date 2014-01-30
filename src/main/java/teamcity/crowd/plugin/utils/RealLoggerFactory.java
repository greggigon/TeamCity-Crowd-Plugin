package teamcity.crowd.plugin.utils;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;

public class RealLoggerFactory implements LoggerFactory {

    @Override
    public Logger getServerLogger() {
        return Loggers.SERVER;
    }

}
