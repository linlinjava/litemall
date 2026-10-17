package org.linlinjava.litemall.db.mybatis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Integer 数组与 JSON 字符串之间的 MyBatis 类型处理器。
 *
 * <p>用于把数据库中以 JSON 形式存储的整型数组列映射为 Java 的 Integer[]，
 * 例如 litemall_admin.role_ids、litemall_topic.goods、litemall_coupon.goods_value。
 * 需要在 mybatis-generator/generatorConfig.xml 中通过 columnOverride 指定：
 *
 * <pre>
 * &lt;columnOverride column="role_ids" javaType="java.lang.Integer[]"
 *     typeHandler="org.linlinjava.litemall.db.mybatis.JsonIntegerArrayTypeHandler"/&gt;
 * </pre>
 *
 * <p>注意：序列化失败时 toJson 返回 "[]" 而不是抛异常，可能与“确实为空数组”混淆。
 *
 * @author lvweihao0403
 * @since 1.0.0
 */
public class JsonIntegerArrayTypeHandler extends BaseTypeHandler<Integer[]> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Integer[] parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public Integer[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public Integer[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public Integer[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(Integer[] params) {
        try {
            return mapper.writeValueAsString(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "[]";
    }

    private Integer[] toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return (Integer[]) mapper.readValue(content, Integer[].class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }
}