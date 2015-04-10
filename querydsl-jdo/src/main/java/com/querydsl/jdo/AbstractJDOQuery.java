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
import java.io.IOException;
import java.util.*;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.collect.Lists;
import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.querydsl.core.*;
import com.querydsl.core.support.ProjectableQueryBase;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;

/**
 * Abstract base class for custom implementations of the JDOCommonQuery interface.
 *
 * @author tiwe
 *
 * @param <Q>
 */
public abstract class AbstractJDOQuery<T, Q extends AbstractJDOQuery<T, Q>> extends ProjectableQueryBase<T, Q> implements JDOQLQuery<T> {

    private static final Logger logger = LoggerFactory.getLogger(JDOQuery.class);

    private final Closeable closeable = new Closeable() {
        @Override
        public void close() throws IOException {
            AbstractJDOQuery.this.close();
        }
    };

    private final boolean detach;

    private List<Object> orderedConstants = new ArrayList<Object>();

    @Nullable
    private final PersistenceManager persistenceManager;

    private final List<Query> queries = new ArrayList<Query>(2);

    private final JDOQLTemplates templates;

    protected final Set<String> fetchGroups = new HashSet<String>();

    @Nullable
    protected Integer maxFetchDepth;

    @Nullable
    private FactoryExpression<?> projection;

    public AbstractJDOQuery(@Nullable PersistenceManager persistenceManager) {
        this(persistenceManager, JDOQLTemplates.DEFAULT, new DefaultQueryMetadata(), false);
    }

    @SuppressWarnings("unchecked")
    public AbstractJDOQuery(
            @Nullable PersistenceManager persistenceManager,
            JDOQLTemplates templates,
            QueryMetadata metadata, boolean detach) {
        super(new JDOQueryMixin<Q>(metadata));
        this.queryMixin.setSelf((Q) this);
        this.templates = templates;
        this.persistenceManager = persistenceManager;
        this.detach = detach;
    }

    /**
     * Add the fetch group to the set of active fetch groups.
     *
     * @param fetchGroupName
     * @return
     */
    @Override
    public Q addFetchGroup(String fetchGroupName) {
        fetchGroups.add(fetchGroupName);
        return (Q)this;
    }

    /**
     * Close the query and related resources
     */
    @Override
    public void close() {
        for (Query query : queries) {
            query.closeAll();
        }
    }

    @Override
    public long count() {
        try {
            Query query = createQuery(true);
            query.setUnique(true);
            Long rv = (Long) execute(query, true);
            if (rv != null) {
                return rv.longValue();
            } else {
                throw new QueryException("Query returned null");
            }
        } finally {
            reset();
        }

    }

    @Override
    public boolean exists() {
        boolean rv = select(getSource()).firstResult() != null;
        close();
        return rv;
    }

    private Expression<?> getSource() {
        return queryMixin.getMetadata().getJoins().get(0).getTarget();
    }

    private Query createQuery(boolean forCount) {
        Expression<?> source = getSource();

        // serialize
        JDOQLSerializer serializer = new JDOQLSerializer(getTemplates(), source);
        serializer.serialize(queryMixin.getMetadata(), forCount, false);

        logQuery(serializer.toString(), serializer.getConstantToLabel());

        // create Query
        Query query = persistenceManager.newQuery(serializer.toString());
        orderedConstants = serializer.getConstants();
        queries.add(query);

        if (!forCount) {
            Expression<?> projection = queryMixin.getMetadata().getProjection();
            if (projection instanceof FactoryExpression) {
                this.projection = (FactoryExpression<?>)projection;
            }
            if (!fetchGroups.isEmpty()) {
                query.getFetchPlan().setGroups(fetchGroups);
            }
            if (maxFetchDepth != null) {
                query.getFetchPlan().setMaxFetchDepth(maxFetchDepth);
            }
        }

        return query;
    }

    protected void logQuery(String queryString, Map<Object, String> parameters) {
        String normalizedQuery = queryString.replace('\n', ' ');
        MDC.put(MDC_QUERY, normalizedQuery);
        MDC.put(MDC_PARAMETERS, String.valueOf(parameters));
        if (logger.isDebugEnabled()) {
            logger.debug(normalizedQuery);
        }
    }

