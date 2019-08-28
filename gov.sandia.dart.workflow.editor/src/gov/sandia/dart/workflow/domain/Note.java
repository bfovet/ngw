package gov.sandia.dart.workflow.domain;


import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Note</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gov.sandia.dart.workflow.domain.Note#getText <em>Text</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.Note#getColor <em>Color</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.Note#isDrawBorderAndBackground <em>Draw Border And Background</em>}</li>
 * </ul>
 *
 * @see gov.sandia.dart.workflow.domain.DomainPackage#getNote()
 * @model
 * @generated
 */
public interface Note extends EObject {
	/**
	 * Returns the value of the '<em><b>Text</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Text</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Text</em>' attribute.
	 * @see #setText(String)
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getNote_Text()
	 * @model default=""
	 * @generated
	 */
	String getText();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.Note#getText <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Text</em>' attribute.
	 * @see #getText()
	 * @generated
	 */
	void setText(String value);

	/**
	 * Returns the value of the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Color</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Color</em>' attribute.
	 * @see #setColor(String)
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getNote_Color()
	 * @model
	 * @generated
	 */
	String getColor();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.Note#getColor <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Color</em>' attribute.
	 * @see #getColor()
	 * @generated
	 */
	void setColor(String value);

	/**
	 * Returns the value of the '<em><b>Draw Border And Background</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Draw Border And Background</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Draw Border And Background</em>' attribute.
	 * @see #setDrawBorderAndBackground(boolean)
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getNote_DrawBorderAndBackground()
	 * @model default="true"
	 * @generated
	 */
	boolean isDrawBorderAndBackground();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.Note#isDrawBorderAndBackground <em>Draw Border And Background</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Draw Border And Background</em>' attribute.
	 * @see #isDrawBorderAndBackground()
	 * @generated
	 */
	void setDrawBorderAndBackground(boolean value);

} // Note