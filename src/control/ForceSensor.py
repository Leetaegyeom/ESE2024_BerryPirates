import time
import ADS1x15

class ForceSensor:
    def __init__(self, force_params):
        self.I2C_BUS_ID = force_params.I2C_BUS_ID

        self.LEFT_FRONT_PIN = force_params.LEFT_FRONT_PIN
        self.LEFT_BACK_PIN = force_params.LEFT_BACK_PIN
        self.RIGHT_FRONT_PIN = force_params.RIGHT_FRONT_PIN
        self.RIGHT_BACK_PIN = force_params.RIGHT_BACK_PIN

        self.ADS = ADS1x15.ADS1115(self.I2C_BUS_ID, 0x48)
        self.ADS.setGain(self.ADS.PGA_4_096V)
        
        self.right = None
        self.left = None

        self.MIN_VALUE = 1000
        self.COMPARE_VALUE = 5000

    def measure_value(self):
        left_front_value = self.ADS.readADC(self.LEFT_FRONT_PIN)
        left_back_value = self.ADS.readADC(self.LEFT_BACK_PIN)
        right_front_value = self.ADS.readADC(self.RIGHT_FRONT_PIN)
        right_back_value = self.ADS.readADC(self.RIGHT_BACK_PIN)

        return left_front_value, left_back_value, right_front_value, right_back_value

    def guess_user_purpose(self):
        left_front_value, left_back_value, right_front_value, right_back_value = self.measure_value()

        if abs(left_front_value) < self.MIN_VALUE and abs(left_back_value) < self.MIN_VALUE:
            self.left = "FRONT BACK UP"
        else:
            if left_front_value - left_back_value > self.COMPARE_VALUE:
                self.left = "FRONT DOWN" 
            elif abs(left_front_value - left_back_value) < self.COMPARE_VALUE:
                self.left = "FRONT BACK DOWN" 
            else:
                self.left = "BACK DOWN" 
        
        if abs(right_front_value) < self.MIN_VALUE and abs(right_back_value) < self.MIN_VALUE:
            self.right = "FRONT BACK UP" 
        else:
            if right_front_value - right_back_value > self.COMPARE_VALUE:
                self.right = "FRONT DOWN"
            elif abs(right_front_value - right_back_value) < self.COMPARE_VALUE:
                self.right = "FRONT BACK DOWN"
            else:
                self.right = "BACK DOWN" 

        return self.right, self.left