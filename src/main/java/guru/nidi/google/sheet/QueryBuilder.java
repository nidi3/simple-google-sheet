package guru.nidi.google.sheet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 */
class QueryBuilder {
    private String q = "";

    public QueryBuilder addParam(String key, Object value) {
        return addParamIf(true, key, value);
    }

    public QueryBuilder addParamIf(boolean cond, String key, Object value) {
        if (cond) {
            q += (q.length() == 0 ? "?" : "&")
                    + key
                    + "="
                    + (value == null ? "" : encode(value.toString()));
        }
        return this;
    }

    public String build() {
        return q;
    }

    private String encode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
