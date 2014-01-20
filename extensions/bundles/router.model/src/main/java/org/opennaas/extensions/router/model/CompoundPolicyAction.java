package org.opennaas.extensions.router.model;

/*
 * #%L
 * OpenNaaS :: CIM Model
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


/**
 * CompoundPolicyAction is used to represent an expression consisting of an ordered sequence of action terms. Each action term is represented as a
 * subclass of the PolicyAction class. Compound actions are constructed by associating dependent action terms together using the
 * PolicyActionInPolicyAction aggregation.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class CompoundPolicyAction extends PolicyAction {

	/**
	 * Default constructor
	 */
	public CompoundPolicyAction() {
	}

	/**
	 * This property gives a policy administrator a way of specifying how the ordering of the PolicyActions associated with this PolicyRule is to be
	 * interpreted. Three values are supported:<br>
	 * <br>
	 * o mandatory(1): Do the actions in the indicated order, or don't do them at all.<br>
	 * <br>
	 * o recommended(2): Do the actions in the indicated order if you can, but if you can't do them in this order, do them in another order if you
	 * can.<br>
	 * <br>
	 * o dontCare(3): Do them -- I don't care about the order.<br>
	 * <br>
	 * The default value is 3 ("DontCare").
	 */
	public enum sequencedActions_enum {

		Mandatory(1),
		Recommended(2),
		Dont_Care(3);

		private final int	localValue;

		sequencedActions_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private sequencedActions_enum	sequencedActions	= sequencedActions_enum.Dont_Care;

	/**
	 * ExecutionStrategy defines the strategy to be used in executing the sequenced actions aggregated by this CompoundPolicyAction. There are three
	 * execution strategies:<br>
	 * <br>
	 * <br>
	 * <br>
	 * Do Until Success - execute actions according to predefined order, until successful execution of a single action.<br>
	 * <br>
	 * Do All - execute ALL actions which are part of the modeled set, according to their predefined order. Continue doing this, even if one or more
	 * of the actions fails.<br>
	 * <br>
	 * Do Until Failure - execute actions according to predefined order, until the first failure in execution of an action instance.<br>
	 * <br>
	 * The default value is 2 ("Do All").
	 */
	public enum executionStrategy_enum {

		Do_Until_Success(1),
		Do_All(2),
		Do_Until_Failure(3);

		private final int	localValue;

		executionStrategy_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private executionStrategy_enum	executionStrategy	= executionStrategy_enum.Do_All;

	public sequencedActions_enum getSequencedActions() {
		return sequencedActions;
	}

	public void setSequencedActions(sequencedActions_enum sequencedActions) {
		this.sequencedActions = sequencedActions;
	}

	public executionStrategy_enum getExecutionStrategy() {
		return executionStrategy;
	}

	public void setExecutionStrategy(executionStrategy_enum executionStrategy) {
		this.executionStrategy = executionStrategy;
	}

}
