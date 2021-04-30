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
                null
        );
    }
}