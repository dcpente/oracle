package com.dcpente.oracle.osb.deployplan.sorter.model.vo;

import java.io.File;
import java.util.List;

import org.w3c.dom.Document;

public class OsbDeployPlanVO {
//	private static final Logger log = LogManager.getLogger(OsbDeployPlanVO.class);
	
	private File file;
	private Document doc;
	private List<OsbDplCustomizationVO> customizations;

	public OsbDeployPlanVO(File file, Document doc, List<OsbDplCustomizationVO> customizations) {
		setFile(file);
		setDoc(doc);
		setCustomizations(customizations);
	}

	public File getFile() {
		return file;
	}

	private void setFile(File file) {
		this.file = file;
	}

	public Document getDoc() {
		return doc;
	}

	private void setDoc(Document doc) {
		this.doc = doc;
	}

	public List<OsbDplCustomizationVO> getCustomizations() {
		return customizations;
	}

	public void setCustomizations(List<OsbDplCustomizationVO> customizations) {
		this.customizations = customizations;
	}

}
