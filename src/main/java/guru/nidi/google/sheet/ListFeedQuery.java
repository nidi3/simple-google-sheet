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
