package acapulco.images.utils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class InteractiveScoreDialog extends Dialog {

	Image image;
	int score;

	public InteractiveScoreDialog(Shell parentShell, Image image) {
		super(parentShell);
		this.image = image;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Label img = new Label(container, SWT.NONE);
		img.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		img.setImage(image);
		// container.pack();
		return container;
	}

	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Score");
	}

	public double getScore() {
		return score;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button1 = createButton(parent, IDialogConstants.OK_ID, "1", false);
		button1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				score = 1;
			}
		});
		Button button2 = createButton(parent, IDialogConstants.OK_ID, "2", false);
		button2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				score = 2;
			}
		});
		Button button3 = createButton(parent, IDialogConstants.OK_ID, "3", false);
		button3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				score = 3;
			}
		});
		Button button4 = createButton(parent, IDialogConstants.OK_ID, "4", false);
		button4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				score = 4;
			}
		});
		Button button5 = createButton(parent, IDialogConstants.OK_ID, "5", false);
		button5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				score = 5;
			}
		});
	}

}