package cn.panshihao.easyswt.window;

import cn.panshihao.easyswt.core.Data;
import cn.panshihao.easyswt.core.Window;

public class mainWindow extends Window {
	
	@Override
	public void onCreate(Data data) {
		// TODO Auto-generated method stub
		System.out.println("onCreate window "+data);
		setWindowLayout("layout_main.xml");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

}
