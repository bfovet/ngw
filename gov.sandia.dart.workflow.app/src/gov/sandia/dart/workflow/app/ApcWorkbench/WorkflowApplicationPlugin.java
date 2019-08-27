package gov.sandia.dart.workflow.app.ApcWorkbench;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.ui.IPageLayout;
import org.osgi.framework.BundleContext;

import com.strikewire.snl.apc.reporting.AbsReportingUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class WorkflowApplicationPlugin extends AbsReportingUIPlugin
{
  // The shared instance.
  private static WorkflowApplicationPlugin plugin;
  // Resource bundle.
  private ResourceBundle resourceBundle;

  public static String ID = "gov.sandia.dart.workflow.app";




  /**
   * The constructor.
   */
  public WorkflowApplicationPlugin()
  {
    super(ID);
    plugin = this;
  }




  /**
   * This method is called upon plug-in activation
   */
  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
  }




  /**
   * This method is called when the plug-in is stopped
   */
  @Override
  public void stop(BundleContext context) throws Exception
  {
    super.stop(context);
    plugin = null;
    resourceBundle = null;
  }




  /**
   * Returns the shared instance.
   */
  public static WorkflowApplicationPlugin getDefault()
  {
    return plugin;
  }




  /**
   * Returns the string from the plugin's resource bundle, or 'key' if not
   * found.
   */
  public static String getResourceString(String key)
  {
    ResourceBundle bundle = WorkflowApplicationPlugin.getDefault().getResourceBundle();
    try {
      return (bundle != null) ? bundle.getString(key) : key;
    }
    catch (MissingResourceException e) {
      return key;
    }
  }




  /**
   * Returns the plugin's resource bundle,
   */
  public ResourceBundle getResourceBundle()
  {
    try {
      if (resourceBundle == null) resourceBundle =
          ResourceBundle.getBundle("gov.sandia.dart.workflow.app.ApcWorkbenchPluginResources");
    }
    catch (MissingResourceException x) {
      resourceBundle = null;
    }
    return resourceBundle;
  }




  public static void addAllNewWizardsToPerspective(IPageLayout layout)
  {
    layout.addNewWizardShortcut("gov.sandia.dart.materials.ui.wizards.NewMDF");// Materials
                                                                               // Data
                                                                               // File
    layout.addNewWizardShortcut("gov.sandia.apc.wizards.NewInputDeckWizard"); // Sierra
                                                                              // Study
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
    layout.addNewWizardShortcut("gov.sandia.dart.workflow.editor.newWorkflowWizard");   
    layout.addNewWizardShortcut("gov.sandia.dart.workflow.editor.newDakotaStudyWizard");        
  }
}
