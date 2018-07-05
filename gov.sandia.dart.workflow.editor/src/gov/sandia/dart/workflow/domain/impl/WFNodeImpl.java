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

import gov.sandia.dart.workflow.domain.Conductor;
import gov.sandia.dart.workflow.domain.DomainPackage;
import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFNode;
import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>WF Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.WFNodeImpl#getInputPorts <em>Input Ports</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.WFNodeImpl#getOutputPorts <em>Output Ports</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.WFNodeImpl#isStart <em>Start</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.WFNodeImpl#getType <em>Type</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.WFNodeImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.WFNodeImpl#getConductors <em>Conductors</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WFNodeImpl extends NamedObjectWithPropertiesImpl implements WFNode {
	/**
	 * The cached value of the '{@link #getInputPorts() <em>Input Ports</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputPorts()
	 * @generated
	 * @ordered
	 */
	protected EList<InputPort> inputPorts;

	/**
	 * The cached value of the '{@link #getOutputPorts() <em>Output Ports</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputPorts()
	 * @generated
	 * @ordered
	 */
	protected EList<OutputPort> outputPorts;

	/**
	 * The default value of the '{@link #isStart() <em>Start</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStart()
	 * @generated
	 * @ordered
	 */
	protected static final boolean START_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isStart() <em>Start</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStart()
	 * @generated
	 * @ordered
	 */
	protected boolean start = START_EDEFAULT;

	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected String type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected static final String LABEL_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected String label = LABEL_EDEFAULT;

	/**
	 * The cached value of the '{@link #getConductors() <em>Conductors</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConductors()
	 * @generated
	 * @ordered
	 */
	protected EList<Conductor> conductors;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WFNodeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DomainPackage.Literals.WF_NODE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<InputPort> getInputPorts() {
		if (inputPorts == null) {
			inputPorts = new EObjectWithInverseResolvingEList<InputPort>(InputPort.class, this, DomainPackage.WF_NODE__INPUT_PORTS, DomainPackage.INPUT_PORT__NODE);
		}
		return inputPorts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<OutputPort> getOutputPorts() {
		if (outputPorts == null) {
			outputPorts = new EObjectWithInverseResolvingEList<OutputPort>(OutputPort.class, this, DomainPackage.WF_NODE__OUTPUT_PORTS, DomainPackage.OUTPUT_PORT__NODE);
		}
		return outputPorts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isStart() {
		return start;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStart(boolean newStart) {
		boolean oldStart = start;
		start = newStart;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.WF_NODE__START, oldStart, start));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(String newType) {
		String oldType = type;
		type = newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.WF_NODE__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLabel(String newLabel) {
		String oldLabel = label;
		label = newLabel;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.WF_NODE__LABEL, oldLabel, label));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Conductor> getConductors() {
		if (conductors == null) {
			conductors = new EObjectWithInverseResolvingEList<Conductor>(Conductor.class, this, DomainPackage.WF_NODE__CONDUCTORS, DomainPackage.CONDUCTOR__NODE);
		}
		return conductors;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DomainPackage.WF_NODE__INPUT_PORTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getInputPorts()).basicAdd(otherEnd, msgs);
			case DomainPackage.WF_NODE__OUTPUT_PORTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutputPorts()).basicAdd(otherEnd, msgs);
			case DomainPackage.WF_NODE__CONDUCTORS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getConductors()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DomainPackage.WF_NODE__INPUT_PORTS:
				return ((InternalEList<?>)getInputPorts()).basicRemove(otherEnd, msgs);
			case DomainPackage.WF_NODE__OUTPUT_PORTS:
				return ((InternalEList<?>)getOutputPorts()).basicRemove(otherEnd, msgs);
			case DomainPackage.WF_NODE__CONDUCTORS:
				return ((InternalEList<?>)getConductors()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DomainPackage.WF_NODE__INPUT_PORTS:
				return getInputPorts();
			case DomainPackage.WF_NODE__OUTPUT_PORTS:
				return getOutputPorts();
			case DomainPackage.WF_NODE__START:
				return isStart();
			case DomainPackage.WF_NODE__TYPE:
				return getType();
			case DomainPackage.WF_NODE__LABEL:
				return getLabel();
			case DomainPackage.WF_NODE__CONDUCTORS:
				return getConductors();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DomainPackage.WF_NODE__INPUT_PORTS:
				getInputPorts().clear();
				getInputPorts().addAll((Collection<? extends InputPort>)newValue);
				return;
			case DomainPackage.WF_NODE__OUTPUT_PORTS:
				getOutputPorts().clear();
				getOutputPorts().addAll((Collection<? extends OutputPort>)newValue);
				return;
			case DomainPackage.WF_NODE__START:
				setStart((Boolean)newValue);
				return;
			case DomainPackage.WF_NODE__TYPE:
				setType((String)newValue);
				return;
			case DomainPackage.WF_NODE__LABEL:
				setLabel((String)newValue);
				return;
			case DomainPackage.WF_NODE__CONDUCTORS:
				getConductors().clear();
				getConductors().addAll((Collection<? extends Conductor>)newValue);
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
			case DomainPackage.WF_NODE__INPUT_PORTS:
				getInputPorts().clear();
				return;
			case DomainPackage.WF_NODE__OUTPUT_PORTS:
				getOutputPorts().clear();
				return;
			case DomainPackage.WF_NODE__START:
				setStart(START_EDEFAULT);
				return;
			case DomainPackage.WF_NODE__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case DomainPackage.WF_NODE__LABEL:
				setLabel(LABEL_EDEFAULT);
				return;
			case DomainPackage.WF_NODE__CONDUCTORS:
				getConductors().clear();
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
			case DomainPackage.WF_NODE__INPUT_PORTS:
				return inputPorts != null && !inputPorts.isEmpty();
			case DomainPackage.WF_NODE__OUTPUT_PORTS:
				return outputPorts != null && !outputPorts.isEmpty();
			case DomainPackage.WF_NODE__START:
				return start != START_EDEFAULT;
			case DomainPackage.WF_NODE__TYPE:
				return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
			case DomainPackage.WF_NODE__LABEL:
				return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
			case DomainPackage.WF_NODE__CONDUCTORS:
				return conductors != null && !conductors.isEmpty();
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
		result.append(" (start: ");
		result.append(start);
		result.append(", type: ");
		result.append(type);
		result.append(", label: ");
		result.append(label);
		result.append(')');
		return result.toString();
	}

} //WFNodeImpl
