import time
import ADS1x15

class Potentiometer:
    def __init__(self, potentiometer_params):
        self.I2C_BUS_ID = potentiometer_params.I2C_BUS_ID

        self.LEFT_PIN = potentiometer_params.LEFT_PIN
        self.RIGHT_PIN = potentiometer_params.RIGHT_PIN

        self.ADS = ADS1x15.ADS1115(self.I2C_BUS_ID, 0x48)
        self.ADS.setGain(self.ADS.PGA_4_096V)

        self.MAX_ANGLE = 300
        self.MAX_VALUE = 4.096
        self.RATIO = self.MAX_ANGLE/self.MAX_VALUE

        self.RIGHT_OFFSET = 2.660
        self.LEFT_OFFSET = 3.012
        self.f = self.ADS.toVoltage()

    def measure_value(self):
        # value : voltage
        left_value = self.ADS.readADC(self.LEFT_PIN)*self.f - self.LEFT_OFFSET
        right_value = self.ADS.readADC(self.RIGHT_PIN)*self.f - self.RIGHT_OFFSET

        return left_value, right_value

    def get_angle(self):
        left_value, right_value = self.measure_value()

        left_angle = int(left_value * self.RATIO)
        right_angle = int(right_value * self.RATIO)

        return left_angle, right_angle