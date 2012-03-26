package org.opennaas.extensions.router.junos.commandsets.velocity;

/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.velocity.context.Context;

/**
 * Tool for working with <code>null</code> in Velocity templates.
 * It provides a method to set a VTL reference back to <code>null</code>.
 * Also provides methods to check if a VTL reference is <code>null</code> or not.
 * <p>
 *   NOTE: These examples assume you have placed an
 *   instance of the current context within itself as 'ctx'.
 *   And, of course, the NullTool is assumed to be available as 'null'.
 * </p>
 * <p><pre>
 * Example uses:
 *  $foo                              -> bar
 *  $null.isNull($foo)                -> false
 *  $null.isNotNull($foo)             -> true
 *
 *  $null.setNull($ctx, "foo")
 *  $foo                              -> $foo (null)
 *  $null.isNull($foo)                -> true
 *  $null.isNotNull($foo)             -> false
 *
 *  $null.set($ctx, $foo, "hoge")
 *  $foo                              -> hoge
 *  $null.set($ctx, $foo, $null.null)
 *  $foo                              -> $foo (null)
 * </pre></p>
 *
 * <p>This tool is entirely threadsafe, and has no instance members.
 * It may be used in any scope (request, session, or application).
 * </p>
 *
 * @author <a href="mailto:shinobu@ieee.org">Shinobu Kawai</a>
 * @version $Id: $
 */
public class NullTool
{

    /**
     * Default constructor.
     */
    public NullTool()
    {
    }

    /**
     * Sets the given VTL reference back to <code>null</code>.
     * @param context the current Context
     * @param key the VTL reference to set back to <code>null</code>.
     */
    public void setNull(Context context, String key)
    {
        if (this.isNull(context))
        {
            return;
        }
        context.remove(key);
    }

    /**
     * Sets the given VTL reference to the given value.
     * If the value is <code>null</code>,
     * the VTL reference is set to <code>null</code>.
     * @param context the current Context
     * @param key the VTL reference to set.
     * @param value the value to set the VTL reference to.
     */
    public void set(Context context, String key, Object value)
    {
        if (this.isNull(context))
        {
            return;
        }
        if (this.isNull(value))
        {
            this.setNull(context, key);
            return;
        }
        context.put(key, value);
    }

    /**
     * Checks if a VTL reference is <code>null</code>.
     * @param object the VTL reference to check.
     * @return <code>true</code> if the VTL reference is <code>null</code>,
     *          <code>false</code> if otherwise.
     */
    public boolean isNull(Object object)
    {
        return object == null;
    }

    /**
     * Checks if a VTL reference is not <code>null</code>.
     * @param object the VTL reference to check.
     * @return <code>true</code> if the VTL reference is not <code>null</code>,
     *          <code>false</code> if otherwise.
     */
    public boolean isNotNull(Object object)
    {
        return !this.isNull(object);
    }

    /**
     * A convinient method which returns <code>null</code>.
     * Actually, this tool will work the same without this method,
     * because Velocity treats non-existing methods as null. :)
     * @return <code>null</code>
     */
    public Object getNull()
    {
        return null;
    }

}