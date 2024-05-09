import time
import ADS1x15

class Potentiometer:
    def __init__(self, potentiometer_params):
        self.I2C_BUS_ID = potentiometer_params.I2C_BUS_ID

        self.LEFT_PIN = potentiometer_params.LEFT_PIN
        self.RIGHT_PIN = potentiometer_params.RIGHT_PIN

        self.ADS = ADS1x15.ADS1115(self.I2C_BUS_ID, 0x48)
        self.ADS.setGain(self.ADS.PGA_4_096V)
    
    def measure_value(self):
        left_value = self.ADS.readADC(self.LEFT_PIN)
        right_value = self.ADS.readADC(self.RIGHT_PIN)

        return left_value, right_value

    def value_to_angle(self): ## need to change
        left_value, right_value = self.measure_value()

        if left_front_value - left_back_value > 5000:
            self.left = "FRONT DOWN"
        elif abs(left_front_value - left_back_value) < 5000:
            self.left = "FRONT BACK DOWN"
        else:
            self.left = "BACK DOWN"
        
        if right_front_value - right_back_value > 5000:
            self.right = "FRONT DOWN"
        elif abs(right_front_value - right_back_value) < 5000:
            self.right = "FRONT BACK DOWN"
        else:
            self.right = "BACK DOWN"

        return self.right, self.left