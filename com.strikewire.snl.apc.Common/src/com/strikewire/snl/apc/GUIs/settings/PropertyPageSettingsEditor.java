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
 * 
 */
package com.strikewire.snl.apc.GUIs.settings;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.CompositeUtils;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

/**
 * @author mjgibso
 * 
 */
public class PropertyPageSettingsEditor extends AbstractMultiSettingsEditor<Object>
    implements IMultiSettingsEditor<Object>
{
  private Composite _parent;

  private IPropertySheetPage _propSheetPage;  
  
  private List<Object> _nodes;

  /**
   * _classnamesForSources - For all of the inbound potential sources, track the
   * associated class names; we currently do not support selecting from
   * disparate types of objects
   */
  private final Set<String> _classnamesForSources = new HashSet<String>();



  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.GUIs.settings.ISettingsEditor#createPartControl(org.eclipse.swt.widgets.Composite, com.strikewire.snl.apc.selection.MultiControlSelectionProvider, com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar)
   */
  @Override
  public void createPartControl(IManagedForm mform,
                                IMessageView messageView,
                                MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg)
  {
	  super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
	  _parent = mform.getToolkit().createComposite(mform.getForm().getBody());
	  mform.getForm().getBody().setLayout(new FormLayout());
	  final FormData layoutData = new FormData(500, 1500);
	  layoutData.right = new FormAttachment(100, 0);
	  layoutData.left = new FormAttachment(0, 0);
	  _parent.setLayoutData(layoutData);
	  _parent.setLayout(new FillLayout());
  }




  /**
   * May return null
   */
  private IPropertySheetPage getPropertySheetPageForNode(Object node)
  {
    IPropertySheetPage ips =
        (IPropertySheetPage) Platform.getAdapterManager().getAdapter(node,
            IPropertySheetPage.class);


    return ips;
  }



  /**
   * Initializes the property sheet page for the specified object
   */
  private boolean initPropertySheetPage(Object node)
  {
    boolean bIsPropertySheet = false;

    IPropertySheetPage ips = getPropertySheetPageForNode(node);

    if (ips != null) {
      if (_propSheetPage == null) {

        _propSheetPage = ips;

        if (_propSheetPage instanceof IPageBookViewPage) {
        	try {
        		IViewPart settingsView = getHostView();
        		((IPageBookViewPage) _propSheetPage).init(new PageSite(settingsView.getViewSite()));
        	}
        	catch (PartInitException e) {
				CommonPlugin.getDefault().logError("Problem creating property sheet", e);
        	}
        } 

        _propSheetPage.createControl(_parent);
        
      } // if : the property sheet is null

      bIsPropertySheet = true;
    } // if : an IPropertySheet

    return bIsPropertySheet;
  }

  /**
   * Sorts the nodes into a Set in order to allow finding the various classes
   * that are represented by the colleciton of nodes. The inbound collection is
   * cleared at entry.
   */
  private Set<String> sortNodesToClassnames(final Set<String> collection,
                                            final Collection<Object> nodes)
  {
    collection.clear();

    // if nothing to process, then leave
    if (nodes == null) {
      return collection;
    }

    for (Object o : nodes) {
      if (o != null) {
        collection.add(o.getClass().toString());
      }
    }

    return collection;
  }

  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.GUIs.settings.IMultiSettingsEditor#setNodes(java.util.Collection)
   */
  @Override
  public void setNodes(List<Object> nodes)
  {
	this._nodes = nodes;
	
    sortNodesToClassnames(_classnamesForSources, nodes);

    if (_classnamesForSources.size() <= 1 && nodes.size() > 0) {
      Object node = nodes.iterator().next();
      if (initPropertySheetPage(node)) {
        IWorkbenchPart wbp = getActiveWorkbenchPart();

        _propSheetPage.selectionChanged(wbp,
            new StructuredSelection(nodes.toArray()));
      }
    } else {
    	 CompositeUtils.removeChildrenFromComposite(_parent, true);
    }

  }




  /**
   * Returns the current workbench part; may return null if there are issues
   * with determining the part; may happen, e.g., if not on the UI thread.
   */
  private IWorkbenchPart getActiveWorkbenchPart()
  {
    IWorkbenchPart retPart = null;

    IWorkbench wb = PlatformUI.getWorkbench();

    if (wb == null) {
      return retPart;
    }

    IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();

    if (wbw == null) {
      return retPart;
    }

    IWorkbenchPage wbp = wbw.getActivePage();

    if (wbp == null) {
      return retPart;
    }

    retPart = wbp.getActivePart();

    return retPart;
  }




  @Override
  public void dispose()
  {
	  if (_propSheetPage != null) {
		  _propSheetPage.dispose();		  
    	_propSheetPage = null;
	  }
  }




  /**
   * Always returns an empty List; the nodes are not kept in this class
   * 
   * @see com.strikewire.snl.apc.GUIs.settings.IMultiSettingsEditor#getNodes()
   */
  @Override
  public List<Object> getNodes()
  {
	return this._nodes!=null ? Collections.unmodifiableList(this._nodes) : Collections.emptyList();
  }
  
  // FIXME, I don't like us explicitly doing things with specifically the settings view, tying ourselves so closely to it.
  private IViewPart getHostView() {
	    try {
			return
					PlatformUI.getWorkbench()
			        .getActiveWorkbenchWindow()
			        .getActivePage()
			        .showView(SettingsView.VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
		} catch (PartInitException e) {
			CommonPlugin.getDefault().logError("Problems creating property page", e);
			return null;
		}
  }


}
