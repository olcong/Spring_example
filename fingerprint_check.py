# -*- coding: utf-8 -*-
import serial
import adafruit_fingerprint

# 이 스크립트는 오직 지문 센서만 테스트합니다.

print("지문 센서 연결 테스트를 시작합니다...")

try:
    # 라즈베리파이의 하드웨어 시리얼 포트에 연결 시도
    uart = serial.Serial("/dev/ttyS0", baudrate=57600, timeout=1)

    # 지문 센서 라이브러리 초기화
    finger = adafruit_fingerprint.Adafruit_Fingerprint(uart)

    # 센서와 통신이 되는지 확인 (암호 확인)
    if finger.verify_password():
        print("\n======================================")
        print("  🎉 지문 센서 연결 성공! 🎉")
        print("======================================")
        print(f"센서에 저장된 지문 수: {finger.template_count}")
    else:
        print("\n======================================")
        print("  😭 지문 센서 연결 실패! 😭")
        print("======================================")
        print("센서로부터 응답이 없습니다. 배선을 다시 확인해주세요.")

except Exception as e:
    print("\n스크립트 실행 중 심각한 오류가 발생했습니다.")
    print(f"오류 내용: {e}")
