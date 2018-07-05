/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.chart.xyplot.ui.view;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.nebula.visualization.xygraph.figures.ToolbarArmedXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.ZoomType;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

class XYGraphFigure extends Figure {
	public XYGraph xyGraph;
	public Runnable updater;
	boolean running = false;
	private final ToolbarArmedXYGraph toolbarArmedXYGraph;
	public XYGraphFigure() {

		xyGraph = new XYGraph();
		xyGraph.setTitle("Plot View");
		xyGraph.setFont(XYGraphMediaFactory.getInstance().getFont(XYGraphMediaFactory.FONT_TAHOMA));
		xyGraph.primaryXAxis.setRange(new Range(0,200));
		xyGraph.primaryYAxis.setAutoScale(true);
		xyGraph.primaryXAxis.setAutoScale(true);
		xyGraph.primaryXAxis.setShowMajorGrid(true);
		xyGraph.primaryYAxis.setShowMajorGrid(true);
		xyGraph.primaryXAxis.setAutoScaleThreshold(0);
		
		toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);
		add(toolbarArmedXYGraph);

		//add key listener to XY-Graph. The key pressing will only be monitored when the
		//graph gains focus.
		
		xyGraph.setFocusTraversable(true);
		xyGraph.setRequestFocusEnabled(true);
		
		xyGraph.getPlotArea().addMouseListener(new MouseListener.Stub(){
			@Override
			public void mousePressed(final MouseEvent me) {
				xyGraph.requestFocus();
			}
		});	

		xyGraph.addKeyListener(new KeyListener.Stub(){
			@Override
			public void keyPressed(final KeyEvent ke) {
				if((ke.getState() == SWT.CONTROL) && (ke.keycode == 'z')){
					xyGraph.getOperationsManager().undo();
				}
				if((ke.getState() == SWT.CONTROL) && (ke.keycode == 'y')){
					xyGraph.getOperationsManager().redo();
				}
				if((ke.getState() == SWT.CONTROL) && (ke.keycode == 'x')){
					xyGraph.performAutoScale();
				}
				if((ke.getState() == SWT.CONTROL) && (ke.keycode == 'd')){
					for (Trace t: xyGraph.getPlotArea().getTraceList()) {
						xyGraph.removeTrace(t);
					}				}
				
				if((ke.getState() == SWT.CONTROL) && (ke.keycode == 's')){
					final ImageLoader loader = new ImageLoader();
					loader.data = new ImageData[]{xyGraph.getImage().getImageData()};
					  final FileDialog dialog = new FileDialog(Display.getDefault().getShells()[0], SWT.SAVE);
					    dialog.setFilterNames(new String[] {"PNG Files", "All Files (*.*)" });
					    dialog.setFilterExtensions(new String[] { "*.png", "*.*" }); // Windows
					    final String path = dialog.open();
					    if((path != null) && !path.equals("")) {
                            loader.save(path, SWT.IMAGE_PNG);
                        }
				}
				if((ke.getState() == SWT.CONTROL) && (ke.keycode + 'a' -97 == 't')){
					switch (xyGraph.getZoomType()) {
					case RUBBERBAND_ZOOM:
						xyGraph.setZoomType(ZoomType.HORIZONTAL_ZOOM);
						break;
					case HORIZONTAL_ZOOM:
						xyGraph.setZoomType(ZoomType.VERTICAL_ZOOM);
						break;
					case VERTICAL_ZOOM:
						xyGraph.setZoomType(ZoomType.ZOOM_IN);
						break;
					case ZOOM_IN:
						xyGraph.setZoomType(ZoomType.ZOOM_OUT);
						break;
					case ZOOM_OUT:
						xyGraph.setZoomType(ZoomType.PANNING);
						break;
					case PANNING:
						xyGraph.setZoomType(ZoomType.NONE);
						break;
					case NONE:
						xyGraph.setZoomType(ZoomType.RUBBERBAND_ZOOM);
						break;
					default:
						break;
					}
				}
			}
		});
	}
	@Override
	protected void layout() {
		toolbarArmedXYGraph.setBounds(bounds.getCopy().shrink(5, 5));
		super.layout();
	}
}
