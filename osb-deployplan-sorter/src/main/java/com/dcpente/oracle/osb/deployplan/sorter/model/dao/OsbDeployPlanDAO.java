package com.dcpente.oracle.osb.deployplan.sorter.model.dao;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dcpente.oracle.osb.deployplan.sorter.model.exceptions.ModelException;
import com.dcpente.oracle.osb.deployplan.sorter.model.vo.OsbDeployPlanVO;
import com.dcpente.oracle.osb.deployplan.sorter.model.vo.OsbDplCustomizationVO;
import com.dcpente.oracle.osb.deployplan.sorter.model.vo.customizations.OsbDplCusUnknowVO;

public class OsbDeployPlanDAO {

	private static final Logger log = LogManager.getLogger(OsbDeployPlanDAO.class);

	public OsbDeployPlanVO read(File file) throws ModelException {
		if (log.isDebugEnabled())
			log.debug("Reading {}", file);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(file.getAbsoluteFile());

			if (!doc.hasChildNodes())
				throw new ModelException("XML with no nodes in file " + file);

			int nodesCount = 0;
			NodeList nodes = doc.getChildNodes().item(0).getChildNodes();
			List<OsbDplCustomizationVO> customizations = new ArrayList<OsbDplCustomizationVO>();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE)
					continue;

				nodesCount++;
				OsbDplCustomizationVO custom = OsbDplCustomizationVO.from(doc, node);
				if (custom == null || custom == OsbDplCustomizationVO.EMPTY_CUSTOMIZATION
						|| custom instanceof OsbDplCusUnknowVO) {
					if (log.isDebugEnabled()) {
						log.debug("Se ingora customization (index = {}) en fichero {}", i, file.getAbsoluteFile());
					}
				} else {
					customizations.add(custom);
				}
			}

			if (log.isInfoEnabled())
				log.info("Leidos {} nodos en {}", nodesCount, file);

			if (nodesCount != customizations.size())
				log.warn("Interpretados como customization {} de {} nodos disponibles. SE DESCARTAN {} nodos",
						customizations.size(), nodesCount, nodesCount - customizations.size());

			return new OsbDeployPlanVO(file, doc, customizations);

		} catch (ModelException e) {
			log.error("OsbDeployPlanDAO.read({}): {}", file, e);
			throw e;
		} catch (Exception e) {
			log.error("OsbDeployPlanDAO.read({}): {}", file, e);
			throw new ModelException("file: " + file, e);
		} finally {
			if (log.isDebugEnabled())
				log.debug("Readed {}", file);
		}
	}

	public void write(File file, OsbDeployPlanVO data, boolean overwrite) throws ModelException {
		if (log.isDebugEnabled())
			log.debug("Writing {}", file);
		try {
			if (file.exists() && !overwrite) {
				throw new FileAlreadyExistsException(file.getAbsolutePath());
			}
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			Result output = new StreamResult(file);
			Source input = new DOMSource(data.getDoc());

			transformer.transform(input, output);
			
		} catch (Exception e) {
			log.error(e);
			throw new ModelException("Write " + file, e);
		} finally {
			if (log.isDebugEnabled())
				log.debug("Writed {}", file);
		}
	}

}
