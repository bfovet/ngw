package gov.sandia.dart.workflow.editor.rendering;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class ParameterFileGARenderer extends TableGARenderer {

	private Map<String, String> cachedList_ = null;

	long cachedModification_ = -1;

	@Override
	protected Map<String, String> getNameValuePairs() {
		if (cachedList_ == null) {
			cachedList_ = new LinkedHashMap<>();
		}

		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		Object bo = fp.getBusinessObjectForPictogramElement(pe);

		String fileName = null;
		if (bo instanceof WFNode) {
			WFNode node = (WFNode) bo;
			fileName = PropertyUtils.resolveProperty(node, "fileName");
		}

		if (fileName != null) {
			IPath relativePath = new Path(fileName);

			String fullFilename = null;

			URI uri = rc.getDiagramTypeProvider().getDiagram().eResource().getURI();
			if (uri == null) {
				// Do nothing
			} else if (uri.isPlatform()) {
				IPath diagramPath = new Path(uri.toPlatformString(true));
				IFile iDiagramFile = ResourcesPlugin.getWorkspace().getRoot().getFile(diagramPath);
				String osParentPath = iDiagramFile.getParent().getLocation().toOSString();
				IPath parameterOSPath = new Path(osParentPath).append(relativePath);
				fullFilename = parameterOSPath.toOSString();

			} else if (uri.isFile()) {
				fullFilename = uri.toFileString();
			}

			if (fullFilename != null) {
				try {
					File file = new File(fullFilename);

					long lastModified = file.lastModified();

					if (lastModified == cachedModification_) {
						return cachedList_;
					} 
					

					PFile p = new PFile(new FileReader(file));
					cachedModification_ = lastModified;
					cachedList_.clear();
					
					for (String key: p.keys()) {						
						cachedList_.put(key, p.get(key));
					}
				} catch (IOException e) {
					cachedList_.clear();
					cachedList_.put("Error", e.getMessage());
				}
			}
		}

		return cachedList_;
	}

}
