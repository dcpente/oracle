package com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.dcpente.oracle.osb.deployplan.sorter.model.vo.OsbDplCustomizationVO;

public class OsbDplCusFindAndReplaceVO extends OsbDplCustomizationVO {

	public final static String CUST_TYPE = "FindAndReplaceCustomizationType";

	public OsbDplCusFindAndReplaceVO(Document doc, Node nodeObj) {
		super(CUST_TYPE, doc, nodeObj);
	}

}
