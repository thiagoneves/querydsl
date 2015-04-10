/*
 * Copyright 2011, Mysema Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.jdo;

import java.io.Closeable;

import javax.jdo.PersistenceManager;

import com.querydsl.core.ProjectableQuery;
import com.querydsl.core.Query;
import com.querydsl.core.types.EntityPath;

/**
 * Query interface for JDOQL queries
 *
 * @author tiwe
 *
 */
public interface JDOQLQuery<T> extends ProjectableQuery<T, JDOQLQuery<T>>, Query<JDOQLQuery<T>>, Closeable {

    /**
     * Add query sources
     *
     * @param sources
     * @return
     */
    JDOQLQuery<T> from(EntityPath<?>... sources);

    /**
     * Clone the state of the query for the given PersistenceManager
     *
     * @param persistenceManager
     * @return
     */
    JDOQLQuery<T> clone(PersistenceManager persistenceManager);

    /**
     * Add the fetch group to the set of active fetch groups.
     * 
     * @param fetchGroupName
     * @return
     */
    JDOQLQuery<T> addFetchGroup(String fetchGroupName);

    /**
     * Set the maximum fetch depth when fetching. 
     * A value of 0 has no meaning and will throw a JDOUserException.
     * A value of -1 means that no limit is placed on fetching.
     * A positive integer will result in that number of references from the
     * initial object to be fetched.
     * 
     * @param maxFetchDepth
     * @return
     */
    JDOQLQuery<T> setMaxFetchDepth(int maxFetchDepth);
    
    /**
     * Close the query and related resources
     */
    @Override
    void close();

}
