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
package gov.sandia.dart.workflow.domain.impl;

import gov.sandia.dart.workflow.domain.DomainPackage;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.ResponseArc;
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
 * An implementation of the model object '<em><b>Output Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.OutputPortImpl#getNode <em>Node</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.OutputPortImpl#getArcs <em>Arcs</em>}</li>
 *   <li>{@link gov.sandia.dart.workflow.domain.impl.OutputPortImpl#getResponseArcs <em>Response Arcs</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OutputPortImpl extends PortImpl implements OutputPort {
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
	 * The cached value of the '{@link #getResponseArcs() <em>Response Arcs</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResponseArcs()
	 * @generated
	 * @ordered
	 */
	protected EList<ResponseArc> responseArcs;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected OutputPortImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DomainPackage.Literals.OUTPUT_PORT;
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DomainPackage.OUTPUT_PORT__NODE, oldNode, node));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DomainPackage.OUTPUT_PORT__NODE, oldNode, newNode);
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
				msgs = ((InternalEObject)node).eInverseRemove(this, DomainPackage.WF_NODE__OUTPUT_PORTS, WFNode.class, msgs);
			if (newNode != null)
				msgs = ((InternalEObject)newNode).eInverseAdd(this, DomainPackage.WF_NODE__OUTPUT_PORTS, WFNode.class, msgs);
			msgs = basicSetNode(newNode, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DomainPackage.OUTPUT_PORT__NODE, newNode, newNode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<WFArc> getArcs() {
		if (arcs == null) {
			arcs = new EObjectWithInverseResolvingEList<WFArc>(WFArc.class, this, DomainPackage.OUTPUT_PORT__ARCS, DomainPackage.WF_ARC__SOURCE);
		}
		return arcs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ResponseArc> getResponseArcs() {
		if (responseArcs == null) {
			responseArcs = new EObjectWithInverseResolvingEList<ResponseArc>(ResponseArc.class, this, DomainPackage.OUTPUT_PORT__RESPONSE_ARCS, DomainPackage.RESPONSE_ARC__SOURCE);
		}
		return responseArcs;
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
			case DomainPackage.OUTPUT_PORT__NODE:
				if (node != null)
					msgs = ((InternalEObject)node).eInverseRemove(this, DomainPackage.WF_NODE__OUTPUT_PORTS, WFNode.class, msgs);
				return basicSetNode((WFNode)otherEnd, msgs);
			case DomainPackage.OUTPUT_PORT__ARCS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getArcs()).basicAdd(otherEnd, msgs);
			case DomainPackage.OUTPUT_PORT__RESPONSE_ARCS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getResponseArcs()).basicAdd(otherEnd, msgs);
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
			case DomainPackage.OUTPUT_PORT__NODE:
				return basicSetNode(null, msgs);
			case DomainPackage.OUTPUT_PORT__ARCS:
				return ((InternalEList<?>)getArcs()).basicRemove(otherEnd, msgs);
			case DomainPackage.OUTPUT_PORT__RESPONSE_ARCS:
				return ((InternalEList<?>)getResponseArcs()).basicRemove(otherEnd, msgs);
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
			case DomainPackage.OUTPUT_PORT__NODE:
				if (resolve) return getNode();
				return basicGetNode();
			case DomainPackage.OUTPUT_PORT__ARCS:
				return getArcs();
			case DomainPackage.OUTPUT_PORT__RESPONSE_ARCS:
				return getResponseArcs();
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
			case DomainPackage.OUTPUT_PORT__NODE:
				setNode((WFNode)newValue);
				return;
			case DomainPackage.OUTPUT_PORT__ARCS:
				getArcs().clear();
				getArcs().addAll((Collection<? extends WFArc>)newValue);
				return;
			case DomainPackage.OUTPUT_PORT__RESPONSE_ARCS:
				getResponseArcs().clear();
				getResponseArcs().addAll((Collection<? extends ResponseArc>)newValue);
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
			case DomainPackage.OUTPUT_PORT__NODE:
				setNode((WFNode)null);
				return;
			case DomainPackage.OUTPUT_PORT__ARCS:
				getArcs().clear();
				return;
			case DomainPackage.OUTPUT_PORT__RESPONSE_ARCS:
				getResponseArcs().clear();
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
			case DomainPackage.OUTPUT_PORT__NODE:
				return node != null;
			case DomainPackage.OUTPUT_PORT__ARCS:
				return arcs != null && !arcs.isEmpty();
			case DomainPackage.OUTPUT_PORT__RESPONSE_ARCS:
				return responseArcs != null && !responseArcs.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //OutputPortImpl
