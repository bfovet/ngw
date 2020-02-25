/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DARTSystemPreferenceCategoryPage extends PreferencePage implements
    IWorkbenchPreferencePage
{

  public DARTSystemPreferenceCategoryPage()
  {
    noDefaultAndApplyButton();
    setDescription();
  }




private void setDescription() {
	String txt =
        "The sub-categories allow for specifying parameters that "
            + "control operations relating to the DART System.\n\n"
            + "Please expand this category to access the sub-categories.\n\n"
            + "NOTE: Depending on which DART application you are running, this\n"
            + "category may be empty.\n";
    
    setDescription(txt);
}




  public DARTSystemPreferenceCategoryPage(String title)
  {
    super(title);
    noDefaultAndApplyButton();
    setDescription();

  }




  public DARTSystemPreferenceCategoryPage(String title, ImageDescriptor image)
  {
    super(title, image);
    noDefaultAndApplyButton();
    setDescription();
  }




  public void init(IWorkbench workbench)
  {
    noDefaultAndApplyButton();
  }




  @Override
  protected void contributeButtons(Composite parent)
  {
//    super.contributeButtons(parent);
  }




  @Override
  protected GridData setButtonLayoutData(Button button)
  {
    return super.setButtonLayoutData(button);
  }




  @Override
  protected Control createContents(Composite parent)
  {
    Composite area = new Composite(parent, SWT.None);
    return area;
  }

}