    protected void cleanupMDC() {
        MDC.remove(MDC_QUERY);
        MDC.remove(MDC_PARAMETERS);
    }

    @SuppressWarnings("unchecked")
    private <T> T detach(T results) {
        if (results instanceof Collection) {
            return (T) persistenceManager.detachCopyAll(results);
        } else {
            return persistenceManager.detachCopy(results);
        }
    }

    private Object project(FactoryExpression<?> expr, Object row) {
        if (row == null) {
            return null;
        } else if (row.getClass().isArray()) {
            return expr.newInstance((Object[])row);
        } else {
            return expr.newInstance(new Object[]{row});
        }
    }

    @Nullable
    private Object execute(Query query, boolean forCount) {
        Object rv;
        if (!orderedConstants.isEmpty()) {
            rv = query.executeWithArray(orderedConstants.toArray());
        } else {
            rv = query.execute();
        }
        if (isDetach()) {
            rv = detach(rv);
        }
        if (projection != null && !forCount) {
            if (rv instanceof List) {
                List<?> original = (List<?>)rv;
                rv = Lists.newArrayList();
                for (Object o : original) {
                    ((List)rv).add(project(projection, o));
                }
            } else {
                rv = project(projection, rv);
            }
        }

        return rv;
    }

    @Override
    public Q from(EntityPath<?>... args) {
        return queryMixin.from(args);
    }

    public JDOQLTemplates getTemplates() {
        return templates;
    }

    public boolean isDetach() {
        return detach;
    }

    @Override
    public CloseableIterator<T> iterate() {
        return new IteratorAdapter<T>(list().iterator(), closeable);
    }

    @Override
    public List<T> list() {
        try {
            Object rv = execute(createQuery(false), false);
            return rv instanceof List ? (List<T>)rv : Collections.singletonList((T)rv);
        } finally {
            reset();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public QueryResults<T> listResults() {
        try {
            Query countQuery = createQuery(true);
            countQuery.setUnique(true);
            countQuery.setResult("count(this)");
            long total = (Long) execute(countQuery, true);
            if (total > 0) {
                QueryModifiers modifiers = queryMixin.getMetadata().getModifiers();
                Query query = createQuery(false);
                return new QueryResults<T>((List<T>) execute(query, false), modifiers, total);
            } else {
                return QueryResults.emptyResults();
            }
        } finally {
            reset();
        }
    }

    private void reset() {
        queryMixin.getMetadata().reset();
        cleanupMDC();
    }

    /**
     * Set the maximum fetch depth when fetching.
     * A value of 0 has no meaning and will throw a JDOUserException.
     * A value of -1 means that no limit is placed on fetching.
     * A positive integer will result in that number of references from the
     * initial object to be fetched.
     *
     * @param depth
     * @return
     */
    @Override
    public Q setMaxFetchDepth(int depth) {
        maxFetchDepth = depth;
        return (Q)this;
    }

    @Override
    public String toString() {
        if (!queryMixin.getMetadata().getJoins().isEmpty()) {
            Expression<?> source = getSource();
            JDOQLSerializer serializer = new JDOQLSerializer(getTemplates(), source);
            serializer.serialize(queryMixin.getMetadata(), false, false);
            return serializer.toString().trim();
        } else {
            return super.toString();
        }
    }

    @Nullable
    @Override
    public T uniqueResult() {
        if (getMetadata().getModifiers().getLimit() == null) {
            limit(2);
        }
        try {
            Query query = createQuery(false);
            Object rv = execute(query, false);
            if (rv instanceof List) {
                List<?> list = (List)rv;
                if (!list.isEmpty()) {
                    if (list.size() > 1) {
                        throw new NonUniqueResultException();
                    }
                    return (T) list.get(0);
                } else {
                    return null;
                }
            } else {
                return (T) rv;
            }
        } finally {
            reset();
        }

    }

}
