/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by mjgibso on Oct 18, 2013 at 6:23:37 AM
 */
package com.strikewire.snl.apc.Common;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.splash.BasicSplashHandler;
import org.osgi.framework.Bundle;

/**
 * @author mjgibso
 *
 */
public class MessageSplashHandler extends BasicSplashHandler
{
	public static final String SPLASH_TEXT_KEY = "splashMessage";
	public static final String SPLASH_TEXT_POINT = "splashMessagePoint";
	
	@Override
	public void init(Shell splash)
	{
		super.init(splash);
		
		IProduct product = Platform.getProduct();
		
		if(product == null)
		{
			CommonPlugin.getDefault().logError("Error initializing splash.  Product is null.", new Exception());
			return;
		}
		
		String progressRectString = product.getProperty(IProductConstants.STARTUP_PROGRESS_RECT);
		String messageRectString = product.getProperty(IProductConstants.STARTUP_MESSAGE_RECT);
		String foregroundColorString = product.getProperty(IProductConstants.STARTUP_FOREGROUND_COLOR);
		
		Rectangle progressRect;
		try {
			progressRect = StringConverter.asRectangle(progressRectString);
		} catch(Throwable t) {
			CommonPlugin.getDefault().logError("Error reading startup splash progress rectangle: "+progressRectString, t);
			progressRect = new Rectangle(0, 0, 100, 15);
		}
		setProgressRect(progressRect);
		
		Rectangle messageRect;
		try {
			messageRect = StringConverter.asRectangle(messageRectString);
		} catch(Throwable t) {
			CommonPlugin.getDefault().logError("Error reading startup splash message rectangle: "+messageRectString, t);
			messageRect = new Rectangle(0, 20, 100, 15);
		}
		setMessageRect(messageRect);
		
		RGB foregroundColor;
		try {
			foregroundColor = StringConverter.asRGB(foregroundColorString);
		} catch(Throwable t) {
			try {
				foregroundColor = parseColor(foregroundColorString);
			} catch(Throwable t2) {
				CommonPlugin.getDefault().logError("Error reading startup splash foreground color: "+foregroundColorString, t);
				foregroundColor = new RGB(0, 0, 0);
			}
		}
		setForeground(foregroundColor);
		
		
		Composite content = getContent();
		
		String messageString = product.getProperty(SPLASH_TEXT_KEY);
		if(StringUtils.isNotBlank(messageString))
		{
			String messagePointString = product.getProperty(SPLASH_TEXT_POINT);
			Point reqMsgLoc;
			try {
				reqMsgLoc = StringConverter.asPoint(messagePointString);
			} catch(Throwable t) {
				CommonPlugin.getDefault().logError("Error reading startup splash message point: "+messagePointString, t);
				reqMsgLoc = new Point(10, 40);
			}
			final String splashMsg = resolveMessage(messageString, product.getDefiningBundle());
			Label l = new Label(content, SWT.NONE);
			l.setText(splashMsg);
			Point imageSize = content.getSize();
			Point messageSize = l.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int msgX;
			if(reqMsgLoc.x >= 0)
			{
				// if positive, left justify
				msgX = reqMsgLoc.x;
			} else {
				// if negative, right justify (add the loc, because it's already negative)
				msgX = imageSize.x - messageSize.x + reqMsgLoc.x;
				// if it would start before the image, start at 0, and let it truncate off the end
				msgX = Math.max(msgX, 0);
			}
			int msgY;
			if(reqMsgLoc.y >= 0)
			{
				msgY = reqMsgLoc.y;
			} else {
				// if negative, tie to the bottom of the image (add the loc, because it's already negative)
				msgY = imageSize.y - messageSize.y + reqMsgLoc.y;
				// if it would start before the image, start at 0, and let it truncate off the end
				msgY = Math.max(msgY, 0);
			}
			final Point fadjMsgLoc = new Point(msgX, msgY);
			
			content.addPaintListener(new PaintListener() {
				
				@Override
				public void paintControl(PaintEvent e) {
					e.gc.setForeground(getForeground());
					e.gc.drawText(splashMsg, fadjMsgLoc.x, fadjMsgLoc.y, true);
				}
			});
		}
	}
	
	protected RGB parseColor(String colorString)
	{
		int foregroundColorInteger = Integer.parseInt(colorString, 16);

		return new RGB((foregroundColorInteger & 0xFF0000) >> 16,
				(foregroundColorInteger & 0xFF00) >> 8,
				foregroundColorInteger & 0xFF);
	}
	
	protected String resolveMessage(String message, Bundle bundle)
	{
		try {
			Object[] mappings = MappingsUtil.getMappings(bundle);
	        return MessageFormat.format(message, mappings);
		} catch (Throwable t) {
			CommonPlugin.getDefault().logError("Error resolving startup splash message: "+message, t);
			return message;
		}
	}
}
