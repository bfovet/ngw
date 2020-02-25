/*
 * Created on Sep 19, 2018 at 4:27:43 PM by mjgibso
 */
package com.strikewire.snl.apc.Common;

import java.net.URI;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.IRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

import gov.sandia.dart.application.DARTApplicationAdapter;
import gov.sandia.dart.application.DARTApplicationEvent;
import gov.sandia.dart.application.IDARTApplicationListener;
import gov.sandia.dart.configuration.IUpdateSite;
import gov.sandia.dart.configuration.mgr.UpdateSitesMgr;

/**
 * @author mjgibso
 *
 */
public class UpdateSiteRegistrationManager extends DARTApplicationAdapter implements IDARTApplicationListener
{
	private static final Logger _log = LogManager.getLogger(UpdateSiteRegistrationManager.class);	  

	/**
	 * 
	 */
	public UpdateSiteRegistrationManager()
	{}
	
	/* (non-Javadoc)
	 * @see gov.sandia.dart.application.DARTApplicationAdapter#preApplicationEvent(gov.sandia.dart.application.DARTApplicationEvent)
	 */
	@Override
	public void preApplicationEvent(DARTApplicationEvent event)
	{
		if(event == DARTApplicationEvent.WORKBENCH_ADVISOR_INITIALIZE)
		{
			scheduleRegistrationJob();
		}
	}
	
	private void scheduleRegistrationJob()
	{
		Job registerUpdateSitesJob = new Job("Registering update sites") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					registerUpdateSites();
				} catch (ProvisionException e) {
					CommonPlugin.getDefault().logError("Failed to register update sites.", e);
					// don't return the error, always return ok.  Only want to put this in the log, not in the user's face
				}
				return Status.OK_STATUS;
			}
		};
		
		registerUpdateSitesJob.setSystem(true);
		registerUpdateSitesJob.schedule();
	}
	
	private void registerUpdateSites() throws ProvisionException
	{
		_log.info("Registering update sites");

		UpdateSitesMgr usMgr = UpdateSitesMgr.getInstance();

		Collection<IUpdateSite> sites = usMgr.getUpdateSites();

		if(sites != null)
		{
			for(IUpdateSite site : sites)
			{
				if(site != null) {
					addUpdateSite(site);
				}
			} // for
		}
	}

	@SuppressWarnings("restriction")
	private void addUpdateSite(IUpdateSite site) throws ProvisionException
	{
		IMetadataRepositoryManager metadataManager = null;
		IArtifactRepositoryManager artifactManager = null;
		metadataManager = org.eclipse.equinox.p2.internal.repository.tools.Activator.getMetadataRepositoryManager();
		artifactManager = org.eclipse.equinox.p2.internal.repository.tools.Activator.getArtifactRepositoryManager();

		if(metadataManager==null || artifactManager==null)
		{
			return;
		}

		_log.debug("Adding update site: {}", site.getName());
		URI repoMeta = site.getMetadataRepository();
		URI repoArt = site.getArtifactRepository();

		metadataManager.addRepository(repoMeta);
		metadataManager.setRepositoryProperty(repoMeta, IRepository.PROP_NICKNAME, site.getName());

		artifactManager.addRepository(repoArt);
		artifactManager.setRepositoryProperty(repoMeta, IRepository.PROP_NICKNAME, site.getName());

	}

}
