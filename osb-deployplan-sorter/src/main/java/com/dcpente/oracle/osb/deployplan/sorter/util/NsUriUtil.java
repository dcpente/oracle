package com.dcpente.oracle.osb.deployplan.sorter.util;

import org.w3c.dom.Node;

public final class NsUriUtil {

	public final static String BEA_XML_TYPES = "http://www.bea.com/wli/config/xmltypes";
	public final static String BEA_CUSTOMIZATIONS = "http://www.bea.com/wli/config/customizations";
	public final static String W3_2001_XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

	public final static boolean isNsEmpty(final Node nsNode) {
		final String ns = nsNode.getNamespaceURI();
		return ns == null || ns == "";
	}

	public final static boolean chekNs(final Node nodeObj, final String ns) {
		final String nsNode = nodeObj.getNamespaceURI();
		return nsNode == ns;
	}

	public final static String getNodeNameWithoutNs(final Node nodeObj) {
		final String nodeName = nodeObj.getNodeName();
		if (nodeName == null || nodeName == "")
			return "";
		final String[] nodeNameSplit = nodeName.split(":");
		return nodeNameSplit[nodeNameSplit.length - 1];
	}

}
