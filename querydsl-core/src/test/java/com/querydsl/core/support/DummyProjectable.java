package com.querydsl.core.support;

import java.util.List;

import javax.annotation.Nullable;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.Projectable;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;

public class DummyProjectable<T> implements Projectable<T> {

    private final List<T> results;

    public DummyProjectable(List<T> results) {
        this.results = results;
    }

    @Override
    public boolean exists() {
        return !results.isEmpty();
    }

    @Override
    public boolean notExists() {
        return results.isEmpty();
    }

    @Override
    public CloseableIterator<T> iterate() {
        return new IteratorAdapter<T>(results.iterator());
    }

    @Override
    public List<T> list() {
        return results;
    }

    @Nullable
    @Override
    public T firstResult() {
        return results.isEmpty() ? null : results.get(0);
    }

    @Nullable
    @Override
    public T uniqueResult() {
        if (results.size() > 1) {
            throw new NonUniqueResultException();
        } else if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    public QueryResults<T> listResults() {
        return new QueryResults<T>(results, QueryModifiers.EMPTY, results.size());
    }

    @Override
    public long count() {
        return results.size();
    }
}
