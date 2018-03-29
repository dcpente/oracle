package com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.dcpente.oracle.osb.deployplan.sorter.model.vo.OsbDplCustomizationVO;

public class OsbDplCusUnknowVO extends OsbDplCustomizationVO {

	public final static String CUST_TYPE = "Unknow";

	public OsbDplCusUnknowVO(Document doc, Node nodeObj) {
		super(CUST_TYPE, doc, nodeObj);
	}

	@Override
	public String getCustType() {
		try {
			String custType = super.getCustType();
			return custType == null ? "" : custType;
		} catch (Exception e) {
			return CUST_TYPE;
		}
	}

}
