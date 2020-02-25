/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.GUIs.settings;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import com.strikewire.snl.apc.validation.ValidationUtils;

public abstract class AbstractMessageView extends ViewPart implements IMessageView {

	/**
	 * Image registry key for info message image (value
	 * <code>"dialog_messasge_info_image"</code>).
	 * 
	 * @since 2.0
	 */
	public static final String DLG_IMG_MESSAGE_INFO = "dialog_messasge_info_image";
	/**
	 * Image registry key for info message image (value
	 * <code>"dialog_messasge_warning_image"</code>).
	 * 
	 * @since 2.0
	 */
	public static final String DLG_IMG_MESSAGE_WARNING = "dialog_messasge_warning_image";
	/**
	 * Image registry key for info message image (value
	 * <code>"dialog_message_error_image"</code>).
	 * 
	 * @since 2.0
	 */
	public static final String DLG_IMG_MESSAGE_ERROR = "dialog_message_error_image";
	private static final int MAX_MESSAGE_LINES = 3;
	protected String message = "";
	protected Text messageLabel;
	protected Label messageImageLabel;
	protected Image messageImage;
	protected int xTrim;
	
	private boolean checkErrorLevel = false;
	
	private int previousType = 0;

	public AbstractMessageView() {
		super();
	}

	/**
	 * Sets the message for this dialog with an indication of what type of
	 * message it is.
	 * <p>
	 * The valid message types are one of <code>NONE</code>,
	 * <code>INFORMATION</code>,<code>WARNING</code>, or
	 * <code>ERROR</code>.
	 * </p>
	 * <p>
	 * Note that for backward compatibility, a message of type
	 * <code>ERROR</code> is different than an error message (set using
	 * <code>setErrorMessage</code>). An error message overrides the current
	 * message until the error message is cleared. This method replaces the
	 * current message and does not affect the error message.
	 * </p>
	 * 
	 * @param newMessage
	 *            the message, or <code>null</code> to clear the message
	 * @param newType
	 *            the message type
	 * @since 2.0
	 */
	@Override
	public final void setMessage(String newMessage, int newType)
	{		
		if(checkErrorLevel && previousType > newType){
			return;
		}
		
		previousType = newType;
		
		if(messageLabel == null || messageLabel.isDisposed())
		{
			return;
		}
		
		Image newImage = null;
		if (newMessage != null) {
			switch (newType) {
			case IMessageProvider.NONE:
				break;
			case IMessageProvider.INFORMATION:
				newImage = JFaceResources.getImage(DLG_IMG_MESSAGE_INFO);
				break;
			case IMessageProvider.WARNING:
				newImage = JFaceResources.getImage(DLG_IMG_MESSAGE_WARNING);
				break;
			case IMessageProvider.ERROR:
				newImage = JFaceResources.getImage(DLG_IMG_MESSAGE_ERROR);
				break;
			}
		}
		if(StringUtils.isBlank(newMessage)) {
			newMessage = getDefaultMessage();
			newImage = null;
		}
		
		// Any change?
		if(message.equals(newMessage) && messageImage==newImage) {
			return;
		}
		
		if (!StringUtils.isBlank(newMessage)) {
			String[] lines = newMessage.split("[\\n\\r]+");
			if (lines.length > MAX_MESSAGE_LINES) {
				StringBuilder builder = new StringBuilder();
				for (int i=0; i<Math.min(lines.length, MAX_MESSAGE_LINES); ++i) {
					builder.append(lines[i]).append("\n");
				}
				builder.append("...");
				newMessage = builder.toString();
			}
		}
		
		if(newMessage == null)
		{
			newMessage = "";
		}
		
		message = newMessage;
		messageImage = newImage;
		
		messageLabel.setText(newMessage);
		messageImageLabel.setImage(messageImage);
		layoutForNewMessage();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.settings.IMessageView#setMessageFor(org.eclipse.core.runtime.IStatus)
	 */
	@Override
	public void setMessageFor(IStatus status) {		
		String msg = null;
		int msgType = IMessageProvider.NONE;
		
		if(status!=null && !status.isOK())
		{
			int severity = status.getSeverity();
			msgType = ValidationUtils.convertSeverityStatusToMessageProvider(severity);
			msg = ValidationUtils.generateMessage(status, true, false, true, MAX_MESSAGE_LINES);
		}
		setMessage(msg, msgType);
	}
	
	@Override
	public void checkErrorLevel(boolean checkErrorLevel){
		this.checkErrorLevel = checkErrorLevel;
	}

	

	protected abstract String getDefaultMessage();
	
	protected abstract void layoutForNewMessage();

	protected Composite createMessageArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		// composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		
		messageImageLabel = new Label(composite, SWT.CENTER);
		// Message label @ bottom, center
		messageLabel = new Text(composite, SWT.WRAP | SWT.READ_ONLY | SWT.NO_BACKGROUND);
		messageLabel.setFont(JFaceResources.getDialogFont());
		//messageLabel.setLayoutData(new GridData(GridData.FILL_BOTH));		

		// computing trim for later
		Rectangle rect = messageLabel.computeTrim(0, 0, 100, 100);
		xTrim = rect.width - 100;
		
		messageLabel.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				layoutForNewMessage();
			}
		});
		message = "";
		return composite;
	}

}
