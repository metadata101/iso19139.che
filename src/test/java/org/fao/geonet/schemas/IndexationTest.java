package org.fao.geonet.schemas;

import org.fao.geonet.util.XslUtil;
import org.fao.geonet.utils.ResolverWrapper;
import org.fao.geonet.utils.TransformerFactoryFactory;
import org.fao.geonet.utils.Xml;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlunit.assertj.XmlAssert;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.fao.geonet.schemas.TestSupport.getResource;
import static org.fao.geonet.schemas.TestSupport.getResourceInsideSchema;
import static org.junit.Assert.assertEquals;

public class IndexationTest {

    private static Field resolverMapField;

    @BeforeClass
    public static void initOasis() throws NoSuchFieldException, IllegalAccessException {
        resolverMapField = ResolverWrapper.class.getDeclaredField("resolverMap");
        resolverMapField.setAccessible(true);
        ((Map<?, ?>) resolverMapField.get(null)).clear();
        Path oasisCatalogPath = getResource("gn-site/WEB-INF/oasis-catalog.xml");
        ResolverWrapper.createResolverForSchema("DEFAULT", oasisCatalogPath);
    }

    @BeforeClass
    public static void initSaxon() {
        TransformerFactoryFactory.init("net.sf.saxon.TransformerFactoryImpl");
    }

    @AfterClass
    public static void clearOasis() throws IllegalAccessException {
        ((Map<?,?>) resolverMapField.get(null)).clear();
    }

    @Test
    public void indexAmphibians() throws Exception {
        XslUtil.IS_INSPIRE_ENABLED = true;
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        String actual = indexAndPrepareAmphibians();

        String expected = Files.readString(getResource("amphibians-index.xml"));
        XmlAssert.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void useIncorrectTimeZone() throws Exception {
        XslUtil.IS_INSPIRE_ENABLED = true;
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+2"));

        String actual = indexAndPrepareAmphibians();

        try {
            XmlAssert.assertThat(actual).valueByXPath("//resourceTemporalDateRange").contains("2024-02-22T22:00:00.000Z");
        } catch (AssertionError e) {
            throw new AssertionError("The date is no longer shifted to previous day according to system timezone (there might be no need to force system timezone to utc anymore).", e);
        }
    }

    @Test
    public void indexCall112() throws Exception {
        XslUtil.IS_INSPIRE_ENABLED = false;
        Element call112Element = applyIndexation("org/fao/geonet/schemas/call_112.xml");

        XPath xpath = XPath.newInstance("/doc/pointOfContactOrgForResourceObject");
        List<?> orgForResourceObject = xpath.selectNodes(new Document(call112Element));
        Element element = (Element)orgForResourceObject.get(0);
        String test =((Text) (element.getContent().get(0))).getText();
        JSONObject valid = new JSONObject(test);
        assertEquals("Bundesamt für Kommunikation", valid.get("langger"));
        assertEquals("Bundesamt für Kommunikation", valid.get("default"));
        assertEquals("Office fédéral de la communication", valid.get("langfre"));
    }

    @Test
    public void indexServiceGruenflaechen() throws Exception {
        XslUtil.IS_INSPIRE_ENABLED = false;
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Element serviceGruenflaechen = applyIndexation("gruenflaechen-19139.che.xml");
        String actual = prepareAssertEqual(serviceGruenflaechen);

        String expected = Files.readString(getResource("gruenflaechen-index.xml"));
        XmlAssert.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void indexConventionDesAlpesTousLesChamps() throws Exception {
        XslUtil.IS_INSPIRE_ENABLED = false;
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Element conventionElement = applyIndexation("conventionDesAlpesTousLesChamps-19139.che.xml");
        String actual = prepareAssertEqual(conventionElement);

        String expected = Files.readString(getResource("conventionDesAlpesTousLesChamps-index.xml"));
        XmlAssert.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void indexGrundWasserSchutzZonen() throws Exception {
        XslUtil.IS_INSPIRE_ENABLED = false;
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Element grundWasserSchutzZonen = applyIndexation("grundwasserschutzzonen-19139.che.xml");
        String actual = prepareAssertEqual(grundWasserSchutzZonen);

        String expected = Files.readString(getResource("grundwasserschutzzonen-index.xml"));
        XmlAssert.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void indexAsiatischeHornisse() throws Exception {
        XslUtil.IS_INSPIRE_ENABLED = false;
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Element asiatischeHornisse = applyIndexation("asiatischeHornisse-19139.che.xml");
        String actual = prepareAssertEqual(asiatischeHornisse);

        String expected = Files.readString(getResource("asiatischeHornisse-index.xml"));
        XmlAssert.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void indexZonesTranquille() throws Exception {
        XslUtil.IS_INSPIRE_ENABLED = false;
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Element zoneTranquille = applyIndexation("zonesDeTranquillite-iso19139.che.xml");
        String actual = prepareAssertEqual(zoneTranquille);

        String expected = Files.readString(getResource("zonesDeTranquillite-index.xml"));
        XmlAssert.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void indexTLM3D() throws Exception {
        XslUtil.IS_INSPIRE_ENABLED = false;
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Element swissTLM3D = applyIndexation("swissTLM3D-19139.che.xml");
        String actual = prepareAssertEqual(swissTLM3D);

        String expected = Files.readString(getResource("swissTLM3D-index.xml"));
        XmlAssert.assertThat(actual).isEqualTo(expected);
    }

    private String indexAndPrepareAmphibians() throws Exception {
        Element amphibiansIndex = applyIndexation("amphibians-19139.che.xml");
        return prepareAssertEqual(amphibiansIndex);
    }

    private Element applyIndexation(String fileToIndex) throws Exception {
        Path xslFile = getResourceInsideSchema("index-fields/index.xsl");
        Path xmlFile = getResource(fileToIndex);
        Element loadedFile = Xml.loadFile(xmlFile);

        return Xml.transform(loadedFile, xslFile);
    }

    private String prepareAssertEqual(Element amphibiansIndex) {
        return new XMLOutputter(Format.getPrettyFormat().setLineSeparator("\n")) //
                .outputString(new Document(amphibiansIndex)) //
                .replaceAll("<indexingDate>.*</indexingDate>", "<indexingDate>2025-04-11T17:46:21+02:00</indexingDate>");
    }
}
