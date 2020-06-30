package com.fuint.coupon.dao.generator;

import org.apache.commons.lang.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 由数据库表生成jpa实体工具
 * Created by zach on 2019/5/25.
 */
public class GenEntityMysql {
    //数据库连接
    private static final String URL = "jdbc:mysql://localhost:3306/fuint-coupon-db";
    private static final String NAME = "root";
    private static final String PASS = "";

    private String entityPackage = "com.fuint.coupon.dao.entities";//指定实体生成所在包的路径
    private String repositoryPackage = "com.fuint.coupon.dao.repositories";//指定实体仓库接口生成所在包的路径
    private String authorName = "zach";//作者名字
    private String tableName = "mt_coupon";//表名

    private List<String> colNames = new ArrayList<>(); // 列名数组
    private List<String> colTypes = new ArrayList<>(); //列名类型数组
    private List<Integer> colSizes = new ArrayList<>(); //列名大小数组
    private List<String> colExtras = new ArrayList<>();//列补充说明
    private List<String> colComment = new ArrayList<>();//列注释
    private List<String> colNulls = new ArrayList<>();
    private boolean f_util = false; // 是否需要导入包java.util.*
    private boolean f_sql = false; // 是否需要导入包java.sql.*
    private boolean f_bigDecimal = false;// 是否需要导入包java.math.BigDecimal

    private static final String DRIVER = "com.mysql.jdbc.Driver";

    private static final String AUTO = "auto_increment";

    private static final String NO = "NO";

    /**
     * 构造函数
     */
    public GenEntityMysql() {
        //empty
    }

    /**
     * 执行函数
     */
    public static void main(String[] args) {
        GenEntityMysql genEntityMysql = new GenEntityMysql();
        genEntityMysql.generateEntry();
    }

    /**
     * 功能：生成实体类主体代码
     *
     * @return
     */
    private String parseEntity() {
        StringBuffer sb = new StringBuffer();


        sb.append("package " + this.entityPackage + ";\r\n\r\n");
        sb.append("import javax.persistence.Entity;" + "\r\n");
        sb.append("import javax.persistence.Table;" + "\r\n");
        sb.append("import javax.persistence.Column;" + "\r\n");
        sb.append("import javax.persistence.Id;" + "\r\n");
        sb.append("import javax.persistence.GeneratedValue;" + "\r\n");
        sb.append("import javax.persistence.GenerationType;" + "\r\n");
        sb.append("import java.io.Serializable;" + "\r\n");

        //判断是否导入工具包
        if (f_util) {
            sb.append("import java.util.Date;\r\n");
        }
        if (f_sql) {
            sb.append("import java.sql.*;\r\n");
        }
        if (f_bigDecimal) {
            sb.append("import java.math.BigDecimal;\r\n");
        }

        sb.append("\r\n");
        //注释部分
        sb.append("   /**\r\n");
        sb.append("    * " + tableName + " 实体类\r\n");
        sb.append("    * Created by " + this.authorName + "\r\n");
        sb.append("    * " + new Date() + "\r\n");
        sb.append("    */ \r\n");
        //实体部分
        sb.append("@Entity " + "\r\n");
        sb.append("@Table(name = \"" + tableName + "\")" + "\r\n");

        sb.append("public class " + this.allInitialCapital(tableName) + " implements Serializable{\r\n");
        processAllAttrs(sb);//属性
        processAllMethod(sb);//get set方法
        sb.append("}\r\n");

        return sb.toString();
    }

