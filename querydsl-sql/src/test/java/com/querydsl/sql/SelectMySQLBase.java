package com.querydsl.sql;

import static com.querydsl.sql.Constants.survey;
import static com.querydsl.core.Target.MYSQL;

import org.junit.Test;

import com.querydsl.sql.mysql.MySQLQuery;
import com.querydsl.core.testutil.IncludeIn;


public class SelectMySQLBase extends AbstractBaseTest {

    protected MySQLQuery<Void> mysqlQuery() {
        return new MySQLQuery<Void>(connection, configuration);
    }

    @Test
    @IncludeIn(MYSQL)
    public void MySQL_Extensions() {
        mysqlQuery().from(survey).bigResult().select(survey.id).list();
        mysqlQuery().from(survey).bufferResult().select(survey.id).list();
        mysqlQuery().from(survey).cache().select(survey.id).list();
        mysqlQuery().from(survey).calcFoundRows().select(survey.id).list();
        mysqlQuery().from(survey).noCache().select(survey.id).list();

        mysqlQuery().from(survey).highPriority().select(survey.id).list();
        mysqlQuery().from(survey).lockInShareMode().select(survey.id).list();
        mysqlQuery().from(survey).smallResult().select(survey.id).list();
        mysqlQuery().from(survey).straightJoin().select(survey.id).list();
    }

}
