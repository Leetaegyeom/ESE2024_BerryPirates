
MAX_ANGLE = 270
MAX_VALUE = 26366

RATIO = MAX_ANGLE/MAX_VALUE

import os
import time
import ADS1x15

ADS = ADS1x15.ADS1115(0, 0x48)

print(os.path.basename(__file__))
print("ADS1X15_LIB_VERSION: {}".format(ADS1x15.__version__))

# set gain to 4.096V max
ADS.setGain(ADS.PGA_4_096V)
f = ADS.toVoltage()

while True :
    val_1 = ADS.readADC(0)

    print("Analog1: {0:d}\t{1:.3f} V".format(val_1, val_1 * f))