    /**
     * 功能：生成repository代码
     *
     * @return
     */
    private String parseRepository() {
        StringBuffer sb = new StringBuffer();


        sb.append("package " + this.repositoryPackage + ";\r\n\r\n");
        sb.append("import com.fuint.base.dao.BaseRepository;" + "\r\n");
        sb.append("import org.springframework.stereotype.Repository;" + "\r\n");
        sb.append("import " + this.entityPackage + "." + this.allInitialCapital(tableName) + ";" + "\r\n");
        sb.append("\r\n");
        //注释部分
        sb.append("   /**\r\n");
        sb.append("    * " + tableName + " Repository\r\n");
        sb.append("    * Created by " + this.authorName + "\r\n");
        sb.append("    * " + new Date() + "\r\n");
        sb.append("    */ \r\n");
        //实体部分
        sb.append("@Repository " + "\r\n");

        sb.append("public interface " + this.allInitialCapital(tableName) + "Repository extends BaseRepository<" + this.allInitialCapital(tableName) + ", Integer> {\r\n");
        sb.append("}\r\n");

        return sb.toString();
    }

    /**
     * 功能：生成所有属性
     *
     * @param sb
     */
    private void processAllAttrs(StringBuffer sb) {

        for (int i = 0; i < colNames.size(); i++) {
            //注释部分
            sb.append("   /**\r\n");
            sb.append("    * " + colComment.get(i) + " \r\n");
            sb.append("    */ \r\n");
            if (AUTO.equalsIgnoreCase(colExtras.get(i))) {
                sb.append("\t@Id" + "\r\n");
                sb.append("\t@GeneratedValue(strategy = GenerationType.IDENTITY)" + "\r\n");
            }
            sb.append("\t@Column(name = \"" + colNames.get(i) + "\"");
            if (NO.equalsIgnoreCase(colNulls.get(i))) {
                sb.append(", nullable = false");
            }
            if (colSizes.get(i) > 0) {
                sb.append(", length = " + colSizes.get(i));
            }
            sb.append(")" + "\r\n");
            sb.append("\tprivate " + sqlType2JavaType(colTypes.get(i)) + " " + this.secInitialCapital(colNames.get(i)) + ";\r\n\r\n");
        }
    }

    /**
     * 功能：生成所有方法
     *
     * @param sb
     */
    private void processAllMethod(StringBuffer sb) {

        for (int i = 0; i < colNames.size(); i++) {
            sb.append("\tpublic " + sqlType2JavaType(colTypes.get(i)) + " get" + this.allInitialCapital(colNames.get(i)) + "(){\r\n");
            sb.append("\t\treturn " + this.secInitialCapital(colNames.get(i)) + ";\r\n");
            sb.append("\t}\r\n");
            sb.append("\tpublic void set" + this.allInitialCapital(colNames.get(i)) + "(" + sqlType2JavaType(colTypes.get(i)) + " " +
                    this.secInitialCapital(colNames.get(i)) + "){\r\n");
            sb.append("\tthis." + this.secInitialCapital(colNames.get(i)) + "=" + this.secInitialCapital(colNames.get(i)) + ";\r\n");
            sb.append("\t}\r\n");
        }

    }

    /**
     * 功能：将输入字符串的首字母改成大写
     *
     * @param str
     * @return
     */
    private String initialCapital(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 所有字母转成小写
     *
     * @return
     */
    private String allLowerCase(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.toLowerCase();
    }

    /**
     * 分解名称
     *
     * @param str
     * @return
     */
    private String[] splitName(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return str.split("_");
    }

    /**
     * 由数据库表名生成实体类名
     *
     * @param tableName
     * @return
     */
    private String allInitialCapital(String tableName) {
        if (StringUtils.isEmpty(tableName)) {
            return null;
        }
        tableName = this.allLowerCase(tableName);
        String[] tableNameArray = this.splitName(tableName);
        StringBuffer entryName = new StringBuffer();
        for (String part : tableNameArray) {
            entryName.append(this.initialCapital(part));
        }
        return entryName.toString();
    }

    /**
     * 由数据库列名生成实体类属性名
     *
     * @param columnName
     * @return
     */
    private String secInitialCapital(String columnName) {
        if (StringUtils.isEmpty(columnName)) {
            return null;
        }
        columnName = this.allLowerCase(columnName);
        String[] columnNameArray = this.splitName(columnName);
        StringBuffer fieldName = new StringBuffer();
        for (int i = 0; i < columnNameArray.length; i++) {
            String part = columnNameArray[i];
            if (0 == i) {
                fieldName.append(part);
            } else {
                fieldName.append(this.initialCapital(part));
            }
        }
        return fieldName.toString();
    }

    /**
     * 功能：获得列的数据类型
     *
     * @param sqlType
     * @return
     */
    private String sqlType2JavaType(String sqlType) {

        if (sqlType.equalsIgnoreCase("bit")) {
            return "Boolean";
        } else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "Byte";
        } else if (sqlType.equalsIgnoreCase("smallint")) {
            return "Short";
        } else if (sqlType.equalsIgnoreCase("int")) {
            return "Integer";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "Long";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "Float";
        } else if (sqlType.equalsIgnoreCase("decimal")) {
            return "BigDecimal";
        } else if (sqlType.equalsIgnoreCase("numeric")
                || sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")) {
            return "Double";
        } else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
                || sqlType.equalsIgnoreCase("text")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("datetime")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("image")) {
            return "Blod";
        }

