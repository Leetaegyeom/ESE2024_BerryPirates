import RPi.GPIO as GPIO
import time

class UltrasonicSensor:

    def __init__(self):
        GPIO.setmode(GPIO.BCM)
        TRIG_PIN = 23
        ECHO_PIN = 24
        GPIO.setup(TRIG_PIN, GPIO.OUT)
        GPIO.setup(ECHO_PIN, GPIO.IN)

    def get_distance(self):
        GPIO.output(TRIG_PIN, GPIO.HIGH)
        time.sleep(0.00001) 
        GPIO.output(TRIG_PIN, GPIO.LOW)

        while GPIO.input(ECHO_PIN) == 0:
            pulse_start = time.time()

        while GPIO.input(ECHO_PIN) == 1:
            pulse_end = time.time()

        pulse_duration = pulse_end - pulse_start

        distance = pulse_duration * 34300 / 2

        # print(f"Distance: {distance:.2f} cm") ## for test

        return distance
        