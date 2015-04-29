package guru.nidi.google.sheet;

/**
 *
 */
public class CellFeedQuery {
    private int minRow;
    private int maxRow;
    private int minCol;
    private int maxCol;

    public CellFeedQuery maxRow(int maxRow) {
        this.maxRow = maxRow;
        return this;
    }

    public CellFeedQuery minCol(int minCol) {
        this.minCol = minCol;
        return this;
    }

    public CellFeedQuery maxCol(int maxCol) {
        this.maxCol = maxCol;
        return this;
    }

    public CellFeedQuery minRow(int minRow) {
        this.minRow = minRow;
        return this;
    }

    String toQuery() {
        return new QueryBuilder()
                .addParamIf(minRow > 0, "min-row", minRow)
                .addParamIf(maxRow > 0, "max-row", maxRow)
                .addParamIf(minCol > 0, "min-col", minCol)
                .addParamIf(maxCol > 0, "max-col", maxCol)
                .build();
    }
}
