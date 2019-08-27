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
package gov.sandia.dart.workflow.domain;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.util.IColorConstant;

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
 *   <li>{@link gov.sandia.dart.workflow.domain.Image#getColor <em>Color</em>}</li>
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
	 * Returns the value of the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Color</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Color</em>' attribute.
	 * @see #setColor(String)
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getImage_Color()
	 * @model
	 * @generated
	 */
	String getColor();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.Image#getColor <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Color</em>' attribute.
	 * @see #getColor()
	 * @generated
	 */
	void setColor(String value);

	/**
	 * Returns the current background color
	 * @return
	 */
	IColorConstant getBackgroundColor();
	
	
	/**
	 * true if a color has been set, false if the default color is being used
	 * @return
	 */
	boolean hasCustomBackground();
	
	/**
	 * Sets the current background color.  Set to null to use the default color.
	 */
	void setBackgroundColor(IColorConstant color);

	
	/**
	 * Returns the current foreground color
	 * @return
	 */
	IColorConstant getForegroundColor();	
	
	/**
	 * true if a color has been set, false if the default color is being used
	 * @return
	 */
	boolean hasCustomForeground();
	
	/**
	 * Sets the current foreground color.  Set to null to use the default color.
	 */
	void setForegroundColor(IColorConstant color);
	
	boolean isBold();
	
	boolean isItalic();

} // Image