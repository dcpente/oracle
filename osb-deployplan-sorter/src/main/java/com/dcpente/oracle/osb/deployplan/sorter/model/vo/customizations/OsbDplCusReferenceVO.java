package com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.dcpente.oracle.osb.deployplan.sorter.model.vo.OsbDplCustomizationVO;

public class OsbDplCusReferenceVO  extends OsbDplCustomizationVO {

	public final static String CUST_TYPE = "ReferenceCustomizationType";

	public OsbDplCusReferenceVO(Document doc, Node nodeObj) {
		super(CUST_TYPE, doc, nodeObj);
	}


}
