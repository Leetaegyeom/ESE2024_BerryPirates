class ADS_STRUCT:
    def __init__(self):
        self.Model = None

        # TO SELECT FORCE SENSOR OR POTENTIOMETER
        self.I2C_BUS_ID = None
        
        # FOR FORCE SENSOR
        self.LEFT_FRONT_PIN = None
        self.LEFT_BACK_PIN = None
        self.RIGHT_FRONT_PIN = None
        self.RIGHT_BACK_PIN = None

        # FOR POTENTIOMETER
        self.LEFT_PIN = None
        self.RIGHT_PIN = None

class ADS_SETTING:
    @staticmethod
    def getSensorParams(Model):

        model_params = GPIO_STRUCT()

        ### FORCE SENSOR ###
        elif Model == 'force':

            model_params.Model = 'force'

            model_params.I2C_BUS_ID = 1
            model_params.LEFT_FRONT_PIN = 0
            model_params.LEFT_BACK_PIN = 1
            model_params.RIGHT_FRONT_PIN = 2
            model_params.RIGHT_BACK_PIN = 3

        ### POTENTIOMETER ### 
        elif Model == 'potentiometer':

            model_params.Model = 'potentiometer'

            model_params.I2C_BUS_ID = 2
            model_params.LEFT_PIN = 0  
            model_params.RIGHT_PIN = 1             

        else:
            raise ValueError('Model invalid')
        
        return model_params