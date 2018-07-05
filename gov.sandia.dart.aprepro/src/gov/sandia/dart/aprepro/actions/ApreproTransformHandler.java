/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.strikewire.snl.apc.Common.compare.CompareInput;
import com.strikewire.snl.apc.Common.compare.CompareItem;
import com.strikewire.snl.apc.util.IStreamGobblerCallback;
import com.strikewire.snl.apc.util.ResourceUtils;
import com.strikewire.snl.apc.util.StreamGobbler;

import gov.sandia.dart.aprepro.ApreproPlugin;
import gov.sandia.dart.aprepro.util.ApreproUtil;

public class ApreproTransformHandler extends AbstractHandler {	
	public enum DisplayOption { InEditor, InCompareEditor }

	private final DisplayOption displayOption;		
	IStreamGobblerCallback errcallbackfunction;
	IStreamGobblerCallback outcallbackfunction;
	 
	
	public ApreproTransformHandler() {
		this.displayOption = DisplayOption.InEditor;
	}
	
	public ApreproTransformHandler(DisplayOption displayOption) {
		this.displayOption = displayOption;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {	
        
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.getActiveEditor();
	
		if(!(editor instanceof AbstractDecoratedTextEditor)) {
			return null;
		}
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveEditor(editor, true);
		
		IEditorInput input = editor.getEditorInput();			
		
		IFile inputDeckFile = input.getAdapter(IFile.class);
		if( inputDeckFile == null)
		{
			try {
				ILocationProvider ilp = input.getAdapter(ILocationProvider.class);
				IPath path = ilp.getPath(input);
				inputDeckFile = ResourceUtils.getFileForLocation(path.toOSString());
			} catch (CoreException e) {
				// Fall through and die
			}
			if (inputDeckFile == null) {
				MessageDialog.openError(page.getWorkbenchWindow().getShell(), "Error", "Can't get resource for open editor");
				return null;
			}
		}

		IPath workingDir = inputDeckFile.getLocation().removeLastSegments(1);
		
		if (isExistingApreproFile(inputDeckFile))
		{
			return null;
		}
	
		IFile apreproFile = runAprepro(inputDeckFile, workingDir);			
		if (apreproFile == null)
		{
			return null;
		}
		
		try {
			displayApreproTranslation(apreproFile, inputDeckFile);
		} catch (CoreException e) {
			ApreproPlugin.getDefault().logError("Problem opening editor", e);
		}			 
	
		return null;
	}

	
	private IFile runAprepro(IFile inputDeckFile, IPath workingDir) 
	{
		String comment = ApreproUtil.getCommentCharacter();
		String commentCmd = "-c" + comment;
		
		String apreproCommand = ApreproUtil.getApreproCommand(); 
		
		if(StringUtils.isBlank(apreproCommand))
		{
			return null;
		}
				
		try {
			
			MessageConsoleStream errstream = ApreproPlugin.getConsole().newMessageStream();			
			MessageConsoleStream outstream = ApreproPlugin.getConsole().newMessageStream();
			errstream.setColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));
			final PrintWriter err = new  PrintWriter(errstream,true);
			final PrintWriter out = new  PrintWriter(outstream,true);
			
			errcallbackfunction = new IStreamGobblerCallback() {					
					@Override
					public void run(Object line) {
						err.println(line);					
					}
				};
				
			outcallbackfunction = new IStreamGobblerCallback() {					
					@Override
					public void run(Object line) {
						out.println(line);					
					}
				};
			
			IPath outFile = getNewApreproFilePath(inputDeckFile, inputDeckFile.getLocation());
						
			String inFile = inputDeckFile.getLocation().toOSString();
			String[] command = new String[] {apreproCommand, commentCmd, inFile, outFile.toOSString()};
			
			out.println("\nProcessing "+inFile);
			
			Process apreproProcess = Runtime.getRuntime().exec(command, null, workingDir.toFile());							
			
			StreamGobbler errGobbler = new StreamGobbler(apreproProcess.getErrorStream(), -1,errcallbackfunction);
			StreamGobbler outGobbler = new StreamGobbler(apreproProcess.getInputStream(), -1,outcallbackfunction);
			
			errGobbler.start();
			outGobbler.start();
			apreproProcess.waitFor();								
			
			return getNewApreproFile(outFile);
			
		} catch (IOException | InterruptedException e) {
			displayProcessError();
			ApreproPlugin.getDefault().logError("Problem running aprepro", e);
			return null;				
		} catch (Exception e) {
			ApreproPlugin.getDefault().logError("Problem running aprepro", e);
			return null;
		}
	}	
	
	public void displayProcessError()
	{
		MessageDialog.openError(null, "Error", "Unsuccessful executing aprepro command." +
			"Try setting the path to a valid executable in the aprepro preference page and " +
			"try running the action again.");
		
	}  //  displayProcessError()
	
	private IPath getNewApreproFilePath(IFile file, IPath original) throws CoreException, URISyntaxException
	{
		return file.getLocation().removeLastSegments(1).append("aprepro-" + file.getName());
	}  
			
	private IEditorPart captureOpenApreproEditor(IFile file) throws PartInitException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] references = page.getEditorReferences();
		
		for ( IEditorReference ref : references)
		{
			IEditorPart editor = ref.getEditor(true);			
			IResource editorResource = ref.getEditorInput() instanceof IFileEditorInput ? ((IFileEditorInput) ref.getEditorInput()).getFile() : null;	
			
			if ( editorResource != null && file.equals(editorResource) )
			{
				return editor;
			}
			
		}
		return null;
		
	}  //  captureOpenApreproEditor()
	
	
	private IFile getNewApreproFile(IPath path)
	{
		try {
			IProject project = ResourceUtils.getHiddenProject();
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			return ResourceUtils.getFileForLocation( project, path.toOSString() );
		} catch ( CoreException e ) {
			ApreproPlugin.getDefault().logError("Problem creating new file", e);
			return null;
		}
	} 
	
	private boolean isExistingApreproFile(IFile file)
	{
		IPath path = file.getLocation();
		String fileName = path.lastSegment();
		
		if ( fileName.contains("aprepro-") )
		{
			MessageDialog.openInformation(null, fileName, "This file is already an APREPRO translation.");
			return true;
		}
		
		return false;
	}
	
	private void displayApreproTranslation(IFile apreproFile, IFile inputDeckFile) throws CoreException
	{
		switch ( displayOption )
		{
		case InEditor:
			displayApreproInEditor(apreproFile);
			break;
		case InCompareEditor:
			displayApreproInCompareEditor(apreproFile, inputDeckFile);
			break;
		}
	} 
	
	private void displayApreproInEditor(IFile file)
	{
		try {		
			file.refreshLocal(IResource.DEPTH_ONE, null);
			FileEditorInput input = new FileEditorInput(file);
			IEditorPart openEditor = captureOpenApreproEditor(file);

			if ( openEditor != null )
			{
				AbstractTextEditor editor = (AbstractTextEditor)openEditor;
				editor.setInput(input);
				
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(openEditor);
			} 
			else 
			{			
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(page, file);
			}
		} catch (Exception ex) {
			ApreproPlugin.getDefault().logError("Rendering error", ex);
		}		
	} 
		
	private void displayApreproInCompareEditor(IFile apreproFile, IFile inputDeckFile) throws CoreException
	{
		CompareUI.openCompareEditor( setUpCompareInput(apreproFile, inputDeckFile) );	
	}
	
	private CompareInput setUpCompareInput(IFile apreproFile, IFile inputDeckFile) throws CoreException
	{
		apreproFile.refreshLocal(IResource.DEPTH_ONE, null);
		String apreproText = getContents(apreproFile);
		String inputDeckText = getContents(inputDeckFile);							

		CompareItem aprepro = new CompareItem(apreproFile.getName(), apreproText, apreproFile.getLocationURI(), 0);
		CompareItem inputDeck = new CompareItem(inputDeckFile.getName(), inputDeckText, inputDeckFile.getLocationURI(), 0);
		CompareInput compareInput = new CompareInput();
		compareInput.setLeftItem(inputDeck);
		compareInput.setRightItem(aprepro);
		compareInput.getCompareConfiguration().setLeftLabel( inputDeck.getName() );
		compareInput.getCompareConfiguration().setRightLabel( aprepro.getName() );
		
		return compareInput;				
	}

	private String getContents(IFile file) {
		try {
			return IOUtils.toString(file.getContents(), Charset.defaultCharset());
		} catch (IOException | CoreException e) {
			ApreproPlugin.getDefault().logError("Problem reading transformed file", e);
			return "";
		}
	}
} 
