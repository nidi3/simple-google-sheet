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
