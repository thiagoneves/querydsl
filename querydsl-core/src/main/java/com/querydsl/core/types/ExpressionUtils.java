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
package com.querydsl.core.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryException;


/**
 * ExpressionUtils provides utilities for constructing common operation instances. This class is
 * used internally in Querydsl and is not suitable to be used in cases where DSL methods are needed,
 * since the Expression implementations used in this class are minimal internal implementations.
 *
 * @author tiwe
 *
 */
public final class ExpressionUtils {

    private static class UnderscoreTemplates extends Templates {
        private UnderscoreTemplates() {
            add(PathType.PROPERTY, "{0}_{1}");
            add(PathType.COLLECTION_ANY, "{0}");
            add(PathType.LISTVALUE, "{0}_{1}");
            add(PathType.LISTVALUE_CONSTANT, "{0}_{1}");
        }
    }

    private static final Templates TEMPLATES = new UnderscoreTemplates();

    /**
     * Create a new Operation expression
     *
     * @param type
     * @param operator
     * @param args
     * @return
     */
    public static <T> Operation<T> operation(Class<? extends T> type, Operator operator,
                                             Expression<?>... args) {
        return operation(type, operator, ImmutableList.copyOf(args));
    }

    /**
     * Create a new Operation expression
     *
     * @param type
     * @param operator
     * @param args
     * @return
     */
    public static <T> Operation<T> operation(Class<? extends T> type, Operator operator,
                                             ImmutableList<Expression<?>> args) {
        if (type.equals(Boolean.class)) {
            return (Operation<T>) new PredicateOperation(operator, args);
        } else {
            return new OperationImpl<T>(type, operator, args);
        }
    }

    /**
     * Create a new Operation expression
     *
     * @param operator
     * @param args
     * @return
     */
    public static PredicateOperation predicate(Operator operator, Expression<?>... args) {
        return predicate(operator, ImmutableList.copyOf(args));
    }

    /**
     * Create a new Operation expression
     *
     * @param operator
     * @param args
     * @return
     */
    public static PredicateOperation predicate(Operator operator, ImmutableList<Expression<?>> args) {
        return new PredicateOperation(operator, args);
    }

    /**
     * Create a new Path expression
     *
     * @param type
     * @param variable
     * @return
     */
    public static <T> Path<T> path(Class<? extends T> type, String variable) {
        return new PathImpl<T>(type, variable);
    }

    /**
     * Create a new Path expression
     *
     * @param type
     * @param parent
     * @param property
     * @return
     */
    public static <T> Path<T> path(Class<? extends T> type, Path<?> parent, String property) {
        return new PathImpl<T>(type, parent, property);
    }

    /**
     * Create a new Path expression
     *
     * @param type
     * @param metadata
     * @param <T>
     * @return
     */
    public static <T> Path<T> path(Class<? extends T> type, PathMetadata metadata) {
        return new PathImpl<T>(type, metadata);
    }

    /**
     * Create a new Template expression
     *
     * @param template
     * @param args
     * @return
     */
    public static PredicateTemplate predicateTemplate(String template, Object... args) {
        return predicateTemplate(TemplateFactory.DEFAULT.create(template), ImmutableList.copyOf(args));
    }

    /**
     * Create a new Template expression
     *
     * @param template
     * @param args
     * @return
     */
    public static PredicateTemplate predicateTemplate(String template, ImmutableList<?> args) {
        return predicateTemplate(TemplateFactory.DEFAULT.create(template), args);
    }

    /**
     * Create a new Template expression
     *
     * @param template
     * @param args
     * @return
     */
    public static PredicateTemplate predicateTemplate(Template template, Object... args) {
        return predicateTemplate(template, ImmutableList.copyOf(args));
    }

    /**
     * Create a new Template expression
     *
     * @param template
     * @param args
     * @return
     */
    public static PredicateTemplate predicateTemplate(Template template, ImmutableList<?> args) {
        return new PredicateTemplate(template, args);
    }


    /**
     * Create a new Template expression
     *
     * @param cl
     * @param template
     * @param args
     * @return
     */
    public static <T> TemplateExpression<T> template(Class<? extends T> cl, String template, Object... args) {
        return template(cl, TemplateFactory.DEFAULT.create(template), ImmutableList.copyOf(args));
    }

    /**
     * Create a new Template expression
     *
     * @param cl
     * @param template
     * @param args
     * @return
     */
    public static <T> TemplateExpression<T> template(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return template(cl, TemplateFactory.DEFAULT.create(template), args);
    }

    /**
     * Create a new Template expression
     *
     * @param cl
     * @param template
     * @param args
     * @return
     */
    public static <T> TemplateExpression<T> template(Class<? extends T> cl, Template template, Object... args) {
        return template(cl, template, ImmutableList.copyOf(args));
    }

