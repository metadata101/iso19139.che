<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ Copyright (C) 2001-2016 Food and Agriculture Organization of the
  ~ United Nations (FAO-UN), United Nations World Food Programme (WFP)
  ~ and United Nations Environment Programme (UNEP)
  ~
  ~ This program is free software; you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation; either version 2 of the License, or (at
  ~ your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful, but
  ~ WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  ~ General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program; if not, write to the Free Software
  ~ Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
  ~
  ~ Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
  ~ Rome - Italy. email: geonetwork@osgeo.org
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gco="http://www.isotc211.org/2005/gco"
                xmlns:gmd="http://www.isotc211.org/2005/gmd"
                xmlns:che="http://www.geocat.ch/2008/che"
                version="1.0" exclude-result-prefixes="gmd">

  <!-- ================================================================= -->

  <xsl:template match="/root">
    <xsl:apply-templates select="*[name() != 'env']"/>
  </xsl:template>

  <!-- ================================================================= -->

  <xsl:template match="che:CHE_MD_Metadata">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <gmd:fileIdentifier>
        <gco:CharacterString>
          <xsl:value-of select="/root/env/uuid"/>
        </gco:CharacterString>
      </gmd:fileIdentifier>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- ================================================================= -->

  <xsl:template match="gmd:fileIdentifier"/>

  <!-- ================================================================= -->

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- ================================================================= -->

</xsl:stylesheet>
