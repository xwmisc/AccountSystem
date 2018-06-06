package com.xw.GUI;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

import com.sun.scenario.effect.ImageData;
import com.xw.Opencv;
import com.xw.Util;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;

public class AdjustImage extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Canvas imageView;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.loadLibrary("ASNative");
			GUI window = new GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public AdjustImage(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
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

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 486);
		shell.setText(getText());

		imageView = new Canvas(shell, SWT.NONE);
		imageView.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				System.out.println("p");
				Image image = (Image) imageView.getData("image");
				image.getBounds();
				event.gc.drawImage(image, 0, 0);
				// event.gc.drawImage(image, 0, 0, image.getBounds().width,
				// image.getBounds().height, 0, 0, event.width, event.height);
			}
		});
		imageView.setBounds(0, 0, 1000, 1000);
		
		Button btnNewButton_1 = new Button(imageView, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Image image = (Image) imageView.getData("image");
				byte[] b =Util.swtImg2PngByte(image);
				byte[] b2=Opencv.cut(b, 15, 15, image.getBounds().width,image.getBounds().height);
				org.eclipse.swt.graphics.ImageData idata = Util.swtPngByte2Img(b2);
				Image image2 = new Image(Display.getDefault(), idata);
				setimg(image2);
			}
		});
		btnNewButton_1.setBounds(29, 155, 98, 30);
		btnNewButton_1.setText("New Button");

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Image img = shell.
				setimg(null);
			}
		});
		btnNewButton.setBounds(58, 60, 98, 30);
		btnNewButton.setText("New Button");
		setimg(null);

	}

	void setimg(Image image) {
		System.out.println(imageView.getBounds());
		if (image == null)
			image = new Image(Display.getDefault(), "C:\\Users\\acer-pc\\Pictures\\发票\\t5.jpg");
		// imageView.setBounds(imageView.getBounds().x, imageView.getBounds().y,
		// image.getBounds().width,
		// image.getBounds().height);
		System.out.println(imageView.getBounds());
		imageView.setData("image", image);

	}
}
