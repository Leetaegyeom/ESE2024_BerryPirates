
import os
import time
import ADS1x15


ADS = ADS1x15.ADS1115(1, 0x48)

print(os.path.basename(__file__))
print("ADS1X15_LIB_VERSION: {}".format(ADS1x15.__version__))

# set gain to 4.096V max
ADS.setGain(ADS.PGA_4_096V)
f = ADS.toVoltage()

while True :
    val_0 = ADS.readADC(0)
    val_1 = ADS.readADC(1)
    val_2 = ADS.readADC(2)
    val_3 = ADS.readADC(3)

    print("Analog0: {0:d}\t{1:.3f} V".format(val_0, val_0 * f))
    print("Analog1: {0:d}\t{1:.3f} V".format(val_1, val_1 * f))
    print("Analog2: {0:d}\t{1:.3f} V".format(val_2, val_2 * f))
    print("Analog3: {0:d}\t{1:.3f} V".format(val_3, val_3 * f))

    time.sleep(0.1)