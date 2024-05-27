import RPi.GPIO as GPIO
import time

class UltrasonicSensor:

    def __init__(self, ultrasonic_params):
        self.TRIG_PIN = ultrasonic_params.TRIG_PIN
        self.ECHO_PIN = ultrasonic_params.ECHO_PIN
        
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(self.TRIG_PIN, GPIO.OUT)
        GPIO.setup(self.ECHO_PIN, GPIO.IN)

    def get_distance(self):
        GPIO.output(self.TRIG_PIN, GPIO.HIGH)
        time.sleep(0.00001) 
        GPIO.output(self.TRIG_PIN, GPIO.LOW)

        while GPIO.input(self.ECHO_PIN) == 0:
            pulse_start = time.time()

        while GPIO.input(self.ECHO_PIN) == 1:
            pulse_end = time.time()

        pulse_duration = pulse_end - pulse_start

        distance = pulse_duration * 34300 / 2

        time.sleep(0.1)

        return distance
        