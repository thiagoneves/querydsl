package com.querydsl.core.support;

import java.util.List;

import javax.annotation.Nonnegative;

import com.querydsl.core.ProjectableQuery;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;

public class DummyProjectableQuery<T> extends DummyProjectable<T> implements ProjectableQuery<T, DummyProjectableQuery<T>> {

    public DummyProjectableQuery(List<T> results) {
        super(results);
    }

    @Override
    public <U> DummyProjectableQuery<U> select(Expression<U> expr) {
        return (DummyProjectableQuery<U>)this;
    }

    @Override
    public DummyProjectableQuery<Tuple> select(Expression<?>... exprs) {
        return (DummyProjectableQuery<Tuple>)this;
    }

    @Override
    public <T1> T1 transform(ResultTransformer<T1> transformer) {
        return transformer.transform(this);
    }

    @Override
    public DummyProjectableQuery<T> limit(@Nonnegative long limit) {
        return this;
    }

    @Override
    public DummyProjectableQuery<T> offset(@Nonnegative long offset) {
        return this;
    }

    @Override
    public DummyProjectableQuery<T> restrict(QueryModifiers modifiers) {
        return this;
    }

    @Override
    public DummyProjectableQuery<T> orderBy(OrderSpecifier<?>... o) {
        return this;
    }

    @Override
    public <U> DummyProjectableQuery<T> set(ParamExpression<U> param, U value) {
        return this;
    }

    @Override
    public DummyProjectableQuery<T> distinct() {
        return this;
    }

    @Override
    public DummyProjectableQuery<T> where(Predicate... o) {
        return this;
    }
}
