package cn.panshihao.easyswt.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 用于读取easyswt核心配置文件:easyswt.xml
 * @author shihao
 *
 */
public class easyswtCore extends easyswtBase {

	public static String layoutPath = "resource/layout";
	public static String drawablePath = "resource/drawable";
	
	private Document document;
	private SAXReader saxreader;
	private Element rootElement;
	private easyswtCoreWindow startElement;
	
	private String _package;
	private int versionCode = 1;
	private String versionName = "1.0.0";
	private String system = "window";
	private int bit = 32;
	
	private String welcomeId;
	private List<easyswtCoreWindow> windowList;
	
	/**
	 * 构造方法，用于构造该对象，解析传入的file为document，并提取出各种数据
	 * @param file
	 * @throws easyswtException 
	 */
	public easyswtCore(File file) throws easyswtException{
		saxreader = new SAXReader();
		try {
			document = saxreader.read(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		// Get Root Element
		rootElement = document.getRootElement();
		if(!rootElement.getName().equals("easyswt")){
			throw new easyswtException("easyswt.xml的根元素必须是easyswt!");
		}
		// Get Root Element attribute is package
		Attribute packageAttr = rootElement.attribute("package");
		if(packageAttr == null || packageAttr.getValue().equals("")){
			throw new easyswtException("easyswt标签的package属性不能为空!");
		}else{
			_package = packageAttr.getValue();
		}
		// Get Root Element attribute is versionCode
		Attribute versionCodeAttr = rootElement.attribute("versionCode");
		if(versionCodeAttr != null){
			versionCode = Integer.parseInt(versionCodeAttr.getValue());
		}
		// Get Root Element attribute is versionName
		Attribute versionNameAttr = rootElement.attribute("versionName");
		if(versionNameAttr != null){
			versionName = versionNameAttr.getValue();
		}
		
		// Get Root Element attribute is system
		Attribute systemAttr = rootElement.attribute("system");
		if(systemAttr != null){
			system = systemAttr.getValue();
		}
		// Get Root Element attribute is bit
		Attribute bitAttr = rootElement.attribute("bit");
		if(bitAttr != null){
			bit = Integer.parseInt(bitAttr.getValue());
		}	
		// Get Welcome Element From Root Element
		Element welcomeElement = rootElement.element("welcome");
		if(welcomeElement == null){
			throw new easyswtException("welcome标签不存在!");
		}
		// Get Welcome Element attribute is windowId
		Attribute windowIdAttr = welcomeElement.attribute("windowId");
		if(windowIdAttr == null){
			throw new easyswtException("welcome标签必须拥有windowId属性!");
		}else{
			welcomeId = windowIdAttr.getValue();
		}
		// Get Window TAG List from root element
		List<Element> list = rootElement.elements("window");
		if(list == null || list.size() == 0){
			throw new easyswtException("easyswt.xml至少需要包含一个window标签!");
		}
		windowList = new ArrayList<easyswtCoreWindow>();
		for(int i = 0 ; i < list.size() ; i ++){
			Element e = list.get(i);
			
			easyswtCoreWindow corewindow = new easyswtCoreWindow();
			corewindow.setClass(e.attributeValue("class"));
			corewindow.setId(e.attributeValue("id"));
			corewindow.setName(e.attributeValue("name"));
			
			windowList.add(corewindow);
			
			if(startElement == null){
				Attribute idAttr = e.attribute("id");
				if(idAttr != null && idAttr.getValue().equals(welcomeId)){
					startElement = corewindow;
				}
			}
			
		}
		if(startElement == null){
			throw new easyswtException("welcome所标记的windowId不存在!");
		}
		
	}	
	/**
	 * 启动SWT
	 * 如果startElement为空，则调用该方法无效，并且会抛出异常
	 * @throws easyswtException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void startEasySWT() throws easyswtException, InstantiationException, IllegalAccessException{
		if(startElement == null){
			throw new easyswtException("startElement不能为空!无法启动easyswt");
		}
		// 构造class信息
		String startClass = _package + startElement.getClass_();
		Class cls = null;
		try {
			cls = Class.forName(startClass);
		} catch (ClassNotFoundException e) {
			throw new easyswtException("找不到 "+startClass+" 不能为启动easyswt");
		}
		Class superCls = cls.getSuperclass();
		if(superCls == null || !superCls.getName().equals("cn.panshihao.easyswt.core.Window")){
			throw new easyswtException(startClass+" 没有继承于 window!所有的window类都必须继承于cn.panshihao.easyswt.core.Window");
		}
		// 实例化window，并传入默认data
		Window clsWindow = (Window) cls.newInstance();
		Data data = new Data();
		clsWindow.onCreate(data);
		
	}
	
	

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public SAXReader getSaxreader() {
		return saxreader;
	}

	public void setSaxreader(SAXReader saxreader) {
		this.saxreader = saxreader;
	}

	public String getPackage() {
		return _package;
	}

	public void setPackage(String _package) {
		this._package = _package;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public int getBit() {
		return bit;
	}

	public void setBit(int bit) {
		this.bit = bit;
	}
	public Element getRootElement() {
		return rootElement;
	}
	public void setRootElement(Element rootElement) {
		this.rootElement = rootElement;
	}
	public String getWelcomeId() {
		return welcomeId;
	}
	public void setWelcomeId(String welcomeId) {
		this.welcomeId = welcomeId;
	}
	public List<easyswtCoreWindow> getWindowList() {
		return windowList;
	}
	public void setWindowList(List<easyswtCoreWindow> windowList) {
		this.windowList = windowList;
	}
	public easyswtCoreWindow getStartElement() {
		return startElement;
	}
	public void setStartElement(easyswtCoreWindow startElement) {
		this.startElement = startElement;
	}

	
	
	
}
