import os
import sys
import logging
import signal
import subprocess
import time
NumberLoop = sys.argv[1]
NumberProcess = sys.argv[2]

for i in range(int(NumberProcess)):
    print("Stress test", i)

    logging.basicConfig(format='%(asctime)s.%(msecs)03d %(levelname)s {%(module)s} %(message)s', datefmt='%Y-%m-%d,%H:%M:%S', level=logging.INFO)
    logging.info('start time for process')
    proc = subprocess.Popen("python runTestSuite.py " + NumberLoop + ' &', shell=True)
    logging.basicConfig(format='%(asctime)s.%(msecs)03d %(levelname)s {%(module)s} %(message)s', datefmt='%Y-%m-%d,%H:%M:%S', level=logging.INFO)
    logging.info('end time for process')
    proc.wait()


