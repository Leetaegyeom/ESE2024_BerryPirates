# This Raspberry Pi code was developed by newbiely.com
# This Raspberry Pi code is made available for public use without any restriction
# For comprehensive instructions and wiring diagrams, please visit:
# https://newbiely.com/tutorials/raspberry-pi/raspberry-pi-actuator


import RPi.GPIO as GPIO
import time

# Constants
ENA_PIN_1 = 25  # GPIO pin connected to the EN1 pin L298N
IN1_PIN_1 = 8  # GPIO pin connected to the IN1 pin L298N
IN2_PIN_1 = 7  # GPIO pin connected to the IN2 pin L298N

ENA_PIN_2 = 17 # GPIO pin connected to the EN1 pin L298N
IN1_PIN_2 = 27  # GPIO pin connected to the IN1 pin L298N
IN2_PIN_2 = 22  # GPIO pin connected to the IN2 pin L298N

# Setup
GPIO.setmode(GPIO.BCM)
GPIO.setup(ENA_PIN_1, GPIO.OUT)
GPIO.setup(IN1_PIN_1, GPIO.OUT)
GPIO.setup(IN2_PIN_1, GPIO.OUT)
GPIO.setup(ENA_PIN_2, GPIO.OUT)
GPIO.setup(IN1_PIN_2, GPIO.OUT)
GPIO.setup(IN2_PIN_2, GPIO.OUT)

# Set ENA_PIN_1 to HIGH to enable the actuator
GPIO.output(ENA_PIN_1, GPIO.HIGH)
GPIO.output(ENA_PIN_2, GPIO.HIGH)

# Main loop
try:
    while True:
        # Extend the actuator
        GPIO.output(IN1_PIN_1, GPIO.HIGH)
        GPIO.output(IN2_PIN_1, GPIO.LOW)
        GPIO.output(IN1_PIN_2, GPIO.HIGH)
        GPIO.output(IN2_PIN_2, GPIO.LOW)

        time.sleep(20)  # Actuator will stop extending automatically when reaching the limit

        # Retract the actuator
        GPIO.output(IN1_PIN_1, GPIO.LOW)
        GPIO.output(IN2_PIN_1, GPIO.HIGH)
        GPIO.output(IN1_PIN_2, GPIO.LOW)
        GPIO.output(IN2_PIN_2, GPIO.HIGH)

        time.sleep(20)  # Actuator will stop retracting automatically when reaching the limit

except KeyboardInterrupt:
    pass

finally:
    # Cleanup GPIO on program exit
    GPIO.cleanup()
