package com.medzone.framework.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;

import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.util.FileUtils;

public final class ModuleSpecificationXMLUtil {

	private static final String MODULES_DEFINE_XML_FILE = "_modules.xml";

	private static final String FOLDER_MODULE = "module_folder";
	private static final String XML_TAG_MODULES = "modules";
	private static final String XML_TAG_MODULE = "module";
	private static final String XML_TAG_EXTRA_ATTRIBUTES = "extra-attributes";
	private static final String XML_ATTR_MODULE_PACKAGENAME = "packagename";
	private static final String XML_ATTR_MODULE_CLASSNAME = "classname";
	private static final String XML_ATTR_MODULE_CATEGORY = "category";
	private static final String XML_ATTR_MODULE_ORDER = "order";
	private static final String XML_ATTR_MODULE_ID = "id";
	private static final String XML_ATTR_MODULE_LINK = "link";
	private static final String XML_ATTR_MODULE_STATUS = "status";
	private static final String XML_ATTR_MODULE_SYNCHRO_NUMBER = "synchro_number";
	private static final String XML_ATTR_MODULE_ORDER_ITEM = "order-item";

	private static Context mContext;
	private static int mCategoryCount = ModuleSpecificationManager.CATEGOTY_COUNT;
	private static ModuleSpecificationXMLUtil instance;

	public synchronized static void init(Context context) {
		mContext = context;
	}

	public static ModuleSpecificationXMLUtil getInstance(Context context) {
		if (mContext == null) {
			mContext = context;
			init(mContext);
		}
		if (instance == null) {
			instance = new ModuleSpecificationXMLUtil();
		}
		return instance;
	}

	private List<List<ModuleSpecification>> applyForContainer() {
		List<List<ModuleSpecification>> tmpList = new ArrayList<List<ModuleSpecification>>(
				mCategoryCount);
		for (int i = 0; i < mCategoryCount; i++) {
			tmpList.add(new ArrayList<ModuleSpecification>());
		}
		return tmpList;
	}

	public String getXMLName(Account mAccountBean) {
		if (mAccountBean == null) {
			throw new RuntimeException("Load an account object is a must！");
		}
		String accountID = String.valueOf(mAccountBean.getAccountID());
		return accountID.concat(MODULES_DEFINE_XML_FILE);
	}

	public String getXMLPath(Account mAccountBean) {
		File file = mContext.getDir(FOLDER_MODULE, Context.MODE_PRIVATE);
		String path = file.getAbsolutePath().concat(File.separator)
				.concat(getXMLName(mAccountBean));
		return path;
	}

	public boolean isXMLFileExist(Account mAccountBean) {
		return FileUtils.isFileExist(getXMLPath(mAccountBean));
	}

	/**
	 * 根据插入排序顺序
	 * 
	 * @param modules
	 * @return 待插入的集合
	 */
	private List<ModuleSpecification> getXMLInsertOrderCollect(
			List<ModuleSpecification> modules) {
		Collections.sort(modules, new Comparator<ModuleSpecification>() {

			@Override
			public int compare(ModuleSpecification a, ModuleSpecification b) {
				return a.getOrder() - b.getOrder();
			}
		});
		return modules;
	}

