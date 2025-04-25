package org.fao.geonet.schemas;

import java.net.URL;
import java.nio.file.Path;

public class TestSupport {

    public static Path getResource(String name) {
        URL resource = TestSupport.class.getClassLoader().getResource(name);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + name);
        }
        return Path.of(resource.getPath());
    }

    public static Path getResourceInsideSchema(String pathToResourceInsideSchema) {
        return getResource("gn-site/WEB-INF/data/config/schema_plugins/iso19139.che/" + pathToResourceInsideSchema);
    }
}
