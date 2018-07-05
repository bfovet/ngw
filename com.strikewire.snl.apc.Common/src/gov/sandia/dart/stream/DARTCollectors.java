/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by mjgibso on Nov 15, 2016 at 10:00:03 PM
 */
package gov.sandia.dart.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

/**
 * @author mjgibso
 *
 */
public final class DARTCollectors
{
	private DARTCollectors()
	{}
	
	/**
	 * Collector that collects all the values in the given optionals for
	 * which there is a value present.
	 */
	public static <T> Collector<Optional<T>, List<T>, List<T>> optionalValueMapper()
	{
		Collector<Optional<T>, List<T>, List<T>> c = Collector.of(
				ArrayList::new, // supplier
				(list, optional) -> {optional.ifPresent(list::add);}, // accumulator
				(left, right) -> {left.addAll(right); return left;}, // combiner
				Collector.Characteristics.IDENTITY_FINISH // Characteristics
				);
		
		return c;
	}

}
