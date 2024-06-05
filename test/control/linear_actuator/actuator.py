# This Raspberry Pi code was developed by newbiely.com
# This Raspberry Pi code is made available for public use without any restriction
# For comprehensive instructions and wiring diagrams, please visit:
# https://newbiely.com/tutorials/raspberry-pi/raspberry-pi-actuator


import RPi.GPIO as GPIO
import time

# Constants
ENA_PIN_1 = 13  # GPIO pin connected to the EN1 pin L298N
IN1_PIN_1 = 10  # GPIO pin connected to the IN1 pin L298N
IN2_PIN_1 = 9  # GPIO pin connected to the IN2 pin L298N

ENA_PIN_2 = 16 # GPIO pin connected to the EN1 pin L298N
IN1_PIN_2 = 20  # GPIO pin connected to the IN1 pin L298N
IN2_PIN_2 = 21  # GPIO pin connected to the IN2 pin L298N

# Setup
GPIO.setmode(GPIO.BCM)

GPIO.setup(ENA_PIN_1, GPIO.OUT)
GPIO.setup(IN1_PIN_1, GPIO.OUT)
GPIO.setup(IN2_PIN_1, GPIO.OUT)

GPIO.output(ENA_PIN_1, GPIO.HIGH)

GPIO.setup(ENA_PIN_2, GPIO.OUT)
GPIO.setup(IN1_PIN_2, GPIO.OUT)
GPIO.setup(IN2_PIN_2, GPIO.OUT)

GPIO.output(ENA_PIN_2, GPIO.HIGH)


# Main loop
try:
    while True:

        # print("1")
        # extend the actuator
        # GPIO.output(IN1_PIN_1, GPIO.LOW)
        # GPIO.output(IN2_PIN_1, GPIO.HIGH)
        # GPIO.output(IN1_PIN_2, GPIO.LOW)
        # GPIO.output(IN2_PIN_2, GPIO.HIGH)

        # retract the actuator
        GPIO.output(IN1_PIN_1, GPIO.HIGH)
        GPIO.output(IN2_PIN_1, GPIO.LOW)
        GPIO.output(IN1_PIN_2, GPIO.HIGH)
        GPIO.output(IN2_PIN_2, GPIO.LOW)


        time.sleep(1) 

except KeyboardInterrupt:
    pass

finally:
    # Cleanup GPIO on program exit
    GPIO.cleanup()
