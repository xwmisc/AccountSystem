package CustomDialog;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CustomDialog extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public CustomDialog(Shell parent, int style, String title) {
		super(parent, style);
		setText(title);
		shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	public void makeCheckboxList(List<CheckboxBean> beans, int x, int y, int offset) {
		try {
			int maxWidth = 70;
			for (int i = 0; i < beans.size(); i++) {
				CheckboxBean bean = beans.get(i);
				Button checkbox = new Button(shell, SWT.CHECK);
				int width = 12 + bean.text.getBytes().length * 10;
				checkbox.setBounds(x, y + offset * i, width, 30);
				checkbox.setText(bean.getText());
				checkbox.setSelection(bean.selected);
				if (bean.listener != null)
					checkbox.addSelectionListener(bean.listener);
				if (maxWidth < width)
					maxWidth = width;
			}
			shell.setSize(maxWidth + x + 10, y + (beans.size() - 1) * offset + 30 + 60);
		} catch (Exception e) {
			e.printStackTrace();
			DialogFactory.showErr(shell, e.toString());
		}
	}

}
