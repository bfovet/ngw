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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * When using a three column layout across multiple sections, this class assists
 * with the layout
 * 
 * @author kholson
 * 
 */
public class ThreeColumnLayoutHelper
{
  private final TabbedPropertySheetWidgetFactory _widgetFactory;

  private static int LABEL_SIZE = 105;


  /**
   * @param wf
   *          A Widget factory
   */
  public ThreeColumnLayoutHelper(TabbedPropertySheetWidgetFactory wf)
  {
    _widgetFactory = wf;
  }


  public void setLabelSize(int size)
  {
    LABEL_SIZE = size;
  }
  
  
  public int getLabelSize()
  {
    return LABEL_SIZE;
  }

  /**
   * @return The widget factory
   */
  public TabbedPropertySheetWidgetFactory getWidgetFactory()
  {
    return _widgetFactory;
  }


  /**
   * Adds a form layout to the specified control, with:
   * <ul>
   * <li>marginHeight : 5</li>
   * </ul>
   */
  public FormLayout addFormLayout(final Composite control)
  {
    FormLayout layout = new FormLayout();
    layout.marginHeight = 5;
    
    if (control != null && ! control.isDisposed()) {
      control.setLayout(layout);
    }
    
    return layout;
  }


  /**
   * Creates a standard composite via the widget factory
   */
  public Composite makeComposite(final Composite parent)
  {
    Composite comp = _widgetFactory.createComposite(parent);
    return comp;
  }




  /**
   * Creates a new flat form composite on the specified parent from the widget
   * factory
   */
  public Composite makeFlatComposite(final Composite parent)
  {
    Composite comp = _widgetFactory.createFlatFormComposite(parent);

    return comp;
  }




  /**
   * Instantiates a new CLabel on the specified parent, with the specified text,
   * using the widget factory, with the WRAP attribute set
   */
  public Label makeLabel(final Composite parent, final String txt)
  {
    Label lbl = _widgetFactory.createLabel(parent, txt, SWT.WRAP);
    
    lbl.setBackground(parent.getBackground());

    return lbl;
  }

  
  /**
   * Creates a Text via the widget factory on the specified parent,
   * and sets the editable to false.
   */
  public Text makeTextInputField(final Composite parent,
                                 final String initValue)
  {
    String disp = (initValue != null ? initValue : "");
    
    Text txt = _widgetFactory.createText(parent, disp, SWT.WRAP);
    
    txt.setEditable(false);
    
    return txt;
  }

  
  private FormData getFormData(final Control control)
  {
    FormData fd;
    if (control != null && control.getLayoutData() instanceof FormData) {
      fd = (FormData)control.getLayoutData();
    }
    else {
      fd = new FormData();
    }
    
    return fd;
  }
  
  /**
   * Aligns the button to the right by creating a
   * formattachment to 100 % of the form, minus an offset
   * @param btn The control to right align, usually the button; if
   * the control already has form data, then that data is used
   * rather than creating new data
   */
  public FormData alignButtonRight(final Control btn)
  {
    FormData fd = getFormData(btn);
    
    
    fd.right = new FormAttachment(100, -5);
    
    if (btn != null && ! btn.isDisposed()) {
      btn.setLayoutData(fd);
    }
    
    return fd;
  }
  
  
  /**
   * Aligns the text box to the center, with a left anchor
   * of the specified leftLbl control (usually the label), and
   * a right anchor of the rightBtn control (usually the button)
   * @param txtBox The control to center, usually the text box; if
   * it already has a FormData, that data is used otherwise a
   * new FormData is created
   * @param left The control to the left, usually the label, may
   * be null in which case no left form attachment is created
   * @param right The control to the right, usually the button; may
   * be null in which case no right form attachment is made
   */
  public FormData alignTextCenter(final Control txtBox,
                                  final Control leftLbl,
                                  final Control rightBtn)
  {
    FormData fd = getFormData(txtBox);
    
    if (leftLbl != null) {
      fd.left = new FormAttachment(leftLbl, 5);
    }
    
    if (rightBtn != null) {
      fd.right = new FormAttachment(rightBtn, -5);
    }
    
    if (txtBox != null) {
      txtBox.setLayoutData(fd);
    }
    
    return fd;
  }
  
  
  public FormData alignLabelLeft(final Control lbl)
  {
    FormData fd = new FormData();
    
    fd.left = new FormAttachment(0, 5);
    fd.width = getLabelSize();
    
    if (lbl != null) {
      lbl.setLayoutData(fd);
    }
    
    return fd;
  }
  
  
  /**
   * Creates a label, a text input field, and an edit button,
   * sets the layout for the column, adds the edit button to the
   * specified secButtons, and returns the text input field.
   */
  public Text makeThreeColumnTextInput(final Composite parent,
                                       final String labelText,
                                       final SectionButtons secButtons)
  {
    //
    // create the widgets
    //
    Label lbl = makeLabel(parent, labelText);
    Text txt = makeTextInputField(parent, "");
    Button btn = makeEditButton(parent);
    
    secButtons.setEditButton(btn);
    
    //
    // set the layout
    //
    alignButtonRight(btn);
    FormData fd = alignTextCenter(txt, lbl, btn);
    fd.width = 50;
    alignLabelLeft(lbl);
    
    return txt;
  }
  
  
  
  /**
   * Creates a button on the specified parent with an image from the
   * CommonPlugin at the specified path, and returns it.
   */
  public Button makeImageButton(final Composite parent,
                                     final String iconPath)
  {
    Button button = getWidgetFactory().createButton(parent, "",
        SWT.PUSH | SWT.WRAP);

    ImageDescriptor imgDesc = CommonPlugin.getImageDescriptor(iconPath);
    Image img = imgDesc.createImage();

    button.setImage(img);
    // button.setEnabled(false);

    return button;

  }




  public Button makeEditButton(final Composite parent)
  {
    return makeImageButton(parent, "icons/pencil_go.png");
  }




  /**
   * Creates & returns a button with a check mark icon tied to the specified
   * parent; does not apply any layout data. Button is not enabled upon return.
   */
  public Button makeAcceptButton(final Composite parent)
  {
    return makeImageButton(parent, "icons/accept.png");
  }




  /**
   * Creates & returns a button with a cancel indicator icon tied to the
   * specified parent; does not apply any layout data. Button is disabled upon
   * return.
   */
  public Button makeCancelButton(final Composite parent)
  {
    return makeImageButton(parent, "icons/cancel.png");
  }  

} // class ThreeColumnLayoutHelper
