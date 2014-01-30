package teamcity.crowd.plugin.mocks;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NonNls;
import teamcity.crowd.plugin.utils.LoggerFactory;

public class MockLogger extends Logger implements LoggerFactory {

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(@NonNls String s) {
        System.out.println(s);
    }

    @Override
    public void debug(Throwable throwable) {
        if (throwable != null) throwable.printStackTrace();
    }

    @Override
    public void debug(@NonNls String s, Throwable throwable) {
        System.out.println(s);
        if (throwable != null) throwable.printStackTrace();
    }

    @Override
    public void error(@NonNls String s, Throwable throwable, @NonNls String... strings) {
        System.out.println(s);
        for (String string : strings) {
            System.out.println(string);
        }
        if (throwable != null) throwable.printStackTrace();
    }

    @Override
    public void info(@NonNls String s) {
        System.out.println(s);
    }

    @Override
    public void info(@NonNls String s, Throwable throwable) {
        System.out.println(s);
        if (throwable != null) throwable.printStackTrace();
    }

    @Override
    public void warn(@NonNls String s, Throwable throwable) {
        System.out.println(s);
        if (throwable != null) throwable.printStackTrace();
    }

    @Override
    public void setLevel(Level level) {

    }

    @Override
    public Logger getServerLogger() {
        return this;
    }
}