        return null;
    }

    /**
     * 生成实体
     */
    public void generateEntry() {
        //
        this.getTableAttribute();

        String entityContent = this.parseEntity();
        this.writerEntityToFile(entityContent);
        String repositoryContent = this.parseRepository();
        this.writerRepositoryToFile(repositoryContent);
    }

    /**
     * 写入文件
     *
     * @param content
     */
    private void writerRepositoryToFile(String content) {
        try {
            String absolutePath = this.getClass().getResource("").getPath();
            absolutePath = absolutePath.substring(0, absolutePath.indexOf("target"));
            String outputPath = absolutePath + "src/main/java/" + this.repositoryPackage.replace(".", "/") + "/"
                    + this.allInitialCapital(tableName) + "Repository" + ".java";
            System.out.println("Repository路径：" + outputPath);
            FileWriter fw = new FileWriter(outputPath);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 写入文件
     *
     * @param content
     */
    private void writerEntityToFile(String content) {
        try {
            String absolutePath = this.getClass().getResource("").getPath();
            absolutePath = absolutePath.substring(0, absolutePath.indexOf("target"));
            String outputPath = absolutePath + "src/main/java/" + this.entityPackage.replace(".", "/") + "/"
                    + allInitialCapital(tableName) + ".java";
            System.out.println("Entity路径：" + outputPath);
            FileWriter fw = new FileWriter(outputPath);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库信息
     */
    private void getTableAttribute() {
        //创建连接
        Connection con = null;
        //查要生成实体类的表
        String sql = "show full columns from " + tableName;
        Statement state = null;
        try {
            try {
                Class.forName(DRIVER);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            con = DriverManager.getConnection(URL, NAME, PASS);
            state = con.createStatement();
            ResultSet rs = state.executeQuery(sql);
//            ResultSetMetaData metaData = rs.getMetaData();

            while (rs.next()) {
                colNames.add(rs.getString("Field"));
                String type = rs.getString("Type");
                String comment = rs.getString("Comment");
                String extra = rs.getString("Extra");
                String colNull = rs.getString("Null");
                int colSize = 0;
                if (type.contains("(")) {
                    String colSizeStr = type.substring(type.indexOf("(") + 1, type.indexOf(")"));
                    type = type.substring(0, type.indexOf("("));
                    try {
                        colSize = Integer.valueOf(colSizeStr);
                    } catch (NumberFormatException e) {

                    }
                }

                colTypes.add(type);
                colSizes.add(colSize);
                colComment.add(comment);
                colExtras.add(extra);
                colNulls.add(colNull);
                if (type.equalsIgnoreCase("datetime")) {
                    f_util = true;
                }
                if (type.equalsIgnoreCase("image") || type.equalsIgnoreCase("text")) {
                    f_sql = true;
                }
                if (type.equalsIgnoreCase("decimal")) {
                    f_bigDecimal = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
