package com.cyberai.util;



import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;

public class DbUtils {

    public static Connection getMysqlConnection(String dbUrl,String dbUser,String dbPwd){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
        }catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }


    public static JSONArray executeQuery(Connection conn,String sql) throws SQLException {
        Statement st = null;
        ResultSet rs=null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            JSONArray list = convertList(rs);
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void execute(Connection conn,String [] sqlList) {
        Statement st = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            st = conn.createStatement();
            for (String sql : sqlList) {
                st.addBatch(sql);
            }
            st.executeBatch();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    private static JSONArray convertList (ResultSet rs) throws SQLException, JSONException {
        //List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ResultSetMetaData md = rs.getMetaData();// 获取键名
        int columnCount = md.getColumnCount();// 获取行的数量
        JSONArray list=new JSONArray();
        while (rs.next()) {
            //Map<String, Object> rowData = new HashMap<String, Object>();// 声明Map
            JSONObject rowData=new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));// 获取键名及值
            }
            list.put(rowData);
        }
        return list;
    }

    private static String xpath2jsonpath(String path) {
        if (!path.startsWith("$")) {
            String p = path.replaceAll("/", ".");
            return p.startsWith(".") ? "$" + p : "$." + p;
        }
        return path;
    }


    public static void  main(String[] args) throws IOException, SQLException {
        String dbUrl="jdbc:mysql://47.99.121.236:3306/jingrobot-test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&autoReconnectForPools=true&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useLocalSessionState=true";
        Connection con=getMysqlConnection(dbUrl,"byrobot","Indata%8301p");
        JSONArray res=executeQuery(con,"select * from dev_decision limit 10");
        String jsonPath="/[8]/name";
        System.out.println(res.toString());
        String a=JsonPath.read(res.toString(),xpath2jsonpath(jsonPath)).toString();
        System.out.println(a);
    }
}
