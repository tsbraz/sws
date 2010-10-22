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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class QueryMap {

    private String query;
    private Map<String, List<Integer>> parameterMap = new HashMap<String, List<Integer>>();

    public static QueryMap mapQuery(String query) {
        query += " ";
        QueryMap map = new QueryMap();
        StringBuilder sb = new StringBuilder();
        int parameterIndex = 1;
        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);
            if (c == '\'') {
                sb.append(query.charAt(i));
                boolean fechou = false;
                for (i++; i < query.length(); i++) {
                    if (query.charAt(i) == '\'' && !fechou) {
                        fechou = true;
                    } else if (fechou) {
                        i--;
                        break;
                    }
                    sb.append(query.charAt(i));
                }
            } else if (c == ':') {
                StringBuilder temp = new StringBuilder();
                for (i++; i < query.length(); i++) {
                    c = query.charAt(i);
                    if (Character.isJavaIdentifierPart(c)) {
                        temp.append(c);
                    } else {
                        break;
                    }
                }
                sb.append("?").append(c);
                map.addParameter(temp.toString(), parameterIndex++);
            } else {
                sb.append(Character.toLowerCase(query.charAt(i)));
            }
        }
        map.query = sb.toString();
        return map;
    }

    private QueryMap() {
    }

    public void addParameter(String param, int indice) {
        param = param.toLowerCase();
        List<Integer> list = parameterMap.get(param);
        if (list != null) {
            list.add(indice);
        } else {
            list = new ArrayList<Integer>();
            list.add(indice);
            parameterMap.put(param, list);
        }
    }

    public String getQuery() {
        return query;
    }

    public List<Integer> getParameterIndex(String paramName) {
        return parameterMap.get(paramName.toLowerCase());
    }

    public Set<Entry<String, List<Integer>>> listParameters() {
        return parameterMap.entrySet();
    }
}
