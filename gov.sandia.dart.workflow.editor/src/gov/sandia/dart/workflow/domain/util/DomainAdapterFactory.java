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
package gov.sandia.dart.workflow.domain.util;

import gov.sandia.dart.workflow.domain.*;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see gov.sandia.dart.workflow.domain.DomainPackage
 * @generated
 */
public class DomainAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static DomainPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DomainAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = DomainPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DomainSwitch<Adapter> modelSwitch =
		new DomainSwitch<Adapter>() {
			@Override
			public Adapter caseResponse(Response object) {
				return createResponseAdapter();
			}
			@Override
			public Adapter caseWFNode(WFNode object) {
				return createWFNodeAdapter();
			}
			@Override
			public Adapter caseWFArc(WFArc object) {
				return createWFArcAdapter();
			}
			@Override
			public Adapter caseProperty(Property object) {
				return createPropertyAdapter();
			}
			@Override
			public Adapter casePort(Port object) {
				return createPortAdapter();
			}
			@Override
			public Adapter caseInputPort(InputPort object) {
				return createInputPortAdapter();
			}
			@Override
			public Adapter caseOutputPort(OutputPort object) {
				return createOutputPortAdapter();
			}
			@Override
			public Adapter caseNamedObject(NamedObject object) {
				return createNamedObjectAdapter();
			}
			@Override
			public Adapter caseSAWNode(SAWNode object) {
				return createSAWNodeAdapter();
			}
			@Override
			public Adapter caseNote(Note object) {
				return createNoteAdapter();
			}
			@Override
			public Adapter caseNamedObjectWithProperties(NamedObjectWithProperties object) {
				return createNamedObjectWithPropertiesAdapter();
			}
			@Override
			public Adapter caseResponseArc(ResponseArc object) {
				return createResponseArcAdapter();
			}
			@Override
			public Adapter caseConductor(Conductor object) {
				return createConductorAdapter();
			}
			@Override
			public Adapter caseParameter(Parameter object) {
				return createParameterAdapter();
			}
			@Override
			public Adapter caseRunner(Runner object) {
				return createRunnerAdapter();
			}
			@Override
			public Adapter caseArc(Arc object) {
				return createArcAdapter();
			}
			@Override
			public Adapter caseImage(Image object) {
				return createImageAdapter();
			}
			@Override
			public Adapter caseDomain_NamedObject(NamedObject object) {
				return createDomain_NamedObjectAdapter();
			}
			@Override
			public Adapter caseDomain_NamedObjectWithProperties(NamedObjectWithProperties object) {
				return createDomain_NamedObjectWithPropertiesAdapter();
			}
			@Override
			public Adapter caseDomain_Arc(Arc object) {
				return createDomain_ArcAdapter();
			}
			@Override
			public Adapter caseDomain_Port(Port object) {
				return createDomain_PortAdapter();
			}
			@Override
			public Adapter caseDomain_WFNode(WFNode object) {
				return createDomain_WFNodeAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Response <em>Response</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Response
	 * @generated
	 */
	public Adapter createResponseAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.WFNode <em>WF Node</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.WFNode
	 * @generated
	 */
	public Adapter createWFNodeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.WFArc <em>WF Arc</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.WFArc
	 * @generated
	 */
	public Adapter createWFArcAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Property <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Property
	 * @generated
	 */
	public Adapter createPropertyAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Port <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Port
	 * @generated
	 */
	public Adapter createPortAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.InputPort <em>Input Port</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.InputPort
	 * @generated
	 */
	public Adapter createInputPortAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.OutputPort <em>Output Port</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.OutputPort
	 * @generated
	 */
	public Adapter createOutputPortAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.NamedObject <em>Named Object</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.NamedObject
	 * @generated
	 */
	public Adapter createNamedObjectAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.SAWNode <em>SAW Node</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.SAWNode
	 * @generated
	 */
	public Adapter createSAWNodeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Note <em>Note</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Note
	 * @generated
	 */
	public Adapter createNoteAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.NamedObjectWithProperties <em>Named Object With Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.NamedObjectWithProperties
	 * @generated
	 */
	public Adapter createNamedObjectWithPropertiesAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.ResponseArc <em>Response Arc</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.ResponseArc
	 * @generated
	 */
	public Adapter createResponseArcAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Conductor <em>Conductor</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Conductor
	 * @generated
	 */
	public Adapter createConductorAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Parameter
	 * @generated
	 */
	public Adapter createParameterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Runner <em>Runner</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Runner
	 * @generated
	 */
	public Adapter createRunnerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Arc <em>Arc</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Arc
	 * @generated
	 */
	public Adapter createArcAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Image <em>Image</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Image
	 * @generated
	 */
	public Adapter createImageAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.NamedObject <em>Named Object</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.NamedObject
	 * @generated
	 */
	public Adapter createDomain_NamedObjectAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.NamedObjectWithProperties <em>Named Object With Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.NamedObjectWithProperties
	 * @generated
	 */
	public Adapter createDomain_NamedObjectWithPropertiesAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Arc <em>Arc</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Arc
	 * @generated
	 */
	public Adapter createDomain_ArcAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.Port <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.Port
	 * @generated
	 */
	public Adapter createDomain_PortAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.sandia.dart.workflow.domain.WFNode <em>WF Node</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.sandia.dart.workflow.domain.WFNode
	 * @generated
	 */
	public Adapter createDomain_WFNodeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //DomainAdapterFactory
