package cn.panshihao.easyswt.window;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.wb.swt.layout.grouplayout.GroupLayout;
import swing2swt.layout.FlowLayout;
import swing2swt.layout.BoxLayout;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wb.swt.SWTResourceManager;

public class test {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			test window = new test();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		RowLayout rl_shell = new RowLayout(SWT.HORIZONTAL);
		shell.setLayout(rl_shell);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setText("New Button");
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.setText("New Button");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		lblNewLabel.setLayoutData(new RowData(110, 31));
		lblNewLabel.setText("New Label");

	}
}
