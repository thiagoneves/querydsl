package com.querydsl.sql;

import static com.querydsl.core.Target.*;
import static com.querydsl.sql.Constants.employee;
import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Tuple;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.domain.Employee;
import com.querydsl.sql.domain.QEmployee;

public class UnionBase extends AbstractBaseTest {

    @Test
    @ExcludeIn({MYSQL, TERADATA})
    public void In_Union() {
        assertTrue(query().from(employee)
            .where(employee.id.in(
                query().union(query().select(Expressions.ONE),
                              query().select(Expressions.TWO))))
            .exists());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Union() throws SQLException {
        SubQueryExpression<Integer> sq1 = query().from(employee).select(employee.id.max());
        SubQueryExpression<Integer> sq2 = query().from(employee).select(employee.id.min());
        List<Integer> list = query().union(sq1, sq2).list();
        assertFalse(list.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Union_All() {
        SubQueryExpression<Integer> sq1 = query().from(employee).select(employee.id.max());
        SubQueryExpression<Integer> sq2 = query().from(employee).select(employee.id.min());
        List<Integer> list = query().unionAll(sq1, sq2).list();
        assertFalse(list.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void Union_Multiple_Columns() throws SQLException {
        SubQueryExpression<Tuple> sq1 = query().from(employee).select(employee.firstname, employee.lastname);
        SubQueryExpression<Tuple> sq2 = query().from(employee).select(employee.lastname, employee.firstname);
        List<Tuple> list = query().union(sq1, sq2).list();
        assertFalse(list.isEmpty());
        for (Tuple row : list) {
            assertNotNull(row.get(0, Object.class));
            assertNotNull(row.get(1, Object.class));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn(DERBY)
    public void Union_Multiple_Columns2() throws SQLException {
        SubQueryExpression<Tuple> sq1 = query().from(employee).select(employee.firstname, employee.lastname);
        SubQueryExpression<Tuple> sq2 = query().from(employee).select(employee.firstname, employee.lastname);
        SQLQuery<Void> query = query();
        query.union(sq1, sq2);
        List<String> list = query.select(employee.firstname).list();
        assertFalse(list.isEmpty());
        for (String row : list) {
            assertNotNull(row);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn(DERBY)
    public void Union_Multiple_Columns3() throws SQLException {
        SubQueryExpression<Tuple> sq1 = query().from(employee).select(employee.firstname, employee.lastname);
        SubQueryExpression<Tuple> sq2 = query().from(employee).select(employee.firstname, employee.lastname);
        SQLQuery<Void> query = query();
        query.union(sq1, sq2);
        List<Tuple> list = query.select(employee.lastname, employee.firstname).list();
        assertFalse(list.isEmpty());
        for (Tuple row : list) {
            System.out.println(row.get(0, String.class) + " " + row.get(1, String.class));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Union_Empty_Result() throws SQLException {
        SubQueryExpression<Integer> sq1 = query().from(employee).where(employee.firstname.eq("XXX")).select(employee.id);
        SubQueryExpression<Integer> sq2 = query().from(employee).where(employee.firstname.eq("YYY")).select(employee.id);
        List<Integer> list = query().union(sq1, sq2).list();
        assertTrue(list.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Union2() throws SQLException {
        List<Integer> list = query().union(
                query().from(employee).select(employee.id.max()),
                query().from(employee).select(employee.id.min())).list();
        assertFalse(list.isEmpty());

    }

    @Test
    @SuppressWarnings("unchecked")
    public void Union3() throws SQLException {
        SubQueryExpression<Tuple> sq3 = query().from(employee).select(new Expression[]{employee.id.max()});
        SubQueryExpression<Tuple> sq4 = query().from(employee).select(new Expression[]{employee.id.min()});
        List<Tuple> list2 = query().union(sq3, sq4).list();
        assertFalse(list2.isEmpty());
    }

    @Test
    @ExcludeIn({DERBY})
    public void Union4() {
        SubQueryExpression<Tuple> sq1 = query().from(employee).select(employee.id, employee.firstname);
        SubQueryExpression<Tuple> sq2 = query().from(employee).select(employee.id, employee.firstname);
        query().union(employee, sq1, sq2).select(employee.id.count()).list();
    }

    // FIXME for CUBRID
    // Teradata: The ORDER BY clause must contain only integer constants.
    @Test
    @ExcludeIn({DERBY, CUBRID, FIREBIRD, TERADATA})
    public void Union5() {
        /* (select e.ID, e.FIRSTNAME, superior.ID as sup_id, superior.FIRSTNAME as sup_name
         * from EMPLOYEE e join EMPLOYEE superior on e.SUPERIOR_ID = superior.ID)
         * union
         * (select e.ID, e.FIRSTNAME, null, null from EMPLOYEE e)
         * order by ID asc
         */
        QEmployee superior = new QEmployee("superior");
        SubQueryExpression<Tuple> sq1 = query().from(employee)
                .join(employee.superiorIdKey, superior)
                .select(employee.id, employee.firstname, superior.id.as("sup_id"), superior.firstname.as("sup_name"));
        SubQueryExpression<Tuple> sq2 = query().from(employee)
                .select(employee.id, employee.firstname, null, null);
        List<Tuple> results = query().union(sq1, sq2).orderBy(employee.id.asc()).list();
        for (Tuple result : results) {
            System.err.println(Arrays.asList(result));
        }
    }

    @Test
    @ExcludeIn({FIREBIRD, TERADATA}) // The ORDER BY clause must contain only integer constants.
    @SuppressWarnings("unchecked")
    public void Union_With_Order() throws SQLException {
        SubQueryExpression<Integer> sq1 = query().from(employee).select(employee.id);
        SubQueryExpression<Integer> sq2 = query().from(employee).select(employee.id);
        List<Integer> list = query().union(sq1, sq2).orderBy(employee.id.asc()).list();
        assertFalse(list.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn(FIREBIRD)
    public void Union_Multi_Column_Projection_List() throws IOException{
        SubQueryExpression<Tuple> sq1 = query().from(employee).select(employee.id.max(), employee.id.max().subtract(1));
        SubQueryExpression<Tuple> sq2 = query().from(employee).select(employee.id.min(), employee.id.min().subtract(1));

        List<Tuple> list = query().union(sq1, sq2).list();
        assertEquals(2, list.size());
        assertTrue(list.get(0) != null);
        assertTrue(list.get(1) != null);
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn(FIREBIRD)
    public void Union_Multi_Column_Projection_Iterate() throws IOException{
        SubQueryExpression<Tuple> sq1 = query().from(employee).select(employee.id.max(), employee.id.max().subtract(1));
        SubQueryExpression<Tuple> sq2 = query().from(employee).select(employee.id.min(), employee.id.min().subtract(1));

        CloseableIterator<Tuple> iterator = query().union(sq1,sq2).iterate();
        try{
            assertTrue(iterator.hasNext());
            assertTrue(iterator.next() != null);
            assertTrue(iterator.next() != null);
            assertFalse(iterator.hasNext());
        }finally{
            iterator.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void Union_Single_Column_Projections_List() throws IOException{
        SubQueryExpression<Integer> sq1 = query().from(employee).select(employee.id.max());
        SubQueryExpression<Integer> sq2 = query().from(employee).select(employee.id.min());

        List<Integer> list = query().union(sq1, sq2).list();
        assertEquals(2, list.size());
        assertTrue(list.get(0) != null);
        assertTrue(list.get(1) != null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void Union_Single_Column_Projections_Iterate() throws IOException{
        SubQueryExpression<Integer> sq1 = query().from(employee).select(employee.id.max());
        SubQueryExpression<Integer> sq2 = query().from(employee).select(employee.id.min());

        CloseableIterator<Integer> iterator = query().union(sq1,sq2).iterate();
        try{
            assertTrue(iterator.hasNext());
            assertTrue(iterator.next() != null);
            assertTrue(iterator.next() != null);
            assertFalse(iterator.hasNext());
        }finally{
            iterator.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void Union_FactoryExpression() {
        SubQueryExpression<Employee> sq1 = query().from(employee)
                .select(Projections.constructor(Employee.class, employee.id));
        SubQueryExpression<Employee> sq2 = query().from(employee)
                .select(Projections.constructor(Employee.class, employee.id));
        List<Employee> employees = query().union(sq1, sq2).list();
        for (Employee employee : employees) {
            assertNotNull(employee);
        }
    }

    @Test
    @ExcludeIn({DERBY, CUBRID})
    public void Union_Clone() {
        NumberPath<Integer> idAlias = Expressions.numberPath(Integer.class, "id");
        SubQueryExpression<Employee> sq1 = query().from(employee)
                .select(Projections.constructor(Employee.class, employee.id.as(idAlias)));
        SubQueryExpression<Employee> sq2 = query().from(employee)
                .select(Projections.constructor(Employee.class, employee.id.as(idAlias)));

        SQLQuery<Void> query = query();
        query.union(sq1, sq2);
        query.clone().select(idAlias).list();
    }

}
