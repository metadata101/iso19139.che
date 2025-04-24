package org.fao.geonet.schemas;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestSupport {

    public static Path getResource(String name) throws URISyntaxException {
        return Paths.get(TestSupport.class.getClassLoader().getResource(name).toURI());
    }

    public static Path getResourceInsideSchema(String pathToResourceInsideSchema) throws URISyntaxException {
        return getResource("gn-site/WEB-INF/data/config/schema_plugins/iso19139.che/" + pathToResourceInsideSchema);
    }
}
