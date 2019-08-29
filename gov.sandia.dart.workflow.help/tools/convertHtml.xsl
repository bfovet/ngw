<?xml version="1.0"?> 

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:java="http://xml.apache.org/xslt/java"
                xmlns:md="http://dart.sandia.gov/sierra-meta.xsd"
		version="1.0">
  <xsl:output omit-xml-declaration="yes"/>
 
  <xsl:template match="*">
    <xsl:apply-templates select="definition"/>
  </xsl:template>

  <xsl:template match="definition">
    <xsl:variable name="command" select="@command"/>
    <xsl:variable name="id" select="@id"/>
    <xsl:if test="$command != ''">
    <xsl:variable name="filename" select="concat('scratch/', $command,'-', $id, '.html')" />
    <xsl:result-document href="{$filename}">
      <html>
      <head>
	<link rel="stylesheet" type="text/css" href="ngscomps.css"></link>
      </head>
      <body>
      <title>NGW Component Docmentation - <xsl:value-of select="$command"/></title>
      <h1><xsl:value-of select="$command"/></h1>
      <xsl:apply-templates select="layout/layout-section/layout-cell"/>
      </body>
      </html>
    </xsl:result-document>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="layout-cell">
    <xsl:text>&#xa;&#xa;</xsl:text>
    <xsl:element name="div">
      <xsl:apply-templates select="p|h2"/>
    <xsl:text>&#xa;&#xa;</xsl:text>      
    </xsl:element>
  </xsl:template>

  <xsl:template match="p">    
    <xsl:text>&#xa;</xsl:text>
    <xsl:element name="p">
      <xsl:value-of select="."/>
    </xsl:element>
    
  </xsl:template>

    <xsl:template match="h2">    
    <xsl:text>&#xa;</xsl:text>
    <xsl:element name="h2">
      <xsl:value-of select="."/>
    </xsl:element>
    
  </xsl:template>
  
</xsl:stylesheet>  

