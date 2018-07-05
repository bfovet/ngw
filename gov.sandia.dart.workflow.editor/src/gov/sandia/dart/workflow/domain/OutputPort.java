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

import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Output Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gov.sandia.dart.workflow.domain.OutputPort#getNode <em>Node</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.OutputPort#getArcs <em>Arcs</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.OutputPort#getResponseArcs <em>Response Arcs</em>}</li>
 * </ul>
 *
 * @see gov.sandia.dart.workflow.domain.DomainPackage#getOutputPort()
 * @model
 * @generated
 */
public interface OutputPort extends Port {
	/**
	 * Returns the value of the '<em><b>Node</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link gov.sandia.dart.workflow.domain.WFNode#getOutputPorts <em>Output Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Node</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Node</em>' reference.
	 * @see #setNode(WFNode)
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getOutputPort_Node()
	 * @see gov.sandia.dart.workflow.domain.WFNode#getOutputPorts
	 * @model opposite="outputPorts"
	 * @generated
	 */
	WFNode getNode();

	/**
	 * Sets the value of the '{@link gov.sandia.dart.workflow.domain.OutputPort#getNode <em>Node</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Node</em>' reference.
	 * @see #getNode()
	 * @generated
	 */
	void setNode(WFNode value);

	/**
	 * Returns the value of the '<em><b>Arcs</b></em>' reference list.
	 * The list contents are of type {@link gov.sandia.dart.workflow.domain.WFArc}.
	 * It is bidirectional and its opposite is '{@link gov.sandia.dart.workflow.domain.WFArc#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Arcs</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Arcs</em>' reference list.
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getOutputPort_Arcs()
	 * @see gov.sandia.dart.workflow.domain.WFArc#getSource
	 * @model opposite="source"
	 * @generated
	 */
	EList<WFArc> getArcs();

	/**
	 * Returns the value of the '<em><b>Response Arcs</b></em>' reference list.
	 * The list contents are of type {@link gov.sandia.dart.workflow.domain.ResponseArc}.
	 * It is bidirectional and its opposite is '{@link gov.sandia.dart.workflow.domain.ResponseArc#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Response Arcs</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Response Arcs</em>' reference list.
	 * @see gov.sandia.dart.workflow.domain.DomainPackage#getOutputPort_ResponseArcs()
	 * @see gov.sandia.dart.workflow.domain.ResponseArc#getSource
	 * @model opposite="source"
	 * @generated
	 */
	EList<ResponseArc> getResponseArcs();

} // OutputPort
