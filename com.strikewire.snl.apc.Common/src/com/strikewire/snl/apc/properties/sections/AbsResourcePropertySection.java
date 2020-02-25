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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * <p>
 * This class provides some convenience methods for dealing with sections in a
 * tabbed property view with specific regard to IResource objects.
 * </p>
 * 
 * <p>
 * It will track the current selections, updating the IResources. Be sure to
 * call super.setInput(...) in the implemented setInput(...) method.
 * </p>
 * 
 * 
 * A section is specified in the plugin.xml file, and has the following
 * notation:
 * 
 * <pre>
 *          &lt;propertySection
 *                class="com.strikewire.snl.apc.projectexplorer.properties.BasicAttributesSection"
 *                filter="com.strikewire.snl.apc.projectexplorer.properties.filters.IsNotFile"
 *                id="com.strikewire.snl.apc.projectexplorer.properties.BasicAttributesSection"
 *                tab="com.strikewire.snl.apc.ProjectExplorer.properties.basicTab">
 *           &lt;input type="gov.sandia.dart.TreeObject" />
 *         &lt;/propertySection>
 * </pre>
 * 
 * where:
 * <ul>
 * <li><b>class</b>: the specific class to be added; it may inherit from this
 * class. It provides the layout of the widgets, and handles any updating calls
 * that result from a modification.</li>
 * <li><b>filter</b>: an optional filter which indicates if the section should
 * be displayed. If a tab has no sections, the tab is not displayed. A section
 * need not be displayed if it is not relevant to the selected object.</li>
 * <li><b>id</b>: the id for the section; usually just the class name</li>
 * <li><b>tab</b>: the tab for the section. Note that a section may appear on
 * more than one tab, as appropriate</li>
 * <li><b>input type</b>Not invoked (and therefore need not be specified), if a
 * filter is in use; otherwise only those selected objects which are of the
 * given type are passed to the section. Note that either this setting or the
 * filter needs to be specified in order to have anything passed.
 * </ul>
 * Of course, see latest documentation from Eclipse.
 * <p/>
 * *
 * 
 * @author kholson
 * 
 */
