/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.parser;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;

public class Domain {
	public static class IWFObject {
		public String name;
	}
	public static class IWFProperty extends IWFObject {
		public IWFProperty() {}
		public IWFProperty(String name, String type, String value) {
			this.name = name;
			this.type = type;
			this.value = value;
		}
		public String type;
		public String value;
	}
	public static class IWFNode extends IWFObject {
		public IWFNode() {}
		public IWFInputPort getInputPort(String name) {
			for (IWFInputPort p: inputPorts) {
				if (p.name.equals(name))
					return p;
			}
			throw new SAWWorkflowException("No such input port " + name);
		}
		public IWFOutputPort getOutputPort(String name) {
			for (IWFOutputPort p: outputPorts) {
				if (p.name.equals(name))
					return p;
			}
			throw new SAWWorkflowException("No such output port " + name);
		}
		public IWFNode connect(String op, IWFNode node, String ip) {
			new IWFArc(getOutputPort(op), node.getInputPort(ip));		
			return this;
		}
		public IWFNode connect(String op, IWFResponse r) {
			new IWFResponseArc(getOutputPort(op), r);
			return this;
		}
		public String type;
		public List<IWFInputPort> inputPorts = new ArrayList<>();
		public List<IWFOutputPort> outputPorts = new ArrayList<>();
		public List<IWFProperty> properties = new ArrayList<>();
		public List<IWFConductor> conductors = new ArrayList<>();
		public boolean isStart;
	}
	public static class IWFConductor extends IWFObject {
		public List<IWFProperty> properties = new ArrayList<>();
	}

	public static class IWFInputPort extends IWFObject {
		public IWFInputPort() {}
		public IWFInputPort(String name, String itype) {
			this.name = name;
			this.type = itype;
		}
		public String type;
		public IWFNode node;
		public List<IWFArc> arcs = new ArrayList<>();
		public List<IWFProperty> properties = new ArrayList<>();

	}
	public static class IWFOutputPort extends IWFObject {
		public IWFOutputPort() {}
		public IWFOutputPort(String name, String type) {
			this.name = name;
			this.type = type;
		}
		public String type;
		public IWFNode node;
		public List<IWFArc> arcs = new ArrayList<>();
		public List<IWFProperty> properties = new ArrayList<>();
	}
	
	public static class IWFArc extends IWFObject {
		public IWFArc(IWFOutputPort out, IWFInputPort in) {
			source = out;
			target = in;
			out.arcs.add(this);
			in.arcs.add(this);
		}
		public IWFArc() { }
		public IWFInputPort target;
		public IWFOutputPort source;
		public List<IWFProperty> properties = new ArrayList<>();
	}
	public static class IWFParameter extends IWFObject {
		public boolean global;
		public String type;
		public String value;
	}
	public static class IWFResponse extends IWFObject {
		public IWFResponse(String name) {
			this.name = name;
			this.type = "default";
		}
		public IWFResponse() {}
		public String type;
		public List<IWFResponseArc> sources = new ArrayList<>();
	}
	public static class IWFResponseArc extends IWFObject {
		public IWFResponseArc() {}
		public IWFResponseArc(IWFOutputPort outputPort, IWFResponse response) {
			this.source = outputPort;
			this.target = response;
			response.sources.add(this);
		}
		public String type;
		public IWFOutputPort source;
		public IWFResponse target;
	}
}
