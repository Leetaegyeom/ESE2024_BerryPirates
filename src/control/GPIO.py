class GPIO_STRUCT:
    def __init__(self):
        self.Model = None

        # FOR ULTRA SONIC SENSOR
        self.TRIG_PIN = None
        self.ECHO_PIN = None

        # FOR LINEAR ACTUATOR 
        self.ENA_PIN = None
        self.IN1_PIN = None
        self.IN2_PIN = None
        self.FREQUENCY = None

class GPIO_SETTING:
    @staticmethod
    def getSensorParams(Model):

        model_params = GPIO_STRUCT()

        ### ULTRASONIC SENSOR ###
        if Model == 'ultrasonic_right':

            model_params.Model = 'ultrasonic_right'

            model_params.TRIG_PIN = 23
            model_params.ECHO_PIN = 24

        elif Model == 'ultrasonic_left':

            model_params.Model = 'ultrasonic_left'

            model_params.TRIG_PIN = 5
            model_params.ECHO_PIN = 6
        
        ### ACTUATOR ###
        elif Model == 'actuator_right_height':

            model_params.Model = 'actuator_right_height'

            model_params.ENA_PIN = 25
            model_params.IN1_PIN = 8
            model_params.IN2_PIN = 7
            model_params.FREQUENCY = 1000

        elif Model == 'actuator_right_angle':

            model_params.Model = 'actuator_right_angle'

            model_params.ENA_PIN = 16
            model_params.IN1_PIN = 20
            model_params.IN2_PIN = 21
            model_params.FREQUENCY = 1000

        elif Model == 'actuator_left_height':

            model_params.Model = 'actuator_left_height'

            model_params.ENA_PIN = 17
            model_params.IN1_PIN = 27
            model_params.IN2_PIN = 22
            model_params.FREQUENCY = 1000

        elif Model == 'actuator_left_angle':

            model_params.Model = 'actuator_left_angle'

            model_params.ENA_PIN = 13
            model_params.IN1_PIN = 10
            model_params.IN2_PIN = 9
            model_params.FREQUENCY = 1000

        else:
            raise ValueError('Model invalid')
        
        return model_params