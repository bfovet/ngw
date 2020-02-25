/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.chart.xyplot.command;

import gov.sandia.dart.chart.xyplot.expression.FunctionBuilder;
import gov.sandia.dart.chart.xyplot.ui.view.NebulaXYPlotView;
import gov.sandia.dart.chart.xyplot.ui.view.Plottable;
import gov.sandia.dart.command.queue.AbstractCommand;
import gov.sandia.dart.command.queue.ICommandInterpreterState;
import gov.sandia.dart.command.queue.QueueException;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class XYPlotCommand extends AbstractCommand {

	public XYPlotCommand() {
		super("xyplot");
	}

	@Override
	public Object execute(final String[] argv, final ICommandInterpreterState cis)
			throws QueueException {
		checkArgCount(argv, 1);
		final double[][] data = parseData(argv[1]);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					NebulaXYPlotView view = (NebulaXYPlotView) page.showView(NebulaXYPlotView.VIEW_ID);
					view.addPlot(new Plottable(FunctionBuilder.buildPiecewiseLinear("",1.0,1.0,0.0,0.0, data[0], data[1])));
				} catch (final Exception e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							cis.getNotifier().reportError(e);							
						}
					});					
				} 			
			} 
		});
		return null;
	}
	
	/**
	 * Handles "#" prefix comments and blank lines 
	 * Data points must appear one x, y pair on each line 
	 * Delimiters between x and y can be any combination of comma, space, and tab.
	 */
	public static double[][] parseData(String rawData) {
		Pattern comment = Pattern.compile("#[^\n\r]*[\n\r]*");
		Scanner scanner = new Scanner(new StringReader(rawData));
		scanner.useDelimiter("[ \t,\n\r]+");
		ArrayList<double[]> list = new ArrayList<double[]>();
		do {
			while (scanner.hasNext(comment))
				scanner.skip(comment);
			if (scanner.hasNextDouble()) {
				double d1 = scanner.nextDouble();
				if (scanner.hasNextDouble()) {
					double d2 = scanner.nextDouble();
					list.add(new double[] {d1, d2});
				} else {
					break;
				}
			} else {
				break;
			}
			if (scanner.hasNextLine()) {
				scanner.nextLine();
			} else {
				break;
			}
		} while (scanner.hasNextDouble());
		double[][] result = new double[2][list.size()];
		for (int i=0; i<list.size(); ++i) {
			double[] pair = list.get(i);
			result[0][i] = pair[0];
			result[1][i] = pair[1];
		}
		return result;
	}

	@Override
	public String usageArgs() {
		return "<csv-data>";
	}

}
