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
package gov.sandia.dart.common.preferences.fields;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author mjgibso
 *
 */
public class LabelFieldEditor extends FieldEditor
{
  private Label label_ = null;

  private String labelText_ = null;

  private Color labelColor_ = null;

  private Font labelFont_ = null;


  
  public LabelFieldEditor(String label, Composite parent)
  {
    this.labelText_ = label;

    createControl(parent);
  }
  


  public LabelFieldEditor(String label, Color foreground, Composite parent)
  {
    this.labelText_ = label;
    this.labelColor_ = foreground;

    createControl(parent);
  }


  public LabelFieldEditor(String label, Color foreground, Font font, Composite parent)
  {
    this.labelText_ = label;
    this.labelColor_ = foreground;
    this.labelFont_ = font;
    
    createControl(parent);
  }


  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
   */
  @Override
  protected void adjustForNumColumns(int numColumns)
  {
    if (this.label_ == null || this.label_.isDisposed()) {
      return;
    }

    ((GridData) this.label_.getLayoutData()).horizontalSpan = numColumns;
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt
   * .widgets.Composite, int)
   */
  @Override
  protected void doFillIntoGrid(Composite parent, int numColumns)
  {
    label_ = new Label(parent, SWT.WRAP);

    if (this.labelText_ != null) {
      label_.setText(this.labelText_);
    }

    if (this.labelColor_ != null) {
      label_.setForeground(this.labelColor_);
    }

    if (labelFont_ != null && !labelFont_.isDisposed()) {
      label_.setFont(labelFont_);
    }


    GridData gd =
        new GridData(SWT.FILL, SWT.BEGINNING, true, false, numColumns, 1);
    // gd.grabExcessHorizontalSpace = true;
    // gd.horizontalSpan = numColumns;
    gd.widthHint = 0;

    label_.setLayoutData(gd);
  }




  public void setText(String text)
  {
    this.labelText_ = text;

    if (this.label_ != null && !this.label_.isDisposed() && text != null) {
      this.label_.setText(text);
    }
  }




  public void setForeground(Color color)
  {
    this.labelColor_ = color;

    if (this.label_ != null && !this.label_.isDisposed() && color != null) {
      label_.setForeground(color);
    }
  }




  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.preference.FieldEditor#doLoad()
   */
  @Override
  protected void doLoad()
  {
  }




  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
   */
  @Override
  protected void doLoadDefault()
  {
  }




  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.preference.FieldEditor#doStore()
   */
  @Override
  protected void doStore()
  {
  }




  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.preference.FieldEditor#store()
   */
  @Override
  public void store()
  {
    // nothing to do here, nothing to store.

    // we need to override this so in the event the user pressed restore
    // defaults
    // the super implementation won't try to set the default value for this
    // field
    // which will return a null preference name which will cause an error when
    // the
    // user tries to apply the changes, potentially preventing others from being
    // persisted.
  }




  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
   */
  @Override
  public int getNumberOfControls()
  {
    return 1;
  }

}
