/*
 * Created by mjgibso on Apr 3, 2013 at 2:07:32 PM
 */
package gov.sandia.dart.workflow.app.application;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.internal.tweaklets.GrabFocus;

/**
 * @author mjgibso
 *
 */
public class GrabFocusTweaklet extends GrabFocus
{
	public static final Collection<String> DONT_GRAB_FOCUS_VIEW_IDS = 
			Collections.unmodifiableCollection(Arrays.asList(new String[] {
				IPageLayout.ID_PROBLEM_VIEW,
			}));
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.tweaklets.GrabFocus#grabFocusAllowed(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public boolean grabFocusAllowed(IWorkbenchPart part)
	{
		if(part == null)
		{
			return true;
		}
		
		IWorkbenchPartSite site = part.getSite();
		if(site == null)
		{
			return true;
		}
		
		return !DONT_GRAB_FOCUS_VIEW_IDS.contains(site.getId());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.tweaklets.GrabFocus#init(org.eclipse.swt.widgets.Display)
	 */
	@Override
	public void init(Display display)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.tweaklets.GrabFocus#dispose()
	 */
	@Override
	public void dispose()
	{
	}
}
