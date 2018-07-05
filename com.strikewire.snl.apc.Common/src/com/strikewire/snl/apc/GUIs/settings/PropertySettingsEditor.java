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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.CompositeUtils;
import com.strikewire.snl.apc.gui.propertydescriptors.IMultiSelectPropertyDescriptor;
import com.strikewire.snl.apc.properties.INotifyingPropertySource;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;
import com.strikewire.snl.apc.util.Utils;

/**
 * @author mjgibso
 * 
 */
public class PropertySettingsEditor extends AbstractMultiSettingsEditor<Object>
    implements IMultiSettingsEditor<Object>, IPropertyChangeListener
{

  /**
   * _classnamesForSources - For all of the inbound potential sources, track the
   * associated class names; we currently do not support selecting from
   * disparate types of objects
   */
  private final Set<String> _classnamesForSources = new HashSet<String>();

  /**
   * _settingsCategories - The categories for this settings page
   */
  private final Categories _settingsCategories = new Categories();

  /**
   * _numVisibileCategories - Tracks the number of visible categories displayed
   * in the section
   */
  private int _numVisibileCategories;
  private Image image_;

  /**
   * Tracks the current selection of nodes this editor is initialized for
   */
  private List<Object> _nodes;


  public PropertySettingsEditor()
  {
  }


  /*
   * (non-Javadoc)
   * 
   * @see com.strikewire.snl.apc.GUIs.settings.ISettingsEditor#createPartControl
   * (org.eclipse.ui.IWorkbenchPartSite, org.eclipse.swt.widgets.Composite,
   * java.lang.Object)
   */
  @Override
  public void createPartControl(IManagedForm mform,
                                IMessageView messageView,
                                MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg
                                )
  {
	  super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
	    TableWrapLayout layout = new TableWrapLayout();
	    layout.numColumns = 2;
	    form.getBody().setLayout(layout);
	    form.getBody().addControlListener(new ControlAdapter() {
	    	@Override
	    	public void controlResized(ControlEvent e) {
	    		setNodes(getNodes());
	    	}
	    });
  }

  private void createHeader(Collection<Object> nodes)
  {
    Object first = nodes.iterator().next();
    if (first instanceof IAdaptable) {
      IAdaptable adaptable = (IAdaptable) first;
      IPropertiesHeading heading =
          (IPropertiesHeading) adaptable.getAdapter(IPropertiesHeading.class);
      if (heading != null) {
        String title = "";
        if (nodes.size() == 1) {
          title += heading.getName();
        }
        else if (nodes.size() > 1) {
          title += heading.getType();
          title += " Properties";
        }
        form.setText(title);
        if (heading.getImageDescriptor() != null) {
          image_ = heading.getImageDescriptor().createImage();
          form.setImage(image_);
        }
        else
          form.setImage(null);
      }
    }
  }



  /**
   * Takes the object and returns the property source for it if possible, null
   * otherwise.
   * 
   */
  private static IPropertySource getPropertySource(Object obj)
  {
    if (obj == null) {
      return null;
    }
    
    if (obj instanceof IPropertySource) {
      return (IPropertySource)obj;
    }
    
    IPropertySource propSource =
        (IPropertySource) Platform.getAdapterManager().getAdapter(obj,
            IPropertySource.class);
        
    return propSource;
  }




  /**
   * Clears the categories
   */
  private void clearCategories()
  {
    _settingsCategories.clear();
    _numVisibileCategories = 0;
  }




  /**
   * Takes the potential IPropertySource and returns the array of
   * IPropertyDescriptor objects associated with it. Will return an empty array
   * if source is null.
   * 
   * @param source
   *          The IPropertySource; may be null
   * @return An array of IPropertyDescriptor objects, which may be an empty
   *         array if source is null or does not have any associated property
   *         descriptors.
   */
  private IPropertyDescriptor[] getPropertyDescriptorsForSource(IPropertySource source)
  {
    if (source == null) {
      return new IPropertyDescriptor[0];
    }

    return source.getPropertyDescriptors();
  }





  /**
   * Takes all of the specified nodes and obtains the property source for them.
   * For the source, gets all of the property descriptors. For each property
   * descriptor, get the category, put the descriptor into the category.
   */
  private void processNodesIntoCategories(Collection<Object> nodes)
  {
    for (Object node : nodes) {
      IPropertySource source = getPropertySource(node);
      
      if(source instanceof INotifyingPropertySource)
      {
    	  ((INotifyingPropertySource) source).setPropertyChangeListener(this);
      }

      IPropertyDescriptor[] propDescs = getPropertyDescriptorsForSource(source);

      // loop over all of the property descriptors for this source
      // separate each property descriptor by the category
      for (IPropertyDescriptor pd : propDescs) {
        String cat = pd.getCategory();

        // will give us the category entry for the particular category,
        CatEntry catEntry = _settingsCategories.getCatEntry(cat);

        catEntry.addPropertyDescriptorAndSource(pd, source);
      }
    } // for

    _numVisibileCategories = _settingsCategories.getCategories().size();
  }




  /**
   * Adds TableWrapData with fill_grap and colspan = 2 to the specified
   * composite
   */
  private void addTableWrapData(Control control)
  {
    TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
    td.colspan = 2;
    control.setLayoutData(td);
  }




  /**
   * Makes the section entry for the category; a section has the title of the
   * category, and is expandable.
   */
  private Section makeSectionForCategoryEntry(CatEntry catEntry)
  {
    int style =
        Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
            | Section.EXPANDED;
    
    if (form == null || form.isDisposed()) {
      return null;
    }

    Section sec = toolkit.createSection(form.getBody(), style);
    addTableWrapData(sec);

    sec.setText(catEntry.getCategory());

    sec.addExpansionListener(new ExpansionAdapter() {
      public void expansionStateChanged(ExpansionEvent e)
      {
        form.reflow(true);
      }
    });

    return sec;
  }




  /**
   * The section client is the area under a section that may be shown or hidden;
   * this method creates that area (which is a composite), and sets an
   * appropriate layout manager for this new composite.
   */
  private Composite makeSectionClient(Section sec)
  {
    if (toolkit == null) {
      return null;
    }
    
    Composite sectionClient = toolkit.createComposite(sec);
    addTableWrapData(sectionClient);

    FormLayout layout = new FormLayout();

    sectionClient.setLayout(layout);

    return sectionClient;
  }




  private Label makeLabel(Composite comp, String text)
  {
    Label lbl = toolkit.createLabel(comp, text, SWT.WRAP);

    return lbl;
  }


  /**
   * Obtains the form data from the specified control; if the layout data does
   * not exist, then creates a new one
   */
  private FormData getFormData(final Control control)
  {
    FormData fd;
    if (control != null && control.getLayoutData() instanceof FormData) {
      fd = (FormData) control.getLayoutData();
    }
    else {
      fd = new FormData();
    }

    return fd;
  }




  private FormData alignControlLeft(final Control control,
                                    final Control rightControl)
  {
    FormData fd = getFormData(control);

    fd.left = new FormAttachment(0, 5);
    // fd.width = 100;


    if (rightControl != null) {
      fd.right = new FormAttachment(rightControl, -5);
    }

    if (control != null && !control.isDisposed()) {
      control.setLayoutData(fd);
    }

    return fd;
  }


  // The right-hand side controls are offset 25% from the left of the form,
  // and 5% to the right of the form.

  private FormData alignControlRight(final Control control,
                                     final Control leftControl)
  {
    FormData fd = getFormData(control);

    fd.right = new FormAttachment(95, -5);
    // fd.width = rightControlWidth(control);
    fd.width = SWT.DEFAULT;
    //if (leftControl != null) {
      fd.left = new FormAttachment(25);
    //}

    if (control != null && !control.isDisposed()) {
      control.setLayoutData(fd);
    }
    
    return fd;
  }

  private FormData alignControlTop(final Control control,
                                   final Control topControl)
  {
    FormData fd = getFormData(control);

    if (topControl != null) {
      fd.top = new FormAttachment(topControl, 10);
    }

    if (control != null && !control.isDisposed()) {
      control.setLayoutData(fd);
    }

    return fd;
  }




  private Label makePropertyLabel(Composite sectionClient, DescriptorsForID dfid)
  {
    Label lbl = makeLabel(sectionClient, dfid.getDisplayName());
    lbl.setToolTipText(dfid.getDescription());

    return lbl;
  }





  private void makeCellEditor(CellEditor cellEditor, DescriptorsForID dfid)
  {
    if (cellEditor == null) {
      return;
    }

    IPropDescAndSource pdns = dfid.getFirst();

    IPropertySource ps = pdns.getSource();
    IPropertyDescriptor pd = pdns.getDescriptor();

    Object pdId = pd.getId();
    Object psValue = ps.getPropertyValue(pdId);

    if (psValue != null) {
      // System.out.println(psValue.getClass().toString() + ": " +
      // psValue.toString());
      try {
        cellEditor.setValue(psValue);
      }
      catch (Exception noop) {
      }

    }

    cellEditor.getControl().setVisible(true);

    PSECellEditorListener lsnr = new PSECellEditorListener(cellEditor, dfid);
    cellEditor.addListener(lsnr);

  }




  /**
   * When a property descriptor is not editable, its values may be displayed in
   * a label. This method creates the label and inserts the values into it.
   * 
   * @param sectionClient
   * @param values
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Aug 21, 2013
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  private Label makePropertyValueLabel(Composite sectionClient,
                                       DescriptorsForID dfid)
  {
    Label lbl2 = toolkit.createLabel(sectionClient, "", SWT.WRAP);
    // addSecChildLayoutData(lbl2, false, false);
    // addSecChildFormLayoutData(lbl2, lftControl, null, null);


    if (dfid.getDescriptors().isEmpty()) {
      return lbl2;
    }


    // get the first entry; can use it to get the id & label provider
    IPropDescAndSource firstPdns = dfid.getFirst();

    IPropertyDescriptor pd = firstPdns.getDescriptor();

    Object pdId = pd.getId();
    ILabelProvider lblProvider = pd.getLabelProvider();

    //
    // build a collection of object values
    //
    List<Object> lstValues = new ArrayList<Object>();

    for (IPropDescAndSource pdns : dfid.getDescriptors()) {
      IPropertySource source = pdns.getSource();

      lstValues.add(source.getPropertyValue(pdId));
    }    

    String text = lblProvider.getText(lstValues);
    text = StringUtils.abbreviate(text, 64);
	lbl2.setText(text);

    return lbl2;
  }




  /**
   * For a given section, would like to add the display entries for the property
   * descriptors for that section.
   */
  private void addDescriptorsToSection(CatEntry catEntry, Section sec)
  {
    int numEntriesForSection = 0;

    //
    // create the section client
    //
    Composite sectionClient = makeSectionClient(sec);

    //
    // we are in a category, and we have the section client created. Now
    // we wish to create the individual "rows"/entry for the category. These
    // entries may have 1 or N values associated with them, but only a single
    // label. We iterate over the entries for this category.
    //
    // dfid: all of the entries for the category

    CellEditor cellEditor;
    Label rowLabel;
    Label rowValue;

    Control lastTopControlLeft = null;
    Control lastTopControlRight = null;

    for (DescriptorsForID dfid : catEntry.getDescriptors()) {
      cellEditor = null;
      IPropertyDescriptor pd = dfid.getFirst().getDescriptor();

      switch (dfid.getDescriptors().size()) {
        case 0:
          continue;

        case 1:
          if (pd.getClass().equals(TextPropertyDescriptor.class)) {
            cellEditor = new TextCellEditor(sectionClient, SWT.BORDER);
          }
          else {
            cellEditor = pd.createPropertyEditor(sectionClient);
          }
          break;

        default:
          if (!(pd instanceof IMultiSelectPropertyDescriptor)) {
            continue;
          }

          // only attempt to make the cell editor if supports multi
          if (((IMultiSelectPropertyDescriptor) pd).supportsMultiEditing()) {
            cellEditor = pd.createPropertyEditor(sectionClient);
          }

          break;
      } // switch


      //
      // if we don't have an editor, then we make a row label and a value
      // label
      //
      if (cellEditor == null) {
        rowValue = makePropertyValueLabel(sectionClient, dfid);
        alignControlTop(rowValue, lastTopControlRight);

        rowLabel = makePropertyLabel(sectionClient, dfid);
        alignControlTop(rowLabel, lastTopControlLeft);

        alignControlRight(rowValue, null);
        FormData fd = alignControlLeft(rowLabel, rowValue);

        fd.top = new FormAttachment(rowValue, 0, SWT.CENTER);
        rowLabel.setLayoutData(fd);

        lastTopControlLeft = rowLabel;
        lastTopControlRight = rowValue;
      }
      else {
        Control leftControl;
        Control rightControl;

        // TMP: need to adjust

        // these are unfortunate tests to adjust how things are displayed
        if (cellEditor instanceof TextCellEditor) {
          rightControl = cellEditor.getControl();
          makeCellEditor(cellEditor, dfid);
          leftControl = makePropertyLabel(sectionClient, dfid);

          // rightControl.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        }
        else if (cellEditor instanceof ComboBoxCellEditor) {
          rightControl = cellEditor.getControl();
          makeCellEditor(cellEditor, dfid);
          leftControl = makePropertyLabel(sectionClient, dfid);

        }
        else {
          makeCellEditor(cellEditor, dfid);
          rightControl = cellEditor.getControl();

          leftControl = makePropertyLabel(sectionClient, dfid);
        }

        alignControlRight(rightControl, null);
        alignControlTop(rightControl, lastTopControlRight);

        FormData fd = alignControlLeft(leftControl, rightControl);
        fd.top = new FormAttachment(rightControl, 0, SWT.CENTER);
        leftControl.setLayoutData(fd);

        // alignControlTop(leftControl, rightControl);

        // TMP
        lastTopControlRight = rightControl;
        lastTopControlLeft = leftControl;
      } // else : have cell editor



      ++numEntriesForSection;
    } // for : process all of the entries for the category


    sec.setClient(sectionClient);

    if (numEntriesForSection == 0) {
      sec.setVisible(false);
      // decrement the number of visible categories
      _numVisibileCategories--;
    }

  }




  private void indicateNoCatories()
  {
    Label lbl =
        toolkit.createLabel(form,
            "No settings for selected object(s)",
            SWT.WRAP);
    addTableWrapData(lbl);
  }




  private void indicateMultipleClasses()
  {
    Label lbl =
        toolkit.createLabel(form,
            "Settings for different object types is not supported",
            SWT.WRAP);
    addTableWrapData(lbl);
  }

  /**
   * Sorts the nodes into a Set in order to allow finding the various classes
   * that are represented by the colleciton of nodes. The inbound collection
   * is cleared at entry.
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


  /*
   * (non-Javadoc)
   * 
   * @see
   * com.strikewire.snl.apc.GUIs.settings.IMultiSettingsEditor#setNodes(java
   * .util.Collection)
   */
  @Override
  public void setNodes(List<Object> nodes)
  {
	this._nodes = nodes;
	
    // re-set our categories
    clearCategories();

    if (form != null) {
      CompositeUtils.removeChildrenFromComposite(form.getBody(), false);
    }

    //
    // make sure we have selections all from one type
    //
    sortNodesToClassnames(_classnamesForSources, nodes);

    if (_classnamesForSources.size() > 1) {
      indicateMultipleClasses();
      form.setText("<inconsistent object types>");
      return;
    }

    // will make a header display
    createHeader(nodes);

    // split our selections into categories
    processNodesIntoCategories(nodes);

    for (CatEntry ce : _settingsCategories.getCategories()) {
      Section sec = makeSectionForCategoryEntry(ce);

      addDescriptorsToSection(ce, sec);
    }

    // if after drawing everything, we do not have any visible sections,
    // indicate that there is nothing to display
    if (_numVisibileCategories < 1) {
      indicateNoCatories();
    }
    
    processNodesStatus(nodes);
    
    form.reflow(true);
  }




  private void processNodesStatus(List<Object> nodes) {
	IStatus status = Status.OK_STATUS;
	for (Object node : nodes) {
		IPropertySource source = getPropertySource(node);
		
	    if(source instanceof IStatusPropertySource){
	    	IStatus propertyStatus = ((IStatusPropertySource)source).getStatus();
	    	status = Utils.mergeStatus(status, propertyStatus, CommonPlugin.ID);
	    }
	}

   	messageView.setMessageFor(status);
}


@Override
  public List<Object> getNodes()
  {
	return this._nodes!=null ? Collections.unmodifiableList(this._nodes) : Collections.emptyList();
  }




  /**
   * Disposes of the toolkit and sets the toolkit instance variable to null
   * 
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Aug 12, 2013
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  private void disposeToolkit()
  {
    if (toolkit != null) {
      toolkit.dispose();
    }

    toolkit = null;
  }




  /*
   * (non-Javadoc)
   * 
   * @see com.strikewire.snl.apc.GUIs.settings.ISettingsEditor#dispose()
   */
  @Override
  public void dispose()
  {
    disposeToolkit();
    if (image_ != null) {
      image_.dispose();
    }
  }


  //
  // private classes
  //

  private static class PSECellEditorListener implements ICellEditorListener
  {
    private final DescriptorsForID _dfid;
    private final CellEditor _editor;




    public PSECellEditorListener(CellEditor editor, DescriptorsForID dfid)
    {
      _editor = editor;
      _dfid = dfid;
    }




    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellEditorListener#applyEditorValue()
     * 
     * @see
     * org.eclipse.ui.views.properties.PropertySheetEntry#applyEditorValue()
     */
    @Override
    public void applyEditorValue()
    {
      if (_editor == null) {
        return;
      }

      // TODO: may wish to see if the value actually changed
      Object value = _editor.getValue();

      updateValuesOnSourceObjects(value);
    }




    private void updateValuesOnSourceObjects(Object value)
    {
      List<IPropDescAndSource> lstPdns = _dfid.getDescriptors();

      for (IPropDescAndSource pdns : lstPdns) {
        IPropertySource source = pdns.getSource();
        IPropertyDescriptor propDesc = pdns.getDescriptor();

        source.setPropertyValue(propDesc.getId(), value);
      }


      _editor.getControl().addListener(SWT.Hide, new Listener() {

        @Override
        public void handleEvent(Event event)
        {
          _editor.getControl().setVisible(true);
        }
      });
    } // updateValuesOnSourceObjects




    @Override
    public void cancelEditor()
    {
//      System.out.println(this.getClass() + ": cancelEditor()");
    }




    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ICellEditorListener#editorValueChanged(boolean,
     * boolean)
     */
    @Override
    public void editorValueChanged(boolean oldValidState, boolean newValidState)
    {
//      System.out.println(this.getClass() + ": editorValueChanged: "
//          + oldValidState + " > " + newValidState);
    }
  }


  /**
   * <p>An interface for linking IPropertyDescriptors and IPropertySource.
   * The IPropertySource has the
   * value and the list of the PropertyDescriptors, but in order to support the
   * display, it is necessary to sort by the property descriptors. The equals
   * method looks at the ID of the property descriptor</p> 
   * 
   * @author kholson
   * 
   */
  private static interface IPropDescAndSource
  {
    /**
     * Returns the source for the descriptor
     */
    public IPropertySource getSource();



    /**
     * Returns the property descriptor
     */
    public IPropertyDescriptor getDescriptor();



    /**
     * Returns the id of the descriptor
     */
    public Object getId();



    /**
     * Returns the display name for the descriptor
     */
    public String getDisplayName();



    /**
     * Returns the value obtained for the source based upon the
     * id of the descriptor
     */
    public Object getValue();
  }



  /**
   * Links an IPropertyDescriptor to its source. The IPropertySource has the
   * value and the list of the PropertyDescriptors, but in order to support the
   * display, it is necessary to sort by the property descriptors. The equals
   * method looks at the ID of the property descriptor
   * 
   * @author kholson
   * 
   */
  private static class PropDescAndSource implements IPropDescAndSource
  {
    private final IPropertySource _source;
    private final IPropertyDescriptor _propDesc;
    private final Object _id;




    public PropDescAndSource(IPropertySource source,
                             IPropertyDescriptor propDesc)
    {
      _source = source;
      _propDesc = propDesc;

      _id = propDesc.getId();
    }




    public IPropertySource getSource()
    {
      return _source;
    }




    public IPropertyDescriptor getDescriptor()
    {
      return _propDesc;
    }




    public Object getId()
    {
      return _id;
    }




    public String getDisplayName()
    {
      return _propDesc.getDisplayName();
    }




    public Object getValue()
    {
      return _source.getPropertyValue(_id);
    }




    public String toString()
    {
      return _id.toString() + " " + getDisplayName();
    }




    public int hashCode()
    {
      return _id.hashCode();
    }




    public boolean equals(Object obj)
    {
      if (!(obj instanceof PropDescAndSource)) {
        return false;
      }

      if (obj == this) {
        return true;
      }

      boolean isEqual = _id.equals(((PropDescAndSource) obj)._id);

      return (isEqual);

    }
  }

  
  /**
   * For a given id, allows mananging all of the descriptors.
   * @author kholson
   *
   */
  private static class DescriptorsForID
  {
    private final List<IPropDescAndSource> _descriptors =
        new ArrayList<IPropDescAndSource>();


    private final Object _id;




    public DescriptorsForID(Object id)
    {
      _id = id;
    }




    public IPropDescAndSource getFirst() throws IllegalArgumentException
    {
      if (_descriptors.isEmpty()) {
        throw new IllegalArgumentException("No Property Descriptors");
      }

      return _descriptors.iterator().next();
    }




    public String getDisplayName()
    {
      if (_descriptors.isEmpty()) {
        return "<EMPTY>";
      }

      IPropDescAndSource pdns = getFirst();

      return pdns.getDisplayName();
    }




    public String getDescription()
    {
      if (_descriptors.isEmpty()) {
        return "<EMPTY>";
      }

      IPropDescAndSource pdns = getFirst();

      return pdns.getDescriptor().getDescription();
    }




    public void add(IPropDescAndSource pdns)
    {
      _descriptors.add(pdns);
    }




    public List<IPropDescAndSource> getDescriptors()
    {
      return Collections.unmodifiableList(_descriptors);
    }




    public void clear()
    {
      _descriptors.clear();
    }




    public int hashCode()
    {
      return _id.hashCode();
    }




    public boolean equals(Object obj)
    {
      if (!(obj instanceof DescriptorsForID)) {
        return false;
      }

      if (obj == this) {
        return true;
      }

      boolean isEqual = _id.equals(((DescriptorsForID) obj)._id);

      return (isEqual);

    }




    public String toString()
    {
      return _id.toString() + "(" + _descriptors.size() + ")";
    }
  }


  /**
   * An entry for a category, supporting the category name, with a hashcode and
   * equals based upon the category name.
   * 
   * @author kholson
   * 
   */
  private static class CatEntry
  {
    private final String _category;
    private final List<DescriptorsForID> _entries =
        new ArrayList<DescriptorsForID>();




    public CatEntry(String category)
    {
      if (category != null) {
        _category = category;
      } 
      else { 
        _category = ""; 
      }
    }




    public void clear()
    {
      for (DescriptorsForID did : _entries) {
        did.clear();
      }

      _entries.clear();
    }




    public String getCategory()
    {
      return _category;
    }




    public List<DescriptorsForID> getDescriptors()
    {
      return Collections.unmodifiableList(_entries);
    }




    // public DescriptorsForID getDescriptor(Object id)
    // {
    // return getOrMakeDescriptor(id);
    // }


    /**
     * Adds the specified descriptor and source for this category, creating the
     * DescriptorsForID objects in the internal collection
     */
    public void addPropertyDescriptorAndSource(IPropertyDescriptor desc,
                                               IPropertySource source)
    {
      //
      // make sure adding to the correct category
      String chkCat = desc.getCategory();
      if (chkCat == null) {
        chkCat = "";
      }
      if (!chkCat.equals(_category)) {
        String msg =
            "Attempted to add PropertyDescriptor w/a category of " + chkCat
                + " to the category entry of " + _category;
        throw new IllegalArgumentException(msg);
      }

      //
      // each category has N entries, store by IDs under it
      // each ID may have N property descriptors, since one may make
      // multiple selections
      //

      // -- create our lowest level binding, which holds the source
      // and property descriptor together, with an equality
      // check by property descriptor id
      IPropDescAndSource pdns = new PropDescAndSource(source, desc);


      // now get an entry for the id
      DescriptorsForID dfid = getOrMakeDescriptor(pdns.getId());

      dfid.add(pdns);
    }




    private DescriptorsForID getOrMakeDescriptor(final Object id)
    {
      DescriptorsForID dfid = new DescriptorsForID(id);

      boolean bFound = false;
      Iterator<DescriptorsForID> it = _entries.iterator();

      while (it.hasNext()) {
        DescriptorsForID chk = it.next();

        if (chk.equals(dfid)) {
          dfid = chk;
          bFound = true;
          break;
        }
      }

      if (!bFound) {
        _entries.add(dfid);
      }


      return dfid;
    }




    public int hashCode()
    {
      return _category.hashCode();
    }




    public boolean equals(Object obj)
    {
      if (!(obj instanceof CatEntry)) {
        return false;
      }

      if (this == obj) {
        return true;
      }

      return (((CatEntry) obj)._category.equals(_category));

    }




    public String toString()
    {
      return _category + "(" + _entries.size() + ") " + _entries.toString();
    }
  }


  /**
   * A class which contains the various categories
   * 
   * @author kholson
   * 
   */
  private static class Categories
  {
    private final List<CatEntry> _categories = new ArrayList<CatEntry>();




    public void clear()
    {
      for (CatEntry ce : _categories) {
        ce.clear();
      }

      _categories.clear();
    }




    public List<CatEntry> getCategories()
    {
      return Collections.unmodifiableList(_categories);
    }




    /**
     * For the specified category, returns an existing or new CatEntry
     */
    public CatEntry getCatEntry(final String category)
    {
      return getOrMakeCategoryEntry(category);
    }




    /**
     * Looks through the existing categories to find an existing category by the
     * category name. If it is not found, creates a new category entry and
     * returns it, otherwise returns the existing one.
     */
    private CatEntry getOrMakeCategoryEntry(final String category)
    {
      // create a new category entry to use for searching; if not
      // found this will be the one that is returned
      CatEntry catEntry = new CatEntry(category);

      //
      // we will iterate ourselves; using the _tsCategories.contains() would
      // walk the list and only return a boolean, so we would need to search
      // through anyway
      //
      boolean bFound = false;
      Iterator<CatEntry> it = _categories.iterator();

      while (it.hasNext()) {
        CatEntry chk = it.next();

        if (chk.equals(catEntry)) {
          catEntry = chk;
          bFound = true;
          break;
        }
      }

      if (!bFound) {
        _categories.add(catEntry);
      }

      return catEntry;
    }




    public String toString()
    {
      return "Categories: " + _categories.size() + ": "
          + _categories.toString();
    }
  }


  /* (non-Javadoc)
   * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(PropertyChangeEvent event)
  {
    // there are empty events coming in here: why? Should we process
    // when there is no indication of a new value?

    setNodes(getNodes());
  }

	@Override
	public boolean isReusable() 
	{
		return false;
	}
}
