package io.kischang.readme.base.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class R<T> implements java.io.Serializable {

    private boolean state;
    private int code = 0;
    private String msg;
    private T data;

    public static <T> R<T> ok() {
        return R.ok("success");
    }

    public static <T> R<T> ok(String msg) {
        return new R<>(true, 0, msg, null);
    }

    public static <T> R<T> data(T data) {
        return new R<>(true, 0, "success", data);
    }

    public static <T> R<T> fail(String msg) {
        return fail(-1, msg, null);
    }

    public static <T> R<T> fail(int code, String msg) {
        return fail(code, msg, null);
    }

    public static <T> R<T> fail(int code, String msg, T data) {
        return new R<>(false, code, msg, data);
    }

}
