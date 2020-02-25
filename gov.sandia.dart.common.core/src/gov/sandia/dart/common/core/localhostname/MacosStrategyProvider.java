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
 * <p>
 * The Strategy Provider for the Mac O/S. In addition to the default
 * Strategies (Inet, Exec, Env, Static), also provides Mac specific
 * resolution approaches.
 * </p>
 * @author kholson
 *
 */
public class MacosStrategyProvider extends AbsLocalhostStrategyProvider
{

  /**
   * 
   */
  public MacosStrategyProvider()
  {
    addStrategy(new StrategySystemProfiler());
  }



  @Override
  public IHostnameStrategy getDefaultStrategy()
  {
    Optional<IHostnameStrategy> strat = getStrategyByKey(StrategySystemProfiler.KEY);
    
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
