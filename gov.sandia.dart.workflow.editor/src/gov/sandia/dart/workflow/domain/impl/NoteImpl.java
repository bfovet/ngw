/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 */
package gov.sandia.dart.workflow.domain.impl;

import gov.sandia.dart.workflow.domain.DomainPackage;
import gov.sandia.dart.workflow.domain.Note;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.swt.SWT;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Note</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.NoteImpl#getText <em>Text</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.NoteImpl#getColor <em>Color</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.NoteImpl#getFont <em>Font</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.NoteImpl#getFontHeight <em>Font Height</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.NoteImpl#getFontColor <em>Font Color</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.NoteImpl#getFontStyle <em>Font Style</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NoteImpl extends MinimalEObjectImpl.Container implements Note {
	/**
	 * The default value of the '{@link #getText() <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getText()
	 * @generated
	 * @ordered
	 */
	protected static final String TEXT_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getText() <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getText()
	 * @generated
	 * @ordered
	 */
	protected String text = TEXT_EDEFAULT;
	
	/**
	 * The default value of the '{@link #getColor() <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected static final String COLOR_EDEFAULT = null;

	protected String color = null;

	/**
	 * The default value of the '{@link #getFont() <em>Font</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFont()
	 * @generated
	 * @ordered
	 */
	protected static final String FONT_EDEFAULT =  null;

	/**
	 * The cached value of the '{@link #getFont() <em>Font</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFont()
	 * @generated
	 * @ordered
	 */
	protected String font = FONT_EDEFAULT;

	/**
	 * The default value of the '{@link #getFontHeight() <em>Font Height</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFontHeight()
	 * @generated
	 * @ordered
	 */
	protected static final int FONT_HEIGHT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getFontHeight() <em>Font Height</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFontHeight()
	 * @generated
	 * @ordered
	 */
	protected int fontHeight = FONT_HEIGHT_EDEFAULT;

	/**
	 * The default value of the '{@link #getFontColor() <em>Font Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFontColor()
	 * @generated
	 * @ordered
	 */
	protected static final String FONT_COLOR_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getFontColor() <em>Font Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFontColor()
	 * @generated
	 * @ordered
	 */
	protected String fontColor = FONT_COLOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getFontStyle() <em>Font Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFontStyle()
	 * @generated
	 * @ordered
	 */
	protected static final int FONT_STYLE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getFontStyle() <em>Font Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFontStyle()
	 * @generated
	 * @ordered
	 */
	protected int fontStyle = FONT_STYLE_EDEFAULT;

	protected static final IColorConstant DEFAULT_BACKGROUND = new ColorConstant("FEFEF4");

	protected static final IColorConstant DEFAULT_FOREGROUND = IColorConstant.BLACK;


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NoteImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DomainPackage.Literals.NOTE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setText(String newText) {
		String oldText = text;
		text = newText;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.NOTE__TEXT, oldText, text));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getColor() {
		return color;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setColor(String newColor) {
		String oldColor = color;
		color = newColor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.NOTE__COLOR, oldColor, color));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFont() {
		return font;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFont(String newFont) {
		String oldFont = font;
		font = newFont;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.NOTE__FONT, oldFont, font));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getFontHeight() {
		return fontHeight;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFontHeight(int newFontHeight) {
		int oldFontHeight = fontHeight;
		fontHeight = newFontHeight;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.NOTE__FONT_HEIGHT, oldFontHeight, fontHeight));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFontColor() {
		return fontColor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFontColor(String newFontColor) {
		String oldFontColor = fontColor;
		fontColor = newFontColor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.NOTE__FONT_COLOR, oldFontColor, fontColor));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getFontStyle() {
		return fontStyle;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFontStyle(int newFontStyle) {
		int oldFontStyle = fontStyle;
		fontStyle = newFontStyle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.NOTE__FONT_STYLE, oldFontStyle, fontStyle));
	}

	public IColorConstant getBackgroundColor(){	
		
		IColorConstant colorObject = deserializeColor(color);
		
		if(colorObject == null){
			return DEFAULT_BACKGROUND;
		}
		
		return colorObject;
	}
	
	public boolean hasCustomBackground(){		
		IColorConstant colorObject = deserializeColor(color);
		return (colorObject != null);
	}

	public void setBackgroundColor(IColorConstant newColor){
		setColor(serializeColor(newColor));
	}
	
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DomainPackage.NOTE__TEXT:
				return getText();
			case DomainPackage.NOTE__COLOR:
				return getColor();
			case DomainPackage.NOTE__FONT:
				return getFont();
			case DomainPackage.NOTE__FONT_HEIGHT:
				return getFontHeight();
			case DomainPackage.NOTE__FONT_COLOR:
				return getFontColor();
			case DomainPackage.NOTE__FONT_STYLE:
				return getFontStyle();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DomainPackage.NOTE__TEXT:
				setText((String)newValue);
				return;
			case DomainPackage.NOTE__COLOR:
				setColor((String)newValue);
				return;
			case DomainPackage.NOTE__FONT:
				setFont((String)newValue);
				return;
			case DomainPackage.NOTE__FONT_HEIGHT:
				setFontHeight((Integer)newValue);
				return;
			case DomainPackage.NOTE__FONT_COLOR:
				setFontColor((String)newValue);
				return;
			case DomainPackage.NOTE__FONT_STYLE:
				setFontStyle((Integer)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case DomainPackage.NOTE__TEXT:
				setText(TEXT_EDEFAULT);
				return;
			case DomainPackage.NOTE__COLOR:
				setColor(COLOR_EDEFAULT);
				return;
			case DomainPackage.NOTE__FONT:
				setFont(FONT_EDEFAULT);
				return;
			case DomainPackage.NOTE__FONT_HEIGHT:
				setFontHeight(FONT_HEIGHT_EDEFAULT);
				return;
			case DomainPackage.NOTE__FONT_COLOR:
				setFontColor(FONT_COLOR_EDEFAULT);
				return;
			case DomainPackage.NOTE__FONT_STYLE:
				setFontStyle(FONT_STYLE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DomainPackage.NOTE__TEXT:
				return TEXT_EDEFAULT == null ? text != null : !TEXT_EDEFAULT.equals(text);
			case DomainPackage.NOTE__COLOR:
				return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(color);
			case DomainPackage.NOTE__FONT:
				return FONT_EDEFAULT == null ? font != null : !FONT_EDEFAULT.equals(font);
			case DomainPackage.NOTE__FONT_HEIGHT:
				return fontHeight != FONT_HEIGHT_EDEFAULT;
			case DomainPackage.NOTE__FONT_COLOR:
				return FONT_COLOR_EDEFAULT == null ? fontColor != null : !FONT_COLOR_EDEFAULT.equals(fontColor);
			case DomainPackage.NOTE__FONT_STYLE:
				return fontStyle != FONT_STYLE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (text: ");
		result.append(text);
		result.append(", color: ");
		result.append(color);
		result.append(", font: ");
		result.append(font);
		result.append(", fontHeight: ");
		result.append(fontHeight);
		result.append(", fontColor: ");
		result.append(fontColor);
		result.append(", fontStyle: ");
		result.append(fontStyle);
		result.append(')');
		return result.toString();
	}
	
	private IColorConstant deserializeColor(String color){
		if(color == null){
			return null;
		}
		
		String[] tokens = color.split(",");
		
		if(tokens.length != 3){
			return null;
		}
		
		try{
			return new ColorConstant(Integer.parseInt(tokens[0]),
					Integer.parseInt(tokens[1]),
					Integer.parseInt(tokens[2]));
			
		}catch(NumberFormatException nfe){
			return null;
		}
	}
	
	private String serializeColor(IColorConstant color){
		if(color == null){
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(color.getRed());
		builder.append(',');
		builder.append(color.getGreen());
		builder.append(',');
		builder.append(color.getBlue());
		
		return builder.toString();
	}

	@Override
	public IColorConstant getForegroundColor() {
		IColorConstant colorObject = deserializeColor(fontColor);
		
		if(colorObject == null){
			return DEFAULT_FOREGROUND;
		}
		
		return colorObject;
	}

	@Override
	public boolean hasCustomForeground() {
		IColorConstant colorObject = deserializeColor(fontColor);
		return (colorObject != null);
	}

	@Override
	public void setForegroundColor(IColorConstant color) {
		setFontColor(serializeColor(color));
	}

	@Override
	public boolean isBold() {
		return (getFontStyle() & SWT.BOLD) > 0;
	}

	@Override
	public boolean isItalic() {
		return (getFontStyle() & SWT.ITALIC) > 0;
	}

} //NoteImpl
