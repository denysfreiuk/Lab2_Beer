<?xml version="1.0" encoding="UTF-8"?>
<!--
    Regroups the flat list of BeerItem elements into sub-lists by beer type:

        <Beer>                         <Beer>
            <BeerItem>..dark..             <dark count="2">
            <BeerItem>..light..     =>         <BeerItem>..</BeerItem>
            <BeerItem>..dark..                 <BeerItem>..</BeerItem>
        </Beer>                            </dark>
                                           <light count="1">..</light>
                                       </Beer>

    XSLT 1.0 has no "for-each-group", so Muenchian grouping (xsl:key) is used.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" encoding="UTF-8" indent="yes" standalone="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:key name="beers-by-type" match="BeerItem" use="Type"/>

    <xsl:template match="/Beer">
        <Beer>
            <!-- the first BeerItem of every type starts a new group -->
            <xsl:for-each select="BeerItem[generate-id() = generate-id(key('beers-by-type', Type)[1])]">
                <xsl:sort select="Type"/>
                <xsl:element name="{Type}">
                    <xsl:attribute name="count">
                        <xsl:value-of select="count(key('beers-by-type', Type))"/>
                    </xsl:attribute>
                    <xsl:apply-templates select="key('beers-by-type', Type)" mode="copy">
                        <xsl:sort select="Name"/>
                    </xsl:apply-templates>
                </xsl:element>
            </xsl:for-each>
        </Beer>
    </xsl:template>

    <!--
        Copies an element with its attributes and content. Unlike xsl:copy-of it
        does not drag the xsi namespace declaration of the root into every item.
    -->
    <xsl:template match="*" mode="copy">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="copy"/>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
