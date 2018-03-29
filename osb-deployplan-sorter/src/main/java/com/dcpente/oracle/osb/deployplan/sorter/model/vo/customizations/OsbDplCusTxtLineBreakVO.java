package com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations;

import com.dcpente.oracle.osb.deployplan.sorter.model.vo.OsbDplCustomizationVO;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class OsbDplCusTxtLineBreakVO extends OsbDplCustomizationVO {

	public final static String CUST_TYPE = "\n";
	
	private String completeKey = null;
	
	public OsbDplCusTxtLineBreakVO(Document doc, String completeKey) {
		super(CUST_TYPE, doc, (Node)doc.createTextNode("\n"));
		setCompleteKey(completeKey);
	}
	
	public OsbDplCusTxtLineBreakVO(Document doc, String completeKey, int lines) {
		super(CUST_TYPE, doc, createTextNode(doc, lines));
		setCompleteKey(completeKey);
	}
	
	@Override
	public String getCompleteKey() {
		return completeKey;
	}
	
	private void setCompleteKey(String key) {
		this.completeKey = key;
	}

	private static Node createTextNode(Document doc, int lines) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < lines; i++)
			sb.append("\n");
		return (Node)doc.createTextNode(sb.toString());
	}
}
