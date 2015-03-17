/*
 * tnm4j - Simplified SNMP API for Java
 * Copyright (C) 2012 Carl Harris, Jr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.soulwing.snmp;


/**
 * A configuration for an {@link SnmpContext}.
 *
 * @author Carl Harris
 */
public interface SnmpTargetConfig extends Cloneable {

  /**
   * Gets the number of retries for an SNMP operation when no response
   * is received before the configured timeout.
   * @return retry count
   */
  int getRetries();
  
  /**
   * Gets the interval of time that should elapse before an SNMP operation 
   * should be assumed to have not been received by the target agent.
   * @return timeout in milliseconds
   */
  long getTimeout();
  
  /**
   * Gets the maximum repetitions for repeating OIDs in a call to a walk
   * operation.
   * <p>
   * This value is used as the {@code maxRepetitions} value for the underlying
   * GETBULK operation used to implement MIB walking.  It does not effect
   * the total number of objects that can be returned from a walk.  Rather,
   * it controls the number of objects that are fetched in a single protocol
   * operation with the target agent.  The value selected here represents a
   * tradeoff between minimizing the number of round trips needed to complete
   * a walk versus the effort required by the agent to respond to each request
   * and the amount of information that will be discarded from the last
   * response (when the end of the information needed to complete the walk
   * has been reached).
   * @return max repetitions value > 0
   */
  int getWalkMaxRepetitions();
  
  /**
   * Gets a flag that determines whether a walk operation should allow a
   * truncated response.
   * <p>
   * The underlying GETBULK protocol operation used to implement walks allows
   * an arbitrary number of non-repeating and repeating objects to be 
   * requested.  If a GETBULK response contains few repeating objects than
   * were specified in the request, it is generally due to some limitation
   * of the target agent.  When this occurs, the walk is terminated with a
   * {@link TruncatedResponseException}.  Set this flag if you may wish to
   * allow the walk to proceed, making use of the objects that the agent can
   * successfully return, and ignoring the absence of the requested objects 
   * that were not returned by the agent.
   * @return flag state
   */
  boolean isWalkAllowsTruncatedRepetition();
  
  /**
   * Creates a clone of this configuration.
   * @return configuration clone
   */
  SnmpTargetConfig clone();
  
}
