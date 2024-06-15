import RPi.GPIO as GPIO  # RPi.GPIO 라이브러리를 GPIO라는 이름으로 임포트한다. 이 라이브러리를 통해 라즈베리 파이의 GPIO 핀을 제어할 수 있다.
import time  # time 라이브러리를 임포트한다. 이 라이브러리는 시간 관련 기능을 제공하며, 여기서는 특히 대기 시간을 생성하는 데 사용된다.

# GPIO 핀의 번호를 BCM 모드로 사용한다. BCM 모드는 브로드컴 칩에 정의된 핀 번호를 사용하는 방식이다.
GPIO.setmode(GPIO.BCM)

# 초음파 센서의 트리거 핀과 에코 핀의 GPIO 번호를 각각 23, 24로 설정한다.
TRIG_PIN = 23
ECHO_PIN = 24

# 트리거 핀을 출력으로, 에코 핀을 입력으로 설정한다. 이는 트리거 핀에서 신호를 보내고, 에코 핀에서 신호를 받아들이기 위함이다.
GPIO.setup(TRIG_PIN, GPIO.OUT)
GPIO.setup(ECHO_PIN, GPIO.IN)

# 거리를 측정하는 함수이다.
def get_distance():
    # 트리거 핀을 통해 초음파 신호를 발생시킨다. 먼저 HIGH 상태로 만든 후, 짧은 시간(10us) 대기한 다음 LOW 상태로 돌려놓는다.
    GPIO.output(TRIG_PIN, GPIO.HIGH)
    time.sleep(0.00001)  # 10us 동안 대기
    GPIO.output(TRIG_PIN, GPIO.LOW)

    # 에코 핀에서 신호를 기다린다. 신호가 돌아오기 시작하는 시간을 기록한다.
    while GPIO.input(ECHO_PIN) == 0:
        pulse_start = time.time()

    # 신호가 돌아와서 에코 핀이 HIGH 상태가 되면, 신호가 끝나는 시간을 기록한다.
    while GPIO.input(ECHO_PIN) == 1:
        pulse_end = time.time()

    # 신호가 돌아오는 데 걸린 시간을 계산한다.
    pulse_duration = pulse_end - pulse_start

    # 거리를 계산한다. 속도(음속) = 34300 cm/s, 시간 = pulse_duration, 거리 = 속도 * 시간 / 2
    distance = pulse_duration * 34300 / 2

    return distance  # 계산된 거리를 반환한다.

try:
    while True:
        distance = get_distance()  # 거리를 측정하는 함수를 호출한다.
        print(f"Distance: {distance:.2f} cm")  # 측정된 거리를 출력한다.
        time.sleep(1)  # 1초마다 반복한다.

except KeyboardInterrupt:
    # 프로그램을 종료하기 위해 Ctrl+C를 누르면, GPIO 설정을 초기화한다.
    GPIO.cleanup()
