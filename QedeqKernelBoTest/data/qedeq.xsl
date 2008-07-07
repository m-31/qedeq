<?xml version="1.0" encoding="ISO-8859-1"?> 

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
	<xsl:output method="html" encoding="ISO-8859-1" indent="no"/>
	<xsl:template match="QEDEQ">
		<xsl:message>
				XSLT Processor: <xsl:value-of select="system-property('xsl:vendor')"/>
		</xsl:message>
		<html>
			<head>
				<title>
					<xsl:apply-templates select="HEADER/TITLE/LATEX"/>
				</title>
			</head>
			<body>
				<xsl:apply-templates> 
				</xsl:apply-templates>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="HEADER/TITLE">
		<h1>
			<xsl:apply-templates> 
			</xsl:apply-templates>
		</h1>
	</xsl:template>
	<xsl:template match="ABSTRACT">
		<p>
			<xsl:apply-templates> 
			</xsl:apply-templates>
		</p>
	</xsl:template>
	<xsl:template match="AUTHORS">
		<h4>Autoren</h4>
		<p>
			<table>
				<xsl:apply-templates> 
			</xsl:apply-templates>
			</table>
		</p>
	</xsl:template>
	<xsl:template match="AUTHOR">
		<tr>
			<td>
				<xsl:apply-templates select="LATEX"/>
			</td>
			<td>&lt;<xsl:value-of select="@email"/>&gt;</td>
		</tr>
	</xsl:template>
	<xsl:template match="IMPORTS">
		<h4>Importierte Module</h4>
		<p>
			<table>
				<xsl:apply-templates> 
			</xsl:apply-templates>
			</table>
		</p>
	</xsl:template>
	<xsl:template match="IMPORT">
		<tr>
			<td>
				<xsl:value-of select="//SPEC/@name"/>
			</td>
			<td>
				<xsl:value-of select="//SPEC/@ruleVersion"/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="CHAPTER">
		<h2>Chapter <xsl:apply-templates select="./TITLE"/>
		</h2>
		<xsl:apply-templates select="LATEX">
		</xsl:apply-templates>
		<xsl:apply-templates select="SECTION">
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="SECTION">
		<h3>Section <xsl:apply-templates select="./TITLE"/>
		</h3>
		<xsl:apply-templates select="./NODE">
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="NODE">
		<p>
			<xsl:apply-templates select="./LATEX[@language='de'][1]"/>
		</p>
		<xsl:apply-templates select="./DEFINITION"/>
		<xsl:apply-templates select="./AXIOM"/>
		<xsl:apply-templates select="./THEOREM"/>
		<p>
			<xsl:apply-templates select="./LATEX[@language='de'][2]"/>
		</p>
	</xsl:template>
	<xsl:template match="DEFINITION">
		<xsl:if test="@type='PREDCON'">
			<h4>Definition</h4>
			<p>
			P<sub>
					<xsl:value-of select="@number"/>,<xsl:value-of select="@arguments"/>
				</sub> = <xsl:apply-templates select="LATEXPATTERN"/>
			</p>
		</xsl:if>
	</xsl:template>
	<xsl:template match="AXIOM">
		<h4>Axiom</h4>
		<p>
			<xsl:apply-templates select="FORMULA"/>
		</p>
	</xsl:template>
	<xsl:template match="THEOREM">
		<h4>Theorem</h4>
		<p>
			<xsl:apply-templates select="FORMULA"/>
		</p>
	</xsl:template>
	
	<xsl:template match="AND">
		<xsl:for-each select="*">
			<xsl:apply-templates select="."/>
			<xsl:if test="not(position()=last())">
				and
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="EXISTS">
	    Existiert 
			<xsl:apply-templates select="node()[1]"/>:
			<xsl:apply-templates select="node()[2]"/>
	</xsl:template>

	<xsl:template match="FORALL">
	    Existiert 
			<xsl:apply-templates select="node()[1]"/>:
			<xsl:apply-templates select="node()[2]"/>
	</xsl:template>

	<xsl:template match="PREDCON[@ref='isSet']">
		<xsl:apply-templates select="node()[1]"/>
		ist Menge
	</xsl:template>

	<xsl:template match="PREDCON[@ref='in']">
		<xsl:apply-templates select="node()[1]"/>
		ist in
		<xsl:apply-templates select="node()[2]"/>
	</xsl:template>
	
	<xsl:template match="VAR">
	   x<sub>
			<xsl:value-of select="."/>
		</sub>
	</xsl:template>
	<!--
	<xsl:template match="LATEX">
		<xsl:if test="@language='de'">
			<xsl:value-of select="."/>
		</xsl:if>
	</xsl:template>
-->
	<xsl:template match="LATEX[@language='de']">
		<xsl:value-of select="."/>
	</xsl:template>
	<xsl:template match="LATEX[@language='us']">
	</xsl:template>
</xsl:stylesheet>
