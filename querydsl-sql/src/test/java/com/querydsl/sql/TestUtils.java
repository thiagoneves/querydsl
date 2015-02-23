package com.querydsl.sql;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

public final class TestUtils {

    private TestUtils() {}

    @Deprecated
    public static BooleanExpression exists(SubQueryExpression<?> query) {
        return Expressions.predicate(Ops.EXISTS, query);
    }

    @Deprecated
    public static BooleanExpression notExists(SubQueryExpression<?> query) {
        return exists(query).not();
    }
}
