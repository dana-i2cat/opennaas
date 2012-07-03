/**
*  This code is part of the Harmony System implemented in Work Package 1 
*  of the Phosphorus project. This work is supported by the European 
*  Comission under the Sixth Framework Programme with contract number 
*  IST-034115.
*
*  Copyright (C) 2006-2009 Phosphorus WP1 partners. Phosphorus Consortium.
*  http://ist-phosphorus.eu/
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */


/**
 *
 */
package org.opennaas.core.resources.helpers;

/**
 * Utility class to represent a 2-tuple.
 * 
 * @author Alexander Willner (willner@cs.uni-bonn.de)
 * @version $Id: Tuple.java 3124 2008-06-11 08:13:24Z dewaal@cs.uni-bonn.de $
 * @param <Type1>
 *            Type of first element.
 * @param <Type2>
 *            Type of second element.
 * @todo Use/extend a java collection.
 */
public class Tuple<Type1, Type2> {
    /** * */
    private final Type1 firstElement;

    /** * */
    private final Type2 secondElement;

    /**
     * Create a 2-tuple with the given element types.
     * 
     * @param element1
     *            First element
     * @param element2
     *            Second element
     */
    public Tuple(final Type1 element1, final Type2 element2) {
        this.firstElement = element1;
        this.secondElement = element2;
    }

    /**
     * Not sure whether we need this ...
     * 
     * @param other
     * @return
     */
    public boolean equals(final Tuple<Type1, Type2> other) {
        final boolean eq1 = ((this.getFirstElement() == null) && (other
                .getFirstElement() == null))
                || this.getFirstElement().equals(other.getFirstElement());
        if (eq1) {
            return ((this.getSecondElement() == null) && (other
                    .getSecondElement() == null))
                    || this.getSecondElement().equals(other.getSecondElement());
        }
        return false;
    }

    /**
     * Get first element.
     * 
     * @return The first element.
     */
    public final Type1 getFirstElement() {
        return this.firstElement;
    }

    /**
     * Get second element.
     * 
     * @return The second element.
     */
    public final Type2 getSecondElement() {
        return this.secondElement;
    }
}
