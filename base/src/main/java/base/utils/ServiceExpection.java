package base.utils;

/**
 * @author chensl [cookchensl@gmail.com]
 * @date 2018/8/7 15:03
 * @description
 * @since 2.8.1
 */
public class ServiceExpection extends RuntimeException {

    private int errorCode;

    public ServiceExpection(int code, String message) {
        super(message);
        this.errorCode = code;
    }

    @Override
    public String getMessage() {
        return this.errorCode == 0 ? super.getMessage() : this.errorCode + " : " + super.getMessage();
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
