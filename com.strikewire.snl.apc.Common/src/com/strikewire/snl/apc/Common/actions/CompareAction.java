/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.Common.actions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.Common.compare.CompareInput;
import com.strikewire.snl.apc.Common.compare.CompareItem;
import com.strikewire.snl.apc.GUIs.ListOrFileDialog;
import com.strikewire.snl.apc.util.ResourceUtils;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;
import gov.sandia.dart.common.preferences.compare.EComparePrefs;

public class CompareAction extends AbstractHandler
{
	private Map<String, IFile> availableFiles = new TreeMap<>();
	public CompareAction(String text) {
		super();
	}
	
	public CompareAction() {
		this("compareAction");
	}

	@Override
  public Object execute(ExecutionEvent event) throws ExecutionException{
		availableFiles.clear();
		IFile file1=null;
		IFile file2=null;
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.getActiveEditor();
		IEditorInput input = editor.getEditorInput();
		file1 = (input.getAdapter(IFile.class));
		if( file1 == null)
		{
			try {
				ILocationProvider ilp = input.getAdapter(ILocationProvider.class);
				IPath path = ilp.getPath(input);
				file1 = ResourceUtils.getFileForLocation(path.toOSString());
			} catch (CoreException e) {
				// Fall through and die
			}
			if (file1 == null) {
				MessageDialog.openError(page.getWorkbenchWindow().getShell(), "Error", "Can't get resource for open editor.");
				return null;
			}
		}
		Object[] otherEditors = getNonActiveEditors(page, (EditorPart) editor);
		if(otherEditors == null)//Compare action was canceled.
		{
			return null;
		}
		file2 = selectFileOrEditor(otherEditors);
		if(file2 == null)//Compare action was canceled.
		{
			return null;
		}		


		if (file1 == null || file2 == null ||
		   !file1.exists() || !file2.exists()){
			MessageDialog.openError(page.getWorkbenchWindow().getShell(), "Files not Found", "one or more files does not exist");
			return null;
		}
		
		//Check that files are of the same type (already ensured based upon available selection for "compare with another editor"
		//but if a file was selected from the file system, this check hasn't been done
		
		//get the content type
//		IContentType file1Type = null;
//		IContentType file2Type = null;
//		if( file1 != null  && file2 != null)
//		{
//			file1Type = IDE.getContentType(file1);
//			file2Type = IDE.getContentType(file2);
//		}
		
		if( file1==null || file2 == null )
		{
			MessageDialog.openError(page.getWorkbenchWindow().getShell(), "Invalid File Selection", "Target file is invalid.");
			return null;
		}
		
//		if(file1Type == null || file2Type == null)
//		{
//			MessageDialog.openError(page.getWorkbenchWindow().getShell(), "Invalid File Selection", "No content type associated with selected file.");
//			return null;
//		}
//
//		if( !(file1Type.isKindOf(file2Type)||file2Type.isKindOf(file1Type)))	
//		{
//			MessageDialog.openError(page.getWorkbenchWindow().getShell(), "Invalid File Selection", "File types differ.");
//			return null;
//		}
		
		
		openInCompare(file1,file2);
		return null;
	}


	private void openInCompare(IFile left, IFile right) {
		CompareEditorInput input = createCompareEditorInput(left, right);
		IWorkbenchPage workBenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		IPreferenceStore prefs = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		boolean openInNewEditor = 
		    prefs.getBoolean(EComparePrefs.OPEN_COMPARE_IN_NEW_TAB.getPrefKey());
		IEditorPart editor = null;
		if(!openInNewEditor)
		{
			editor = findReusableCompareEditor(workBenchPage);
		}
		if (editor != null) { //either user desires a new tab, or a reusable editor was not found
			IEditorInput otherInput = editor.getEditorInput();
			if (otherInput.equals(input)) {
				// simply provide focus to editor
				if (OpenStrategy.activateOnOpen())
					workBenchPage.activate(editor);
				else
					workBenchPage.bringToTop(editor);
			} else {
				// if editor is currently not open on that input either re-use
				// existing
				CompareUI.reuseCompareEditor(input, (IReusableEditor) editor);
				if (OpenStrategy.activateOnOpen())
					workBenchPage.activate(editor);
				else
					workBenchPage.bringToTop(editor);
			}
		} else {
			CompareUI.openCompareEditor(input);
		}
	}

