/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.palette;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.TreeEditPart;
import org.eclipse.gef.editparts.RootTreeEditPart;
import org.eclipse.gef.palette.PaletteListener;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public class PaletteTreeViewer extends PaletteViewer {
	private FigureCanvas figCanvas;

	public PaletteTreeViewer() {
		super();
		dispatcher = new EventDispatcher();
		setKeyHandler(new GraphicalViewerKeyHandler(this));
		setEditPartFactory(new PaletteTreeEditPartFactory());
	}

	// A dummy canvas to please the graphical base classes
	@Override
	protected FigureCanvas getFigureCanvas() {
		if (figCanvas == null) {
			figCanvas = new FigureCanvas(getControl().getParent());
		}
		return figCanvas;
	}

	class EventDispatcher implements MouseListener, MouseMoveListener,
			KeyListener, MouseTrackListener, FocusListener {
		protected static final int ANY_BUTTON = SWT.BUTTON1 | SWT.BUTTON2
				| SWT.BUTTON3;

		@Override
		public void keyPressed(KeyEvent kee) {
			getEditDomain().keyDown(kee, PaletteTreeViewer.this);
		}

		@Override
		public void keyReleased(KeyEvent kee) {
			getEditDomain().keyUp(kee, PaletteTreeViewer.this);
		}

		@Override
		public void mouseDoubleClick(MouseEvent me) {
			getEditDomain().mouseDoubleClick(me, PaletteTreeViewer.this);
		}

		@Override
		public void mouseDown(MouseEvent me) {
			getEditDomain().mouseDown(me, PaletteTreeViewer.this);
		}

		@Override
		public void mouseEnter(MouseEvent me) {
			getEditDomain().viewerEntered(me, PaletteTreeViewer.this);
		}

		@Override
		public void mouseExit(MouseEvent me) {
			getEditDomain().viewerExited(me, PaletteTreeViewer.this);
		}

		@Override
		public void mouseHover(MouseEvent me) {
			getEditDomain().mouseHover(me, PaletteTreeViewer.this);
		}

		@Override
		public void mouseMove(MouseEvent me) {
			if ((me.stateMask & ANY_BUTTON) != 0)
				getEditDomain().mouseDrag(me, PaletteTreeViewer.this);
			else
				getEditDomain().mouseMove(me, PaletteTreeViewer.this);
		}

		@Override
		public void mouseUp(MouseEvent me) {
			getEditDomain().mouseUp(me, PaletteTreeViewer.this);
		}

		@Override
		public void focusGained(FocusEvent event) {
			getEditDomain().focusGained(event, PaletteTreeViewer.this);
		}

		@Override
		public void focusLost(FocusEvent event) {
			getEditDomain().focusLost(event, PaletteTreeViewer.this);
		}
	}

	private EventDispatcher dispatcher;

	@Override
	protected void createDefaultRoot() {
		try {
			setRootEditPart(new RootTreeEditPart());
		} catch (ClassCastException e) {
			// to catch the wrong cast to a GraphicalEditPart in the
			// GraphicalViewerImpl.setRootEditPart implementation
		}
	}

	@Override
	public void setPaletteRoot(PaletteRoot root) {
		super.setPaletteRoot(root);
		// TODO Could order the toplevel categories here
		((FilteredTree) getControl()).getViewer().setInput(
				getRootEditPart().getContents().getChildren());
	}

	/**
	 * Creates the default tree and sets it as the control. The default styles
	 * will show scrollbars as needed, and allows for multiple selection.
	 * <p>
	 * Doesn't use the default createControl method name, as that one is made
	 * final in the ScrollingGraphicalViewer base class...
	 * </p>
	 *
	 * @param parent
	 *            The parent for the Tree
	 * @return the control
	 */
	public Control createTreeControl(Composite parent) {
		PatternFilter filter = new ShowChildrenPatternFilter();
		filter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new FilteredTree(parent, SWT.H_SCROLL | SWT.V_SCROLL, filter, true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.getViewer().setContentProvider(new PaletteTreeContentProvider());
		tree.getViewer().setLabelProvider(new PaletteLabelProvider());
		setControl(tree);
		return tree;
	}

	protected Tree getTreeControl() {
		final FilteredTree filteredTree = (FilteredTree) getControl();
		return filteredTree.getViewer().getTree();
	}

	/**
	 * @see org.eclipse.gef.EditPartViewer#findObjectAtExcluding(Point,
	 *      Collection, EditPartViewer.Conditional)
	 */
	@Override
	public EditPart findObjectAtExcluding(Point pt, @SuppressWarnings("rawtypes") Collection exclude,
			Conditional condition) {
		if (getControl() == null)
			return null;

		final Tree tree = getTreeControl();
		Rectangle area = tree.getClientArea();
		if (pt.x < area.x || pt.y < area.y || pt.x >= area.x + area.width
				|| pt.y >= area.y + area.height)
			return null;

		EditPart result = null;
		TreeItem tie = tree.getItem(new org.eclipse.swt.graphics.Point(pt.x,
				pt.y));

		if (tie != null) {
			result = (EditPart) tie.getData();
		} else if (tree.getData() instanceof EditPart) {
			result = (EditPart) tree.getData();
		}
		while (result != null) {
			if ((condition == null || condition.evaluate(result))
					&& !exclude.contains(result))
				return result;
			result = result.getParent();
		}
		return null;
	}

	/**
	 * "Hooks up" a Control, i.e. sets it as the control for the
	 * RootTreeEditPart, adds necessary listener for proper operation, etc.
	 */
	@Override
	protected void hookControl() {
		if (getControl() == null)
			return;

		final Tree tree = getTreeControl();
		
		//Route events to the appropriate EditDomain
		tree.addFocusListener(dispatcher);
		tree.addMouseListener(dispatcher);
		tree.addMouseMoveListener(dispatcher);
		tree.addKeyListener(dispatcher);
		tree.addMouseTrackListener(dispatcher);

		tree.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] ties = tree.getSelection();
				Object newSelection[] = new Object[ties.length];
				for (int i = 0; i < ties.length; i++)
					newSelection[i] = ties[i].getData();
				setSelection(new StructuredSelection(newSelection));

				if (newSelection.length > 0
						&& newSelection[0] instanceof PaletteEntryEditPart) {
					Object model = ((PaletteEntryEditPart) newSelection[0])
							.getModel();

					if (model instanceof ToolEntry) {
						setActiveTool((ToolEntry) model);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		TreeEditPart tep = (TreeEditPart) getRootEditPart();
		tep.setWidget(tree);
		try {
			super.hookControl();
		} catch (ClassCastException e) {
			// to catch the wrong cast to graphical widget etc in the
			// super-classes
		}
	}

	/**
	 * @see org.eclipse.gef.ui.parts.AbstractEditPartViewer#reveal(org.eclipse.gef.EditPart)
	 */
	@Override
	public void reveal(EditPart part) {
		if (!(part instanceof TreeEditPart))
			return;
		TreeEditPart treePart = (TreeEditPart) part;
		final Tree tree = getTreeControl();
		Widget widget = treePart.getWidget();
		if (widget instanceof TreeItem && !widget.isDisposed())
			tree.showItem((TreeItem) widget);
	}

	/**
	 * Creates or disposes a DragSource as needed, and sets the supported
	 * transfer types. Clients should not need to call or override this method.
	 */
	@Override
	protected void refreshDragSourceAdapter() {
		if (getControl() == null)
			return;
		if (getDelegatingDragAdapter().isEmpty())
			setDragSource(null);
		else {
			if (getDragSource() == null)
				setDragSource(new DragSource(getTreeControl(), DND.DROP_MOVE
						| DND.DROP_COPY | DND.DROP_LINK));
			getDragSource().setTransfer(
					getDelegatingDragAdapter().getTransfers());
		}
	}

	/**
	 * Creates or disposes a DropTarget as needed, and sets the supported
	 * transfer types. Clients should not need to call or override this method.
	 */
	@Override
	protected void refreshDropTargetAdapter() {
		if (getControl() == null)
			return;
		if (getDelegatingDropAdapter().isEmpty())
			setDropTarget(null);
		else {
			if (getDropTarget() == null)
				setDropTarget(new DropTarget(getTreeControl(), DND.DROP_MOVE
						| DND.DROP_COPY | DND.DROP_LINK));
			getDropTarget().setTransfer(
					getDelegatingDropAdapter().getTransfers());
		}
	}

	/**
	 * Unhooks a control so that it can be reset. This method deactivates the
	 * contents, removes the Control as being the Control of the
	 * RootTreeEditPart, etc. It does not remove the listeners because it is
	 * causing errors, although that would be a desirable outcome.
	 */
	@Override
	protected void unhookControl() {
		if (getControl() == null)
			return;
		super.unhookControl();
		// Ideally, you would want to remove the listeners here
		TreeEditPart tep = (TreeEditPart) getRootEditPart();
		tep.setWidget(null);
	}

	
	// Handle active Tool selection (override to allow cast from PaletteEntryEditPart)
	
	ToolEntry activeTool = null;

	@Override
	public ToolEntry getActiveTool() {
		return activeTool;
	}
	

	@Override
	public void setActiveTool(ToolEntry newMode) {
		if (newMode == null)
			newMode = getPaletteRoot().getDefaultEntry();
		if (activeTool != null && getTreeEntryEditPart(activeTool) != null )
			getTreeEntryEditPart(activeTool).setSelected(EditPart.SELECTED);
		activeTool = newMode;
		if (activeTool != null) {
			PaletteEntryEditPart editpart = getTreeEntryEditPart(activeTool);
			if (editpart != null) {
				editpart.setSelected(EditPart.SELECTED);
			}
		}
		fireModeChanged();
	}
	
	private PaletteEntryEditPart getTreeEntryEditPart(ToolEntry entry) {
		return (PaletteEntryEditPart) getEditPartRegistry().get(entry);
	}
	
	// Since we have to manually manage tool selection,
	// We need to manually manage the listeners as well to pass the correct tool	
	
	private List<PaletteListener> paletteListeners = new ArrayList<>();

	@Override
	public void addPaletteListener(PaletteListener paletteListener) {
		if (paletteListeners != null)
			paletteListeners.add(paletteListener);
	}	

	@Override
	public void removePaletteListener(PaletteListener paletteListener) {
		paletteListeners.remove(paletteListener);
	}
	
	@Override
	protected void fireModeChanged() {
		if (paletteListeners == null)
			return;
		for (int listener = 0; listener < paletteListeners.size(); listener++)
			paletteListeners.get(listener)
					.activeToolChanged(this, activeTool);
	}

}
