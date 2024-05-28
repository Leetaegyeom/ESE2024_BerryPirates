import time
import ADS1x15

class Potentiometer:
    def __init__(self, potentiometer_params):
        self.I2C_BUS_ID = potentiometer_params.I2C_BUS_ID

        self.LEFT_PIN = potentiometer_params.LEFT_PIN
        self.RIGHT_PIN = potentiometer_params.RIGHT_PIN

        self.ADC = ADS1x15.ADS1115(self.I2C_BUS_ID, 0x48)
        self.ADC.setGain(self.ADC.PGA_4_096V)

        self.MAX_ANGLE = 270
        self.MAX_VALUE = 26366
        self.RATIO = self.MAX_ANGLE/self.MAX_VALUE
    
    def measure_value(self):
        left_value = self.ADC.readADC(self.LEFT_PIN)
        right_value = self.ADC.readADC(self.RIGHT_PIN)

        return left_value, right_value

    def get_angle(self):
        left_value, right_value = self.measure_value()

        left_angle = int(left_value * self.RATIO)
        right_angle = int(right_value * self.RATIO)

        return left_angle, right_angle