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
          .failure {
            font-weight:bold;
            background:#eeeee0;
            color:red;
          }
          .pass {
            background:#eeeee0;
            color:green;
          }
        </style>
      </head>
      <body>
        <xsl:call-template name="header"/>
        <xsl:call-template name="summary"/>
        <xsl:call-template name="testsuite"/>
      </body>
    </html>
  </xsl:template>
  <xsl:template name="header">
    <h1>RegaDB Tests Results</h1>
  </xsl:template>
  <xsl:template name="summary">
    <xsl:variable name="suiteCount" select="@suites"/>
    <xsl:variable name="testCount" select="@tests"/>
    <xsl:variable name="runCount" select="@runs"/>
    <xsl:variable name="errorCount" select="@errors"/>
    <xsl:variable name="failureCount" select="@failures"/>
    <xsl:variable name="successRate" select="($testCount - $failureCount - $errorCount) div $testCount"/>
    <h2>Summary</h2>
    <table border="0" cellpadding="5" cellspacing="2"  width="100%">
      <tr bgcolor="#a6caf0" valign="top">
        <td><b>Suites</b></td>
        <td><b>Tests</b></td>
        <td><b>Runs</b></td>
        <td><b>Failures</b></td>
        <td><b>Errors</b></td>
        <td><b>Success Rate</b></td>
      </tr>
      <tr valign="top">
        <xsl:attribute name="class">
          <xsl:choose>
            <xsl:when test="$failureCount &gt; 0">failure</xsl:when>
            <xsl:when test="$errorCount &gt; 0">error</xsl:when>
            <xsl:otherwise>pass</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <td><xsl:value-of select="$suiteCount"/></td>
        <td><xsl:value-of select="$testCount"/></td>
        <td><xsl:value-of select="$runCount"/></td>
        <td><xsl:value-of select="$failureCount"/></td>
        <td><xsl:value-of select="$errorCount"/></td>
        <td><xsl:value-of select="format-number($successRate,'0.00%')"/></td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template name="testsuite">
    <xsl:for-each select="testsuite">
      <xsl:variable name="testCount" select="@tests"/>
      <xsl:variable name="runCount" select="@runs"/>
      <xsl:variable name="errorCount" select="@errors"/>
      <xsl:variable name="failureCount" select="@failures"/>
      <xsl:variable name="successRate" select="($runCount - $failureCount - $errorCount) div $runCount"/>
      <h2><xsl:value-of select="@name"/></h2>
      <table border="0" cellpadding="5" cellspacing="2" width="100%">
        <tr bgcolor="#a6caf0" valign="top">
          <td><b>Tests</b></td>
          <td><b>Runs</b></td>
          <td><b>Failures</b></td>
          <td><b>Errors</b></td>
          <td><b>Success Rate</b></td>
        </tr>
        <tr>
          <xsl:attribute name="class">
            <xsl:choose>
              <xsl:when test="@failures[.&gt; 0]">failure</xsl:when>
              <xsl:when test="@errors[.&gt; 0]">error</xsl:when>
              <xsl:otherwise>pass</xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <td><xsl:value-of select="$testCount"/></td>
          <td><xsl:value-of select="$runCount"/></td>
          <td><xsl:value-of select="$failureCount"/></td>
          <td><xsl:value-of select="$errorCount"/></td>
          <td><xsl:value-of select="format-number($successRate,'0.00%')"/></td>
        </tr>
      </table>
      <xsl:choose>
        <xsl:when test="@failures[.&gt; 0]">
          <h3>Failures</h3>
          <table border="0" cellpadding="5" cellspacing="2" width="100%">
            <tr bgcolor="#a6caf0" valign="top">
              <td><b>Package</b></td>
              <td><b>Class</b></td>
              <td><b>Method</b></td>
              <td><b>Failure</b></td>
            </tr>
            <xsl:for-each select="testcase">
              <xsl:for-each select="failure">
                <tr class="failure" bgcolor="#eeeee0">
                  <td><xsl:value-of select="@package"/></td>
                  <td><xsl:value-of select="@class"/></td>
                  <td><xsl:value-of select="@method"/></td>
                  <td><xsl:value-of select="@failure"/></td>
                </tr>
              </xsl:for-each>
            </xsl:for-each>
          </table>
        </xsl:when>
        <xsl:when test="@failures[.&gt; 0]">
          <h3>Errors</h3>
          <table border="0" cellpadding="5" cellspacing="2" width="100%">
            <tr bgcolor="#a6caf0" valign="top">
              <td><b>Package</b></td>
              <td><b>Class</b></td>
              <td><b>Method</b></td>
              <td><b>Error</b></td>
            </tr>
            <xsl:for-each select="testcase">
              <xsl:for-each select="failure">
                <tr class="failure" bgcolor="#eeeee0">
                  <td><xsl:value-of select="@package"/></td>
                  <td><xsl:value-of select="@class"/></td>
                  <td><xsl:value-of select="@method"/></td>
                  <td><xsl:value-of select="@error"/></td>
                </tr>
              </xsl:for-each>
            </xsl:for-each>
          </table>
        </xsl:when>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>

