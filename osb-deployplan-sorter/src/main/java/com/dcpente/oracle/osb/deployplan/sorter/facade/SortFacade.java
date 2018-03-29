package com.dcpente.oracle.osb.deployplan.sorter.facade;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.dcpente.oracle.osb.deployplan.sorter.MainArguments;
import com.dcpente.oracle.osb.deployplan.sorter.model.dao.OsbDeployPlanDAO;
import com.dcpente.oracle.osb.deployplan.sorter.model.exceptions.ModelException;
import com.dcpente.oracle.osb.deployplan.sorter.model.vo.OsbDeployPlanVO;
import com.dcpente.oracle.osb.deployplan.sorter.model.vo.OsbDplCustomizationVO;
import com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations.OsbDplCusTxtLineBreakVO;

public class SortFacade {

	private final static Logger log = LogManager.getLogger(SortFacade.class);

	private MainArguments mArgs = null;

	public SortFacade(MainArguments mArgs) {
		setmArgs(mArgs);
	}

	private MainArguments getmArgs() {
		return mArgs;
	}

	private void setmArgs(MainArguments mArgs) {
		this.mArgs = mArgs;
	}

	public void run() {
		if (log.isDebugEnabled())
			log.debug("SortFacade.run() - begin");

		OsbDeployPlanDAO dao = new OsbDeployPlanDAO();
		OsbDeployPlanVO src = null;
		OsbDeployPlanVO dst = null;
		OsbDeployPlanVO cmp = null;

		try {
			if (log.isInfoEnabled()) log.info("Sorting source");
			
			src = dao.read(new File(getmArgs().getSourceFile()));
			sort(src, "src");
			dst = createFrom(src, "dst");
			
			if (log.isInfoEnabled()) log.info("Write destination");
			dao.write(new File(getmArgs().getDestinationFile()), dst, getmArgs().isOverwrite());
			
			if (getmArgs().getCompareFile() == null) return;
			
			if (log.isInfoEnabled()) log.info("Sorting compare file");
			cmp = dao.read(new File(getmArgs().getCompareFile()));
			sort(cmp, "cmp");
			cmp = createFrom(cmp, "cmp");
			dao.write(new File(getmArgs().getCompareSrcFile()), cmp, true);
			
			dst = dao.read(new File(getmArgs().getDestinationFile()));
			cmp = dao.read(new File(getmArgs().getCompareSrcFile()));
			
			sortAndFill(dst, cmp);
			cmp = createFrom(cmp, "cmp.from");
			dst = createFrom(dst, "dst.to");
			
			if (log.isInfoEnabled()) log.info("Write comparation");
			dao.write(new File(getmArgs().getCompareSrcFile()), cmp, true);
			dao.write(new File(getmArgs().getCompareDstFile()), dst, true);
		
		} catch (Exception e) {
			log.error(e);			
		} finally {
			if (log.isDebugEnabled())
				log.debug("SortFacade.run() - end");
		}
	}

	private void sort(OsbDeployPlanVO dplan, String name) {
		if (dplan.getCustomizations() == null) {
			log.warn("No customizations for {}", name);
			return;
		}

		long millis = System.currentTimeMillis();
		if (log.isDebugEnabled())
			log.debug("Sorting plan {}", name);

		Collections.sort(dplan.getCustomizations());

		if (log.isDebugEnabled())
			log.debug("Sorted plan {} in {}ms", name, System.currentTimeMillis() - millis);
	}

	private OsbDeployPlanVO createFrom(OsbDeployPlanVO dplan, String name) throws ModelException {
		if (log.isDebugEnabled())
			log.debug("Create deploy plan {}", name);

		try {
			if (dplan.getCustomizations() == null) {
				throw new ModelException("No customizations, no work");
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document copiedDocument = db.newDocument();

			Node copiedRoot = copiedDocument.importNode(dplan.getDoc().getDocumentElement(), false);
			copiedDocument.appendChild(copiedRoot);
			for (OsbDplCustomizationVO cust : dplan.getCustomizations()) {
				Node copiedNode = copiedDocument.importNode(cust.getNodeObj(), true);
				copiedRoot.appendChild(copiedNode);
			}

			return new OsbDeployPlanVO(null, copiedDocument,
					new ArrayList<OsbDplCustomizationVO>(dplan.getCustomizations()));

		} catch (ModelException e) {
			log.error("SortFacade.createFrom(): {}", e);
			throw e;
		} catch (Exception e) {
			log.error("SortFacade.createFrom(): {}", e);
			throw new ModelException("SortFacade.createFrom()", e);
		} finally {
			if (log.isDebugEnabled())
				log.debug("Created deploy plan {}", name);
		}
	}
	
	private void sortAndFill(OsbDeployPlanVO src, OsbDeployPlanVO cmp) {

		long millis = System.currentTimeMillis();
		if (log.isDebugEnabled())
			log.debug("Sorting and filling");
		
		List<OsbDplCustomizationVO> srcList = src.getCustomizations();
		List<OsbDplCustomizationVO> cmpList = cmp.getCustomizations();
		int srcIndex = 0;
		int cmpIndex = 0;
		
		while(srcIndex < srcList.size() && cmpIndex < cmpList.size()) {
			
			OsbDplCustomizationVO srcItem = srcList.get(srcIndex);
			OsbDplCustomizationVO cmpItem = cmpList.get(cmpIndex);

//			int lineSrcItem =(Integer)srcItem.getNodeObj().getUserData("lineNumber");
//			int lineCmpItem = (Integer)cmpItem.getNodeObj().getUserData("lineNumber");
//			int lineDiff = Math.abs(lineSrcItem-lineCmpItem);
			int lineDiff = 50;
			
			if (srcItem.compareTo(cmpItem) == 0) {
				// keys are equals
				if (log.isTraceEnabled()) log.trace("Sort&compare: keys equals", lineDiff);
				srcIndex++;
				cmpIndex++;
			} else if (srcItem.compareTo(cmpItem) < 0) {
				// gap in cmp, fill with empties
				if (log.isTraceEnabled()) log.trace("Sort&compare: fill in cmp {} lines", lineDiff);
				cmpList.add(cmpIndex, new OsbDplCusTxtLineBreakVO(cmp.getDoc(), srcItem.getCompleteKey(), lineDiff));
			} else {
				// gap in src, fill with empties
				if (log.isTraceEnabled()) log.trace("Sort&compare: fill in src {} lines", lineDiff);
				srcList.add(srcIndex, new OsbDplCusTxtLineBreakVO(src.getDoc(), cmpItem.getCompleteKey(), lineDiff));
			}
		}
		
		if (log.isDebugEnabled())
			log.debug("Sorted and filled in {}ms", System.currentTimeMillis() - millis);
		
		if (log.isDebugEnabled()) log.debug("Compare plans (END)");
	}

	
	
}
