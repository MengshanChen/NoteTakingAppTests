import smtplib, ssl
from email import encoders
import os
import sys
import logging
import subprocess

# set export JAVA_HOME=$(/usr/libexec/java_home -v 1.8) in terminal first
os.chdir("Swiftnotes-master")
NumberLoop = sys.argv[1]
# log a time message 
logging.basicConfig(format='%(asctime)s.%(msecs)03d %(levelname)s {%(module)s} %(message)s', datefmt='%Y-%m-%d,%H:%M:%S', level=logging.INFO)
logging.info('start time')
msg = ""

for i in range(int(NumberLoop)):
    s = subprocess.check_output("./gradlew test", shell=True)
    #print(type(s))
    msg = str(s, 'utf-8')
    if("BUILD SUCCESSFUL" in msg):
        msg = "Pass"
    else:
        msg = "Fail"
    print('result %d : %s' % (i, msg))

logging.basicConfig(format='%(asctime)s.%(msecs)03d %(levelname)s {%(module)s} %(message)s', datefmt='%Y-%m-%d,%H:%M:%S', level=logging.INFO)
logging.info('end time')

if len(sys.argv) > 2 :
    # get params
    print("send email to ", sys.argv[2])
    SMTP_SERVER = 'smtp.gmail.com'
    SMTP_PORT = 587
    SMTP_FROM = 'erica820822@gmail.com'
    SMTP_TO = sys.argv[2]
    password = input("Type your password and press enter: ")

    # Now send the message
    context = ssl.create_default_context()
    with smtplib.SMTP(SMTP_SERVER, SMTP_PORT) as server:
        server.starttls(context=context)
        server.login(SMTP_FROM, password)
        server.sendmail(SMTP_FROM, SMTP_TO, msg)