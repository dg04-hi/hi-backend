package com.ktds.hi.common.exception;

/**
 * 파일 처리 관련 예외
 * 파일 업로드, 다운로드, 변환 등에서 발생하는 예외
 */
public class FileProcessingException extends BusinessException {
    
    public FileProcessingException(String message) {
        super("FILE_PROCESSING_ERROR", message);
    }
    
    public FileProcessingException(String message, Throwable cause) {
        super("FILE_PROCESSING_ERROR", message, cause);
    }
    
    /**
     * 지원하지 않는 파일 형식
     */
    public static FileProcessingException unsupportedFileType(String fileType) {
        return new FileProcessingException("지원하지 않는 파일 형식입니다: " + fileType);
    }
    
    /**
     * 파일 크기 초과
     */
    public static FileProcessingException fileSizeExceeded(long maxSize) {
        return new FileProcessingException("파일 크기가 제한을 초과했습니다. 최대 크기: " + maxSize + "bytes");
    }
    
    /**
     * 파일 업로드 실패
     */
    public static FileProcessingException uploadFailed() {
        return new FileProcessingException("파일 업로드에 실패했습니다");
    }
    
    /**
     * 파일 변환 실패
     */
    public static FileProcessingException conversionFailed(String fromType, String toType) {
        return new FileProcessingException(String.format("파일 변환에 실패했습니다: %s -> %s", fromType, toType));
    }
}
