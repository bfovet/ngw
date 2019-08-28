package gov.sandia.dart.workflow.domain.impl;


import gov.sandia.dart.workflow.domain.DomainPackage;
import gov.sandia.dart.workflow.domain.Note;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

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
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.NoteImpl#isDrawBorderAndBackground <em>Draw Border And Background</em>}</li>
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

	/**
	 * The cached value of the '{@link #getColor() <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected String color = COLOR_EDEFAULT;

	/**
	 * The default value of the '{@link #isDrawBorderAndBackground() <em>Draw Border And Background</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDrawBorderAndBackground()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DRAW_BORDER_AND_BACKGROUND_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isDrawBorderAndBackground() <em>Draw Border And Background</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDrawBorderAndBackground()
	 * @generated
	 * @ordered
	 */
	protected boolean drawBorderAndBackground = DRAW_BORDER_AND_BACKGROUND_EDEFAULT;

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
	public boolean isDrawBorderAndBackground() {
		return drawBorderAndBackground;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDrawBorderAndBackground(boolean newDrawBorderAndBackground) {
		boolean oldDrawBorderAndBackground = drawBorderAndBackground;
		drawBorderAndBackground = newDrawBorderAndBackground;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.NOTE__DRAW_BORDER_AND_BACKGROUND, oldDrawBorderAndBackground, drawBorderAndBackground));
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
			case DomainPackage.NOTE__DRAW_BORDER_AND_BACKGROUND:
				return isDrawBorderAndBackground();
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
			case DomainPackage.NOTE__DRAW_BORDER_AND_BACKGROUND:
				setDrawBorderAndBackground((Boolean)newValue);
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
			case DomainPackage.NOTE__DRAW_BORDER_AND_BACKGROUND:
				setDrawBorderAndBackground(DRAW_BORDER_AND_BACKGROUND_EDEFAULT);
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
			case DomainPackage.NOTE__DRAW_BORDER_AND_BACKGROUND:
				return drawBorderAndBackground != DRAW_BORDER_AND_BACKGROUND_EDEFAULT;
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
		result.append(", drawBorderAndBackground: ");
		result.append(drawBorderAndBackground);
		result.append(')');
		return result.toString();
	}

} //NoteImpl