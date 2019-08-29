<?xml version="1.0"?> 

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:java="http://xml.apache.org/xslt/java"
                xmlns:md="http://dart.sandia.gov/sierra-meta.xsd"
		version="1.0">

  
  
  <xsl:template match="*">
    <root>
    <xsl:apply-templates
        select="object[@class='BodyContent']/property[@name='body']"/>
    <xsl:text>&#xa;</xsl:text>
    </root>
  </xsl:template>
  
  <xsl:template match="property">
     <xsl:if test="not(contains(text(), 'space-key'))">
    <xsl:text>&#xa;&#xa;</xsl:text>
    <xsl:variable name="id">
      <xsl:value-of select="../id[text()]"/>
    </xsl:variable>
    <xsl:element name="definition">
      <xsl:attribute name="command"><xsl:value-of select="/*/object/collection/element/id[text()=$id]/../../../property[@name='title']"/></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
      <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:element>
    </xsl:if>    
  </xsl:template>
  
</xsl:stylesheet>  

