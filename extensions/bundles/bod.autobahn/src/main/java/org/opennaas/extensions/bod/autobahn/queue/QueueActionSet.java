package org.opennaas.extensions.bod.autobahn.queue;

import org.opennaas.core.resources.action.ActionSet;

public class QueueActionSet extends ActionSet
{
    public QueueActionSet()
    {
        setActionSetId("queueActionSet");
        putAction(ConfirmAction.ACTIONID, ConfirmAction.class);
        putAction(IsAliveAction.ACTIONID, IsAliveAction.class);
        putAction(PrepareAction.ACTIONID, PrepareAction.class);
        putAction(RestoreAction.ACTIONID, RestoreAction.class);
    }
}