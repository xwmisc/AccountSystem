package com.xw.GUI;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.highgui.ImageWindow;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.utils.Converters;
import org.eclipse.swt.widgets.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			System.out.println("0");
			System.loadLibrary("opencv_java341");
			System.loadLibrary("opencv_world341d");
			System.loadLibrary("opencv_world341");
			System.loadLibrary("ASNative");
			System.out.println("1");

			File file = new File("C:/Users/acer-pc/Pictures/发票/xt1.png");
			FileInputStream in = new FileInputStream(file);
			byte[] b = new byte[in.available()];
			in.read(b);
			List<Byte> list = new ArrayList();
			for (int i = 0; i < b.length; i++)
				list.add(b[i]);


//			Mat mat = Converters.vector_char_to_Mat(list);
			 Mat mat = Imgcodecs.imread("t1.png");
			// Mat mat = Imgcodecs.imread();

			System.out.println(mat.rows());
			System.out.println(mat.cols());

			ArrayList<Byte> bl = new ArrayList<>();
//			Converters.Mat_to_vector_char(mat, bl);
//			HighGui.imshow("x", mat);
//			HighGui.waitKey();

			GUI window = new GUI();
			System.out.println("2");
			window.open();
			System.out.println("3");
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

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Image img = shell.

				try {
					File file = new File("C:/Users/acer-pc/Pictures/发票/t1.png");
					FileInputStream in = new FileInputStream(file);
					byte[] b = new byte[in.available()];
					in.read(b);
					List<Byte> list = new ArrayList();
					for (int i = 0; i < b.length; i++)
						list.add(b[i]);

					Mat mat = Converters.vector_char_to_Mat(list);
					System.out.println(mat.rows());
					final ArrayList<Byte> bl = new ArrayList<>();
					Converters.Mat_to_vector_char(mat, bl);
					HighGui.imshow("x", mat);
					HighGui.waitKey();

				} catch (Exception e1) {
					e1.printStackTrace();
				}

				setimg(null);
			}
		});
		btnNewButton.setBounds(58, 60, 98, 30);
		btnNewButton.setText("New Button");
		
				Button btn2 = new Button(shell, SWT.NONE);
				btn2.setSize(98, 30);
				btn2.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						// Image image = (Image) imageView.getData("image");
						// byte[] b =Util.swtImg2PngByte(image);
						// byte[] b2=Opencv.cut(b, 15, 15,
						// image.getBounds().width,image.getBounds().height);
						byte[] b = Opencv.cut(12);
						System.out.println(b[0]);
						// org.eclipse.swt.graphics.ImageData idata = Util.swtPngByte2Img(b2);
						// Image image2 = new Image(Display.getDefault(), idata);
						// setimg(image2);
					}
				});
				btn2.setText("New Button");
				
						imageView = new Canvas(shell, SWT.NONE);
						imageView.addPaintListener(new PaintListener() {
							public void paintControl(PaintEvent event) {
								// System.out.println("p");
								Image image = (Image) imageView.getData("image");
								image.getBounds();
								event.gc.drawImage(image, 0, 0);
								// event.gc.drawImage(image, 0, 0, image.getBounds().width,
								// image.getBounds().height, 0, 0, event.width, event.height);
							}
						});
						imageView.setBounds(0, 0, 1000, 1000);
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
