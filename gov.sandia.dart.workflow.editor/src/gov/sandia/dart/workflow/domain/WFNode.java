/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 */
package gov.sandia.dart.workflow.domain;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>DART Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gov.sandia.dart.workflow.domain.WFNode#getInputPorts <em>Input Ports</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.WFNode#getOutputPorts <em>Output Ports</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.WFNode#isStart <em>Start</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.WFNode#getType <em>Type</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.WFNode#getLabel <em>Label</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.WFNode#getConductors <em>Conductors</em>}</li>
 * </ul>
 *
 * @see gov.sandia.dart.workflow.domain.DomainPackage#getWFNode()
 * @model
 * @generated
 */
public interface WFNode extends NamedObjectWithProperties {

	/**
	 * Returns the value of the '<em><b>Input Ports</b></em>' reference list.
	 * The list contents are of type {@link gov.sandia.dart.workflow.domain.InputPort}.
	 * It is bidirectional and its opposite is '{@link gov.sandia.dart.workflow.domain.InputPort#getNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Input Ports</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Input Ports</em>' reference list.
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getWFNode_InputPorts()
	 * @see gov.sandia.dart.workflow.domain.InputPort#getNode
	 * @model opposite="node"
	 * @generated
	 */
	EList<InputPort> getInputPorts();

	/**
	 * Returns the value of the '<em><b>Output Ports</b></em>' reference list.
	 * The list contents are of type {@link gov.sandia.dart.workflow.domain.OutputPort}.
	 * It is bidirectional and its opposite is '{@link gov.sandia.dart.workflow.domain.OutputPort#getNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Output Ports</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Output Ports</em>' reference list.
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getWFNode_OutputPorts()
	 * @see gov.sandia.dart.workflow.domain.OutputPort#getNode
	 * @model opposite="node"
	 * @generated
	 */
	EList<OutputPort> getOutputPorts();

	/**
	 * Returns the value of the '<em><b>Start</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Start</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Start</em>' attribute.
	 * @see #setStart(boolean)
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getWFNode_Start()
	 * @model default="false"
	 * @generated
	 */
	boolean isStart();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.WFNode#isStart <em>Start</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Start</em>' attribute.
	 * @see #isStart()
	 * @generated
	 */
	void setStart(boolean value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getWFNode_Type()
	 * @model
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.WFNode#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * Returns the value of the '<em><b>Label</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Label</em>' attribute.
	 * @see #setLabel(String)
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getWFNode_Label()
	 * @model default=""
	 * @generated
	 */
	String getLabel();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.WFNode#getLabel <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Label</em>' attribute.
	 * @see #getLabel()
	 * @generated
	 */
	void setLabel(String value);

	/**
	 * Returns the value of the '<em><b>Conductors</b></em>' reference list.
	 * The list contents are of type {@link gov.sandia.dart.workflow.domain.Conductor}.
	 * It is bidirectional and its opposite is '{@link gov.sandia.dart.workflow.domain.Conductor#getNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Conductors</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conductors</em>' reference list.
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getWFNode_Conductors()
	 * @see gov.sandia.dart.workflow.domain.Conductor#getNode
	 * @model opposite="node"
	 * @generated
	 */
	EList<Conductor> getConductors();
} // WFNode
