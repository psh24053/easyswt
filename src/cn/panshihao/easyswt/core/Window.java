package cn.panshihao.easyswt.core;

import org.eclipse.swt.widgets.Display;

public abstract class Window {
	
	private Data data;
	private Layout windowLayout;
	private Display display;
	
	public Window(){
		display = Display.getDefault();
	}
	
	public void onCreate(Data data){
		this.data = data;
	}
	public void onDestroy(){
		
	}
	public void destroy(){
		onDestroy();
		windowLayout.hide();
	}
	
	public void setWindowLayout(String layout){
		if(windowLayout != null){
			windowLayout.hide();
		}
		try {
			windowLayout = new Layout(layout, display);
			windowLayout.show();
		} catch (easyswtException e) {
			e.printStackTrace();
		}
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	public Display getDisplay() {
		return display;
	}
	public void setDisplay(Display display) {
		this.display = display;
	}
	public Layout getWindowLayout() {
		return windowLayout;
	}
	public void setWindowLayout(Layout windowLayout) {
		this.windowLayout = windowLayout;
	}
	

}
