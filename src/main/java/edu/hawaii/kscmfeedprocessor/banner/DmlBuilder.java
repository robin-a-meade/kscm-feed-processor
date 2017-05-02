package edu.hawaii.kscmfeedprocessor.banner;


import com.google.common.collect.ObjectArrays;

import java.util.*;

import static java.lang.String.join;

/**
 *  Build an SQL INSERT command along with the parameter values array to be used with it. Guarantees
 *  that the column names and values match up.
 *
 *  The JDBC driver is configured to implicitly cache SQL statements, so generating the sql isn't as bad as it sounds.
 *  TODO: investigate using DAO/hibernate instead.
 */
public class DmlBuilder {

    private static String commaSeparatedQuestionMarks(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append('?');
        }
        return sb.toString();
    }

    List<String> colNameList = new ArrayList<>();
    List<Object> paramValList = new ArrayList<>();

    Map<String, Object> colValMap = new HashMap<>();

    String tableName;

    /**
     *  Constructor
     */
    public DmlBuilder(String tableName) {
        this.tableName = tableName;
    }

    public void add(String colName, Object value) {
        colNameList.add(colName);
        paramValList.add(value);
    }

    public void clear() {
        colNameList.clear();
        paramValList.clear();
    }

    public String getWhereClause() {
        StringBuilder sb = new StringBuilder();
        sb.append("WHERE ");
        for (int i = 0; i < colNameList.size(); i++) {
            if (i > 0) {
                sb.append(" AND ");
            }
            sb.append(colNameList.get(i) + " = ?");
        }
        return sb.toString();
    }

    public String getInsert() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(tableName);
        sb.append(" (\n");
        sb.append(join(", ", colNameList));
        sb.append("\n) VALUES (");
        sb.append(commaSeparatedQuestionMarks(colNameList.size()));
        sb.append(")");
        return sb.toString();
    }

    public String getUpdate() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(tableName);
        sb.append("\nSET ");
        for (int i = 0; i < colNameList.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(colNameList.get(i) + " = ?");
        }
        return sb.toString();
    }

    public String getDelete() {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE ");
        sb.append(tableName);
        return sb.toString();
    }

    public Object[] getParamValueArray() {
        return paramValList.toArray(new Object[1]);
    }

    public static void main(String[] args) {
        DmlBuilder ib = new DmlBuilder("SCBCRSE");
        ib.add("SCRCRSE_TITLE", "New title");
        ib.add("SCRCRSE_APRV_CODE", "A");
        ib.add("SCRCRSE_CSTA_CODE", "E");
        System.out.println(ib.getInsert());
        Object[] paramValues = ib.getParamValueArray();
        System.out.println(Arrays.toString(paramValues));
        System.out.println(ib.getUpdate());
        ib.clear();
        ib.add("SCRCRSE_SUBJ_CODE", "ACC");
        ib.add("SCRCRSE_CRSE_NUMB", "996");
        ib.add("SCRCRSE_EFF_TERM", "201710");
        System.out.println(ib.getWhereClause());
        paramValues = ObjectArrays.concat(paramValues, ib.getParamValueArray(), Object.class);
        System.out.println(Arrays.toString(paramValues));
    }

}