	protected CompareEditorInput createCompareEditorInput(
			IFile left, IFile right) {
			CompareInput compareInput = new CompareInput();
			CompareItem leftItem = new CompareItem(left.getName(),getFileContents(left),left.getLocationURI(),0);
			CompareItem rightItem = new CompareItem(right.getName(),getFileContents(right),right.getLocationURI(),0);
			compareInput.setLeftItem(leftItem);
			compareInput.setRightItem(rightItem);
			compareInput.getCompareConfiguration().setLeftLabel( leftItem.getName() );
			compareInput.getCompareConfiguration().setRightLabel( rightItem.getName() );

		return compareInput;
	}

	/**
	 * Returns an editor that can be re-used. An open compare editor that
	 * has un-saved changes cannot be re-used.
	 * @param page
	 * @return an EditorPart or <code>null</code> if none can be found
	 */
	public static IEditorPart findReusableCompareEditor(IWorkbenchPage page) {
		IEditorReference[] editorRefs = page.getEditorReferences();
		for (int i = 0; i < editorRefs.length; i++) {
			IEditorPart part = editorRefs[i].getEditor(false);
			if(part != null
					&& (part.getEditorInput() instanceof CompareEditorInput)
					&& part instanceof IReusableEditor) {
				if(! part.isDirty()) {
					return part;
				}
			}
		}
		return null;
	}


	public static IFile selectFile()
	{		
		Shell shell = new Shell();
		String name = new FileDialog(shell, SWT.OPEN).open();
		
		
		if (name == null)
		   return null;
		
		try {
			IFile file = ResourceUtils.getFileForLocation(name);
			return file;
		} catch(Exception e) {
			CommonPlugin.getDefault().logError("Error getting IFile for path: " + name, e);
			return null;
		}
	}  //  selectFile()
	
	/**
	 * Places all the text content of a file into a String.
	 */
	public static String getFileContents(IFile file)
	{
		try {

			InputStream in = file.getContents();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			synchronized (in) 
			{
	            synchronized (out) 
	            {
	                byte[] buffer = new byte[2048];
	                
	                while (true) 
	                {
	                    int bytesRead = in.read(buffer);
	                    if ( bytesRead == -1 ) break;
	                    out.write(buffer, 0, bytesRead);
	                }
	                
	                out.close();
	            }
			}
	            
			return out.toString();
		} catch ( CoreException e ) {
			return null;
		} catch ( IOException e ) {
			return null;
		}
		
	}  //  getFileContents()
	
	/**
	 * Called in execute() to fetch the names of all the open editors minus
	 * the one identified in the activeEditor parameter.
	 */
	private Object[] getNonActiveEditors(IWorkbenchPage page, EditorPart activeEditor)
	{		
		IEditorReference[] references = page.getEditorReferences();
		Shell shell = page.getWorkbenchWindow().getShell();
		try {
		
		    new ProgressMonitorDialog(shell).run(true, true,
		        new ContentTypeFinder(references, activeEditor));
		  } catch (InvocationTargetException e) {
			  Throwable t = e.getTargetException();
			  if(t instanceof CoreException)
			  {
				  CommonPlugin.getDefault().log(((CoreException) t).getStatus());
			  } else {
				  CommonPlugin.getDefault().logError("Error gathering list of non-active editors: "+t.getMessage(), t);
			  }
		    return null;
		  } catch (InterruptedException e) {
		    return null;
		  }
		
		return availableFiles.keySet().toArray();
	}  //  getOpenEditors()
	
	
	/**
	   * This class represents a long running operation
	   */
	  class ContentTypeFinder implements IRunnableWithProgress {

	    private IEditorReference[] references;

		private IEditorPart activeEditor;

	    /**
	     * LongRunningOperation constructor
	     * 
	     * @param references whether the animation is unknown
	     * @param activeEditor 
	     */
	    public ContentTypeFinder(IEditorReference[] references, IEditorPart activeEditor) {
	      this.references = references;
	      this.activeEditor = activeEditor;
	    }
	    
	    /**
	     * Runs the long running operation
	     * 
	     * @param monitor the progress monitor
	     */
	    @Override
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
	      monitor.beginTask("Determining Content Types of Open Editors", references.length);
	      
	      
	      IFile activeFile = (activeEditor.getEditorInput().getAdapter(IFile.class));
	      
