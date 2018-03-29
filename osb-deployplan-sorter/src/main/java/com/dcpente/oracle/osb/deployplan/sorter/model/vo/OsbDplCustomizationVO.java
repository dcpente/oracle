package com.dcpente.oracle.osb.deployplan.sorter.model.vo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations.OsbDplCusEnvValueActionsVO;
import com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations.OsbDplCusFindAndReplaceVO;
import com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations.OsbDplCusReferenceVO;
import com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations.OsbDplCusUnknowVO;
import com.dcpente.oracle.osb.deployplan.sorter.util.NsUriUtil;

public class OsbDplCustomizationVO implements Comparable<OsbDplCustomizationVO> {

	private static final Logger log = LogManager.getLogger(OsbDplCustomizationVO.class);

	private final static String NODENAME_CUSTOMIZATION = "customization";
	private final static String ATTRNAME_TYPE = "type";

	public final static OsbDplCustomizationVO EMPTY_CUSTOMIZATION = new OsbDplCustomizationVO("", null, null);

	public String custType;
	private Node nodeObj = null;
	private Document doc = null;

	protected OsbDplCustomizationVO(String custType, Document doc, Node nodeObj) {
		setCustType(custType);
		setNodeObj(nodeObj);
		setDoc(doc);
	}

//	public String getNodeCustType() {
//		return nodeObj == null ? ""
//				: nodeObj.getAttributes().getNamedItemNS(NsUriUtil.W3_2001_XML_SCHEMA_INSTANCE, ATTRNAME_TYPE)
//						.getNodeValue();
//	}

	public String getCustType() {
		return this.custType;
	}

	private void setCustType(String custType) {
		this.custType = custType;
	}

	public Node getNodeObj() {
		return nodeObj;
	}

	private void setNodeObj(Node nodeObj) {
		this.nodeObj = nodeObj;
	}

	public Document getDoc() {
		return doc;
	}

	private void setDoc(Document doc) {
		this.doc = doc;
	}

	public String getCompleteKey() {
		return String.format("%s:%s", getCustType(), getKey());
	}
	
	public String getKey() {
		return "";
	}
	
	
	public int compareTo(OsbDplCustomizationVO o) {
		return getCompleteKey().compareTo(o.getCompleteKey());
	}
	
	public static OsbDplCustomizationVO from(Document doc, Node xmlNode) {
		if (xmlNode == null) {
			log.warn("OsbDplCustomizationVO.from(null): not expected. Ignored and continue");
			return EMPTY_CUSTOMIZATION;
		}

		if (NsUriUtil.chekNs(xmlNode, NsUriUtil.BEA_CUSTOMIZATIONS)) {
			log.warn("OsbDplCustomizationVO.from({}): not expected. Ignored and continue",
					NsUriUtil.isNsEmpty(xmlNode) ? "xmlns:''" : "xmnls:" + xmlNode.getNamespaceURI());
			return EMPTY_CUSTOMIZATION;
		}

		if (!NODENAME_CUSTOMIZATION.equalsIgnoreCase(NsUriUtil.getNodeNameWithoutNs(xmlNode))) {
			log.warn("OsbDplCustomizationVO.from({}): not expected. Ignored and continue", xmlNode.getNodeName());
			return EMPTY_CUSTOMIZATION;
		}

		String typeValue = null;
		{
			NamedNodeMap attrs = xmlNode.getAttributes();
			Node attrType = null;
			for(int i = 0; i < attrs.getLength() && attrType == null ; i++) {
				Node candidate = attrs.item(i);
				if (ATTRNAME_TYPE.equalsIgnoreCase(NsUriUtil.getNodeNameWithoutNs(candidate)))
					attrType = candidate;
			}
			String attrTypeValue = attrType == null ? null : attrType.getNodeValue();
			if (attrTypeValue == null) {
				log.warn(
						"OsbDplCustomizationVO.from(<customization>): without type, not expected. Ignored and continue");
				return EMPTY_CUSTOMIZATION;
			}

			if (attrTypeValue == "") {
				typeValue = "";
			} else {
				String[] attrTypeSplit = attrTypeValue.split(":");
				typeValue = attrTypeSplit[attrTypeSplit.length - 1];
			}
		}

		if (OsbDplCusEnvValueActionsVO.CUST_TYPE.equalsIgnoreCase(typeValue))
			return new OsbDplCusEnvValueActionsVO(doc, xmlNode);
		if (OsbDplCusFindAndReplaceVO.CUST_TYPE.equalsIgnoreCase(typeValue))
			return new OsbDplCusFindAndReplaceVO(doc, xmlNode);
		if (OsbDplCusReferenceVO.CUST_TYPE.equalsIgnoreCase(typeValue))
			return new OsbDplCusReferenceVO(doc, xmlNode);

		return new OsbDplCusUnknowVO(doc, xmlNode);

	}


}
