package CustomDialog;

import java.util.function.Consumer;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

public class CheckboxBean {
	protected String text;

	protected boolean selected;
	protected SelectionListener listener;

	public CheckboxBean(String text, boolean selected, final Consumer<Boolean> listener) {
		super();
		this.text = text;
		this.selected = selected;
		setListener(listener);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public SelectionListener getListener() {
		return listener;
	}

	public void setListener(final Consumer<Boolean> e) {
		this.listener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e1) {
				e.accept(((Button)e1.getSource()).getSelection());
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e1) {
			}
		};
	}

}
