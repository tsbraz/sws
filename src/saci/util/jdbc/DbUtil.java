/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2009 SACI Informática Ltda.
 */

package saci.util.jdbc;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import saci.util.Cache;
import saci.util.SimpleCache;

/**
 * Classe utilitária para facilitar o uso de JDBC.
 * <p>
 *       try {
 *           DbUtil dbUtil = new DbUtil(conn);
 *           return dbUtil.execute(
 *               "\nSELECT" +
 *               "\n  cpf_cgc AS cpfCgc," +
 *               "\n  nome," +
 *               "\n  tipo," +
 *               "\n  razao_social AS razaoSocial," +
 *               "\n  endereco," +
 *               "\n  cidade," +
 *               "\n  estado," +
 *               "\n  pais" +
 *               "\nFROM" +
 *               "\n  cliente c" +
 *               "\nWHERE" +
 *               "\n  nome LIKE ?" +
 *               "\nORDER BY" +
 *               "\n  nome"
 *           ).addParameter("%" + nome + "%")
 *            .list(Cliente.class);
 *       } catch (Exception e) {
 *           throw new DataException(e);
 *       }
 *
 * @author Thiago
 */
public class DbUtil {

    private static Cache<Class<?>, Map<String, AccessorMap>> beanMap = new SimpleCache<Class<?>, Map<String, AccessorMap>>();
    static Logger logger = Logger.getLogger(DbUtil.class.getName());
    private Connection connection;
    private boolean datasource = false;

    protected class AccessorMap {

        Method method;
        Field field;
        Class<?> paramType;

    }

    /**
     * Cria um nova instancia sem informações do banco de dados
     */
    public DbUtil() {
    }

    /**
     * Cria uma nova instância com a conexão passada por parametro
     * 
     * @param conn
     */
    public DbUtil(Connection conn) {
        this.connection = conn;
    }

    /**
     * Cria uma nova instância com o datasource passado por parametro
     * 
     * @param conn
     */
    public DbUtil(DataSource datasource) throws SQLException {
        this(datasource.getConnection());
        this.datasource = true;
    }

