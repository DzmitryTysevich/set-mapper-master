package com.epam.rd.autocode;

import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigInteger;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class SetMapperImpl implements SetMapper<Set<Employee>> {

    private static final String ID = "id";
    private static final String FIRST_NAME = "firstname";
    private static final String LAST_NAME = "lastname";
    private static final String MIDDLE_NAME = "middleName";
    private static final String POSITION = "position";
    private static final String HIREDATE = "hiredate";
    private static final String SALARY = "salary";
    private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
    private static final String URL = "jdbc:hsqldb:mem:myDb";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";
    private static final String SQL_SELECT = "select * from EMPLOYEE E left join EMPLOYEE N on E.MANAGER = N.ID";
    private static final String MANAGER = "manager";

    @Override
    public Set<Employee> mapSet(ResultSet resultSet) {
        Set<Employee> employees = new HashSet<>();
        try {
            while (resultSet.next()) {
                employees.add(getEmployee(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private Employee getEmployee(ResultSet resultSet) throws SQLException {
        return new Employee(
                new BigInteger(String.valueOf(resultSet.getInt(ID))),
                new FullName(
                        resultSet.getString(FIRST_NAME),
                        resultSet.getString(LAST_NAME),
                        resultSet.getString(MIDDLE_NAME)),
                Position.valueOf(resultSet.getString(POSITION)),
                resultSet.getDate(HIREDATE).toLocalDate(),
                resultSet.getBigDecimal(SALARY),
                getManager(resultSet)
        );
    }

    private Employee getManager(ResultSet resultSet) throws SQLException {
        Employee manager = null;
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SQL_SELECT);
            while (rs.next()) {
                if (resultSet.getInt(MANAGER) == (rs.getInt(ID))) {
                    manager = new Employee(
                            new BigInteger(String.valueOf(rs.getInt(ID))),
                            new FullName(
                                    rs.getString(FIRST_NAME),
                                    rs.getString(LAST_NAME),
                                    rs.getString(MIDDLE_NAME)),
                            Position.valueOf(rs.getString(POSITION)),
                            rs.getDate(HIREDATE).toLocalDate(),
                            rs.getBigDecimal(SALARY),
                            getManager(rs)
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return manager;
    }
}