	public void createXMLFile(List<ModuleSpecification> modules,
			Account mAccountBean) {
		modules = getXMLInsertOrderCollect(modules);
		Document doc;
		Element modulesRoot;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			doc = dbFactory.newDocumentBuilder().newDocument();
			if (doc != null) {
				modulesRoot = doc.createElement(XML_TAG_MODULES);
				for (ModuleSpecification spec : modules) {
					Element moduleElement = doc.createElement(XML_TAG_MODULE);
					moduleElement.setAttribute(XML_ATTR_MODULE_ID,
							spec.getModuleID());
					moduleElement.setAttribute(XML_ATTR_MODULE_CATEGORY, spec
							.getCategory().toString());
					moduleElement.setAttribute(XML_ATTR_MODULE_CLASSNAME,
							spec.getClassName());
					moduleElement.setAttribute(XML_ATTR_MODULE_LINK,
							spec.getDownLoadLink());
					moduleElement.setAttribute(XML_ATTR_MODULE_ORDER, spec
							.getOrder().toString());
					moduleElement.setAttribute(XML_ATTR_MODULE_ORDER_ITEM, spec
							.getOrder().toString());
					moduleElement.setAttribute(XML_ATTR_MODULE_PACKAGENAME,
							spec.getPackageName());
					moduleElement.setAttribute(XML_ATTR_MODULE_STATUS, spec
							.getModuleStatus().getStatusId().toString());
					if (spec.getDownSerial() == null) {
						spec.setDownSerial(0);
					}
					moduleElement.setAttribute(XML_ATTR_MODULE_SYNCHRO_NUMBER,
							spec.getDownSerial().toString());

					Element extraElement = doc
							.createElement(XML_TAG_EXTRA_ATTRIBUTES);

					extraElement
							.setAttribute(
									ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL,
									spec.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL));
					extraElement
							.setAttribute(
									ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE,
									spec.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE));
					extraElement
							.setAttribute(
									ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE,
									spec.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE));

