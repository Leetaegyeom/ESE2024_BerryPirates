import time
import ADS1x15

class Potentiometer:
    def __init__(self, potentiometer_params):
        self.I2C_BUS_ID = potentiometer_params.I2C_BUS_ID

        self.LEFT_PIN = potentiometer_params.LEFT_PIN
        self.RIGHT_PIN = potentiometer_params.RIGHT_PIN

        self.ADS = ADS1x15.ADS1115(self.I2C_BUS_ID, 0x48)
        self.ADS.setGain(self.ADS.PGA_4_096V)

        self.MAX_ANGLE = 40
        self.MAX_VALUE = 26366
        self.RATIO = self.MAX_ANGLE/self.MAX_VALUE

        self.RIGHT_OFFSET = 1000
        self.LEFT_OFFSET = 1000

    def measure_value(self):
        left_value = self.ADS.readADC(self.LEFT_PIN) - self.LEFT_OFFSET
        right_value = self.ADS.readADC(self.RIGHT_PIN) - self.RIGHT_OFFSET

        return left_value, right_value

    def get_angle(self):
        left_value, right_value = self.measure_value()

        left_angle = int(left_value * self.RATIO)
        right_angle = int(right_value * self.RATIO)

        return left_angle, right_angle