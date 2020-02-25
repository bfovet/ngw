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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * The contract class for a Strategy Provider, done as an abstract class rather
 * than java interface. Provides the definition for obtaining a set of
 * Strategies to resolve the local hostname.
 * </p>
 * 
 * @author kholson
 *
 */
public abstract class AbsLocalhostStrategyProvider
{
  private final List<IHostnameStrategy> _strategies = new ArrayList<>();

  private final IHostnameStrategy LOCALHOST_STRATEGY = new StrategyLocalhost();




  /**
   * Constructor that adds 4 basic strategies:
   * <ul>
   * <li>Inet</li>
   * <li>Exec (for a specified command, e.g., hostname)</li>
   * <li>Env (for a specified variable, e.g., COMPUTERNAME</li>
   * <li>Static (a specified value)</li>
   * </ul>
   */
  protected AbsLocalhostStrategyProvider()
  {
    _strategies.add(new StrategyInet());
    _strategies.add(new StrategyExec());
    _strategies.add(new StrategyEnv());
    _strategies.add(new StrategyStaticName());
  }




  /**
   * Returns the defined strategie
   */
  public Collection<IHostnameStrategy> getStrategies()
  {
    return Collections.unmodifiableList(_strategies);
  }




  /**
   * Returns the default strategy for a given Strategy Provider; must not return
   * null.
   */
  public abstract IHostnameStrategy getDefaultStrategy();




  /**
   * Returns the default input that should be used for a given Strategy; must
   * not return null; may return EmptyHostnameInput
   */
  public abstract AbsHostnameInput getDefaultInput(IHostnameStrategy strategy);




  /**
   * Allows subclasses to add strategies; strategies are added to the end of the
   * internal collection
   */
  protected void addStrategy(IHostnameStrategy s)
  {
    _strategies.add(s);
  }




  /**
   * Allows subclasses to add a strategy at the specified index; in this way a
   * strategy could be added to the start of the internal collection if desired
   */
  protected void addStrategy(IHostnameStrategy s, int idx)
  {
    _strategies.add(idx, s);
  }




  /**
   * Allows subclasses to clear the current strategies in preparation for adding
   * a different set
   */
  protected void clearStrategies()
  {
    _strategies.clear();
  }



  /**
   * The Localhost strategy returns "localhost" as the hostname; it is
   * a generic hostname; this method provides access to a the strategy
   * directly as a convenience method.
   */
  protected IHostnameStrategy getLocalhostStrategy()
  {
    return LOCALHOST_STRATEGY;
  }



  /**
   * Though strategies usually implement the equals() method, when
   * attempting to find a provided strategy it may be useful to
   * look up by the key (e.g., if the key were saved to the preferences);
   * this method returns the strategy in the internal collection
   * for the specific key if it is present in the internal collection.
   */
  protected Optional<IHostnameStrategy> getStrategyByKey(String key)
  {
    Optional<IHostnameStrategy> ret = Optional.empty();

    ret =
        _strategies.stream()
            .filter(s -> StringUtils.equals(key, s.getKey()))
            .findAny();

    return ret;
  }
}
