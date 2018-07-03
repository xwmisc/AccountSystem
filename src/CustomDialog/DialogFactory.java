package CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DialogFactory {

	public static void showMsg(Shell shell, String message) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				int style = SWT.APPLICATION_MODAL | SWT.YES;
				MessageBox messageBox = new MessageBox(shell, style);
				messageBox.setText("Tip");
				messageBox.setMessage(message);
				messageBox.open();
				
			}
		});
	}

	public static void showErr(Shell shell, String err) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				int style = SWT.APPLICATION_MODAL | SWT.ERROR;
				MessageBox messageBox = new MessageBox(shell, style);
				messageBox.setText("Error Occurred!");
				messageBox.setMessage(err);
				messageBox.open();
			}
		});

	}

	public static void showCustomCheckboxList(Shell shell, ArrayList<CheckboxBean> beans) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				int style = shell.getStyle() | SWT.APPLICATION_MODAL;
				CustomDialog dialog = new CustomDialog(shell, style, "CustomCheckboxList");
				dialog.makeCheckboxList(beans, 20, 10, 30);
				dialog.open();
			}
		});
	}

}
