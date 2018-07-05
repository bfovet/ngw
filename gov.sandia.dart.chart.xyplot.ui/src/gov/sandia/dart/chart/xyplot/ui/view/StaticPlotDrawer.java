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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;


/**
 * Draws a static (ie, formatted as an image) plot
 * @author lolney
 *
 */
public class StaticPlotDrawer {

	private Image createPlot(Plottable plottable, int width, int height) {
		if (plottable == null) {
			return null;
		}
		Image image = new Image(Display.getDefault(), width, height);
		XYGraphFigure graph = new XYGraphFigure();
		NebulaXYPlotView.addPlotToGraph(plottable, graph.xyGraph);
		render(image, true, graph, graph.getBounds());
		return image;		
	}

	private Image render(Image image, boolean returnMissingImageOnError, IFigure graph, Rectangle exportArea) {
		GC gc = new GC(image);
		SWTGraphics baseGraphcis = new SWTGraphics(gc);
		baseGraphcis.translate(-exportArea.x, -exportArea.y);

		Graphics graphics = baseGraphcis;

		try {
			graphics.pushState();
			try {
				graph.paint(graphics);
				graphics.restoreState();

			} finally {
				graphics.popState();
			}
		} catch (Throwable t) {
			if (!returnMissingImageOnError) {
				image.dispose();
				image = null;
			}
		} finally {
			baseGraphcis.dispose();
			gc.dispose();
		}
		return image;
	}

	public static Path writeImage(Image image, java.nio.file.Path absPath) {

		try (FileOutputStream out = new FileOutputStream(absPath.toFile())){
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] {image.getImageData()};
			loader.save(out, SWT.IMAGE_PNG);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return absPath;
	}

	public static Image drawPlot(Plottable plottable) {
		StaticPlotDrawer drawer = new StaticPlotDrawer();
		Image img = drawer.createPlot(plottable, 200, 200);
		return img;
	}
}

