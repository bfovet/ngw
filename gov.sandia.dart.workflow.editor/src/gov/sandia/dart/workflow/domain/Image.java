/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.domain;


import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Image</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gov.sandia.dart.workflow.domain.Image#getText <em>Text</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.Image#isZoomToFit <em>Zoom To Fit</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.Image#isDrawBorder <em>Draw Border</em>}</li>
 * </ul>
 *
 * @see gov.sandia.dart.workflow.domain.DomainPackage#getImage()
 * @model
 * @generated
 */
public interface Image extends EObject {
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
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getImage_Text()
	 * @model default=""
	 * @generated
	 */
	String getText();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.Image#getText <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Text</em>' attribute.
	 * @see #getText()
	 * @generated
	 */
	void setText(String value);

	/**
	 * Returns the value of the '<em><b>Zoom To Fit</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Zoom To Fit</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Zoom To Fit</em>' attribute.
	 * @see #setZoomToFit(boolean)
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getImage_ZoomToFit()
	 * @model default="false"
	 * @generated
	 */
	boolean isZoomToFit();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.Image#isZoomToFit <em>Zoom To Fit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Zoom To Fit</em>' attribute.
	 * @see #isZoomToFit()
	 * @generated
	 */
	void setZoomToFit(boolean value);

	/**
	 * Returns the value of the '<em><b>Draw Border</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Draw Border</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Draw Border</em>' attribute.
	 * @see #setDrawBorder(boolean)
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getImage_DrawBorder()
	 * @model default="true"
	 * @generated
	 */
	boolean isDrawBorder();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.Image#isDrawBorder <em>Draw Border</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Draw Border</em>' attribute.
	 * @see #isDrawBorder()
	 * @generated
	 */
	void setDrawBorder(boolean value);

} // Image
