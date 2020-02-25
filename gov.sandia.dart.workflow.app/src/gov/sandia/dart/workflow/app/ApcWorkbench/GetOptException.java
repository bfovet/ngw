package gov.sandia.dart.workflow.app.ApcWorkbench;

/**
 * Ripped off from simba plugin...
 * 
 * An exception for GetOpt errors.
 * <P>
 * (C) 2000 Sandia National Laboratories
 * @version $Id: GetOptException.java,v 1.3 2001/10/19 17:15:21 ejfried Exp $
 */

public class GetOptException extends Exception
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 625398392390707546L;

	public GetOptException(String gripe) {
        super(gripe);
    }
}
