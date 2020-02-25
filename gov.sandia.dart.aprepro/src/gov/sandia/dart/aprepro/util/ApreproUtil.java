/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.strikewire.snl.apc.util.StreamGobbler;

import gov.sandia.dart.aprepro.ApreproPlugin;
import gov.sandia.dart.aprepro.actions.CreateParameterAction;
import gov.sandia.dart.aprepro.actions.IParameterSource;
import gov.sandia.dart.aprepro.actions.IParameterUpdater;
import gov.sandia.dart.aprepro.actions.InsertParameterAction;
import gov.sandia.dart.aprepro.pref.ApreproConstants;
import gov.sandia.dart.aprepro.ui.ApreproVariableData;

public class ApreproUtil {

	private static final String LAST_EVENT = "LAST_EVENT";
	private final static int BUFFER_SIZE = 5000;
	private final static int BUFFER_OVERLAP = 50;
	private final static Log log = LogFactory.getLog(ApreproUtil.class);

	// This pattern is, loosely, trying to match just { variable = number }, and rejects anything like
	// { variable = expression }, although you can fool it with a few pathological cases like 
	// { x = e+e } . This is probably good enough.
	private static Pattern APREPRO_NUM =
			Pattern.compile("\\{\\s*([a-z0-9_]+)\\s*=\\s*([efg0-9.+-]+)\\s*\\}", 
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	// This pattern is, loosely, trying to match just { variable = 'string' }. The regex can't handle embedded quotes.
	// We probably need to improve this one. Maybe we need a whole aprepro parser.
	private static Pattern APREPRO_STR =
			Pattern.compile("\\{\\s*([a-z0-9_]+)\\s*=\\s*['\"]([^'\"]+)['\"]\\s*\\}", 
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	
	// implemented the current way to deal with issue where user has a gigantic file represented as one long string
	public static Map<String, ApreproVariableData> processIResource(IResource resource) throws CoreException
	{		
		Map<String, ApreproVariableData> list = new HashMap<>();
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(resource.getFullPath());
		
		if(!file.exists())
		{
			return list;
		}

		InputStream stream = file.getContents();
		InputStreamReader input = new InputStreamReader(stream);

		BufferedReader reader = new BufferedReader(input);
		try {
			char buffer[] = new char[BUFFER_SIZE];
			int count = 0;
			int streamIndex = 0;
			reader.mark(BUFFER_SIZE * 2);		

			// Read BUFFER_SIZE chunks, overlapping by BUFFER_OVERLAP		
			while( (count = reader.read(buffer, 0, BUFFER_SIZE)) != -1) {
				String text = new String(buffer, 0, count);
				buildApreproVariableList(text, list, streamIndex);

				//Advance in inputstream
				reader.reset();
				reader.skip(BUFFER_SIZE - BUFFER_OVERLAP);
				reader.mark(BUFFER_SIZE * 2);

				//update the index
				streamIndex += BUFFER_SIZE - BUFFER_OVERLAP;
			}
		} catch (IOException e) {
			throw ApreproPlugin.getDefault().newError(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				ApreproPlugin.getDefault().logError(e);
			}
		}
		return list;
										
	}
	
	// EJFH Dead code?
	/*
	public static Map<String, ApreproVariableData> processEditor(EditorPart textEditor, final Shell shell, IResource selectedIResource) throws BadLocationException {
//		EditorPart textEditor = (EditorPart) part;		
//		IResource selectedIResource = textEditor.getEditorInput() instanceof IFileEditorInput ? ((IFileEditorInput) textEditor.getEditorInput()).getFile() : null;

		if (selectedIResource == null) {
			// probably not in workspace (type FileStoreEditorInput)...send warning
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openWarning(shell, "Error", "File was not loaded properly into workspace.  Please close and reopen file.");
				}
			});
			
			

			return new HashMap<String, ApreproVariableData>();
		}
		
		IDocument document = (IDocument)textEditor.getAdapter(IDocument.class);
		if(document == null) {
			if(textEditor instanceof AbstractDecoratedTextEditor) {
				AbstractDecoratedTextEditor editor = (AbstractDecoratedTextEditor)textEditor;
				document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			}
		}
		//build list of aprepro variables
		Map<String, ApreproVariableData> aPreProList = buildApreproVariableList(document);
		return aPreProList;
	}
	*/
	
	public static Map<String, ApreproVariableData> buildApreproVariableList(IDocument document) throws BadLocationException {
		if (document != null && document.getLength() > 0) {
			String text = document.get();
			return buildApreproVariableList(text);
		}
		return Collections.emptyMap();		
	}

	public static Map<String, ApreproVariableData> buildApreproVariableList(String text) {
		Map<String, ApreproVariableData> list = new HashMap<>();
		buildApreproVariableList(text, list, 0);
		return list;
	}

	private static void buildApreproVariableList(String text, Map<String, ApreproVariableData> list, int offset) {
		Matcher match_num = APREPRO_NUM.matcher(text);
		while(match_num.find()) {
			Region region = new Region(offset + match_num.start(), match_num.end() - match_num.start());
			ApreproVariableData data = createVariableData(match_num.group(1), match_num.group(2), region.getOffset(), region.getLength());
			list.put(data.getKey(), data);
		}
		Matcher match_str = APREPRO_STR.matcher(text);
		while(match_str.find()) {
			Region region = new Region(offset + match_str.start(), match_str.end() - match_str.start());
			ApreproVariableData data = createVariableData(match_str.group(1), match_str.group(2), region.getOffset(), region.getLength());
			list.put(data.getKey(), data);
		}
	}
	
	private static ApreproVariableData createVariableData(String key, String value, int offset, int length) {
		
		ApreproVariableData data = new ApreproVariableData(key, value, offset, length);
		return data;
	}
	
	public static String constructApreproString(String name, String value) {
		if (!isNumeric(value))
			value = new StringBuilder("'").append(value).append("'").toString();
			
		return new StringBuilder("{").append(name).append(" = ").append(value).append("}").toString();	
	}
	
	private static boolean isNumeric(String num) {
		try {
			Double.parseDouble(num);
			return true;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}
	
	public static String constructApreproString(String name) {
		return new StringBuilder("{").append(name).append("}").toString();	
	}
	
	
	// EJFH Dead code?
	/*
	public static Map<String, ApreproVariableData> getVariablesBasedOnLatestResource(IResource selectedIResource, Shell shell) throws Exception {
		Map<String, ApreproVariableData> variableList = null;
		
		IWorkbenchWindow wb = PlatformUI.getWorkbench().getActiveWorkbenchWindow();		
		IWorkbenchPage page = wb!=null ? wb.getActivePage() : null;
		IEditorReference[] editorRefs = page!=null ? page.getEditorReferences() : null;
		
		if(editorRefs == null)
			return new HashMap<String, ApreproVariableData>();
		
		try {
			for(IEditorReference ref : editorRefs) {
				IResource editorResource = ref.getEditorInput() instanceof IFileEditorInput ? ((IFileEditorInput) ref.getEditorInput()).getFile() : null;	
				if(ref.isDirty() && editorResource != null && editorResource.equals(selectedIResource)) {
					
					variableList = ApreproUtil.processEditor((EditorPart)ref.getEditor(true), shell, editorResource);
				}
			}

			if(variableList == null)
				variableList = ApreproUtil.processIResource(selectedIResource);
			
		} catch (PartInitException e) {
			ApreproPlugin.getDefault().logError("Error initializing part", e);
		}
		
		return variableList;
	}	*/
		
	public static void setupContextMenu(final Text text,
			final IParameterUpdater valueUpdater, final IParameterSource source) {
		
		text.addMenuDetectListener(new MenuDetectListener() {			
			@Override
			public void menuDetected(MenuDetectEvent e) {
				MenuDetectEvent last = (MenuDetectEvent) text.getData(LAST_EVENT);
				if (last == null || last.time < e.time) {
					Menu menu = new Menu(text);

				    MenuItem item = new MenuItem(menu, SWT.PUSH);
				    item.setText("Cut");
			    	item.setImage(ApreproPlugin.getDefault().getImageRegistry().get(ApreproPlugin.CUT_ICON));				    
				    item.addListener(SWT.Selection, new Listener()
				    {
				        @Override
				        public void handleEvent(Event event)
				        {
				            text.cut();
				        }
				    });
				    item.setEnabled(text.getSelectionCount() > 0);
				    
				    item = new MenuItem(menu, SWT.PUSH);
				    item.setText("Copy");
			    	item.setImage(ApreproPlugin.getDefault().getImageRegistry().get(ApreproPlugin.COPY_ICON));				    
				    item.addListener(SWT.Selection, new Listener()
				    {
				        @Override
				        public void handleEvent(Event event)
				        {
				        	text.copy();
				        }
				    });
				    item.setEnabled(text.getSelectionCount() > 0);
				    
				    item = new MenuItem(menu, SWT.PUSH);
				    item.setText("Paste");
			    	item.setImage(ApreproPlugin.getDefault().getImageRegistry().get(ApreproPlugin.PASTE_ICON));
				    item.addListener(SWT.Selection, new Listener()
				    {
				        @Override
				        public void handleEvent(Event event)
				        {
				        	text.paste();
				        }
				    });
				    Display display = Display.getCurrent();
			        Clipboard clipboard = new Clipboard(display);
			        String data = (String) clipboard.getContents(TextTransfer.getInstance());
				    item.setEnabled(!StringUtils.isEmpty(data));
				    
				    item = new MenuItem(menu, SWT.PUSH);
				    item.setText("Select All");
				    item.addListener(SWT.Selection, new Listener()
				    {
				        @Override
				        public void handleEvent(Event event)
				        {
				        	text.selectAll();
				        }
				    });
				    item.setEnabled(!StringUtils.isEmpty(text.getText()));
				    
				    
				    new MenuItem(menu, SWT.SEPARATOR);

				    if (source.allowCreate()) {
				    	item = new MenuItem(menu, SWT.PUSH);
				    	final CreateParameterAction cpa = new CreateParameterAction(valueUpdater, source);
				    	item.setText(cpa.getText());
				    	item.setImage(ApreproPlugin.getDefault().getImageRegistry().get(CreateParameterAction.CREATE_PARAMETER_ICON));
				    	item.addListener(SWT.Selection, new Listener()
				    	{
				    		@Override
				    		public void handleEvent(Event event)
				    		{
				    			cpa.run();
				    		}
				    	});
				    }
				    item = new MenuItem(menu, SWT.PUSH);
			    	final InsertParameterAction ipa = new InsertParameterAction(valueUpdater, source);
			    	item.setText(ipa.getText());
			    	item.setImage(ApreproPlugin.getDefault().getImageRegistry().get(InsertParameterAction.INSERT_PARAMETER_ICON));
			    	item.addListener(SWT.Selection, new Listener()
			    	{
			    		@Override
			    		public void handleEvent(Event event)
			    		{
			    			ipa.run();
			    		}
			    	});
					text.setMenu(menu);
				}
				text.setData(LAST_EVENT, e);
			}
		});
	}

	public static String getCommentCharacter() {
		return ApreproPlugin.getDefault().getPreferenceStore().getString(ApreproConstants.COMMENT_PARAMS_ID);
	}
	
	public static void setCommentCharacter(String commentChar) {
		ApreproPlugin.getDefault().getPreferenceStore().setValue(ApreproConstants.COMMENT_PARAMS_ID, commentChar);	
	}
	
	public static int transform(File paramsFile, File definitionFile, File outputFile, File workingDir) throws IOException, InterruptedException
	{
		if(Objects.equals(definitionFile, outputFile) || outputFile == null) {
			File tmpFile = File.createTempFile("apreproTemp", null);
			int retCode = doTransform(paramsFile, definitionFile, tmpFile, workingDir);
			if (outputFile == null)
				outputFile = definitionFile;
			outputFile.delete();
			FileUtils.moveFile(tmpFile, outputFile);
			return retCode;
		} else {
			return doTransform(paramsFile, definitionFile, outputFile, workingDir);
		}
	}
	
	public static String transform(String input, File workingDir) throws IOException, InterruptedException {
		File inputFile = File.createTempFile("apreproTemp", null);
		File paramsFile = File.createTempFile("apreproTemp", null);
		File outputFile = File.createTempFile("apreproTemp", null);
		try {
			FileUtils.writeStringToFile(inputFile, input, Charset.defaultCharset());		
			doTransform(paramsFile, inputFile, outputFile, workingDir);
			String results = FileUtils.readFileToString(outputFile, Charset.defaultCharset());
			return results;
		} finally {
			inputFile.delete();
			paramsFile.delete();
			outputFile.delete();
		}
	}
	
	public static String getApreproCommand()
	{
		IPreferenceStore store = ApreproPlugin.getDefault().getPreferenceStore();        
        String apreproExec = store.getString(ApreproConstants.APREPRO_EXECUTABLE);
        return apreproExec;
	}
	
	private static int doTransform(File paramsFile, File definitionFile, File outputFile, File workingDir) throws IOException, InterruptedException
	{
		List<String> commands = new ArrayList<>();
		
		// aprepro command
		String apreproCommand = getApreproCommand();
		commands.add(apreproCommand);
		
		// Don't emit header
		commands.add("-q");
		
		// comment character
		String commentChar = getCommentCharacter();
		String commentCmd = "-c" + commentChar;
		commands.add(commentCmd);
		
		// immutable flag
		commands.add("--immutable");
		
		// params file
		commands.add("--include");
		commands.add(paramsFile.getAbsolutePath());
		
		commands.add(definitionFile.getAbsolutePath());
		commands.add(outputFile.getAbsolutePath());
	
		
		ProcessBuilder builder = new ProcessBuilder(commands).directory(workingDir);
		Process process = builder.start();
		StreamGobbler errGobbler = new StreamGobbler(process.getErrorStream(), -1);
		StreamGobbler outGobbler = new StreamGobbler(process.getInputStream(), -1);
		errGobbler.start();
		outGobbler.start();
		int retCode = process.waitFor();
		log.debug(outGobbler.toString());
		log.debug(errGobbler.toString());
		
		retCode = process.exitValue();
		
		if(retCode != 0 || errGobbler.toString().contains("ERROR:") || errGobbler.toString().contains("ERR:")) {
			throw new RuntimeException(errGobbler.toString());
		}		
		
		return retCode;		
	}
	
	public static int findApreproDefinitionInsertionOffset(IDocument doc) {
		if (looksLikeXyce(doc))
			return getXyceOffset(doc);
		else
			return 0;
	}

	// Horrible hard-coded hacks!
	private static int getXyceOffset(IDocument doc) {
		try {
			return doc.getLineOffset(1);
		} catch (BadLocationException e) {
			return 0;
		}
	}

	private static boolean looksLikeXyce(IDocument doc) {
		try {
			return doc.search(0, ".END", true, false, false) > -1;
		} catch (BadLocationException e) {
			return false;
		}
	}

}
