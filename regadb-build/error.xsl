<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:html="http://www.w3.org/Profiles/XHTML-transitional">
<xsl:template match="test">
    <html>
      <head>
        <style type="text/css">
          body {
            font:normal 68% verdana,arial,helvetica;
            color:#000000;
          }
          td {
            font-size: 68%
          }
          p {
            line-height:1.5em;
            margin-top:0.5em; margin-bottom:1.0em;
          }
          h1 {
            margin: 0px 0px 5px;
            font: 165% verdana,arial,helvetica
          }
          h2 {
            margin-top: 1em;
            margin-bottom: 0.5em;
            font: bold 150% verdana,arial,helvetica
          }
          h3 {
            margin-bottom: 0.5em;
            font: bold 125% verdana,arial,helvetica
          }
          .error {
            font-weight:bold;
            background:#eeeee0;
            color:purple;
          }
        </style>
      </head>
      <body>
        <xsl:call-template name="header"/>
        <xsl:call-template name="error"/>
      </body>
    </html>
  </xsl:template>
  <xsl:template name="header">
    <h1>RegaDB Tests Results</h1>
  </xsl:template>
  <xsl:template name="error">
    <xsl:for-each select="error">
      <xsl:variable name="projectname" select="@projectname"/>
      <xsl:variable name="exception" select="@exception"/>
      <h2>Exception</h2>
      <table border="0" cellpadding="5" cellspacing="2" width="100%">
        <tr bgcolor="#a6caf0" valign="top">
          <td><b>Project</b></td>
          <td><b>Error</b></td>
        </tr>
        <tr>
          <xsl:attribute name="class">error</xsl:attribute>
          <td><xsl:value-of select="$projectname"/></td>
          <td><xsl:value-of select="$exception"/></td>
        </tr>
      </table>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>

