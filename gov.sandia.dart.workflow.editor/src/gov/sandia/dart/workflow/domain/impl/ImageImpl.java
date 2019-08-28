package gov.sandia.dart.workflow.domain.impl;


import gov.sandia.dart.workflow.domain.DomainPackage;
import gov.sandia.dart.workflow.domain.Image;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Image</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.ImageImpl#getText <em>Text</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.ImageImpl#isZoomToFit <em>Zoom To Fit</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.ImageImpl#isDrawBorder <em>Draw Border</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ImageImpl extends MinimalEObjectImpl.Container implements Image {
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
	 * The default value of the '{@link #isZoomToFit() <em>Zoom To Fit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isZoomToFit()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ZOOM_TO_FIT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isZoomToFit() <em>Zoom To Fit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isZoomToFit()
	 * @generated
	 * @ordered
	 */
	protected boolean zoomToFit = ZOOM_TO_FIT_EDEFAULT;

	/**
	 * The default value of the '{@link #isDrawBorder() <em>Draw Border</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDrawBorder()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DRAW_BORDER_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isDrawBorder() <em>Draw Border</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDrawBorder()
	 * @generated
	 * @ordered
	 */
	protected boolean drawBorder = DRAW_BORDER_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ImageImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DomainPackage.Literals.IMAGE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.IMAGE__TEXT, oldText, text));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isZoomToFit() {
		return zoomToFit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setZoomToFit(boolean newZoomToFit) {
		boolean oldZoomToFit = zoomToFit;
		zoomToFit = newZoomToFit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.IMAGE__ZOOM_TO_FIT, oldZoomToFit, zoomToFit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isDrawBorder() {
		return drawBorder;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDrawBorder(boolean newDrawBorder) {
		boolean oldDrawBorder = drawBorder;
		drawBorder = newDrawBorder;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.IMAGE__DRAW_BORDER, oldDrawBorder, drawBorder));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DomainPackage.IMAGE__TEXT:
				return getText();
			case DomainPackage.IMAGE__ZOOM_TO_FIT:
				return isZoomToFit();
			case DomainPackage.IMAGE__DRAW_BORDER:
				return isDrawBorder();
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
			case DomainPackage.IMAGE__TEXT:
				setText((String)newValue);
				return;
			case DomainPackage.IMAGE__ZOOM_TO_FIT:
				setZoomToFit((Boolean)newValue);
				return;
			case DomainPackage.IMAGE__DRAW_BORDER:
				setDrawBorder((Boolean)newValue);
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
			case DomainPackage.IMAGE__TEXT:
				setText(TEXT_EDEFAULT);
				return;
			case DomainPackage.IMAGE__ZOOM_TO_FIT:
				setZoomToFit(ZOOM_TO_FIT_EDEFAULT);
				return;
			case DomainPackage.IMAGE__DRAW_BORDER:
				setDrawBorder(DRAW_BORDER_EDEFAULT);
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
			case DomainPackage.IMAGE__TEXT:
				return TEXT_EDEFAULT == null ? text != null : !TEXT_EDEFAULT.equals(text);
			case DomainPackage.IMAGE__ZOOM_TO_FIT:
				return zoomToFit != ZOOM_TO_FIT_EDEFAULT;
			case DomainPackage.IMAGE__DRAW_BORDER:
				return drawBorder != DRAW_BORDER_EDEFAULT;
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
		result.append(", zoomToFit: ");
		result.append(zoomToFit);
		result.append(", drawBorder: ");
		result.append(drawBorder);
		result.append(')');
		return result.toString();
	}

} //ImageImpl