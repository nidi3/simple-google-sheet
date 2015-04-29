/*
 * Copyright (C) 2015 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.google.sheet;

/**
 *
 */
public class ListFeedQuery {
    private boolean inverseRowOrder;
    private String orderByColumn;
    private String query;

    public ListFeedQuery query(String query) {
        this.query = query;
        return this;
    }

    public ListFeedQuery orderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
        return this;
    }

    public ListFeedQuery inverseRowOrder() {
        this.inverseRowOrder = true;
        return this;
    }

    String toQuery() {
        return new QueryBuilder()
                .addParamIf(inverseRowOrder, "reverse", true)
                .addParamIf(orderByColumn != null, "orderby", "column:" + orderByColumn)
                .addParamIf(query != null, "sq", query)
                .build();
    }


}
