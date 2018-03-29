package com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.dcpente.oracle.osb.deployplan.sorter.model.vo.OsbDplCustomizationVO;

public class OsbDplCusEnvValueActionsVO extends OsbDplCustomizationVO {

	private static final Logger log = LogManager.getLogger(OsbDplCusEnvValueActionsVO.class);
	
	public final static String CUST_TYPE = "EnvValueActionsCustomizationType";
	public final static String XPATH_TYPE = "*[local-name()='owners']/*[local-name()='owner']/*[local-name()='type']/text()";
	public final static String XPATH_PATH = "*[local-name()='owners']/*[local-name()='owner']/*[local-name()='path']/text()";
	

	private String key = null;

	public OsbDplCusEnvValueActionsVO(Document doc, Node nodeObj) {
		super(CUST_TYPE, doc, nodeObj);
	}

	@Override
	public String getKey() {
		if (key == null) {
			key = createKey();
		}
		return key;
	}

	private String createKey() {
		XPathFactory xPathfactory = XPathFactory.newInstance();

		String ownerType = null;
		try {
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(XPATH_TYPE);
			ownerType = expr.evaluate(getNodeObj());					
		} catch (XPathExpressionException ex) {
			log.error("OsbDplCusEnvValueActionsVO.createKey(): xpath expression compile error: {} : {}", XPATH_TYPE, ex);
			throw new RuntimeException("XPath expression compile error", ex);
		}
		
		String ownerPath = null;
		try {
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(XPATH_PATH);
			ownerPath = expr.evaluate(getNodeObj());
		} catch (XPathExpressionException ex) {
			log.error("OsbDplCusEnvValueActionsVO.createKey(): xpath expression compile error: {} : {}", XPATH_PATH, ex);
			throw new RuntimeException("XPath expression compile error", ex);
		}

		return String.format("%s:%s", ownerType, ownerPath);
	}
}