	      for ( IEditorReference ref : references)
			{
	    	  try {
		    	  if(monitor.isCanceled()) break; // if Cancel was pressed, end type look-up loop
//		    	  Thread.sleep(increment);
		    	  monitor.subTask("Processing " + ref.getName());
					//If editor contains an appropriate file, add it to the list of editors.
					//First check that the two editors are of the same contentType
					
					//Get the IFile for other editor
					IFile otherFile;
					try {
						otherFile = ref.getEditorInput().getAdapter(IFile.class);
					} catch (PartInitException e) {
						MessageDialog.openError(ref.getPage().getWorkbenchWindow().getShell(), "Bad Editor Open", "An open tab has an unobtainable IFile object");
						continue;
					}
					
					if(otherFile == null)
					{
						continue;
					}
					
					//get the content type
					IContentType activeType = null;
					IContentType otherType = null;
					if( activeFile != null)
					{
						activeType = IDE.getContentType(activeFile);
						otherType = IDE.getContentType(otherFile);
					}
					
					if(otherType != null && activeType!=null)
					{
						//get paths, and check that they aren't the same
						String activePath = getFilePath((EditorPart) activeEditor);
						String otherPath = otherFile.getLocation().toOSString();
						
						if ((activeType.isKindOf(otherType)||otherType.isKindOf(activeType)) && !activePath.equals(otherPath)) //check that the contenttypes match, and that the two editors are not the same
						{
									availableFiles.put(otherPath, otherFile);
						}
					}
					else if(activeType == null)
					{
						//get paths, and check that they aren't the same
						String activePath = getFilePath((EditorPart) activeEditor);
						String otherPath = otherFile.getLocation().toOSString();
						
						if (!activePath.equals(otherPath)) //check that the two editors are not the same
						{
									availableFiles.put(otherPath, otherFile);
						}
					}
						
	    	  } finally {
					monitor.worked(1);
			      if (monitor.isCanceled())
				          throw new InterruptedException("getNonActiveEditors was cancelled");
	    	  }
	    	  
			}//for
	      
	      
	      
	      
	      monitor.done();
	    }
	  }
	
	
	  
	  
	/**
	 * Called in execute() to display a dialog to the user for an editor
	 * selection.
	 */
	private IFile selectFileOrEditor(Object[] openEditors)
	{
		Shell shell = new Shell();
		ListOrFileDialog dialog = new ListOrFileDialog(shell);
		dialog.setAddCancelButton(true);
		dialog.setContentProvider( new ArrayContentProvider() );
		dialog.setLabelProvider( new LabelProvider() );
		dialog.setInput(openEditors);
		dialog.setInitialSelections(openEditors);
		dialog.setTitle("Select a file to compare");
		dialog.setMessage("Select one of the open editors listed to compare with, or press the \"Choose external file\" button to compare with another file.");
		dialog.setHeightInChars(Math.min(openEditors.length+10,50));
		dialog.setWidthInChars(Math.min(findMaxPathLength(openEditors), 150));
		

		PlatformUI.getWorkbench().getHelpSystem().setHelp(shell, "com.strikewire.snl.apc.Common.mapping");//enable Help text

		
		int dialogExitStatus = dialog.open();
		if ( dialogExitStatus == 0 || dialogExitStatus == -1 ) //Okay or Select External File Pressed
		{
			Object[] strResult = dialog.getResult();
			if(strResult!=null)
			{
				for ( Object o: strResult )
				{
					if ( o instanceof String )
						//return ((IFileEditorInput) getEditorPart((String) o).getEditorInput()).getFile();//File from an open editor
						return getEditorPart((String) o);
					if ( o instanceof IFile)
						return (IFile) o;
				}
			}
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell(), "File Selection Error", "No file was selected for comparison.");
			return null;
		}
		else if ( dialogExitStatus == 1 ) //Cancel Pressed
		{
			return null;
		}
		else
		{
			return null;
		}
	}  //  selectEditor()
	
	
	private String getFilePath(EditorPart editor)
	{
		FileEditorInput input = (FileEditorInput) editor.getEditorInput();
		IFile theFile = input.getFile();
		return theFile.getLocation().toOSString();
	}
	
	/**
	 * Fetches an IFile based on the chosen file from the availableFiles map.
	 * @param editorName
	 * @return
	 */
	
	public IFile getEditorPart(String editorName)
	{
		if ( editorName == null )
			return null;
		
		return availableFiles.get(editorName);
		
	}  //  getEditor()
	

	private int findMaxPathLength(Object[] editors)
	{
		int maxLength = 0;
		int curLength = 0;
		for(int i = 0; i < editors.length; i++)
		{
			curLength = ((String) editors[0]).length();
			maxLength = Math.max(maxLength,curLength);
		}
		return maxLength;
	}
	
	
		
}




  
  
  