    /**
     * Create a new Template expression
     *
     * @param cl
     * @param template
     * @param args
     * @return
     */
    public static <T> TemplateExpression<T> template(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        if (cl.equals(Boolean.class)) {
            return (TemplateExpression<T>) new PredicateTemplate(template, args);
        } else {
            return new TemplateExpressionImpl<T>(cl, template, args);
        }
    }

    /**
     * @param col
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Expression<T> all(CollectionExpression<?, ? super T> col) {
        return new OperationImpl<T>((Class<T>)col.getParameter(0), Ops.QuantOps.ALL,
                ImmutableList.<Expression<?>>of(col));
    }

    /**
     * @param col
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Expression<T> any(CollectionExpression<?, ? super T> col) {
        return new OperationImpl<T>((Class<T>)col.getParameter(0), Ops.QuantOps.ANY,
                ImmutableList.<Expression<?>>of(col));
    }

    /**
     * @param col
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Expression<T> all(SubQueryExpression<? extends T> col) {
        return new OperationImpl<T>(col.getType(), Ops.QuantOps.ALL, ImmutableList.<Expression<?>>of(col));
    }

    /**
     * @param col
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Expression<T> any(SubQueryExpression<? extends T> col) {
        return new OperationImpl<T>(col.getType(), Ops.QuantOps.ANY, ImmutableList.<Expression<?>>of(col));
    }

    /**
     * Create the intersection of the given arguments
     *
     * @param exprs
     * @return
     */
    @Nullable
    public static Predicate allOf(Collection<Predicate> exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b != null) {
                rv = rv == null ? b : ExpressionUtils.and(rv,b);
            }
        }
        return rv;
    }

    /**
     * Create the intersection of the given arguments
     *
     * @param exprs
     * @return
     */
    @Nullable
    public static Predicate allOf(Predicate... exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b != null) {
                rv = rv == null ? b : ExpressionUtils.and(rv,b);
            }
        }
        return rv;
    }

    /**
     * Create the intersection of the given arguments
     *
     * @param left
     * @param right
     * @return
     */
    public static Predicate and(Predicate left, Predicate right) {
        left = (Predicate) extract(left);
        right = (Predicate) extract(right);
        if (left == null) {
            return right;
        } else if (right == null) {
            return left;
        } else {
            return predicate(Ops.AND, left, right);
        }
    }


    /**
     * Create the union of the given arguments
     *
     * @param exprs
     * @return
     */
    @Nullable
    public static Predicate anyOf(Collection<Predicate> exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b != null) {
                rv = rv == null ? b : ExpressionUtils.or(rv,b);
            }
        }
        return rv;
    }

    /**
     * Create the union of the given arguments
     *
     * @param exprs
     * @return
     */
    @Nullable
    public static Predicate anyOf(Predicate... exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b != null) {
                rv = rv == null ? b : ExpressionUtils.or(rv,b);
            }
        }
        return rv;
    }

    /**
     * Create an alias expression (source as alias) with the given source and alias
     *
     * @param <D>
     * @param source
     * @param alias
     * @return
     */
    public static <D> Expression<D> as(Expression<D> source, Path<D> alias) {
        return operation(alias.getType(), Ops.ALIAS, source, alias);
    }

    /**
     * Create an alias expression (source as alias) with the given source and alias
     *
     * @param <D>
     * @param source
     * @param alias
     * @return
     */
    public static <D> Expression<D> as(Expression<D> source, String alias) {
        return as(source, path(source.getType(), alias));
    }

    /**
     * @param source
     * @return
     */
    public static Expression<Long> count(Expression<?> source) {
        return operation(Long.class, Ops.AggOps.COUNT_AGG, source);
    }

    /**
     * Create an left equals constant expression
     *
     * @param <D>
     * @param left
     * @param constant
     * @return
     */
    public static <D> Predicate eqConst(Expression<D> left, D constant) {
        return eq(left, ConstantImpl.create(constant));
    }

    /**
     * Create an left equals right expression
     *
     * @param <D>
     * @param left
     * @param right
     * @return
     */
    public static <D> Predicate eq(Expression<D> left, Expression<? extends D> right) {
        return predicate(Ops.EQ, left, right);
    }

    /**
     * Create an left in right expression
     *
     * @param <D>
     * @param left
     * @param right
     * @return
     */
    public static <D> Predicate in(Expression<D> left, CollectionExpression<?,? extends D> right) {
        return predicate(Ops.IN, left, right);
    }

    /**
     * Create an left in right expression
     *
     * @param <D>
     * @param left
     * @param right
     * @return
     */
    public static <D> Predicate in(Expression<D> left, Collection<? extends D> right) {
        if (right.size() == 1) {
            return eqConst(left, right.iterator().next());
        } else {
            return predicate(Ops.IN, left, ConstantImpl.create(right));
        }
    }

    /**
     * Create a {@code left in right or...} expression for each list
     *
     * @param <D>
     * @param left
     * @param lists
     * @return a {@code left in right or...} expression
     */
    public static <D> Predicate inAny(Expression<D> left, Iterable<? extends Collection<? extends D>> lists) {
        BooleanBuilder rv = new BooleanBuilder();
        for (Collection<? extends D> list : lists) {
            rv.or(in(left, list));
        }
        return rv;
    }

    /**
     * Create a left is null expression
     *
     * @param left
     * @return
     */
    public static Predicate isNull(Expression<?> left) {
        return predicate(Ops.IS_NULL, left);
    }

    /**
     * Create a left is not null expression
     *
     * @param left
     * @return
     */
    public static Predicate isNotNull(Expression<?> left) {
        return predicate(Ops.IS_NOT_NULL, left);
    }

    /**
     * Convert the given like pattern to a regex pattern
     *
     * @param expr
     * @return
     */
    public static Expression<String> likeToRegex(Expression<String> expr) {
        return likeToRegex(expr, true);
    }


    @SuppressWarnings("unchecked")
    public static Expression<String> likeToRegex(Expression<String> expr, boolean matchStartAndEnd) {
        // TODO : this should take the escape character into account
        if (expr instanceof Constant<?>) {
            final String like = expr.toString();
            final StringBuilder rv = new StringBuilder(like.length() + 4);
            if (matchStartAndEnd && !like.startsWith("%")) {
                rv.append('^');
            }
            for (int i = 0; i < like.length(); i++) {
                char ch = like.charAt(i);
                if (ch == '.' || ch == '*' || ch == '?') {
                    rv.append('\\');
                } else if (ch == '%') {
                    rv.append(".*");
                    continue;
                } else if (ch == '_') {
                    rv.append('.');
                    continue;
                }
                rv.append(ch);
            }
            if (matchStartAndEnd && !like.endsWith("%")) {
                rv.append('$');
            }
            if (!like.equals(rv.toString())) {
                return ConstantImpl.create(rv.toString());
            }
        } else if (expr instanceof Operation<?>) {
            Operation<?> o = (Operation<?>)expr;
            if (o.getOperator() == Ops.CONCAT) {
                Expression<String> lhs = likeToRegex((Expression<String>) o.getArg(0), false);
                Expression<String> rhs = likeToRegex((Expression<String>) o.getArg(1), false);
                if (lhs != o.getArg(0) || rhs != o.getArg(1)) {
                    return operation(String.class, Ops.CONCAT, lhs, rhs);
                }
            }
        }
        return expr;
    }

    /**
     * @param exprs
     * @return
     */
    public static <T> Expression<T> list(Class<T> clazz, Expression<?>... exprs) {
        return list(clazz, ImmutableList.copyOf(exprs));
    }


    /**
     * @param exprs
     * @return
     */
    public static <T> Expression<T> list(Class<T> clazz, List<? extends Expression<?>> exprs) {
        Expression<T> rv = (Expression<T>)exprs.get(0);
        if (exprs.size() == 1) {
            rv = operation(clazz, Ops.SINGLETON, rv, exprs.get(0));
        } else {
            for (int i = 1; i < exprs.size(); i++) {
                rv = operation(clazz, Ops.LIST, rv, exprs.get(i));
            }
        }

        return rv;
    }

    @SuppressWarnings("unchecked")
    public static Expression<String> regexToLike(Expression<String> expr) {
        if (expr instanceof Constant<?>) {
            final String str = expr.toString();
            final StringBuilder rv = new StringBuilder(str.length() + 2);
            boolean escape = false;
            for (int i = 0; i < str.length(); i++) {
                final char ch = str.charAt(i);
                if (!escape && ch == '.') {
                    if (i < str.length() - 1 && str.charAt(i+1) == '*') {
                        rv.append('%');
                        i++;
                    } else {
                        rv.append('_');
                    }
                    continue;
                } else if (!escape && ch == '\\') {
                    escape = true;
                    continue;
                } else if (!escape && (ch == '[' || ch == ']' || ch == '^' || ch == '.' || ch == '*')) {
                    throw new QueryException("'" + str + "' can't be converted to like form");
                } else if (escape && (ch == 'd' || ch == 'D' || ch == 's' || ch == 'S' || ch == 'w' || ch == 'W')) {
                    throw new QueryException("'" + str + "' can't be converted to like form");
                }
                rv.append(ch);
                escape = false;
            }
            if (!rv.toString().equals(str)) {
                return ConstantImpl.create(rv.toString());
            }
        } else if (expr instanceof Operation<?>) {
            Operation<?> o = (Operation<?>)expr;
            if (o.getOperator() == Ops.CONCAT) {
                Expression<String> lhs = regexToLike((Expression<String>) o.getArg(0));
                Expression<String> rhs = regexToLike((Expression<String>) o.getArg(1));
                if (lhs != o.getArg(0) || rhs != o.getArg(1)) {
                    return operation(String.class, Ops.CONCAT, lhs, rhs);
                }
            }
        }
        return expr;
    }

    /**
     * Create a left not equals constant expression
     *
     * @param <D>
     * @param left
     * @param constant
     * @return
     */
    public static <D> Predicate neConst(Expression<D> left, D constant) {
        return ne(left, ConstantImpl.create(constant));
    }

    /**
     * Create a left not equals right expression
     *
     * @param <D>
     * @param left
     * @param right
     * @return
     */
    public static <D> Predicate ne(Expression<D> left, Expression<? super D> right) {
        return predicate(Ops.NE, left, right);
    }

    /**
     * Create an left not in right expression
     *
     * @param <D>
     * @param left
     * @param right
     * @return
     */
    public static <D> Predicate notIn(Expression<D> left, CollectionExpression<?,? extends D> right) {
        return predicate(Ops.NOT_IN, left, right);
    }

    /**
     * Create an left not in right expression
     *
     * @param <D>
     * @param left
     * @param right
     * @return
     */
    public static <D> Predicate notIn(Expression<D> left, Collection<? extends D> right) {
        if (right.size() == 1) {
            return neConst(left, right.iterator().next());
        } else {
            return predicate(Ops.NOT_IN, left, ConstantImpl.create(right));
        }
    }

    /**
     * Create a {@code left not in right and...} expression for each list
     *
     * @param <D>
     * @param left
     * @param lists
     * @return a {@code left not in right and...} expression
     */
    public static <D> Predicate notInAny(Expression<D> left, Iterable<? extends Collection<? extends D>> lists) {
        BooleanBuilder rv = new BooleanBuilder();
        for (Collection<? extends D> list : lists) {
            rv.and(notIn(left, list));
        }
        return rv;
    }

    /**
     * Create a left or right expression
     *
     * @param left
     * @param right
     * @return
     */
    public static Predicate or(Predicate left, Predicate right) {
        left = (Predicate) extract(left);
        right = (Predicate) extract(right);
        if (left == null) {
            return right;
        } else if (right == null) {
            return left;
        } else {
            return predicate(Ops.OR, left, right);
        }
    }

    /**
     * Get a distinct list of the given args
     *
     * @param args
     * @return
     */
    public static ImmutableList<Expression<?>> distinctList(Expression<?>... args) {
        final ImmutableList.Builder<Expression<?>> builder = ImmutableList.builder();
        final Set<Expression<?>> set = new HashSet<Expression<?>>(args.length);
        for (Expression<?> arg : args) {
            if (set.add(arg)) {
                builder.add(arg);
            }
        }
        return builder.build();
    }

    /**
     * Get a distinct list of the concatenated array contents
     *
     * @param args
     * @return
     */
    public static ImmutableList<Expression<?>> distinctList(Expression<?>[]... args) {
        final ImmutableList.Builder<Expression<?>> builder = ImmutableList.builder();
        final Set<Expression<?>> set = new HashSet<Expression<?>>();
        for (Expression<?>[] arr : args) {
            for (Expression<?> arg : arr) {
                if (set.add(arg)) {
                    builder.add(arg);
                }
            }
        }
        return builder.build();
    }

    /**
     * Get the potentially wrapped expression
     *
     * @param expr
     * @return
     */
    public static <T> Expression<T> extract(Expression<T> expr) {
        if (expr != null) {
            final Class<?> clazz = expr.getClass();
            if (clazz == PathImpl.class || clazz == PredicateOperation.class || clazz == ConstantImpl.class) {
                return expr;
            } else {
                return (Expression<T>) expr.accept(ExtractorVisitor.DEFAULT, null);
            }
        } else {
            return null;
        }
    }

    /**
     * Create a new root variable based on the given path and suffix
     *
     * @param path
     * @param suffix
     * @return
     */
    public static String createRootVariable(Path<?> path, int suffix) {
        String variable = path.accept(ToStringVisitor.DEFAULT, TEMPLATES);
        return variable + "_" + suffix;
    }

    /**
     * Create a new root variable based on the given path
     *
     * @param path
     * @return
     */
    public static String createRootVariable(Path<?> path) {
        return path.accept(ToStringVisitor.DEFAULT, TEMPLATES);
    }

    /**
     * Converts the given object to an Expression
     *
     * @param o
     * @return
     */
    public static Expression<?> toExpression(Object o) {
        if (o instanceof Expression) {
            return (Expression<?>) o;
        } else {
            return ConstantImpl.create(o);
        }
    }

    private ExpressionUtils() {}

}
