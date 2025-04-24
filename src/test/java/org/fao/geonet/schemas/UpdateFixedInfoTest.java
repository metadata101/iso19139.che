/*
 * Copyright (C) 2001-2016 Food and Agriculture Organization of the
 * United Nations (FAO-UN), United Nations World Food Programme (WFP)
 * and United Nations Environment Programme (UNEP)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
 *
 * Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
 * Rome - Italy. email: geonetwork@osgeo.org
 */

package org.fao.geonet.schemas;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.fao.geonet.schema.iso19139.ISO19139Namespaces;
import org.fao.geonet.schema.iso19139che.ISO19139cheNamespaces;
import org.fao.geonet.utils.TransformerFactoryFactory;
import org.fao.geonet.utils.Xml;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

public class UpdateFixedInfoTest {

    static private Path PATH_TO_XSL;

    static private final ImmutableList<Namespace> ALL_NAMESPACES = ImmutableSet .<Namespace>builder()
            .add(ISO19139Namespaces.GCO)
            .add(ISO19139Namespaces.GMD)
            .add(ISO19139Namespaces.SRV)
            .add(ISO19139Namespaces.XSI)
            .add(ISO19139cheNamespaces.CHE)
            .build().asList();

    @BeforeClass
    public static void initSaxon() {
        TransformerFactoryFactory.init("net.sf.saxon.TransformerFactoryImpl");
    }

    @BeforeClass
    public static void setup() throws URISyntaxException {
        PATH_TO_XSL = TestSupport.getResourceInsideSchema("update-fixed-info.xsl");

    }

    @Test
    public void dataLetUnchangedText() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeText.xml"));

