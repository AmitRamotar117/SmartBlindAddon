import RPi.GPIO as GPIO
from time import sleep
import board
import busio
from smbus2 import SMBus
from mlx90614 import MLX90614
import adafruit_bh1750
import time
import pyrebase

#init code for temperature sensor
bus = SMBus(1)
tempsensor = MLX90614(bus, address=0x5A)

#init code for lux sensor
i2c = board.I2C()
luxsensor = adafruit_bh1750.BH1750(i2c)

#init code for distance sensor
GPIO.setwarnings(False)
TRIG = 23
ECHO = 24

#init code of dc motor and l293d
GPIO.setmode(GPIO.BCM) # set borad to gpio
INO = 26 #Pin 37
INT = 19 #Pin 35
EN  = 13 #Pin 33
GPIO.setup(INO,GPIO.OUT)
GPIO.setup(INT,GPIO.OUT)
GPIO.setup(EN,GPIO.OUT)

#led init
LED = 11 #Pin 23
GPIO.setup(LED,GPIO.OUT)

#Pyrebase setup config
# https://tutorial.cytron.io/2020/12/09/send-data-to-firebase-using-raspberry-pi/
# https://www.youtube.com/watch?v=_cdQ0BsMt-Y&t=44s
# https://github.com/thisbejim/Pyrebase
config = {
  "apiKey": "PnU2aAR7J0EZDzPAASX6VoeTUrEIl25siW2EUyLi",
  "authDomain": "smartblindaddon.firebaseapp.com",
  "databaseURL": "https://smartblindaddon-default-rtdb.firebaseio.com",
  "storageBucket": "smartblindaddon.appspot.com"
}
firebase = pyrebase.initialize_app(config)
db = firebase.database()


#this function will activate the motor and turn it clockwise
def motor_open():
    print("opening blind")
    GPIO.output(INO,GPIO.HIGH)
    GPIO.output(INT,GPIO.LOW)
    GPIO.output(EN,GPIO.HIGH)
    
#this function will activiate the motor and trun it counterclockwise
def motor_close():
    print("closeing blind")
    GPIO.output(INO,GPIO.LOW)
    GPIO.output(INT,GPIO.HIGH)
    #pwm.ChangeDutyCycle(50)
    GPIO.output(EN,GPIO.HIGH)

#this function will stop the motor   
def motor_stop():
    print ("Stopping motor")
    GPIO.output(EN,GPIO.LOW)
    
#this function will send temperatue data to the firebase database
def send_temp(temp):
    temp = str(temp)
    db.child("0001").child("Temperature").set(temp)

#this will get the current blind command from database used to either open or close the blind    
def get_command():
    cmd = db.child("0001").child("Status").get()
    print("Current command", cmd.val())
    return cmd.val()

#used to set the status of the blind on the firebase database
def update_status(currentStatus):
    db.child("0001").child("Status").set(currentStatus)
    print("updated status to ", currentStatus)
    

#this function will send lux data to the firebase database
def send_lux(lux):
    lux = str(lux)
    db.child("0001").child("Light").set(lux)

#this function will get the height of the blind from the firebase database
def get_height():
    h = db.child("0001").child("Height").get()
    print(h.val())
    return int(h.val())

#this function will get the date and time the user wants the blind to open or close
def get_schedule():
    date = db.child("0001").child("Schedule").child("date").get()
    op = db.child("0001").child("Schedule").child("operation").get()
    time = db.child("0001").child("Schedule").child("time").get()
    sechedule = {
        "date": date.val(),
        "op": op.val(),
        "time": time.val() 
        }
    return sechedule

# this function will check the blinds current status to see if it matches the command in the database
def blind_status(currentStatus):
    command = db.child("0001").child("Status").get()
    if currentStatus == command.val():
        return True # do nothing
    else:
        return False # either open or close blind

#turns on the led
def led_on():
    GPIO.output(LED,GPIO.HIGH)
    print("led on")

#turns off the led
def led_off():
    GPIO.output(LED,GPIO.LOW)
    print("led off")

# gets data from temp sensor
def get_tempData():
    temp = tempsensor.get_ambient()
    #print("Ambient Temperature :", tempsensor.get_ambient())
    t = "{:.0f}".format(temp)
    print(t)
    return t

#gets data from the BH1750 light sensor
def get_luxData():
    lux = luxsensor.lux
    #print("%.2f LUX" % luxsensor.lux)
    l = "{:.0f}".format(lux)
    print(l)
    return l

#gets reading from the hc sr04 distance sensor
def get_distance():
    print ("Distance Measurement In Progress")
    GPIO.setup(TRIG,GPIO.OUT)
    GPIO.setup(ECHO,GPIO.IN) 

    GPIO.output(TRIG,False)
    print ("Waiting For Sensor To Settle")
    time.sleep(2) 

    GPIO.output(TRIG, True)
    time.sleep(0.00001)
    GPIO.output(TRIG, False) 

    while GPIO.input(ECHO)==0:
        pulse_start = time.time()

    while GPIO.input(ECHO)==1:
        pulse_end = time.time()

    pulse_duration = pulse_end - pulse_start

    distance = pulse_duration * 17150

    distance = round(distance, 0)
    print ("Distance:",distance,"cm")
    return distance

# to check the reading from the distance sensor
def check_distance(dis):
    while newd < bheight: #loop to get new distance values untill they are less then the users blind height
        if dis > bheight:
            newd = get_distance()
    return newd

def get_current_date_time():
    return

bheight = get_height()
print(bheight)
s = get_schedule()
print(s)
blindStatus = ""

while True:
    light = get_luxData() #gets data from light sensor
    send_lux(light) # sends data to database
    
    temperature = get_tempData()
    send_temp(temperature)
    
    statusCheck = blind_status(blindStatus)
    
    if statusCheck == False:
        cmd = get_command()
        if cmd == "open":
            #run open blind routine
            print("blind is to be opened")
            update_status(cmd) #updates current blind status in firebase 
            blindStatus = cmd  #updates current blind status on device
        if cmd == "close":
            #run close blind routine
            print("blind is to be closed")
            update_status(cmd) #updates current blind status in firebase 
            blindStatus = cmd  #updates current blind status on device
        
    
    print(blindStatus)
    
    distance = get_distance()
    
    led_on()
    sleep(5)
    led_off()
    sleep(5) 
    
    motor_open()
    sleep(1)
    motor_close()
    sleep(1)
    motor_stop()
    sleep(3)
    
    print("")
    print("")
    
    
GPIO.cleanup()

