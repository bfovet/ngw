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
import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.WFArc;
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
 * An implementation of the model object '<em><b>Input Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.InputPortImpl#getNode <em>Node</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.InputPortImpl#getArcs <em>Arcs</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.InputPortImpl#isTriggerOnly <em>Trigger Only</em>}</li>
 * </ul>
 *
 * @generated
 */
public class InputPortImpl extends PortImpl implements InputPort {
	/**
	 * The cached value of the '{@link #getNode() <em>Node</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNode()
	 * @generated
	 * @ordered
	 */
	protected WFNode node;

	/**
	 * The cached value of the '{@link #getArcs() <em>Arcs</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArcs()
	 * @generated
	 * @ordered
	 */
	protected EList<WFArc> arcs;

	/**
	 * The default value of the '{@link #isTriggerOnly() <em>Trigger Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isTriggerOnly()
	 * @generated
	 * @ordered
	 */
	protected static final boolean TRIGGER_ONLY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isTriggerOnly() <em>Trigger Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isTriggerOnly()
	 * @generated
	 * @ordered
	 */
	protected boolean triggerOnly = TRIGGER_ONLY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InputPortImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DomainPackage.Literals.INPUT_PORT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WFNode getNode() {
		if (node != null && node.eIsProxy()) {
			InternalEObject oldNode = (InternalEObject)node;
			node = (WFNode)eResolveProxy(oldNode);
			if (node != oldNode) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DomainPackage.INPUT_PORT__NODE, oldNode, node));
			}
		}
		return node;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WFNode basicGetNode() {
		return node;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetNode(WFNode newNode, NotificationChain msgs) {
		WFNode oldNode = node;
		node = newNode;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DomainPackage.INPUT_PORT__NODE, oldNode, newNode);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNode(WFNode newNode) {
		if (newNode != node) {
			NotificationChain msgs = null;
			if (node != null)
				msgs = ((InternalEObject)node).eInverseRemove(this, DomainPackage.WF_NODE__INPUT_PORTS, WFNode.class, msgs);
			if (newNode != null)
				msgs = ((InternalEObject)newNode).eInverseAdd(this, DomainPackage.WF_NODE__INPUT_PORTS, WFNode.class, msgs);
			msgs = basicSetNode(newNode, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.INPUT_PORT__NODE, newNode, newNode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<WFArc> getArcs() {
		if (arcs == null) {
			arcs = new EObjectWithInverseResolvingEList<WFArc>(WFArc.class, this, DomainPackage.INPUT_PORT__ARCS, DomainPackage.WF_ARC__TARGET);
		}
		return arcs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isTriggerOnly() {
		return triggerOnly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTriggerOnly(boolean newTriggerOnly) {
		boolean oldTriggerOnly = triggerOnly;
		triggerOnly = newTriggerOnly;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.INPUT_PORT__TRIGGER_ONLY, oldTriggerOnly, triggerOnly));
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
			case DomainPackage.INPUT_PORT__NODE:
				if (node != null)
					msgs = ((InternalEObject)node).eInverseRemove(this, DomainPackage.WF_NODE__INPUT_PORTS, WFNode.class, msgs);
				return basicSetNode((WFNode)otherEnd, msgs);
			case DomainPackage.INPUT_PORT__ARCS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getArcs()).basicAdd(otherEnd, msgs);
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
			case DomainPackage.INPUT_PORT__NODE:
				return basicSetNode(null, msgs);
			case DomainPackage.INPUT_PORT__ARCS:
				return ((InternalEList<?>)getArcs()).basicRemove(otherEnd, msgs);
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
			case DomainPackage.INPUT_PORT__NODE:
				if (resolve) return getNode();
				return basicGetNode();
			case DomainPackage.INPUT_PORT__ARCS:
				return getArcs();
			case DomainPackage.INPUT_PORT__TRIGGER_ONLY:
				return isTriggerOnly();
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
			case DomainPackage.INPUT_PORT__NODE:
				setNode((WFNode)newValue);
				return;
			case DomainPackage.INPUT_PORT__ARCS:
				getArcs().clear();
				getArcs().addAll((Collection<? extends WFArc>)newValue);
				return;
			case DomainPackage.INPUT_PORT__TRIGGER_ONLY:
				setTriggerOnly((Boolean)newValue);
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
			case DomainPackage.INPUT_PORT__NODE:
				setNode((WFNode)null);
				return;
			case DomainPackage.INPUT_PORT__ARCS:
				getArcs().clear();
				return;
			case DomainPackage.INPUT_PORT__TRIGGER_ONLY:
				setTriggerOnly(TRIGGER_ONLY_EDEFAULT);
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
			case DomainPackage.INPUT_PORT__NODE:
				return node != null;
			case DomainPackage.INPUT_PORT__ARCS:
				return arcs != null && !arcs.isEmpty();
			case DomainPackage.INPUT_PORT__TRIGGER_ONLY:
				return triggerOnly != TRIGGER_ONLY_EDEFAULT;
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
		result.append(" (triggerOnly: ");
		result.append(triggerOnly);
		result.append(')');
		return result.toString();
	}

} //InputPortImpl
