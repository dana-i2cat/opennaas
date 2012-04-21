package org.opennaas.extensions.bod.autobahn.commands;

import com.google.common.collect.Lists;
import java.util.List;
import org.opennaas.core.resources.action.ActionException;

import static com.google.common.base.Preconditions.checkState;

public class Transaction
{
	private static final ThreadLocal<Transaction> transaction =
		new ThreadLocal<Transaction>() {
			@Override
			protected Transaction initialValue()
			{
				return new Transaction();
			}
		};

	/** Commands to execute. */
	private final List<IAutobahnCommand> queue = Lists.newArrayList();

	/** Commands already executed. */
	private final List<IAutobahnCommand> log = Lists.newArrayList();

	private boolean open = false;

	public static Transaction getInstance()
	{
		return transaction.get();
	}

	public void begin()
		throws ActionException
	{
		checkState(!open);
		queue.clear();
		log.clear();
		open = true;
	}

	public void commit()
		throws ActionException
	{
		checkState(open);
		for (IAutobahnCommand command: queue) {
			command.execute();
			log.add(command);
		}
		open = false;
	}

	public void rollback()
		throws ActionException
	{
		checkState(open);
		try {
			for (IAutobahnCommand command: log) {
				command.undo();
			}
		} finally {
			open = false;
		}
	}

	public void add(IAutobahnCommand command)
	{
		checkState(open);
		queue.add(command);
	}
}