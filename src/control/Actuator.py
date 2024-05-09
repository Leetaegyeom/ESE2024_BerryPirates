import RPi.GPIO as GPIO
import time

class ActuatorController:
    def __init__(self, actuator_params):
        # Constants
        self.ENA_PIN = actuator_params.ENA_PIN
        self.IN1_PIN = actuator_params.IN1_PIN
        self.IN2_PIN = actuator_params.IN2_PIN

        # Setup
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(self.ENA_PIN, GPIO.OUT)
        GPIO.setup(self.IN1_PIN, GPIO.OUT)
        GPIO.setup(self.IN2_PIN, GPIO.OUT)

        # Enable the actuator
        GPIO.output(self.ENA_PIN, GPIO.HIGH)

    def extend_actuator(self):
        # Extend the actuator
        GPIO.output(self.IN1_PIN, GPIO.HIGH)
        GPIO.output(self.IN2_PIN, GPIO.LOW)
        # Assuming actuator will stop extending automatically when reaching the limit

    def retract_actuator(self):
        # Retract the actuator
        GPIO.output(self.IN1_PIN, GPIO.LOW)
        GPIO.output(self.IN2_PIN, GPIO.HIGH)
        # Assuming actuator will stop retracting automatically when reaching the limit

    def stop_actuator(self):
        # Retract the actuator
        # GPIO.setup(self.ENA_PIN, GPIO.LOW)
        GPIO.output(self.ENA_PIN, GPIO.LOW)

    def clean_up(self):
        # Cleanup GPIO on program exit
        GPIO.cleanup()

# # Example usage
# if __name__ == "__main__":
#     actuator = ActuatorController(25, 8, 7, 17, 27, 22)
#     try:
#         while True:
#             actuator.extend_actuator()
#             time.sleep(20)  # Adjust time as necessary
#             actuator.retract_actuator()
#             time.sleep(20)  # Adjust time as necessary
#     except KeyboardInterrupt:
#         pass
#     finally:
#         actuator.clean_up()
