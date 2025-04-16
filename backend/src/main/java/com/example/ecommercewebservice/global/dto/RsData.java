package com.example.ecommercewebservice.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 범용 응답 데이터 전송 객체
 * 
 * 사용 위치:
 * - 모든 컨트롤러에서 API 응답을 표준화하기 위해 사용
 * - 서비스 계층에서 결과 및 메시지를 포함한 응답 생성 시 사용
 * 
 * 용도:
 * - 응답 코드, 메시지, 데이터를 포함하는 표준화된 응답 형식 제공
 * - API 응답 형식의 일관성 보장
 * - 클라이언트가 응답 상태를 쉽게 확인할 수 있도록 함
 */
@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsData<T> {
    /**
     * 응답 상태 코드 (예: "200-SUCCESS", "400-BAD_REQUEST")
     * HTTP 상태 코드와 상세 코드를 포함
     */
    private String code;
    
    /**
     * 응답 메시지
     * 사용자에게 표시할 메시지 또는 오류 설명
     */
    private String msg;
    
    /**
     * 응답 데이터
     * API 응답에 포함될 실제 데이터 객체
     */
    private T data;

    public RsData(String code, String msg) {
        this(code, msg, null);
    }

    /**
     * HTTP 상태 코드 추출
     * code 문자열에서 첫 번째 부분을 추출하여 HTTP 상태 코드로 변환
     * 
     * @return HTTP 상태 코드 (예: 200, 400, 500)
     */
    @JsonIgnore
    public int getStatusCode() {
        String statusCodeStr = code.split("-")[0];
        return Integer.parseInt(statusCodeStr);
    }

}