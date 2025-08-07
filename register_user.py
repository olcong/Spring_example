
import time
import serial
import adafruit_fingerprint
from mfrc522 import SimpleMFRC522
import RPi.GPIO as GPIO


# RFID 센서
rfid_reader = SimpleMFRC522()

# 지문 센서
uart = serial.Serial("/dev/ttyS0", baudrate=57600, timeout=1)
fingerprint_sensor = adafruit_fingerprint.Adafruit_Fingerprint(uart)




def get_fingerprint_enroll(finger_id):
    """지문을 새로운 ID로 등록하는 함수"""
    print(f"지문 ID #{finger_id} 에 새로운 지문을 등록합니다.")
    print("센서에 손가락을 대주세요")
    
    # 1. 첫 번째 이미지 스캔
    while fingerprint_sensor.get_image() != adafruit_fingerprint.OK:
        pass
    print("이미지 스캔 완료. 손가락을 떼주세요.")
    if fingerprint_sensor.image_2_tz(1) != adafruit_fingerprint.OK:
        print("템플릿 생성 실패")
        return False
    time.sleep(1)
  
    print("같은 손가락을 다시 한번 센서에 대주세요")
    while fingerprint_sensor.get_image() != adafruit_fingerprint.OK:
        pass
    print("이미지 스캔 완료.")
    if fingerprint_sensor.image_2_tz(2) != adafruit_fingerprint.OK:
        print("템플릿 생성 실패")
        return False

    # 3. 두 템플릿을 합쳐서 모델 생성
    print("두 지문 이미지를 결합합니다")
    if fingerprint_sensor.create_model() != adafruit_fingerprint.OK:
        print("지문 결합 실패")
        return False

    # 4. 모델을 센서에 저장
    print(f"ID #{finger_id} 에 지문을 저장합니다")
    if fingerprint_sensor.store_model(finger_id) != adafruit_fingerprint.OK:
        print("지문 저장 실패")
        return False
        
    print("지문 등록 성공!")
    return True

def get_next_fingerprint_id():

    for finger_id in range(1, fingerprint_sensor.library_size + 1):
        if fingerprint_sensor.get_template(finger_id)['status'] != adafruit_fingerprint.OK:
            return finger_id 
    return None 


# --- 메인 프로그램 ---

if __name__ == "__main__":
    try:
        # 1. 지문 센서 연결 확인
        if fingerprint_sensor.verify_password() is not True:
            raise RuntimeError("지문 센서를 찾을 수 없습니다")
        print("지문 센서가 성공적으로 연결")
        
        # 2. 등록할 사용자 학번(ID) 입력받기
        user_id_str = input("등록할 사용자 번호를 입력하세요: ")
        if not user_id_str.isdigit():
            raise ValueError("번호 숫자로만 입력해야 합니다.")
        
        # 3. NFC 카드 등록
        print("\n등록할 NFC 카드를 리더기에 태그해주세요")
        card_id, text = rfid_reader.read()
        print(f"카드 ID가 성공적으로 읽혔습니다: {card_id}")
        time.sleep(1)

        # 4. 지문 등록
        # 비어있는 지문 ID 찾기
        next_id = get_next_fingerprint_id()
        if next_id is None:
            print("지문 센서에 더 이상 저장할 공간이 없습니다")
        else:
            # 지문 등록 절차 진행
            if get_fingerprint_enroll(next_id):
                # 5. 최종 결과 출력
                print("\n--- 등록 정보 ---")
                print(f"번호: {user_id_str}")
                print(f"카드 ID: {card_id}")
                print(f"지문 ID: {next_id}")
                print("-------------------")
                print("\n위 정보를 UserData.csv 파일에 추가해주세요.")
                print(f"CSV 추가 형식: {user_id_str},이름,{card_id},{next_id}")

    except Exception as e:
        print(f"오류가 발생했습니다: {e}")
    finally:
        # 프로그램 종료 시 GPIO 핀 초기화
        GPIO.cleanup()