    /**
     * Cria uma nova instância com o jndi do datasource passado por parametro
     * 
     * @param conn
     */
    public DbUtil(String datasourceJndi) throws SQLException {
        try {
            DataSource datasource = (DataSource) new InitialContext().lookup(datasourceJndi);
            this.connection = datasource.getConnection();
            this.datasource = true;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    <T> List<T> list(Query query, Class<T> voClass) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = query.getPreparedStatement();
            long ini = System.currentTimeMillis();
            rs = stmt.executeQuery();
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Tempo da query: " + (System.currentTimeMillis() - ini));
            }
            List<T> result = new ArrayList<T>();
            if (isPrintable(voClass)) {
                while (rs.next()) {
                    result.add(saci.util.Types.cast(rs.getObject(1), voClass));
                }
            } else {
                while (rs.next()) {
                    T vo = voClass.newInstance();
                    fillBean(vo, rs);
                    query.callListeners(vo, rs);
                    result.add(vo);
                }
            }
            return result;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            closeStatement(stmt);
            closeResultSet(rs);
            closeConnectionDS();
        }
    }
    
    private boolean isPrintable(Class<?> value) {
        return saci.util.Types.isPrintable(value) || InputStream.class.isAssignableFrom(value) || byte[].class.equals(value);
    }

    <T> T get(Query query, Class<T> voClass) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = query.getPreparedStatement();
            long ini = System.currentTimeMillis();
            rs = stmt.executeQuery();
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Tempo da query: " + (System.currentTimeMillis() - ini));
            }
            if (isPrintable(voClass)) {
                if (rs.next()) {
                    return saci.util.Types.cast(rs.getObject(1), voClass);
                }
            } else {
                if (rs.next()) {
                    T vo = voClass.newInstance();
                    fillBean(vo, rs);
                    query.callListeners(vo, rs);
                    return vo;
                }
            }
            return null;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            closeStatement(stmt);
            closeResultSet(rs);
            closeConnectionDS();
        }
    }

    boolean get(Query query, Object vo) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = query.getPreparedStatement();
            long ini = System.currentTimeMillis();
            rs = stmt.executeQuery();
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Tempo da query: " + (System.currentTimeMillis() - ini));
            }
            if (rs.next()) {
                fillBean(vo, rs);
                query.callListeners(vo, rs);
                return true;
            }
            return false;
        } finally {
            closeStatement(stmt);
            closeResultSet(rs);
            closeConnectionDS();
        }
    }

    int update(Query query) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = query.getPreparedStatement();
            long ini = System.currentTimeMillis();
            int result = stmt.executeUpdate();
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Tempo do update: " + (System.currentTimeMillis() - ini));
            }
            return result;
        } finally {
            closeStatement(stmt);
            closeConnectionDS();
        }
    }

    /**
     * Cria uma nova query baseada no comando passado por parametro.
     * 
     * @param query
     *            a query que deve ser executada
     * @return a nova query gerada
     * @throws SQLException
     *             caso ocorra algum erro de SQLException
     */
    public Query execute(String query) throws SQLException {
        assert (connection != null);
        return new Query(this, query, connection);
    }

    /**
     * Preenche o objeto passado por parametro, baseado nas informações do
     * <i>ResultSet</i>
     * 
     * @param bean
     *            o objeto que deve ser preenchido
     * @param rs
     *            o ResultSet que contém as informações
     * @throws SQLException
     *             caso ocorra algum erro de SQLException
     */
    public void fillBean(Object bean, ResultSet rs) throws SQLException {
        Map<String, AccessorMap> fields = beanMap(bean.getClass(), rs.getMetaData());
        Iterator<String> it = fields.keySet().iterator();
        Object[] param = new Object[1];
        while (it.hasNext()) {
            String campo = it.next();
            AccessorMap accessorMap = fields.get(campo);
            if (accessorMap != null) {
                Class<?> paramClass = accessorMap.paramType;
                try {
                    setAttributeValue(bean, rs, param, campo, accessorMap, paramClass);
                } catch (SQLException e) {
                    System.err.println(campo + " " + rs.getObject(campo) + "\n" + e.toString());
                    throw e;
                }
            }
        }
    }

    private void setAttributeValue(Object bean, ResultSet rs, Object[] param, String campo, AccessorMap accessorMap,
            Class<?> paramClass) throws SQLException {
        readValue(rs, param, campo, paramClass);
        setValue(accessorMap, bean, param);
    }

    private void readValue(ResultSet rs, Object[] param, String campo, Class<?> paramClass) throws SQLException {
        if (paramClass.equals(Integer.class) || paramClass.equals(Integer.TYPE)) {
            readInteger(rs, param, campo, paramClass);
        } else if (paramClass.equals(Long.class) || paramClass.equals(Long.TYPE)) {
            readLong(rs, param, campo, paramClass);
        } else if (paramClass.equals(Float.class) || paramClass.equals(Float.TYPE)) {
            readFloat(rs, param, campo, paramClass);
        } else if (paramClass.equals(Double.class) || paramClass.equals(Double.TYPE)) {
            readDouble(rs, param, campo, paramClass);
        } else if (paramClass.equals(Boolean.class) || paramClass.equals(Boolean.TYPE)) {
            readBoolean(rs, param, campo, paramClass);
        } else if (paramClass.equals(String.class)) {
            readString(rs, param, campo);
        } else if (paramClass.equals(BigDecimal.class)) {
            readBigDecimal(rs, param, campo);
        } else if (paramClass.equals(BigInteger.class)) {
            readBigInteger(rs, param, campo);
        } else if (Date.class.isAssignableFrom(paramClass)) {
            readDate(rs, param, campo);
        } else if (InputStream.class.isAssignableFrom(paramClass) || byte[].class.equals(paramClass)) {
            readInputStream(rs, param, campo, paramClass);
        } else {
            throw new SQLException("Invalid data type " + paramClass);
        }
    }

    private void readInputStream(ResultSet rs, Object[] param, String campo, Class<?> paramClass) throws SQLException {
        InputStream in = getInputStream(campo, rs);
        param[0] = saci.util.Types.cast(in, paramClass);
    }

    private void readDate(ResultSet rs, Object[] param, String campo) throws SQLException {
        param[0] = getDate(campo, rs);
    }

    private void readBigInteger(ResultSet rs, Object[] param, String campo) throws SQLException {
        param[0] = getBigInteger(campo, rs);
    }

    private void readBigDecimal(ResultSet rs, Object[] param, String campo) throws SQLException {
        param[0] = getBigDecimal(campo, rs);
    }

    private void readString(ResultSet rs, Object[] param, String campo) throws SQLException {
        Object o = rs.getObject(campo);
        if (rs.wasNull()) {
            param[0] = null;
        } else {
            if (o instanceof Date) {
                param[0] = saci.util.Types.parseString((Date) o);
            } else {
                param[0] = o.toString();
            }
        }
    }

    private void readBoolean(ResultSet rs, Object[] param, String campo, Class<?> paramClass) throws SQLException {
        param[0] = getBoolean(campo, rs);
        if (param[0] == null && paramClass.equals(Boolean.TYPE)) {
            param[0] = Boolean.FALSE;
        }
    }

    private void readDouble(ResultSet rs, Object[] param, String campo, Class<?> paramClass) throws SQLException {
        param[0] = getDouble(campo, rs);
        if (param[0] == null && paramClass.equals(Double.TYPE)) {
            param[0] = new Double(0);
        }
    }

    private void readFloat(ResultSet rs, Object[] param, String campo, Class<?> paramClass) throws SQLException {
        param[0] = getFloat(campo, rs);
        if (param[0] == null && paramClass.equals(Float.TYPE)) {
            param[0] = new Float(0);
        }
    }

    private void readLong(ResultSet rs, Object[] param, String campo, Class<?> paramClass) throws SQLException {
        param[0] = getLong(campo, rs);
        if (param[0] == null && paramClass.equals(Long.TYPE)) {
            param[0] = new Long(0);
        }
    }

    private void readInteger(ResultSet rs, Object[] param, String campo, Class<?> paramClass) throws SQLException {
        param[0] = getInt(campo, rs);
        if (param[0] == null && paramClass.equals(Integer.TYPE)) {
            param[0] = new Integer(0);
        }
    }

    private void setValue(AccessorMap accessorMap, Object bean, Object[] param) throws IllegalArgumentException,
            SQLException {
        if (accessorMap.method != null) {
            try {
                accessorMap.method.invoke(bean, param);
            } catch (IllegalAccessException e) {
                throw new SQLException(e.toString());
            } catch (InvocationTargetException e) {
                throw new SQLException(e.toString());
            }
        } else {
            try {
                accessorMap.field.set(bean, param[0]);
            } catch (IllegalArgumentException e) {
                throw new SQLException(e.toString());
            } catch (IllegalAccessException e) {
                throw new SQLException(e.toString());
            }
        }
    }

    public Integer getInt(String field, ResultSet rs) throws SQLException {
        int val = rs.getInt(field);
        if (rs.wasNull()) {
            return null;
        } else {
            return new Integer(val);
        }
    }

    public Long getLong(String field, ResultSet rs) throws SQLException {
        long val = rs.getLong(field);
        if (rs.wasNull()) {
            return null;
        } else {
            return new Long(val);
        }
    }

    public Float getFloat(String field, ResultSet rs) throws SQLException {
        float val = rs.getFloat(field);
        if (rs.wasNull()) {
            return null;
        } else {
            return new Float(val);
        }
    }

    public Double getDouble(String field, ResultSet rs) throws SQLException {
        double val = rs.getDouble(field);
        if (rs.wasNull()) {
            return null;
        } else {
            return new Double(val);
        }
    }

    public Date getDate(String field, ResultSet rs) throws SQLException {
        java.sql.Timestamp val = rs.getTimestamp(field);
        if (rs.wasNull()) {
            return null;
        } else {
            return val;
        }
    }

    public String getString(String field, ResultSet rs) throws SQLException {
        String val = rs.getString(field);
        if (rs.wasNull()) {
            return null;
        } else {
            return val;
        }
    }

    public Boolean getBoolean(String field, ResultSet rs) throws SQLException {
        String s = getString(field, rs);
        if (s != null) {
            return s.equals("S") || s.equals("true") || s.equals("1") ? Boolean.TRUE : Boolean.FALSE;
        } else {
            return null;
        }
    }

    public BigDecimal getBigDecimal(String field, ResultSet rs) throws SQLException {
        BigDecimal s = rs.getBigDecimal(field);
        if (s != null) {
            return s;
        } else {
            return null;
        }
    }

    public BigInteger getBigInteger(String field, ResultSet rs) throws SQLException {
        BigDecimal b = getBigDecimal(field, rs);
        return b == null ? null : b.toBigInteger();
    }

    public InputStream getInputStream(String field, ResultSet rs) throws SQLException {
        InputStream in = rs.getBinaryStream(field);
        if (rs.wasNull()) {
            return null;
        } else {
            return in;
        }
    }

    public void setInt(int i, Integer value, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.NUMERIC);
        } else {
            stmt.setInt(i, value.intValue());
        }
    }

    public void setShort(int i, Short value, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.NUMERIC);
        } else {
            stmt.setInt(i, value.shortValue());
        }
    }

    public void setByte(int i, Byte value, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.NUMERIC);
        } else {
            stmt.setByte(i, value.byteValue());
        }
    }

    public void setBytes(int i, byte[] value, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.NUMERIC);
        } else {
            stmt.setBytes(i, value);
        }
    }

    public void setLong(int i, Long value, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.NUMERIC);
        } else {
            stmt.setLong(i, value.longValue());
        }
    }

    public void setFloat(int i, Float value, PreparedStatement stmt) throws SQLException {
        if (value == null || Float.isNaN(value.floatValue()) || Float.isInfinite(value.floatValue())) {
            stmt.setNull(i, Types.NUMERIC);
        } else {
            stmt.setFloat(i, value.floatValue());
        }
    }

    public void setDouble(int i, Double value, PreparedStatement stmt) throws SQLException {
        if (value == null || Double.isNaN(value.doubleValue()) || Double.isInfinite(value.doubleValue())) {
            stmt.setNull(i, Types.NUMERIC);
        } else {
            stmt.setDouble(i, value.doubleValue());
        }
    }

    public void setDate(int i, java.util.Date value, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.DATE);
        } else {
            stmt.setTimestamp(i, new Timestamp(value.getTime()));
        }
    }

    public void setString(int i, String value, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.VARCHAR);
        } else {
            stmt.setString(i, value);
        }
    }

    public void setBoolean(int i, Boolean value, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.VARCHAR);
        } else {
            stmt.setString(i, value.booleanValue() ? "S" : "N");
        }
    }

    public void setBigDecimal(int i, BigDecimal value, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.DECIMAL);
        } else {
            stmt.setBigDecimal(i, value);
        }
    }

    public void setBigInteger(int i, BigInteger value, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.INTEGER);
        } else {
            stmt.setBigDecimal(i, new BigDecimal(value));
        }
    }

    public void setInputStream(int i, InputStream value, int length, PreparedStatement stmt) throws SQLException {
        if (value == null) {
            stmt.setNull(i, Types.BLOB);
        } else {
            stmt.setBinaryStream(i, value, length);
        }
    }

    public void set(int i, Integer value, PreparedStatement stmt) throws SQLException {
        setInt(i, value, stmt);
    }

    public void set(int i, Short value, PreparedStatement stmt) throws SQLException {
        setShort(i, value, stmt);
    }

    public void set(int i, Byte value, PreparedStatement stmt) throws SQLException {
        setByte(i, value, stmt);
    }

    public void set(int i, Long value, PreparedStatement stmt) throws SQLException {
        setLong(i, value, stmt);
    }

    public void set(int i, Float value, PreparedStatement stmt) throws SQLException {
        setFloat(i, value, stmt);
    }

    public void set(int i, Double value, PreparedStatement stmt) throws SQLException {
        setDouble(i, value, stmt);
    }

    public void set(int i, Date value, PreparedStatement stmt) throws SQLException {
        setDate(i, value, stmt);
    }

    public void set(int i, String value, PreparedStatement stmt) throws SQLException {
        setString(i, value, stmt);
    }

    public void set(int i, Boolean value, PreparedStatement stmt) throws SQLException {
        setBoolean(i, value, stmt);
    }

    public void set(int i, BigDecimal value, PreparedStatement stmt) throws SQLException {
        setBigDecimal(i, value, stmt);
    }

    public void set(int i, BigInteger value, PreparedStatement stmt) throws SQLException {
        setBigInteger(i, value, stmt);
    }

    public void set(int i, byte[] value, PreparedStatement stmt) throws SQLException {
        setBytes(i, value, stmt);
    }

    public void set(int i, InputStream value, int length, PreparedStatement stmt) throws SQLException {
        setInputStream(i, value, length, stmt);
    }

    protected Map<String, AccessorMap> beanMap(Class<?> beanClass, ResultSetMetaData metaData) throws SQLException {
        Map<String, AccessorMap> map = beanMap.get(beanClass);
        if (map == null) {
            map = createBeanMap(beanClass, metaData);
            beanMap.put(beanClass, map);
        }
        return map;
    }

    private Map<String, AccessorMap> createBeanMap(Class<?> beanClass, ResultSetMetaData metaData) throws SQLException {
        Map<String, AccessorMap> map = new HashMap<String, AccessorMap>();
        Method[] methods = beanClass.getMethods();
        Field[] fields = beanClass.getFields();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String column = metaData.getColumnLabel(i).toLowerCase();
            String field = getFieldName(column);
            String method = "set" + field;
            AccessorMap accessorMap = new AccessorMap();
            accessorMap.method = seekMethod(method, methods);
            if (accessorMap.method != null) {
                accessorMap.paramType = accessorMap.method.getParameterTypes()[0];
                map.put(column, accessorMap);
            } else {
                accessorMap.field = seekField(field, fields);
                if (accessorMap.field != null) {
                    accessorMap.paramType = accessorMap.field.getType();
                    map.put(column, accessorMap);
                }
            }
        }
        return map;
    }

    private boolean isValid(Class<?> clazz) {
        return saci.util.Types.isPrintable(clazz) || InputStream.class.isAssignableFrom(clazz);
    }

    protected Method seekMethod(String fieldName, Method[] methods) {
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(fieldName) && method.getParameterTypes().length == 1) {
                if (isValid(method.getParameterTypes()[0])) {
                    return method;
                }
            }
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Method " + fieldName + " not found");
        }
        return null;
    }

    protected Field seekField(String fieldName, Field[] fields) {
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                if (isValid(field.getType())) {
                    return field;
                }
            }
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Field " + fieldName + " not found");
        }
        return null;
    }

    protected String getFieldName(String fieldName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (c == '_') {
                continue;
            }
            sb.append(c);
        }
        return sb.toString().toLowerCase();
    }

    private void closeResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }

        try {
            resultSet.close();
            resultSet = null;
        } catch (SQLException ignored) {
        }
    }

    private void closeStatement(Statement statement) {
        if (statement == null) {
            return;
        }

        try {
            statement.close();
            statement = null;
        } catch (SQLException ignored) {
        }
    }

    private void closeConnectionDS() {
        if (datasource) {
            closeConnection();
        }
    }

    public void closeConnection() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
            connection = null;
        } catch (SQLException ignored) {
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setDataSource(DataSource ds) throws SQLException {
        this.connection = ds.getConnection();
    }
}
