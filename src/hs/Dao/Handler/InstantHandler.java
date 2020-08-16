package hs.Dao.Handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.time.Instant;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/4 12:56
 */
public class InstantHandler implements TypeHandler<Instant> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Instant parameter, JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, Timestamp.from(parameter));
    }

    @Override
    public Instant getResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp=rs.getTimestamp(columnName);
        return  timestamp.toInstant();
    }

    @Override
    public Instant getResult(ResultSet rs, int columnIndex) throws SQLException {

        return  rs.getTimestamp(columnIndex).toInstant();
    }

    @Override
    public Instant getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getTimestamp(columnIndex).toInstant();
    }
}
