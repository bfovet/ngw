/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*---------------------------------------------------------------------------*/
/*
 *  Copyright (C) 2012
 *  Sandia National Laboratories
 *
 *  File originated by:
 *  StrikeWire, LLC
 *  149 South Briggs St., #102-A
 *  Erie, CO 80516
 *  (720) 890-8590
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.properties.sections;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


/**
 * @author kholson
 * 
 */
public class ResourceSection extends AbsResourcePropertySection
{

  /**
   * 
   */
  public ResourceSection()
  {
  }




  /*
   * (non-Javadoc)
   * 
   * @see com.strikewire.snl.apc.projectexplorer.properties.sections.
   * AbsProjectExplorerPropertySection#enableEditing(boolean)
   */
  protected void enableEditing(final boolean enable)
  {
  }


  public void createControls(Composite parent,
                             final TabbedPropertySheetPage atabbedPropertySheetPage)
  {
    super.createControls(parent, atabbedPropertySheetPage);
    
    ThreeColumnLayoutHelper tclh =
        new ThreeColumnLayoutHelper(getWidgetFactory());
    
    Composite comp = tclh.makeFlatComposite(parent);
    
    createResourceControls(comp, atabbedPropertySheetPage);
  }


  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.
   * runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void done(IJobChangeEvent event)
  {
  }




  /**
   * @see org.eclipse.ui.views.properties.tabbed.ISection#dispose()
   */
  public void dispose()
  {
    super.dispose();

    if (_page != null) {
      _page.dispose();
      _page = null;
    }

  }



  /**
   * If page is not null, will set the specified selection onto the page
   */
  protected void setResourceSelectionChanged(IWorkbenchPart part,
                                             IStructuredSelection ssel)
  {
    if (_page != null) {
      _page.selectionChanged(part, ssel);
    }
  }
  

  /*
   * (non-Javadoc)
   * 
   * @see com.strikewire.snl.apc.projectexplorer.properties.sections.
   * AbsProjectExplorerPropertySection#setInput(org.eclipse.ui.IWorkbenchPart,
   * org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);

    _propertySource = null;

    int numElements = (_selections != null) ? _selections.size() : 0;

    IResource res = getFirstIResource();
    
    IStructuredSelection structSel = new StructuredSelection();

    if (res != null) {
      _propertySource = (IPropertySource) res.getAdapter(IPropertySource.class);
      structSel = new StructuredSelection(res);
    }
//    else {
//      res = (IResource)(new Object());
//    }



    switch (numElements) {
      case 0: // really shouldn't hit this case
        enableEditing(false);
        setResourceSelectionChanged(part, structSel);
        break;

      case 1:
        // test
        setResourceSelectionChanged(part, structSel);
        enableEditing(false);
        break;

      default:
        setResourceSelectionChanged(part, structSel);
        enableEditing(false);
        break;
    } // switch
  } // setInput




  /**
   * @see org.eclipse.ui.views.properties.tabbed.ISection#shouldUseExtraSpace()
   */
  public boolean shouldUseExtraSpace()
  {
    return true;
  }


}
