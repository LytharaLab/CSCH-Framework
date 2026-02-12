package org.lytharalab.csch.core.layer;

public class CSCHException extends Exception {
    private final String component;
    private final ErrorCode errorCode;
    
    public CSCHException(String message) {
        super(message);
        this.component = "Unknown";
        this.errorCode = ErrorCode.UNKNOWN;
    }
    
    public CSCHException(String message, Throwable cause) {
        super(message, cause);
        this.component = "Unknown";
        this.errorCode = ErrorCode.UNKNOWN;
    }
    
    public CSCHException(String component, String message) {
        super(message);
        this.component = component;
        this.errorCode = ErrorCode.UNKNOWN;
    }
    
    public CSCHException(String component, ErrorCode errorCode, String message) {
        super(message);
        this.component = component;
        this.errorCode = errorCode;
    }
    
    public CSCHException(String component, ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.component = component;
        this.errorCode = errorCode;
    }
    
    public String getComponent() {
        return component;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public enum ErrorCode {
        INITIALIZATION_FAILED("初始化失败"),
        SHUTDOWN_FAILED("关闭失败"),
        EXECUTION_ERROR("执行错误"),
        INVALID_STATE("无效状态"),
        INVALID_INPUT("无效输入"),
        TIMEOUT("超时"),
        RESOURCE_UNAVAILABLE("资源不可用"),
        CONFIGURATION_ERROR("配置错误"),
        UNKNOWN("未知错误");
        
        private final String description;
        
        ErrorCode(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
