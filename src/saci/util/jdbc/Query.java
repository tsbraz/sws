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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * Classe utilitária para facilitar o uso de JDBC.
 * 
 * @author Thiago
 */
public class Query {

    private Map<Class<?>, Map<String, Method>> beanMap = new HashMap<Class<?>, Map<String, Method>>();
    @SuppressWarnings("unchecked")
    private List<QueryListener> listeners;
    private PreparedStatement stmt;
    private QueryMap queryMap;
    private DbUtil dbUtil;
    private int param = 1;

    PreparedStatement getPreparedStatement() {
        return stmt;
    }

    Query(DbUtil dbUtil, String query, Connection conn) throws SQLException {
        this.queryMap = QueryMap.mapQuery(query);
        this.dbUtil = dbUtil;
        this.stmt = conn.prepareStatement(queryMap.getQuery());
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(Integer value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(Short value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(Byte value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(Long value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(Float value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(Double value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(BigDecimal value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(BigInteger value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(Date value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(String value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query, adicionando "%" no final da string
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addLikeParameter(String value) throws SQLException {
        dbUtil.set(param++, value + "%", stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query, adicionando "%" no inicio e fim da string
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addLikeAnyParameter(String value) throws SQLException {
        dbUtil.set(param++, "%" + value + "%", stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(InputStream value, int length) throws SQLException {
        dbUtil.set(param++, value, length, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(Boolean value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query addParameter(byte[] value) throws SQLException {
        dbUtil.set(param++, value, stmt);
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, Integer value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, Short value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, Byte value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, Long value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, Float value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, Double value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, BigDecimal value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, BigInteger value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, Date value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, String value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    private List<Integer> getParameterIndex(String name) throws SQLException {
        List<Integer> parameterIndex = queryMap.getParameterIndex(name);
        if (parameterIndex == null) {
            throw new SQLException("Parameter " + name + " not found.");
        }
        return parameterIndex;
    }

    /**
     * Adiciona o parametro à query, adicionando "%" no final da string
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setLikeParameter(String name, String value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value + "%", stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query, adicionando "%" no inicio e fim da string
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setLikeAnyParameter(String name, String value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, "%" + value + "%", stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, byte[] value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, InputStream value, int length) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, length, stmt);
        }
        return this;
    }

    /**
     * Adiciona o parametro à query
     * 
     * @param value
     * @return
     * @throws SQLException
     */
    public Query setParameter(String name, Boolean value) throws SQLException {
        List<Integer> parameterIndex = getParameterIndex(name);
        for (Integer i : parameterIndex) {
            dbUtil.set(i, value, stmt);
        }
        return this;
    }

    /**
     * Adiciona como parametros nomeados da query, o objeto passado por
     * parametro
     * 
     * @param vo
     *            o objeto que contem os parametros da query
     * @return
     * @throws SQLException
     */
    public Query setNamedParameter(Object vo) throws SQLException {
        if (vo instanceof Map) {
            mapMapParameters((Map<?, ?>) vo);
        } else {
            mapBeanParameters(vo);
        }
        return this;
    }

    private void mapMapParameters(Map<?, ?> map) throws SQLException {
        Map<String, Object> values = prepareParameterMap(map);
        for (Entry<String, List<Integer>> parameter : queryMap.listParameters()) {
            if (!values.containsKey(parameter.getKey())) {
                if (DbUtil.logger.isLoggable(Level.INFO)) {
                    DbUtil.logger.info("Parameter " + parameter.getKey() + " not found in parameter map");
                }
                continue;
            }
            Object value = values.get(parameter.getKey());
            setStatementParameter(parameter, value == null ? String.class : value.getClass(), value);
        }
    }

    private Map<String, Object> prepareParameterMap(Map<?, ?> map) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Entry<?, ?> entry : map.entrySet()) {
            result.put(entry.getKey().toString().toLowerCase(), entry.getValue());
        }
        return result;
    }

    private void mapBeanParameters(Object vo) throws SQLException {
        try {
            Map<String, Method> methods = mapBean(vo);
            for (Entry<String, List<Integer>> parameter : queryMap.listParameters()) {
                Method method = methods.get(parameter.getKey());
                if (method == null) {
                    if (DbUtil.logger.isLoggable(Level.INFO)) {
                        DbUtil.logger.info("Parameter " + parameter.getKey() + " not found in "
                                + vo.getClass().getName());
                    }
                    continue;
                }
                Class<?> type = method.getReturnType();
                Object object = method.invoke(vo, (Object[]) null);
                setStatementParameter(parameter, type, object);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void setStatementParameter(Entry<String, List<Integer>> parameter, Class<?> type, Object object)
            throws SQLException {
        for (Integer index : parameter.getValue()) {
            if (type.equals(String.class)) {
                dbUtil.set(index, (String) object, stmt);
            } else if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
                dbUtil.set(index, (Byte) object, stmt);
            } else if (type.equals(Short.class) || type.equals(Short.TYPE)) {
                dbUtil.set(index, (Short) object, stmt);
            } else if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
                dbUtil.set(index, (Integer) object, stmt);
            } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
                dbUtil.set(index, (Long) object, stmt);
            } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
                dbUtil.set(index, (Float) object, stmt);
            } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
                dbUtil.set(index, (Double) object, stmt);
            } else if (type.equals(BigInteger.class)) {
                dbUtil.set(index, (BigInteger) object, stmt);
            } else if (type.equals(BigDecimal.class)) {
                dbUtil.set(index, (BigDecimal) object, stmt);
            } else if (type.equals(Date.class)) {
                dbUtil.set(index, (Date) object, stmt);
            } else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
                dbUtil.set(index, (Boolean) object, stmt);
            } else {
                stmt.setObject(index, object);
            }
        }
    }

    protected Map<String, Method> mapBean(Object bean) throws SQLException {
        if (beanMap.containsKey(bean.getClass())) {
            return beanMap.get(bean.getClass());
        }
        Map<String, Method> map = new HashMap<String, Method>();
        Method[] methods = bean.getClass().getMethods();
        for (int i = 1; i < methods.length; i++) {
            String methodName = methods[i].getName();
            if (methodName.startsWith("get")) {
                methodName = methodName.substring(3).toLowerCase();
                map.put(methodName, methods[i]);
            } else if (methodName.startsWith("is")) {
                methodName = methodName.substring(2).toLowerCase();
                map.put(methodName, methods[i]);
            }
        }
        beanMap.put(bean.getClass(), map);
        return map;
    }

    /**
     * Adiciona um listener para quando as linhas forem carregadas para um VO
     * 
     * @param listener
     * @return
     */
    @SuppressWarnings("unchecked")
    public Query addListener(QueryListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<QueryListener>();
        }
        listeners.add(listener);
        return this;
    }

    @SuppressWarnings("unchecked")
    protected void callListeners(Object vo, ResultSet rs) throws SQLException {
        if (listeners != null) {
            for (QueryListener listener : listeners) {
                listener.loadVO(vo, rs, dbUtil);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<QueryListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    /**
     * Executa a query, retornando uma lista de objetos da classe
     * 
     * @param <T>
     * @param voClass
     *            A classe que deve ser usada para recuperar os dados da query
     * @return uma lista com os dados da query
     * @throws SQLException
     */
    public <T> List<T> list(Class<T> voClass) throws SQLException {
        return dbUtil.list(this, voClass);
    }

    /**
     * Executa a query, retornando um objeto
     * 
     * @param <T>
     * @param voClass
     *            o objeto que deve ser preenchido com o resultado da query
     * @return uma nova instância do objeto
     * @throws SQLException
     */
    public <T> T get(Class<T> voClass) throws SQLException {
        return dbUtil.get(this, voClass);
    }

    /**
     * Executa a query, retornando os dados da query no objeto passado por
     * parametro
     * 
     * @param vo
     *            o objeto que deve ser preenchido com o resultado da query
     * @return se algum resultado foi encontrado
     * @throws SQLException
     */
    public boolean get(Object vo) throws SQLException {
        return dbUtil.get(this, vo);
    }

    /**
     * Executa um comando de INSERT, UPDATE ou DELETE, retornando a quantidade
     * de registros atualizados
     * 
     * @return a quantidade de registros atualizados
     * @throws SQLException
     */
    public int update() throws SQLException {
        return dbUtil.update(this);
    }
}
