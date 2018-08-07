package base.utils;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 14:43
 * @description
 * @since 2.8.1
 */
import java.util.HashMap;
import java.util.Map;

public class ApiResponse {
    private int start;
    private int pageSize = 20;
    private int totalRows;
    private PageUtil page;
    private Object data;
    private Map<String, Object> model;
    private int code = 200;
    private int result;
    private String msg;

    private ApiResponse() {
        this.code = 200;
        this.result = 1;
        this.msg = "SUCCESS";
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.result = -1;
        this.msg = message;
    }

    public Object getData() {
        return this.data == null ? this.model : this.data;
    }

    public ApiResponse setData(Object data) {
        this.data = data;
        return this;
    }

    public ApiResponse addAttribute(String name, Object value) {
        if (this.model == null) {
            this.model = new HashMap();
        }

        this.model.put(name, value);
        return this;
    }

    public int getStart() {
        return this.page != null ? this.page.getStart() : this.start;
    }

    public ApiResponse setStart(int start) {
        this.start = start > 0 ? start : 0;
        return this;
    }

    public int getPageSize() {
        return this.page != null ? this.page.getRows() : this.pageSize;
    }

    public ApiResponse setPageSize(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        }

        return this;
    }

    public int getTotalRows() {
        return this.page != null ? this.page.getTotalRows() : this.totalRows;
    }

    public ApiResponse setTotalRows(int totalRows) {
        this.totalRows = totalRows > 0 ? totalRows : 0;
        return this;
    }

    public int getCode() {
        return this.code;
    }

    public ApiResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public int getResult() {
        return this.result;
    }

    public ApiResponse setResult(int result) {
        this.result = result;
        return this;
    }

    public String getMsg() {
        return this.msg;
    }

    public ApiResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public static ApiResponse create() {
        return new ApiResponse();
    }

    public static ApiResponse ok() {
        return new ApiResponse();
    }

    public static ApiResponse error(int errorCode) {
        return new ApiResponse(errorCode, "系统错误，错误码：" + errorCode);
    }

    public ApiResponse setPage(PageUtil page) {
        this.page = page;
        return this;
    }
}