public abstract class AbsResourcePropertySection extends
    AbstractPropertySection implements IJobChangeListener,
    IPropertyChangeListener
{

  /**
   * TEXT_FIELD_FORM_PERCENTAGE - When using a text field, what percentage of
   * the form should be used for it?
   */
  protected int TEXT_FIELD_FORM_PERCENTAGE = 80;

  /**
   * LABEL_RIGHT_SIZE - When using a label on the front of a field, where is the
   * right edge?
   */
  protected int LABEL_RIGHT_SIZE = STANDARD_LABEL_WIDTH + 30;


  protected final static IStructuredSelection EMPTY_SELECTION =
      new StructuredSelection();


  /**
   * _selections - The current selections; some overlap with the
   * AbstractPropertySection "selections" variable, but that one is of type
   * ISelection, and is private
   */
  protected IStructuredSelection _selections = new StructuredSelection();


  /**
   * _curResources - The current resources, updated in the setInput(...) method.
   */
  protected List<IResource> _curResources = new ArrayList<>();

  // /**
  // * _composite - Set on the call to .createControls(), a basic composite to
  // * which widgets may be added. It is null until the call is made.
  // */
  // protected Composite _composite = null;
  //
  // /**
  // * _widgetFactory - The widget factory; convenience reference
  // */
  // protected TabbedPropertySheetWidgetFactory _widgetFactory;

  /**
   * _propertySource - A source for properties
   */
  protected IPropertySource _propertySource = null;

  /**
   * _page - Holds the "page" of properties
   */
  protected PropertySheetPage _page;


  /**
   * _setAddedButtons - All of the Buttons that have been added; the .dispose()
   * will clear them
   */
  protected Set<SectionButtons> _setAddedButons = new HashSet<>();


  /**
   * _gc - Graphics Context
   */
  protected GC _gc = null;


  /**
   * _fontMetrics - The font metrics
   */
  protected FontMetrics _fontMetrics = null;

  /**
   * _lineSep - Holds the line separator, which is system dependent
   */
  protected String _lineSep = System.getProperty("line.separator", "\n");




  // /**
  // * _sbBuf - A buffer for formatting; do not allocate every time
  // */
  // private StringBuilder _sbBuf = new StringBuilder();


  /**
   * Default constructor
   */
  public AbsResourcePropertySection()
  {
  }




  protected SectionButtons newSectionButtons()
  {
    SectionButtons secBtns = new SectionButtons();
    _setAddedButons.add(secBtns);

    return secBtns;
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
   */
  @Override
  public void dispose()
  {

    for (SectionButtons secButton : _setAddedButons)
    {
      for (Button btn : secButton.getAllocatedButtons())
      {
        if (btn != null && !btn.isDisposed())
        {
          Image img = btn.getImage();
          if (img != null && !img.isDisposed())
          {
            img.dispose();
            btn.setImage(null);
          }
        }
      }
    }


    if (_gc != null && !_gc.isDisposed())
    {
      _gc.dispose();
    }

    _gc = null;
    _fontMetrics = null;


    super.dispose();
  }




  /**
   * If a class needs to use the graphics context, it should call this method;
   * the GC is disposed in this dispose method if it is allocated. Many methods
   * in this class attempt to initilize if possible, but if the implementing
   * classes do not utilize the label or formatting methods, it is possible for
   * the GC to not be automatically initialized, and would need to be called
   * directly. Setting the GC also sets the font metrics.
   * <p/>
   * <b>Note:</b> will only initialize if not already initialized. However, the
   * test/initialization is likely not thread safe, but since this class is
   * (most likely) running in the single threaded UI, it should not matter. That
   * is test & then instantiate is not atomic.
   */
  protected void initGC(final Drawable widget)
  {
    if (_gc == null)
    {
      _gc = new GC(widget);
      _fontMetrics = _gc.getFontMetrics();
    }
  }




  protected GC getGraphicsContext()
  {
    return _gc;
  }




  protected FontMetrics getFontMetrics()
  {
    return _fontMetrics;
  }




  /**
   * When the selection changes, this method is invoked. This method calls
   * super.setInput(), and then updates the instance variable for the current
   * IResource objects, obtained via the getAdapter method
   * 
   * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput
   *      (org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);

    // release existing instance
    _curResources.clear();

    if (selection instanceof IStructuredSelection)
    {
      _selections = (IStructuredSelection) selection;

      Iterator<?> it = _selections.iterator();

      while (it.hasNext())
      {
        Object o = it.next();

        if (o instanceof IAdaptable)
        {
          IResource res =
              ((IAdaptable) o).getAdapter(IResource.class);

          if (res != null)
          {
            _curResources.add(res);
          }
        }


      } // while : process all of the selections
    } // if : we are dealing w/a structured selection
    else
    {
      _selections = EMPTY_SELECTION;
    }
  } // setInput





  /**
   * <p>
   * Adds the two column name/value on the form; be sure to have called the
   * basic createControls(...) first.
   * </p>
   * <p>
   * Shifts the layout of the specified parent to form layout, and
   * sets the attachments to left and right
   * </p>
   * <p>
   * instantiates the instance variable _page to a new PropertySheetPage(), and
   * sets the control to the specified parent.
   * </p>
   * 
   * @param parent
   * @param atabbedPropertySheetPage
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 6, 2012
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  protected void createResourceControls(final Composite parent,
                                        final TabbedPropertySheetPage atabbedPropertySheetPage)
  {
    Composite comp;

    _page = new PropertySheetPage();


    comp = parent;    
    _page.createControl(comp);
    
    
    ThreeColumnLayoutHelper tclh = 
        new ThreeColumnLayoutHelper(getWidgetFactory());
    
    tclh.addFormLayout(parent);
    
    Control pageControl = _page.getControl();
    
    FormData fd = tclh.alignButtonRight(pageControl);
    fd.left = new FormAttachment(0, 5);
    fd.width = 50;
     

  } // createResourceControls




  /**
   * Returns the IStructuredSelection, with the raw selections from a setInput()
   * change; may return an empty selection (use .isEmpty()).
   */
  public IStructuredSelection getSelections()
  {
    return _selections;
  }




  /**
   * From the getSelections(), find and return all instances of IResource. May
   * return an empty List. Returned list is not modifiable
   */
  public List<IResource> getIResources()
  {
    return Collections.unmodifiableList(_curResources);
  } // getIResources




  /**
   * Often only need the first IResource in a selection; provides a convenience
   * method for accessing; may return null if no IResources are available.
   * 
   * @return
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 6, 2012
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  protected IResource getFirstIResource()
  {
    IResource res = null;


    if (getIResources().size() > 0)
    {
      res = getIResources().get(0);
    }

    return res;
  } // getFirstIResource




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
   * .jface.util.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(PropertyChangeEvent event)
  {
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse
   * .core.runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void aboutToRun(IJobChangeEvent event)
  {
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core
   * .runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void awake(IJobChangeEvent event)
  {
  }




  /**
   * <p>
   * If invoked, will log if the event indicates anything but status.ok_status
   * </p>
   * 
   * <p>
   * Remember that no longer in the UI thread, so may need to shift back to
   * update values.
   * </p>
   * 
   * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void done(IJobChangeEvent event)
  {
    IStatus status = event.getResult();

    if (status != Status.OK_STATUS)
    {
      CommonPlugin.getDefault().log(status);
    }
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core
   * .runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void running(IJobChangeEvent event)
  {
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.
   * core.runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void scheduled(IJobChangeEvent event)
  {
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core
   * .runtime.jobs.IJobChangeEvent)
   */
  @Override
  public void sleeping(IJobChangeEvent event)
  {
  }

}
