package cn.panshihao.easyswt.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class Layout {

	private String layoutResource;
	private SAXReader saxreader;
	private Document document;
	private Element rootElement;
	private Display display;
	private int layoutWidth;
	private int layoutHeight;
	private Shell layoutShell;
	private String layoutLocation;
	
	private String title;
	private String icon;
	
	private Map<String, Control> ElementMap;
	
	public Layout(String layout, Display display) throws easyswtException{
		layoutResource = layout;
		this.display = display;
		
		if(layoutResource != null && !layoutResource.equals("")){
			saxreader = new SAXReader();
			try {
				document = saxreader.read(new File(easyswtCore.layoutPath, layoutResource));
			} catch (DocumentException e) {
				throw new easyswtException(e.getMessage());
			}
		}else{
			throw new easyswtException("layout不能为空!");
		}
		
		rootElement = document.getRootElement();
		// 开始解析layout
		parseLayout();
		
	}
	/**
	 * 获取宽度高度值，可能是百分比或者px，将转换为整型
	 * @param widthValue
	 * @return
	 * @throws easyswtException 
	 */
	private int getSizeValue(String Value, int maxValue, String errorMsg) throws easyswtException{
		// 判断高度值的格式是否符合标准
		if(!Value.matches("\\d+(%|px)")){
			if(errorMsg != null){
				throw new easyswtException(errorMsg);
			}
		}
		// 通过计算，得到Layout的实际宽度
		if(Value.contains("%")){
			return (int) (maxValue * (double)(Double.parseDouble(Value.replace("%", "")) / 100)); 
		}else if(Value.contains("px")){
			return Integer.parseInt(Value.replace("px", ""));
		}
		
		return 0;
	}
	
	/**
	 * 解析layout，将layout转换为swt
	 * @throws easyswtException 
	 */
	private void parseLayout() throws easyswtException{
		if(!rootElement.getName().equals("window")){
			throw new easyswtException("layout的根元素必须为window!");
		}
		// 获取宽度值
		String widthValue = rootElement.attributeValue("width");
		if(widthValue == null){
			throw new easyswtException("window的width属性不能为空!");
		}
		layoutWidth = getSizeValue(widthValue, display.getClientArea().width, "width属性格式错误！只能为百分比（100%）或像素(200px)");
		
		
		// 获取高度值
		String heightValue = rootElement.attributeValue("height");
		if(heightValue == null){
			throw new easyswtException("window的height属性不能为空!");
		}
		layoutHeight = getSizeValue(heightValue, display.getClientArea().height, "width属性格式错误！只能为百分比（100%）或像素(200px)");
		
		// 获取window的location属性
		layoutLocation = rootElement.attributeValue("location");
		if(layoutLocation == null){
			layoutLocation = "center";
		}
		
		
		// 获取title元素
		Element titleElement = rootElement.element("title");
		if(titleElement == null){
			throw new easyswtException("window标签中必须包含一个title标签！");
		}
		// 获取title元素上的属性
		title = titleElement.attributeValue("name");
		if(title == null){
			title = "Easy SWT";
		}
		icon = titleElement.attributeValue("icon");
		if(icon == null){
			icon = "@logo";
		}
		
		// 为该Layout构造shell
		layoutShell = new Shell(display, SWT.MIN | SWT.CLOSE | SWT.MAX);
		layoutShell.setText(title);
		layoutShell.setImage(new ImageHandler(display, icon).getImage());
		layoutShell.setSize(layoutWidth, layoutHeight);
		RowLayout rowlayout = new RowLayout(SWT.HORIZONTAL);
		rowlayout.marginBottom = 0;
		rowlayout.marginLeft = 0;
		rowlayout.marginRight = 0;
		rowlayout.marginTop = 0;
		rowlayout.spacing = 0;
		layoutShell.setLayout(rowlayout);
		
		// 设置layout的位置
		switch (layoutLocation) {
		case "center":
			layoutShell.setLocation(getCenterX(layoutShell), getCenterY(layoutShell));
			break;

		default:
			break;
		}
		
		// 设置程序是否最大化
		String maximized = rootElement.attributeValue("maximized");
		if(maximized != null){
			layoutShell.setMaximized(Boolean.parseBoolean(maximized));
		}
		
		loadElement();
		
		
	}
	/**
	 * 加载子元素
	 * @throws easyswtException 
	 */
	private void loadElement() throws easyswtException{
		Element contentElement = rootElement.element("content");
		if(contentElement == null){
			throw new easyswtException("window标签中必须包含一个content标签！");
		}
		
		// 获取content标签中的所有元素
		List<Element> elements = contentElement.elements();
		ElementMap = new HashMap<String, Control>();
		
		for(int i = 0 ; i < elements.size() ; i ++){
			Element item = elements.get(i);
			Control control = GenerateControl(item, layoutShell);
			if(control != null ){
				if(control.getData("id") != null){
					ElementMap.put(control.getData("id").toString(), control);
				}else{
					ElementMap.put(control.toString(), control);
				}
			}
		}
		
		
	}
	/**
	 * 根据传入的element生成control
	 * @param e
	 * @return
	 * @throws easyswtException 
	 */
	private Control GenerateControl(Element e, Composite parent) throws easyswtException{
		Control control = null;
		
		/*
		 * 从element中获得各种属性
		 */
		String id = e.attributeValue("id");
		String marginTop = e.attributeValue("marginTop");
		String marginLeft = e.attributeValue("marginLeft");
		String marginRight = e.attributeValue("marginRight");
		String marginBottom = e.attributeValue("marginBottom");
		String align = e.attributeValue("align");
		String background = e.attributeValue("background");		
		
		
		/*
		 * 根据element的类型来构造control
		 */
		switch (e.getName()) {
		/**
		 * label组件，显示文字标签
		 */
		case "label":
			control = new Label(parent, SWT.NONE);
			((Label)control).setText(e.getStringValue());
			((Label)control).setAlignment(SWT.CENTER);
			break;
		/**
		 * Button组件，按钮组件
		 */
		case "button":
				
			control = new Button(parent, SWT.NONE);
			((Button)control).setText(e.getStringValue());
			break;
		/**
		 * composite组件，块组件。用于包裹其他元素
		 */
		case "composite":
			if(align != null && align.equals("right")){
				control = new Composite(parent, SWT.RIGHT_TO_LEFT);
			}else{
				control = new Composite(parent, SWT.NONE);
			}
			((Composite)control).setLayout(new RowLayout(SWT.HORIZONTAL));
			break;
		default:
			break;
		}
		// 让组件生成布局位置
		if(!(control instanceof Composite)){
			layoutShell.layout();
			/*
			 * 当control的类型不为composite时，要立即为每一个元素设置大小
			 */
			
			// 获取标签上的width和height属性
			int width = control.getBounds().width;
			int height = control.getBounds().height;
			String widthValue = e.attributeValue("width");
			if(widthValue != null){
				int maxWidth = 0;
				if(parent.getBounds().width != Integer.parseInt(parent.getData("width").toString())){
					maxWidth = Integer.parseInt(parent.getData("width").toString());
				}else{
					maxWidth = parent.getBounds().width;
				}
				width = getSizeValue(widthValue, maxWidth, "width属性格式错误！只能为百分比（100%）或像素(200px)");
			}
			String heightValue = e.attributeValue("height");
			if(heightValue != null){
				int maxHeight = 0;
				if(parent.getBounds().height != Integer.parseInt(parent.getData("height").toString())){
					maxHeight = Integer.parseInt(parent.getData("height").toString());
				}else{
					maxHeight = parent.getBounds().height;
				}
				height = getSizeValue(heightValue, maxHeight, "height属性格式错误！只能为百分比（100%）或像素(200px)");
			}
			
			control.setLayoutData(new RowData(width, height));
		}else{
			/*
			 * 当control的类型为composite时，要先把大小保存到data中，等到该composite的所有子元素被构造完成后
			 * 再为其设置大小
			 */
			String widthValue = e.attributeValue("width");
			if(widthValue != null){
				int maxWidth = 0;
				if(parent.getData("width") != null && parent.getBounds().width != Integer.parseInt(parent.getData("width").toString())){
					maxWidth = Integer.parseInt(parent.getData("width").toString());
				}else{
					maxWidth = parent.getBounds().width;
				}
				int width = getSizeValue(widthValue, maxWidth, "width属性格式错误！只能为百分比（100%）或像素(200px)");
				control.setData("width", width);
			}
			String heightValue = e.attributeValue("height");
			if(heightValue != null){
				int maxHeight = 0;
				if(parent.getData("height") != null && parent.getBounds().height != Integer.parseInt(parent.getData("height").toString())){
					maxHeight = Integer.parseInt(parent.getData("height").toString());
				}else{
					maxHeight = parent.getBounds().height;
				}
				int height = getSizeValue(heightValue, maxHeight, "height属性格式错误！只能为百分比（100%）或像素(200px)");
				control.setData("height", height);
			}
		}
		
		/*
		 * 如果background属性不为Null，则为每一个元素设置背景
		 * background格式：
		 * 1.@xxxx   代表调用drawable目录下的资源图片
		 * 2.#ffffff 代表16进制颜色代码
		 */
		if(background != null){
			
			switch (background.charAt(0)) {
			case '@':
				control.setBackgroundImage(new ImageHandler(display, background).getImage());
				break;
			case '#':
				int r = Integer.parseInt("0x" + background.substring(1, 3));
				int g = Integer.parseInt("0x" + background.substring(3, 5));
				int b = Integer.parseInt("0x" + background.substring(5, 7));
				System.out.println(r+" "+g+" "+b);
				display.getSystemColor(SWT.COLOR_BLACK);
//				control.setBackground(arg0)
				break;
			default:
				break;
			}
			
		}
		
		
		
		/*
		 * 将ID属性保存到data中
		 */
		if(id != null){
			control.setData("id", id);
		}
		// 将xml的element 保存到control中
		control.setData("dom", e);
		
		// 遍历该元素的子元素
		List<Element> list = e.elements();
		// 如果control是一个composite并且对齐方式是right，那么将逆序生成元素
		if(control instanceof Composite && align != null && align.equals("right")){
			for(int i = list.size() - 1 ; i >= 0 ; i --){
				Element item = list.get(i);
				GenerateControl(item, (Composite)control);
			}
		}else{
			for(int i = 0 ; i < list.size() ; i ++){
				Element item = list.get(i);
				GenerateControl(item, (Composite)control);
			}
		}
		
		// 当composite的所有子元素已经layout完毕之后，最后来构造composite
		if(control instanceof Composite){
			// 获取标签上的width和height属性
			layoutShell.layout();
			int width = control.getBounds().width;
			int height = control.getBounds().height;
			
			if(control.getData("width") != null){
				width = Integer.parseInt(control.getData("width").toString());
			}
			if(control.getData("height") != null){
				height = Integer.parseInt(control.getData("height").toString());
			}
			
			control.setLayoutData(new RowData(width, height));
		}
		
			
		
		return control;
	}
	/**
	 * 根据shell，计算该shell如果出现在屏幕中央，那么需要的X坐标
	 * @param shell
	 * @return
	 */
	public int getCenterX(Shell shell){
		if(shell == null){
			return -1;
		}
		Rectangle shellBounds = shell.getBounds();
		int x = display.getPrimaryMonitor().getBounds().x + (display.getPrimaryMonitor().getBounds().width - shellBounds.width)>>1;
		return x;
	}
	/**
	 * 根据shell，计算该shell如果出现在屏幕中央，那么需要的Y坐标
	 * @param shell
	 * @return
	 */
	public int getCenterY(Shell shell){
		if(shell == null){
			return -1;
		}
		Rectangle shellBounds = shell.getBounds();
		int y = display.getPrimaryMonitor().getBounds().y + (display.getPrimaryMonitor().getBounds().height - shellBounds.height)>>1;
		return y;
	}
	/**
	 * 显示这个layout
	 */
	public void show(){
		if(layoutShell != null){
			// 显示layout
			layoutShell.layout();
			layoutShell.open();
		}
		while (!layoutShell.isDisposed()) {
			if (!layoutShell.getDisplay().readAndDispatch()) {
				layoutShell.getDisplay().sleep();
			}
		}
	}
	/**
	 * 隐藏这个layout
	 */
	public void hide(){
		if(layoutShell != null){
			layoutShell.setVisible(false);
		}
	}
	/**
	 * 销毁这个layout，释放内存
	 */
	public void destroy(){
		if(layoutShell != null){
			layoutShell.dispose();
		}
	}
	
}