					moduleElement.appendChild(extraElement);
					modulesRoot.appendChild(moduleElement);
				}
				doc.appendChild(modulesRoot);

				saveXMLFile(doc, mAccountBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveXMLFile(Node node, Account mAccountBean) {
		File file;
		FileOutputStream fos = null;
		TransformerFactory transFactory = TransformerFactory.newInstance();
		String filePath = getXMLPath(mAccountBean);
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("encoding", "UTF-8");
			DOMSource source = new DOMSource();
			source.setNode(node);
			StreamResult result = new StreamResult();
			if (filePath == null) {
				result.setOutputStream(System.out);
			} else {
				file = new File(filePath);
				fos = new FileOutputStream(file);
				result.setOutputStream(fos);
			}
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<List<ModuleSpecification>> getModulesFromXML(Context context,
			Account mAccountBean) {
		try {
			return parseXMLFileWithDom(new File(getXMLPath(mAccountBean)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<List<ModuleSpecification>> parseXMLFileWithDom(File xmlFile) {
		DocumentBuilderFactory dbFactory;
		DocumentBuilder dBuilder;
		Document doc;
		List<List<ModuleSpecification>> tmpLists = applyForContainer();
		try {
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName(XML_TAG_MODULE);
			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					ModuleSpecification moduleSpec = new ModuleSpecification();
					String moduleId = element.getAttribute(XML_ATTR_MODULE_ID);
					String moduleCagetory = element
							.getAttribute(XML_ATTR_MODULE_CATEGORY);
					String moduleImplementClassName = element
							.getAttribute(XML_ATTR_MODULE_CLASSNAME);
					String moduleLink = element
							.getAttribute(XML_ATTR_MODULE_LINK);
					String moduleOrder = element
							.getAttribute(XML_ATTR_MODULE_ORDER);
					String moduleOrderItem = element
							.getAttribute(XML_ATTR_MODULE_ORDER_ITEM);
					String modulePackageName = element
							.getAttribute(XML_ATTR_MODULE_PACKAGENAME);
					String moduleStatus = element
							.getAttribute(XML_ATTR_MODULE_STATUS);
					String moduleSynchroNumber = element
							.getAttribute(XML_ATTR_MODULE_SYNCHRO_NUMBER);

					moduleSpec.setPackageName(modulePackageName);
					moduleSpec.setClassName(moduleImplementClassName);
					moduleSpec.setOrder(Integer.valueOf(moduleOrder));
					moduleSpec.setCategory(Integer.valueOf(moduleCagetory));
					moduleSpec.setModuleID(moduleId);
					moduleSpec.setDownLoadLink(moduleLink);
					moduleSpec.setOrder(Integer.valueOf(moduleOrderItem));
					moduleSpec.setDownSerial(Integer
							.valueOf(moduleSynchroNumber));
					switch (Integer.valueOf(moduleStatus)) {
					case -2:
						moduleSpec.setModuleStatus(ModuleStatus.HIDDEN);
						break;
					case -1:
						moduleSpec.setModuleStatus(ModuleStatus.UNINSTALL);
						break;
					case 0:
						moduleSpec.setModuleStatus(ModuleStatus.INITIAL);
						break;
					case 1:
						moduleSpec.setModuleStatus(ModuleStatus.INSTALL);
						break;
					case 2:
						moduleSpec.setModuleStatus(ModuleStatus.DISPLAY);
						break;
					default:
						break;
					}
					/***
					 * Parse extra-attribute
					 */
					NodeList extraNode = element
							.getElementsByTagName(XML_TAG_EXTRA_ATTRIBUTES);
					String defaultInstall = ((Element) extraNode.item(0))
							.getAttribute(ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL);
					String showInHomePage = ((Element) extraNode.item(0))
							.getAttribute(ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE);
					String isUnInstallable = ((Element) extraNode.item(0))
							.getAttribute(ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE);

					moduleSpec
							.putExtraAttribute(
									ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL,
									defaultInstall);
					moduleSpec
							.putExtraAttribute(
									ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE,
									showInHomePage);
					moduleSpec
							.putExtraAttribute(
									ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE,
									isUnInstallable);
					tmpLists.get(Integer.valueOf(moduleCagetory)).add(
							moduleSpec);
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmpLists;
	}

	/**
	 * 
	 * 
	 * @param context
	 * @param packageInfo
	 * @param itemOrder
	 *            这里的顺序与解析xml时的顺序耦合，导致xml中的模块不可以随便变更
	 * @param key
	 * @param value
	 * @param extraAttributes
	 */
	public void updateXMLSimpleModuleSpecification(Context context,
			ModuleSpecification spec, Account mAccountBean) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc = null;
		InputStream moduleXmlInput = null;
		int orderItem = spec.getOrder();

		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			File file = new File(getXMLPath(mAccountBean));
			moduleXmlInput = new FileInputStream(file);
			doc = builder.parse(moduleXmlInput);
			Element root = doc.getDocumentElement();

			NodeList moduleNode = root.getElementsByTagName(XML_TAG_MODULE);

			Element et = ((Element) moduleNode.item(orderItem));
			et.setAttribute(XML_ATTR_MODULE_ID, spec.getModuleID());
			et.setAttribute(XML_ATTR_MODULE_CATEGORY, spec.getCategory()
					.toString());
			et.setAttribute(XML_ATTR_MODULE_CLASSNAME, spec.getClassName());
			et.setAttribute(XML_ATTR_MODULE_LINK, spec.getDownLoadLink());
			et.setAttribute(XML_ATTR_MODULE_ORDER, spec.getOrder().toString());
			et.setAttribute(XML_ATTR_MODULE_PACKAGENAME, spec.getPackageName());
			et.setAttribute(XML_ATTR_MODULE_STATUS, spec.getModuleStatus()
					.getStatusId().toString());
			if (spec.getDownSerial() == null) {
				spec.setDownSerial(0);
			}
			et.setAttribute(XML_ATTR_MODULE_SYNCHRO_NUMBER, spec
					.getDownSerial().toString());

			NodeList extraNode = root
					.getElementsByTagName(XML_TAG_EXTRA_ATTRIBUTES);
			((Element) extraNode.item(orderItem))
					.setAttribute(
							ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL,
							spec.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL));
			((Element) extraNode.item(orderItem))
					.setAttribute(
							ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE,
							spec.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE));
			((Element) extraNode.item(orderItem))
					.setAttribute(
							ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE,
							spec.getExtraAttributeString(ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE));
			saveXMLFile(doc, mAccountBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean delXMLModuleSpecification(Account mAccountBean) {
		return FileUtils.deleteFile(getXMLPath(mAccountBean));
	}

}
