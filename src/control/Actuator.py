import RPi.GPIO as GPIO
import time

class ActuatorController:
    def __init__(self, actuator_params):
        # Constants
        self.ENA_PIN = actuator_params.ENA_PIN
        self.IN1_PIN = actuator_params.IN1_PIN
        self.IN2_PIN = actuator_params.IN2_PIN
        self.FREQUENCY = actuator_params.FREQUENCY

        # Setup
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(self.ENA_PIN, GPIO.OUT)
        GPIO.setup(self.IN1_PIN, GPIO.OUT)
        GPIO.setup(self.IN2_PIN, GPIO.OUT)

        # PWM setup
        self.pwm = GPIO.PWM(self.ENA_PIN, self.FREQUENCY)
        self.pwm.start(0)  # Start with PWM off (0% duty cycle)

    def extend_actuator(self, speed):
        # Extend the actuator
        GPIO.output(self.IN1_PIN, GPIO.LOW)
        GPIO.output(self.IN2_PIN, GPIO.HIGH)
        self.pwm.ChangeDutyCycle(speed)  # Set speed (duty cycle)

    def retract_actuator(self, speed):
        # Retract the actuator
        GPIO.output(self.IN1_PIN, GPIO.HIGH)
        GPIO.output(self.IN2_PIN, GPIO.LOW)
        self.pwm.ChangeDutyCycle(speed)  # Set speed (duty cycle)

    def stop_actuator(self):
        GPIO.output(self.IN1_PIN, GPIO.LOW)
        GPIO.output(self.IN2_PIN, GPIO.LOW)
        self.pwm.ChangeDutyCycle(0)  # Stop the actuator by setting duty cycle to 0%

    def clean_up(self):
        self.pwm.stop()
        GPIO.cleanup()