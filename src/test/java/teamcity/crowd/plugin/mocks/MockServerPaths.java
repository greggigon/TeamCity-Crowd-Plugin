package teamcity.crowd.plugin.mocks;

import java.io.File;

public class MockServerPaths {

    public String getConfigDir() {
        return new File(".", "\\src\\test\\resources").getAbsolutePath();
    }

}
