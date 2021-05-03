package com.epam.rd.autocode;

import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigInteger;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class SetMapperImpl implements SetMapper<Set<Employee>> {
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
                new BigInteger(String.valueOf(resultSet.getInt("id"))),
                new FullName(
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getString("middleName")),
                Position.valueOf(resultSet.getString("position")),
                resultSet.getDate("hiredate").toLocalDate(),
                resultSet.getBigDecimal("salary"),
                getManager(resultSet)
        );
    }

    private Employee getManager(ResultSet resultSet) throws SQLException {
        Employee manager = null;
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:hsqldb:mem:myDb",
                    "sa", "sa"
            );
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from EMPLOYEE E left join EMPLOYEE N on E.MANAGER = N.ID");
            while (rs.next()) {
                if (resultSet.getInt("manager") == (rs.getInt("id"))) {
                    manager = new Employee(
                            new BigInteger(String.valueOf(rs.getInt("id"))),
                            new FullName(
                                    rs.getString("firstname"),
                                    rs.getString("lastname"),
                                    rs.getString("middleName")),
                            Position.valueOf(rs.getString("position")),
                            rs.getDate("hiredate").toLocalDate(),
                            rs.getBigDecimal("salary"),
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