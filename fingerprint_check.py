# -*- coding: utf-8 -*-
import serial
import adafruit_fingerprint


print("지문 센서 연결 테스트를 시작합니다...")

try:
    # 라즈베리파이의 하드웨어 시리얼 포트에 연결 시도
    uart = serial.Serial("/dev/ttyS0", baudrate=57600, timeout=1)

    # 지문 센서 라이브러리 초기화
    finger = adafruit_fingerprint.Adafruit_Fingerprint(uart)

    # 센서와 통신이 되는지 확인
    if finger.verify_password()
        print("성공")
    else:

        print("실패")

except Exception as e:
    print("예외")
    print(f"오류 내용: {e}")