        assertProcessedEqualsToExpected(input, input);
    }

    @Test
    public void defaultLanguageAddedAsLocaleText() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeText.xml"));
        Element expected = (Element) input.clone();
        List<Element> elements = getElements(input, ".//gmd:locale[gmd:PT_Locale/@id = 'DE']");
        elements.forEach(Element::detach);

        assertProcessedEqualsToExpected(input, expected);
    }

    @SuppressWarnings("unchecked")
    private List<Element> getElements(Element input, String elementToExtract) throws JDOMException {
        return (List<Element>) Xml.selectNodes(input, elementToExtract, ALL_NAMESPACES);
    }

    @Test
    public void noLocaleDontDiscardLocalizedBindToDefaultText() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeText.xml"));
        getElements(input, ".//gmd:locale").forEach(Element::detach);

        Element expected = (Element) input.clone();
        getElements(expected, ".//gmd:PT_FreeText").forEach(Element::detach);
        getElements(expected, ".//gmd:title[@xsi:type='gmd:PT_FreeText_PropertyType']")
            .forEach(x -> x.removeAttribute("type", ISO19139Namespaces.XSI));

        getElements(input, ".//gmd:title/gco:CharacterString").forEach(Element::detach);

        assertProcessedEqualsToExpected(input, expected);
    }

    @Test
    public void localeCopiedIfDefault() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeText.xml"));
        Element expected = (Element) input.clone();
        getElements(input, ".//gmd:title/gco:CharacterString").forEach(x -> x.setText("to be overridden"));

        assertProcessedEqualsToExpected(input, expected);
    }

    @Test
    public void localeCopiedIfNoDefault() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeText.xml"));
        Element expected = (Element) input.clone();
        getElements(input, ".//gmd:title/gco:CharacterString").forEach(Element::detach);

        assertProcessedEqualsToExpected(input, expected);
    }

    @Test
    public void dataLetUnchangedUrl() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeUrl.xml"));

        assertProcessedEqualsToExpected(input, input);
    }

    @Test
    public void defaultLanguageAddedAsLocaleUrl() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeUrl.xml"));
        Element expected = (Element) input.clone();
        getElements(input, ".//gmd:locale[gmd:PT_Locale/@id = 'DE']").forEach(Element::detach);

        assertProcessedEqualsToExpected(input, expected);
    }

    @Test
    public void localeUrlCopiedIfDefault() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeUrl.xml"));
        Element expected = (Element) input.clone();
        getElements(input, ".//gmd:URL").forEach(x -> x.setText("to be overridden"));

        assertProcessedEqualsToExpected(input, expected);
    }

    @Test
    public void localeUrlCopiedIfDefaultEvenIfLocaleEmpty() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeUrl.xml"));
        getElements(input, ".//che:LocalisedURL").forEach(x -> x.setText(""));
        Element expected = (Element) input.clone();
        getElements(expected, ".//gmd:URL").forEach(x -> x.setText(""));
        getElements(expected, ".//che:PT_FreeURL").forEach(Element::detach);
        assertProcessedEqualsToExpected(input, expected);
    }

    @Test
    public void localeUrlCopiedIfNoDefault() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeUrl.xml"));
        Element expected = (Element) input.clone();
        getElements(input, ".//gmd:URL").forEach(Element::detach);

        assertProcessedEqualsToExpected(input, expected);
    }

    @Test
    public void localeUrlCopiedIfNoDefaultEvenIfLocaleEmpty() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeUrl.xml"));
        getElements(input, ".//che:LocalisedURL").forEach(x -> x.setText(""));
        Element expected = (Element) input.clone();
        getElements(input, ".//gmd:URL").forEach(Element::detach);
        getElements(expected, ".//gmd:URL").forEach(x -> x.setText(""));
        getElements(expected, ".//che:PT_FreeURL").forEach(Element::detach);

        assertProcessedEqualsToExpected(input, expected);
    }

    @Test
    public void whenNoLocaleUrlNotOverridden() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeUrl.xml"));
        getElements(input, ".//gmd:URL").forEach(x -> x.setText("from url"));
        Element localeDE = getElements(input, ".//che:URLGroup").stream().findFirst()
            .orElseThrow(() -> new AssertionError("No URLGroup found in input"));
        Element localeIT = (Element) localeDE.clone();
        getElements(localeIT, ".//che:LocalisedURL").forEach(x -> x.getAttribute("locale").setValue("#IT"));
        Element expected = (Element) input.clone();
        getElements(localeDE, ".//che:LocalisedURL").forEach(x -> x.getAttribute("locale").setValue("#IT"));
        getElements(expected, ".//che:LocalisedURL").forEach(x -> x.setText("from url"));
        Element element = getElements(expected, ".//che:PT_FreeURL").stream().findFirst()
                .orElseThrow(() -> new AssertionError("No PT_FreeURL found in expected result"));
        element.addContent(localeIT);

        assertProcessedEqualsToExpected(input, expected);
    }
    @Test
    public void noLocaleDontDiscardLocalizedBindToDefaultUrl() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeUrl.xml"));
        List<Element> elements = getElements(input, ".//gmd:locale");
        elements.forEach(Element::detach);

        Element expected = (Element) input.clone();
        elements = getElements(expected, ".//che:PT_FreeURL");
        elements.forEach(Element::detach);
        elements = getElements(expected, ".//gmd:linkage[@xsi:type='che:PT_FreeURL_PropertyType']");
        elements.forEach(x -> x.removeAttribute("type", ISO19139Namespaces.XSI));

        elements = getElements(input, ".//gmd:linkage/gmd:URL");
        elements.forEach(Element::detach);


        assertProcessedEqualsToExpected(input, expected);
    }

    @Test
    public void noLocaleDataLetUnchangedText() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/charstring.xml"));

        assertProcessedEqualsToExpected(input, input);
    }

    @Test
    public void noLocaleDataLetUnchangedUrl() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/url.xml"));

        assertProcessedEqualsToExpected(input, input);
    }

    @Test
    public void xlinkMulti() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/xlink.xml"));

        assertProcessedEqualsToExpected(input, input);
    }

    @Test
    public void xlinkMono() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/xlink.xml"));
        List<Element> toRemove = getElements(input, ".//gmd:locale");
        toRemove.forEach(Element::detach);
        Element expected = (Element) input.clone();
        List<Element> elements = getElements(expected, ".//gmd:contact");
        elements.forEach(elem ->
                elem.getAttribute("href", ISO19139Namespaces.XLINK)
                        .setValue("local://srv/api/registries/entries/4cb273e2-e26a-4e66-bb55-5dd09e39449b?lang=ger&process=gmd:role/gmd:CI_RoleCode/@codeListValue~partner"));

        assertProcessedEqualsToExpected(input, expected);
    }

    @Test
    public void carriageReturnAreNotDiscardedWhenCopiedInDefault() throws Exception {
        Element input = Xml.loadFile(TestSupport.getResource("org/fao/geonet/schemas/ufi/PT_FreeText.xml"));
        List<Element> elements = getElements(input, ".//gmd:LocalisedCharacterString");
        elements.forEach(x -> x.setText("   1 11\n2  22   "));

        String copiedAsDefault = ((Element) Xml.selectNodes((Xml.transform(input, PATH_TO_XSL)), ".//gco:CharacterString[contains(text(),'11')]", ALL_NAMESPACES).get(0)).getText();

        assertEquals("1 11\n2  22", copiedAsDefault);
    }

    private void assertProcessedEqualsToExpected(Element input, Element preparingExpected) throws Exception {
        String expected = Xml.getString(preparingExpected.getChild("CHE_MD_Metadata", ISO19139cheNamespaces.CHE));
        String processed = Xml.getString(Xml.transform(input, PATH_TO_XSL));
        Diff diff = DiffBuilder
                .compare(Input.fromString(processed))
                .withTest(Input.fromString(expected))
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                .checkForSimilar()
                .build();

        assertFalse("Process did not produce the expected result.", diff.hasDifferences());
    }
}
