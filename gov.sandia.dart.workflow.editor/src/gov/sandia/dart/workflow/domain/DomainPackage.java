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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see gov.sandia.dart.workflow.domain.DomainFactory
 * @model kind="package"
 * @generated
 */
public interface DomainPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "domain";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://dart.sandia.gov/workflow/domain";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "gov.sandia.dart.workflow.domain";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	DomainPackage eINSTANCE = gov.sandia.dart.workflow.domain.impl.DomainPackageImpl.init();

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.NamedObjectImpl <em>Named Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.NamedObjectImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getNamedObject()
	 * @generated
	 */
	int NAMED_OBJECT = 7;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.WFNodeImpl <em>WF Node</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.WFNodeImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getWFNode()
	 * @generated
	 */
	int WF_NODE = 1;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.WFArcImpl <em>WF Arc</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.WFArcImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getWFArc()
	 * @generated
	 */
	int WF_ARC = 2;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.PortImpl <em>Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.PortImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getPort()
	 * @generated
	 */
	int PORT = 4;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.InputPortImpl <em>Input Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.InputPortImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getInputPort()
	 * @generated
	 */
	int INPUT_PORT = 5;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.OutputPortImpl <em>Output Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.OutputPortImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getOutputPort()
	 * @generated
	 */
	int OUTPUT_PORT = 6;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.SAWNodeImpl <em>SAW Node</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.SAWNodeImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getSAWNode()
	 * @generated
	 */
	int SAW_NODE = 8;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.PropertyImpl <em>Property</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.PropertyImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getProperty()
	 * @generated
	 */
	int PROPERTY = 3;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.ResponseImpl <em>Response</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.ResponseImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getResponse()
	 * @generated
	 */
	int RESPONSE = 0;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.NoteImpl <em>Note</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.NoteImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getNote()
	 * @generated
	 */
	int NOTE = 9;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.NamedObjectWithPropertiesImpl <em>Named Object With Properties</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.NamedObjectWithPropertiesImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getNamedObjectWithProperties()
	 * @generated
	 */
	int NAMED_OBJECT_WITH_PROPERTIES = 10;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.ArcImpl <em>Arc</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.ArcImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getArc()
	 * @generated
	 */
	int ARC = 15;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.ResponseArcImpl <em>Response Arc</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.ResponseArcImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getResponseArc()
	 * @generated
	 */
	int RESPONSE_ARC = 11;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.ConductorImpl <em>Conductor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.ConductorImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getConductor()
	 * @generated
	 */
	int CONDUCTOR = 12;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.ParameterImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 13;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.RunnerImpl <em>Runner</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.RunnerImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getRunner()
	 * @generated
	 */
	int RUNNER = 14;

	/**
	 * The meta object id for the '{@link gov.sandia.dart.workflow.domain.impl.ImageImpl <em>Image</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.sandia.dart.workflow.domain.impl.ImageImpl
	 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getImage()
	 * @generated
	 */
	int IMAGE = 16;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__NAME = DomainPackage.NAMED_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__TYPE = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE__SOURCE = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Response</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_FEATURE_COUNT = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Response</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_OPERATION_COUNT = DomainPackage.NAMED_OBJECT_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_NODE__NAME = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_NODE__PROPERTIES = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_NODE__INPUT_PORTS = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_NODE__OUTPUT_PORTS = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Start</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_NODE__START = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_NODE__TYPE = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_NODE__LABEL = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Conductors</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_NODE__CONDUCTORS = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>WF Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_NODE_FEATURE_COUNT = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 6;

	/**
	 * The number of operations of the '<em>WF Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_NODE_OPERATION_COUNT = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_ARC__NAME = DomainPackage.ARC__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_ARC__PROPERTIES = DomainPackage.ARC__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_ARC__SOURCE = DomainPackage.ARC_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_ARC__TARGET = DomainPackage.ARC_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>WF Arc</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_ARC_FEATURE_COUNT = DomainPackage.ARC_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>WF Arc</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WF_ARC_OPERATION_COUNT = DomainPackage.ARC_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__NAME = DomainPackage.NAMED_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__TYPE = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__VALUE = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__NODE = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_FEATURE_COUNT = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_OPERATION_COUNT = DomainPackage.NAMED_OBJECT_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT__NAME = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT__PROPERTIES = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT__TYPE = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT_FEATURE_COUNT = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT_OPERATION_COUNT = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT__NAME = DomainPackage.PORT__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT__PROPERTIES = DomainPackage.PORT__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT__TYPE = DomainPackage.PORT__TYPE;

	/**
	 * The feature id for the '<em><b>Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT__NODE = DomainPackage.PORT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Arcs</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT__ARCS = DomainPackage.PORT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Trigger Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT__TRIGGER_ONLY = DomainPackage.PORT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Input Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT_FEATURE_COUNT = DomainPackage.PORT_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Input Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT_OPERATION_COUNT = DomainPackage.PORT_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT__NAME = DomainPackage.PORT__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT__PROPERTIES = DomainPackage.PORT__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT__TYPE = DomainPackage.PORT__TYPE;

	/**
	 * The feature id for the '<em><b>Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT__NODE = DomainPackage.PORT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Arcs</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT__ARCS = DomainPackage.PORT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Response Arcs</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT__RESPONSE_ARCS = DomainPackage.PORT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Output Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT_FEATURE_COUNT = DomainPackage.PORT_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Output Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT_OPERATION_COUNT = DomainPackage.PORT_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT__NAME = 0;

	/**
	 * The number of structural features of the '<em>Named Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Named Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT_OPERATION_COUNT = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE__NAME = DomainPackage.WF_NODE__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE__PROPERTIES = DomainPackage.WF_NODE__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE__INPUT_PORTS = DomainPackage.WF_NODE__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE__OUTPUT_PORTS = DomainPackage.WF_NODE__OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Start</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE__START = DomainPackage.WF_NODE__START;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE__TYPE = DomainPackage.WF_NODE__TYPE;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE__LABEL = DomainPackage.WF_NODE__LABEL;

	/**
	 * The feature id for the '<em><b>Conductors</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE__CONDUCTORS = DomainPackage.WF_NODE__CONDUCTORS;

	/**
	 * The feature id for the '<em><b>Model</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE__MODEL = DomainPackage.WF_NODE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Component</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE__COMPONENT = DomainPackage.WF_NODE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>SAW Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE_FEATURE_COUNT = DomainPackage.WF_NODE_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>SAW Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SAW_NODE_OPERATION_COUNT = DomainPackage.WF_NODE_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE__TEXT = 0;

	/**
	 * The feature id for the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE__COLOR = 1;

	/**
	 * The feature id for the '<em><b>Font</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE__FONT = 2;

	/**
	 * The feature id for the '<em><b>Font Height</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE__FONT_HEIGHT = 3;

	/**
	 * The feature id for the '<em><b>Font Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE__FONT_COLOR = 4;

	/**
	 * The feature id for the '<em><b>Font Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE__FONT_STYLE = 5;

	/**
	 * The number of structural features of the '<em>Note</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Note</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTE_OPERATION_COUNT = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT_WITH_PROPERTIES__NAME = DomainPackage.NAMED_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT_WITH_PROPERTIES__PROPERTIES = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Named Object With Properties</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Named Object With Properties</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_OBJECT_WITH_PROPERTIES_OPERATION_COUNT = DomainPackage.NAMED_OBJECT_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_ARC__NAME = DomainPackage.ARC__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_ARC__PROPERTIES = DomainPackage.ARC__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_ARC__SOURCE = DomainPackage.ARC_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_ARC__TARGET = DomainPackage.ARC_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Response Arc</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_ARC_FEATURE_COUNT = DomainPackage.ARC_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Response Arc</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESPONSE_ARC_OPERATION_COUNT = DomainPackage.ARC_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDUCTOR__NAME = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDUCTOR__PROPERTIES = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDUCTOR__NODE = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Conductor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDUCTOR_FEATURE_COUNT = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Conductor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONDUCTOR_OPERATION_COUNT = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__NAME = DomainPackage.NAMED_OBJECT__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__TYPE = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__VALUE = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = DomainPackage.NAMED_OBJECT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_OPERATION_COUNT = DomainPackage.NAMED_OBJECT_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUNNER__NAME = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUNNER__PROPERTIES = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUNNER__TYPE = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Runner</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUNNER_FEATURE_COUNT = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Runner</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RUNNER_OPERATION_COUNT = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARC__NAME = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES__NAME;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARC__PROPERTIES = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES__PROPERTIES;

	/**
	 * The number of structural features of the '<em>Arc</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARC_FEATURE_COUNT = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Arc</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARC_OPERATION_COUNT = DomainPackage.NAMED_OBJECT_WITH_PROPERTIES_OPERATION_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMAGE__TEXT = 0;

	/**
	 * The feature id for the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMAGE__COLOR = 1;

	/**
	 * The number of structural features of the '<em>Image</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMAGE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Image</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMAGE_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.Response <em>Response</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Response</em>'.
	 * @see gov.sandia.dart.workflow.domain.Response
	 * @generated
	 */
	EClass getResponse();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Response#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see gov.sandia.dart.workflow.domain.Response#getType()
	 * @see #getResponse()
	 * @generated
	 */
	EAttribute getResponse_Type();

	/**
	 * Returns the meta object for the reference list '{@link gov.sandia.dart.workflow.domain.Response#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Source</em>'.
	 * @see gov.sandia.dart.workflow.domain.Response#getSource()
	 * @see #getResponse()
	 * @generated
	 */
	EReference getResponse_Source();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.WFNode <em>WF Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>WF Node</em>'.
	 * @see gov.sandia.dart.workflow.domain.WFNode
	 * @generated
	 */
	EClass getWFNode();

	/**
	 * Returns the meta object for the reference list '{@link gov.sandia.dart.workflow.domain.WFNode#getInputPorts <em>Input Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Input Ports</em>'.
	 * @see gov.sandia.dart.workflow.domain.WFNode#getInputPorts()
	 * @see #getWFNode()
	 * @generated
	 */
	EReference getWFNode_InputPorts();

	/**
	 * Returns the meta object for the reference list '{@link gov.sandia.dart.workflow.domain.WFNode#getOutputPorts <em>Output Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Output Ports</em>'.
	 * @see gov.sandia.dart.workflow.domain.WFNode#getOutputPorts()
	 * @see #getWFNode()
	 * @generated
	 */
	EReference getWFNode_OutputPorts();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.WFNode#isStart <em>Start</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Start</em>'.
	 * @see gov.sandia.dart.workflow.domain.WFNode#isStart()
	 * @see #getWFNode()
	 * @generated
	 */
	EAttribute getWFNode_Start();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.WFNode#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see gov.sandia.dart.workflow.domain.WFNode#getType()
	 * @see #getWFNode()
	 * @generated
	 */
	EAttribute getWFNode_Type();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.WFNode#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see gov.sandia.dart.workflow.domain.WFNode#getLabel()
	 * @see #getWFNode()
	 * @generated
	 */
	EAttribute getWFNode_Label();

	/**
	 * Returns the meta object for the reference list '{@link gov.sandia.dart.workflow.domain.WFNode#getConductors <em>Conductors</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Conductors</em>'.
	 * @see gov.sandia.dart.workflow.domain.WFNode#getConductors()
	 * @see #getWFNode()
	 * @generated
	 */
	EReference getWFNode_Conductors();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.WFArc <em>WF Arc</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>WF Arc</em>'.
	 * @see gov.sandia.dart.workflow.domain.WFArc
	 * @generated
	 */
	EClass getWFArc();

	/**
	 * Returns the meta object for the reference '{@link gov.sandia.dart.workflow.domain.WFArc#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source</em>'.
	 * @see gov.sandia.dart.workflow.domain.WFArc#getSource()
	 * @see #getWFArc()
	 * @generated
	 */
	EReference getWFArc_Source();

	/**
	 * Returns the meta object for the reference '{@link gov.sandia.dart.workflow.domain.WFArc#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see gov.sandia.dart.workflow.domain.WFArc#getTarget()
	 * @see #getWFArc()
	 * @generated
	 */
	EReference getWFArc_Target();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.Property <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property</em>'.
	 * @see gov.sandia.dart.workflow.domain.Property
	 * @generated
	 */
	EClass getProperty();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Property#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see gov.sandia.dart.workflow.domain.Property#getType()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Type();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Property#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see gov.sandia.dart.workflow.domain.Property#getValue()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Value();

	/**
	 * Returns the meta object for the reference '{@link gov.sandia.dart.workflow.domain.Property#getNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Node</em>'.
	 * @see gov.sandia.dart.workflow.domain.Property#getNode()
	 * @see #getProperty()
	 * @generated
	 */
	EReference getProperty_Node();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.Port <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Port</em>'.
	 * @see gov.sandia.dart.workflow.domain.Port
	 * @generated
	 */
	EClass getPort();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Port#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see gov.sandia.dart.workflow.domain.Port#getType()
	 * @see #getPort()
	 * @generated
	 */
	EAttribute getPort_Type();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.InputPort <em>Input Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Input Port</em>'.
	 * @see gov.sandia.dart.workflow.domain.InputPort
	 * @generated
	 */
	EClass getInputPort();

	/**
	 * Returns the meta object for the reference '{@link gov.sandia.dart.workflow.domain.InputPort#getNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Node</em>'.
	 * @see gov.sandia.dart.workflow.domain.InputPort#getNode()
	 * @see #getInputPort()
	 * @generated
	 */
	EReference getInputPort_Node();

	/**
	 * Returns the meta object for the reference list '{@link gov.sandia.dart.workflow.domain.InputPort#getArcs <em>Arcs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Arcs</em>'.
	 * @see gov.sandia.dart.workflow.domain.InputPort#getArcs()
	 * @see #getInputPort()
	 * @generated
	 */
	EReference getInputPort_Arcs();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.InputPort#isTriggerOnly <em>Trigger Only</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Trigger Only</em>'.
	 * @see gov.sandia.dart.workflow.domain.InputPort#isTriggerOnly()
	 * @see #getInputPort()
	 * @generated
	 */
	EAttribute getInputPort_TriggerOnly();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.OutputPort <em>Output Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Output Port</em>'.
	 * @see gov.sandia.dart.workflow.domain.OutputPort
	 * @generated
	 */
	EClass getOutputPort();

	/**
	 * Returns the meta object for the reference '{@link gov.sandia.dart.workflow.domain.OutputPort#getNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Node</em>'.
	 * @see gov.sandia.dart.workflow.domain.OutputPort#getNode()
	 * @see #getOutputPort()
	 * @generated
	 */
	EReference getOutputPort_Node();

	/**
	 * Returns the meta object for the reference list '{@link gov.sandia.dart.workflow.domain.OutputPort#getArcs <em>Arcs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Arcs</em>'.
	 * @see gov.sandia.dart.workflow.domain.OutputPort#getArcs()
	 * @see #getOutputPort()
	 * @generated
	 */
	EReference getOutputPort_Arcs();

	/**
	 * Returns the meta object for the reference list '{@link gov.sandia.dart.workflow.domain.OutputPort#getResponseArcs <em>Response Arcs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Response Arcs</em>'.
	 * @see gov.sandia.dart.workflow.domain.OutputPort#getResponseArcs()
	 * @see #getOutputPort()
	 * @generated
	 */
	EReference getOutputPort_ResponseArcs();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.NamedObject <em>Named Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Named Object</em>'.
	 * @see gov.sandia.dart.workflow.domain.NamedObject
	 * @generated
	 */
	EClass getNamedObject();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.NamedObject#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see gov.sandia.dart.workflow.domain.NamedObject#getName()
	 * @see #getNamedObject()
	 * @generated
	 */
	EAttribute getNamedObject_Name();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.SAWNode <em>SAW Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>SAW Node</em>'.
	 * @see gov.sandia.dart.workflow.domain.SAWNode
	 * @generated
	 */
	EClass getSAWNode();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.SAWNode#getModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Model</em>'.
	 * @see gov.sandia.dart.workflow.domain.SAWNode#getModel()
	 * @see #getSAWNode()
	 * @generated
	 */
	EAttribute getSAWNode_Model();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.SAWNode#getComponent <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Component</em>'.
	 * @see gov.sandia.dart.workflow.domain.SAWNode#getComponent()
	 * @see #getSAWNode()
	 * @generated
	 */
	EAttribute getSAWNode_Component();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.Note <em>Note</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Note</em>'.
	 * @see gov.sandia.dart.workflow.domain.Note
	 * @generated
	 */
	EClass getNote();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Note#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Text</em>'.
	 * @see gov.sandia.dart.workflow.domain.Note#getText()
	 * @see #getNote()
	 * @generated
	 */
	EAttribute getNote_Text();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Note#getColor <em>Color</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Color</em>'.
	 * @see gov.sandia.dart.workflow.domain.Note#getColor()
	 * @see #getNote()
	 * @generated
	 */
	EAttribute getNote_Color();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Note#getFont <em>Font</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Font</em>'.
	 * @see gov.sandia.dart.workflow.domain.Note#getFont()
	 * @see #getNote()
	 * @generated
	 */
	EAttribute getNote_Font();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Note#getFontHeight <em>Font Height</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Font Height</em>'.
	 * @see gov.sandia.dart.workflow.domain.Note#getFontHeight()
	 * @see #getNote()
	 * @generated
	 */
	EAttribute getNote_FontHeight();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Note#getFontColor <em>Font Color</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Font Color</em>'.
	 * @see gov.sandia.dart.workflow.domain.Note#getFontColor()
	 * @see #getNote()
	 * @generated
	 */
	EAttribute getNote_FontColor();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Note#getFontStyle <em>Font Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Font Style</em>'.
	 * @see gov.sandia.dart.workflow.domain.Note#getFontStyle()
	 * @see #getNote()
	 * @generated
	 */
	EAttribute getNote_FontStyle();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.NamedObjectWithProperties <em>Named Object With Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Named Object With Properties</em>'.
	 * @see gov.sandia.dart.workflow.domain.NamedObjectWithProperties
	 * @generated
	 */
	EClass getNamedObjectWithProperties();

	/**
	 * Returns the meta object for the reference list '{@link gov.sandia.dart.workflow.domain.NamedObjectWithProperties#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Properties</em>'.
	 * @see gov.sandia.dart.workflow.domain.NamedObjectWithProperties#getProperties()
	 * @see #getNamedObjectWithProperties()
	 * @generated
	 */
	EReference getNamedObjectWithProperties_Properties();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.ResponseArc <em>Response Arc</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Response Arc</em>'.
	 * @see gov.sandia.dart.workflow.domain.ResponseArc
	 * @generated
	 */
	EClass getResponseArc();

	/**
	 * Returns the meta object for the reference '{@link gov.sandia.dart.workflow.domain.ResponseArc#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source</em>'.
	 * @see gov.sandia.dart.workflow.domain.ResponseArc#getSource()
	 * @see #getResponseArc()
	 * @generated
	 */
	EReference getResponseArc_Source();

	/**
	 * Returns the meta object for the reference '{@link gov.sandia.dart.workflow.domain.ResponseArc#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target</em>'.
	 * @see gov.sandia.dart.workflow.domain.ResponseArc#getTarget()
	 * @see #getResponseArc()
	 * @generated
	 */
	EReference getResponseArc_Target();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.Conductor <em>Conductor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Conductor</em>'.
	 * @see gov.sandia.dart.workflow.domain.Conductor
	 * @generated
	 */
	EClass getConductor();

	/**
	 * Returns the meta object for the reference '{@link gov.sandia.dart.workflow.domain.Conductor#getNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Node</em>'.
	 * @see gov.sandia.dart.workflow.domain.Conductor#getNode()
	 * @see #getConductor()
	 * @generated
	 */
	EReference getConductor_Node();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see gov.sandia.dart.workflow.domain.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Parameter#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see gov.sandia.dart.workflow.domain.Parameter#getType()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Type();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Parameter#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see gov.sandia.dart.workflow.domain.Parameter#getValue()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Value();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.Runner <em>Runner</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Runner</em>'.
	 * @see gov.sandia.dart.workflow.domain.Runner
	 * @generated
	 */
	EClass getRunner();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Runner#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see gov.sandia.dart.workflow.domain.Runner#getType()
	 * @see #getRunner()
	 * @generated
	 */
	EAttribute getRunner_Type();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.Arc <em>Arc</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Arc</em>'.
	 * @see gov.sandia.dart.workflow.domain.Arc
	 * @generated
	 */
	EClass getArc();

	/**
	 * Returns the meta object for class '{@link gov.sandia.dart.workflow.domain.Image <em>Image</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Image</em>'.
	 * @see gov.sandia.dart.workflow.domain.Image
	 * @generated
	 */
	EClass getImage();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Image#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Text</em>'.
	 * @see gov.sandia.dart.workflow.domain.Image#getText()
	 * @see #getImage()
	 * @generated
	 */
	EAttribute getImage_Text();

	/**
	 * Returns the meta object for the attribute '{@link gov.sandia.dart.workflow.domain.Image#getColor <em>Color</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Color</em>'.
	 * @see gov.sandia.dart.workflow.domain.Image#getColor()
	 * @see #getImage()
	 * @generated
	 */
	EAttribute getImage_Color();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DomainFactory getDomainFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.ResponseImpl <em>Response</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.ResponseImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getResponse()
		 * @generated
		 */
		EClass RESPONSE = eINSTANCE.getResponse();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESPONSE__TYPE = eINSTANCE.getResponse_Type();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE__SOURCE = eINSTANCE.getResponse_Source();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.WFNodeImpl <em>WF Node</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.WFNodeImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getWFNode()
		 * @generated
		 */
		EClass WF_NODE = eINSTANCE.getWFNode();

		/**
		 * The meta object literal for the '<em><b>Input Ports</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WF_NODE__INPUT_PORTS = eINSTANCE.getWFNode_InputPorts();

		/**
		 * The meta object literal for the '<em><b>Output Ports</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WF_NODE__OUTPUT_PORTS = eINSTANCE.getWFNode_OutputPorts();

		/**
		 * The meta object literal for the '<em><b>Start</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WF_NODE__START = eINSTANCE.getWFNode_Start();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WF_NODE__TYPE = eINSTANCE.getWFNode_Type();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WF_NODE__LABEL = eINSTANCE.getWFNode_Label();

		/**
		 * The meta object literal for the '<em><b>Conductors</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WF_NODE__CONDUCTORS = eINSTANCE.getWFNode_Conductors();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.WFArcImpl <em>WF Arc</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.WFArcImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getWFArc()
		 * @generated
		 */
		EClass WF_ARC = eINSTANCE.getWFArc();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WF_ARC__SOURCE = eINSTANCE.getWFArc_Source();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WF_ARC__TARGET = eINSTANCE.getWFArc_Target();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.PropertyImpl <em>Property</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.PropertyImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getProperty()
		 * @generated
		 */
		EClass PROPERTY = eINSTANCE.getProperty();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__TYPE = eINSTANCE.getProperty_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__VALUE = eINSTANCE.getProperty_Value();

		/**
		 * The meta object literal for the '<em><b>Node</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROPERTY__NODE = eINSTANCE.getProperty_Node();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.PortImpl <em>Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.PortImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getPort()
		 * @generated
		 */
		EClass PORT = eINSTANCE.getPort();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PORT__TYPE = eINSTANCE.getPort_Type();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.InputPortImpl <em>Input Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.InputPortImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getInputPort()
		 * @generated
		 */
		EClass INPUT_PORT = eINSTANCE.getInputPort();

		/**
		 * The meta object literal for the '<em><b>Node</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INPUT_PORT__NODE = eINSTANCE.getInputPort_Node();

		/**
		 * The meta object literal for the '<em><b>Arcs</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INPUT_PORT__ARCS = eINSTANCE.getInputPort_Arcs();

		/**
		 * The meta object literal for the '<em><b>Trigger Only</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INPUT_PORT__TRIGGER_ONLY = eINSTANCE.getInputPort_TriggerOnly();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.OutputPortImpl <em>Output Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.OutputPortImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getOutputPort()
		 * @generated
		 */
		EClass OUTPUT_PORT = eINSTANCE.getOutputPort();

		/**
		 * The meta object literal for the '<em><b>Node</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OUTPUT_PORT__NODE = eINSTANCE.getOutputPort_Node();

		/**
		 * The meta object literal for the '<em><b>Arcs</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OUTPUT_PORT__ARCS = eINSTANCE.getOutputPort_Arcs();

		/**
		 * The meta object literal for the '<em><b>Response Arcs</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OUTPUT_PORT__RESPONSE_ARCS = eINSTANCE.getOutputPort_ResponseArcs();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.NamedObjectImpl <em>Named Object</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.NamedObjectImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getNamedObject()
		 * @generated
		 */
		EClass NAMED_OBJECT = eINSTANCE.getNamedObject();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAMED_OBJECT__NAME = eINSTANCE.getNamedObject_Name();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.SAWNodeImpl <em>SAW Node</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.SAWNodeImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getSAWNode()
		 * @generated
		 */
		EClass SAW_NODE = eINSTANCE.getSAWNode();

		/**
		 * The meta object literal for the '<em><b>Model</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SAW_NODE__MODEL = eINSTANCE.getSAWNode_Model();

		/**
		 * The meta object literal for the '<em><b>Component</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SAW_NODE__COMPONENT = eINSTANCE.getSAWNode_Component();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.NoteImpl <em>Note</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.NoteImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getNote()
		 * @generated
		 */
		EClass NOTE = eINSTANCE.getNote();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTE__TEXT = eINSTANCE.getNote_Text();

		/**
		 * The meta object literal for the '<em><b>Color</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTE__COLOR = eINSTANCE.getNote_Color();

		/**
		 * The meta object literal for the '<em><b>Font</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTE__FONT = eINSTANCE.getNote_Font();

		/**
		 * The meta object literal for the '<em><b>Font Height</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTE__FONT_HEIGHT = eINSTANCE.getNote_FontHeight();

		/**
		 * The meta object literal for the '<em><b>Font Color</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTE__FONT_COLOR = eINSTANCE.getNote_FontColor();

		/**
		 * The meta object literal for the '<em><b>Font Style</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTE__FONT_STYLE = eINSTANCE.getNote_FontStyle();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.NamedObjectWithPropertiesImpl <em>Named Object With Properties</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.NamedObjectWithPropertiesImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getNamedObjectWithProperties()
		 * @generated
		 */
		EClass NAMED_OBJECT_WITH_PROPERTIES = eINSTANCE.getNamedObjectWithProperties();

		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NAMED_OBJECT_WITH_PROPERTIES__PROPERTIES = eINSTANCE.getNamedObjectWithProperties_Properties();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.ResponseArcImpl <em>Response Arc</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.ResponseArcImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getResponseArc()
		 * @generated
		 */
		EClass RESPONSE_ARC = eINSTANCE.getResponseArc();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE_ARC__SOURCE = eINSTANCE.getResponseArc_Source();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESPONSE_ARC__TARGET = eINSTANCE.getResponseArc_Target();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.ConductorImpl <em>Conductor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.ConductorImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getConductor()
		 * @generated
		 */
		EClass CONDUCTOR = eINSTANCE.getConductor();

		/**
		 * The meta object literal for the '<em><b>Node</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONDUCTOR__NODE = eINSTANCE.getConductor_Node();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.ParameterImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getParameter()
		 * @generated
		 */
		EClass PARAMETER = eINSTANCE.getParameter();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__TYPE = eINSTANCE.getParameter_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__VALUE = eINSTANCE.getParameter_Value();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.RunnerImpl <em>Runner</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.RunnerImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getRunner()
		 * @generated
		 */
		EClass RUNNER = eINSTANCE.getRunner();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RUNNER__TYPE = eINSTANCE.getRunner_Type();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.ArcImpl <em>Arc</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.ArcImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getArc()
		 * @generated
		 */
		EClass ARC = eINSTANCE.getArc();

		/**
		 * The meta object literal for the '{@link gov.sandia.dart.workflow.domain.impl.ImageImpl <em>Image</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.sandia.dart.workflow.domain.impl.ImageImpl
		 * @see gov.sandia.dart.workflow.domain.impl.DomainPackageImpl#getImage()
		 * @generated
		 */
		EClass IMAGE = eINSTANCE.getImage();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMAGE__TEXT = eINSTANCE.getImage_Text();

		/**
		 * The meta object literal for the '<em><b>Color</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMAGE__COLOR = eINSTANCE.getImage_Color();

	}

} //DomainPackage
