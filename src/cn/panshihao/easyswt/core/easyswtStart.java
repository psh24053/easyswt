package cn.panshihao.easyswt.core;

import java.io.File;

/**
 * 启动easyswt
 * @author shihao
 *
 */
public class easyswtStart {

	public static String easyswtXML = "easyswt.xml";
	
	
	public static void main(String[] args) {
		
		/**
		 * 1.首先加载项目根目录下的easyswt.xml文件
		 * 将程序信息和window声明保存到内存中
		 * 2.然后开始启动easyswt
		 */
		easyswtCore core = null;
		
		try {
			core = new easyswtCore(new File(easyswtXML));
			core.startEasySWT();
		} catch (easyswtException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
		
		
		
	}
	
}
