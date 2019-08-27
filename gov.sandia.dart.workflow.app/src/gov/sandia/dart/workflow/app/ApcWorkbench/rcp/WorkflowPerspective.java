package gov.sandia.dart.workflow.app.ApcWorkbench.rcp;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.osgi.service.event.EventHandler;

import com.strikewire.snl.apc.GUIs.AbstractPerspectiveWithDefaultViewstack;

import gov.sandia.dart.workflow.app.ApcWorkbench.WorkflowApplicationPlugin;

public class WorkflowPerspective extends AbstractPerspectiveWithDefaultViewstack implements IPerspectiveFactory
{
  /**
   * _log -- A Logger instance for TeamPerspective
   */
  private static final Logger _log =
      LogManager.getLogger(WorkflowPerspective.class);

  public static final String ID =
      "gov.sandia.dart.workflow.app.perspective.workflow";


  final static String refid_Left = "left";
  final static String refid_LeftBottom = "leftBottom";
  final static String refid_Right = "right";
  final static String refid_rightBottom = "rightBottom";
  final static String refid_Bottom = "bottom";

  final static EventHandler _eventHandler = new PerspectiveEventHandler(_log, WorkflowPerspective.class.getSimpleName(), refid_Bottom);


  final String topic = UIEvents.UILifeCycle.TOPIC + UIEvents.TOPIC_SEP + "*";




  @Override
  public void createInitialLayout(IPageLayout layout)
  {

    // set up the new menu (file -> new)
    WorkflowApplicationPlugin.addAllNewWizardsToPerspective(layout);

    layout.addActionSet("org.eclipse.debug.ui.launchActionSet");

    // VIEW IDs
    // Machine view
    String sMachViewID = "com.strikewire.snl.apc.FileManager.views.MachineView";
    // DART Login View (2010.09)
    String sDARTLoginViewID = "com.strikewire.snl.apc.dartlogin.LoginView";
    String sProjNavID =
        "org.eclipse.ui.navigator.ProjectExplorer";
    String jobStatusViewID = "gov.sandia.apc.JobSubmission.JobStatusView";
    String fileViewID = "com.strikewire.snl.apc.FileManager.views.FileView";
    String settingsViewID = "com.strikewire.snl.apc.common.view.settings";
    String paletteViewID = "org.eclipse.gef.ui.palette_view";
    String consoleViewID = "org.eclipse.ui.console.ConsoleView";


    final String refid_EditorArea = layout.getEditorArea();
    layout.setEditorAreaVisible(true);



    // perspective shortcuts

    // show view shortcuts
    layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
    layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
    layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
    layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    layout.addShowViewShortcut(sProjNavID);
    layout.addShowViewShortcut(sDARTLoginViewID);
    layout.addShowViewShortcut(sMachViewID);
    layout.addShowViewShortcut(jobStatusViewID);


    // layout folders ----------------



    // left
    IFolderLayout left =
        layout.createFolder(refid_Left,
            IPageLayout.LEFT,
            0.24f,
            refid_EditorArea);
    left.addView(sProjNavID);

    // left-bottom
    IFolderLayout leftBottom =
        layout.createFolder(refid_LeftBottom,
            IPageLayout.BOTTOM,
            .5f,
            refid_Left);
    leftBottom.addView(IPageLayout.ID_PROP_SHEET);

    // right
    IFolderLayout right =
        layout.createFolder(refid_Right,
            IPageLayout.RIGHT,
            0.7f,
            refid_EditorArea);
    right.addPlaceholder(paletteViewID);
    right.addView(IPageLayout.ID_OUTLINE);
    right.addView(jobStatusViewID);
    right.addPlaceholder(fileViewID + ":*");


    // right-bottom
    IFolderLayout rightBottom =
        layout.createFolder(refid_rightBottom,
            IPageLayout.BOTTOM,
            0.5f,
            refid_Right);
    rightBottom.addView(settingsViewID);

    // bottom
    IFolderLayout bottom =
        layout.createFolder(refid_Bottom,
            IPageLayout.BOTTOM,
            0.7f,
            refid_EditorArea);
    bottom.addView(sMachViewID);
    bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
    bottom.addView(IPageLayout.ID_PROGRESS_VIEW);
    bottom.addView(consoleViewID);

    // don't let the project nav be closed
    layout.getViewLayout(sProjNavID).setCloseable(false);

    // setDefaultViewFolder("bottom");

    registerForPerspectiveInitialization(_eventHandler, _log);

  }


} // class
