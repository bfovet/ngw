/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.common.core.localhostname;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;


/**
 * @author kholson
 *
 */
public class LinuxStrategyProvider extends AbsLocalhostStrategyProvider
{

  /**
   * 
   */
  public LinuxStrategyProvider()
  {
  }



  @Override
  public IHostnameStrategy getDefaultStrategy()
  {
    Optional<IHostnameStrategy> strat = getStrategyByKey(StrategyInet.KEY);
    
    return strat.orElse(getLocalhostStrategy());
  }

  
  @Override
  public AbsHostnameInput getDefaultInput(IHostnameStrategy strategy)
  {
    AbsHostnameInput hni = new EmptyHostnameInput();
    
    if (StringUtils.equals(strategy.getKey(), StrategyExec.KEY)) {
      hni = new HostnameInput("/bin/hostname -s");
    }
    
    return hni;
  }
  
}
