package app.system;

public enum ResponseCode {
    /* 코드 목록
     *
     * - 정상처리 (200)
     * ----------------------
     * 출근 등록 (submit)
     * - 올바른 사용자
     * - 없는 사용자
     *
     * - 최초 출근
     * - 출근 세션 연장
     *
     * ----------------------
     * 신규 인원 등록 (admin)
     * - 등록 완료
     * - 이미 있는 사용자
     * - 값 오류
     *
     * 멤버 정보 수정 (admin)
     * - 수정 완료
     * - 없는 사용자
     * - 값 오류
     *
     * 멤버 삭제 (admin)
     * - 삭제 완료
     * - 없는 사용자
     *
     * ---------------------
     *
     *
     *
     */ // 코드 목록

    OK(200, "Processed Successfully"),      // Formal Code
    Created(201, "Created Successfully"),   // for User Sign-up

    // for submit response
    FirstSubmit(211, "First Submitted Successfully"),
    ExtendSubmit(212, "Extend Submitted Successfully"),
    RenewedSubmit(213, "Renewed Submitted Successfully"),   // Time-out and renewed submit
    SessionExpired(219, "Session Expired"),

    Unauthorized(401, "Unauthorized Request"),
    NotFound(404, "Non-existent User");    // Formal Code

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

}
