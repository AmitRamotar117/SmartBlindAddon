# Smart Blind addon device code
# developed by Amit Punit n01203930
import RPi.GPIO as GPIO
from time import sleep
import board
import busio
from smbus2 import SMBus
from mlx90614 import MLX90614
import adafruit_bh1750
import time
import pyrebase
import datetime

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
    print(sechedule)
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
    temp = tempsensor.get_object_1()
    #print("Ambient Temperature :", tempsensor.get_ambient())
    t = "{:.0f}".format(temp)
    print(t," Degrees Celcius")
    return t

#gets data from the BH1750 light sensor
def get_luxData():
    lux = luxsensor.lux
    #print("%.2f LUX" % luxsensor.lux)
    l = "{:.0f}".format(lux)
    print(l," LUX")
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
    newd = dis
    h = float(bheight)
    while newd < h: #loop to get new distance values untill they are less then the users blind height
        if dis > h :
            newd = get_distance()
            print(newd)
    return newd

def get_current_date_time():
    now = datetime.datetime.now()
    d = now.strftime("%d-%m-%Y")
    t = now.strftime("%H:%M")
    dateAndTime = {
        "date": d,
        "time": t
        }
    print(dateAndTime)
    return dateAndTime

# compares current date and time to see if blind should be opened or closed
def compare_time(uTime,curTime):
    if uTime["date"] == curTime["date"]: # compare dates
        if uTime["time"] == curTime["time"]: #if true compare time
            return uTime["op"] #if true will return desired operation.
    if uTime["date"] != curTime["date"]:
        return False

def test_device():
    light = get_luxData() #gets data from light sensor
    send_lux(light) # sends data to database
    temperature = get_tempData()
    send_temp(temperature)
    distance = get_distance()
    led_on()
    sleep(1)
    led_off()
    sleep(1) 
    motor_open()
    sleep(1)
    motor_close()
    sleep(1)
    motor_stop()
    sleep(3)
    print("____________________________________")


bheight = float(get_height())
blindStatus = ""

def open_protocol(): # runs when user wants to open blind
    cd = get_distance()
    led_on()
    while cd > 5: # this is the loop to open blind
        motor_open() #operate blind for 1 second
        sleep(0.5)
        cd = get_distance() # get the new height of the blind
    motor_stop()
    led_off() 
         
def close_protocol(): # runs when user wants to close the blind
    cd = get_distance()
    led_on()
    while cd < bheight: # this is the loop to open blind
        motor_close() #operate blind for 1 second
        sleep(0.5)
        cd = get_distance() # get the new height of the blind
    motor_stop()
    led_off() 

def schedule_blind_protocol(userSetTime):
    cT = get_current_date_time()
    s = compare_time(userSetTime,cT)
    return s

def check_max_light(cLight,mxLight):
    if cLight > mxLight:
         return True
    else:
        return False    

def check_min_light(cLight,mnLight):
    if cLight < mnLight:
         return True
    else:
        return False

def check_max_temp(cTemp,mxTemp):
    if cTemp > mxTemp:
         return True
    else:
        return False    

def check_min_temp(cTemp,mnTemp):
    if cTemp < mnTemp:
         return True
    else:
        return False 

while True:
    light = get_luxData() #gets data from light sensor
    send_lux(light) # sends data to database
    
    temperature = get_tempData() #gets temperature data from sensor
    send_temp(temperature) # send temperature to firebase database.
    
    mode = db.child("0001").child("Mode").get().val()
    print(mode)
    
    minTemp = db.child("0001").child("UTemp").child("minTemp").get().val()
    maxTemp = db.child("0001").child("UTemp").child("maxTemp").get().val()
    print("minTemp, ", minTemp)
    print("maxTemp, ", maxTemp)
    
    minLight = db.child("0001").child("ULight").child("minLight").get().val()
    maxLight = db.child("0001").child("ULight").child("maxLight").get().val()
    print("minLight, ", minLight)
    print("maxLight, ", maxLight)
    
    tempt = int(float(temperature))
    
    cmaxl = False
    cmaxl = check_max_light(int(light),int(maxLight))
    
    cminl = False
    cminl = check_min_light(int(light),int(minLight))
    
    cmaxt = False
    cmaxt = check_max_temp(tempt,int(maxTemp))
    
    cmint = False
    cmint = check_min_temp(tempt,int(minTemp))
    
    userTime = get_schedule() #gets current sehedule from database
    sechCheck = schedule_blind_protocol(userTime) #compares current date and time to see if correct if incorrect return false if correct return operation. 
    
    if mode == "man":
        statusCheck = blind_status(blindStatus) 
        if statusCheck == False:
            cmd = get_command()
            if cmd == "open": #run open blind routine
                print("blind is to be opened")
                open_protocol()
                print("Blinds is now opened")
                update_status("open") #updates current blind status in firebase 
                blindStatus = "open"  #updates current blind status on device
                
            if cmd == "close": #run close blind routine
                print("blind is to be closed")
                close_protocol()
                update_status("close") #updates current blind status in firebase 
                blindStatus = "close"  #updates current blind status on device
                print("Blinds is now closed")
    
    
    if mode == "auto":
        if sechCheck == "Open":
             print("blind is to be opened")
             open_protocol()
             print("Blinds is now opened")
             update_status("open") #updates current blind status in firebase 
             blindStatus = "open"  #updates current blind status on device
         
        if sechCheck == "Close":
             print("blind is to be opened")
             close_protocol()
             print("Blinds is now opened")
             update_status("close") #updates current blind status in firebase 
             blindStatus = "close"  #updates current blind status on device
        
        if cmaxl == True or cmaxt == True:
            if blindStatus == "open":
                print("blind is to be closed")
                close_protocol()
                update_status("close")
                blindStatus = "close"  #updates current blind status on device
        
        
        if cminl == True or cmint == True:
            if blindStatus == "close":
                print("blind is to be opened")
                open_protocol()
                print("Blinds is now opened")
                update_status("open") #updates current blind status in firebase 
                blindStatus = "open"  #updates current blind status on device

    print("Current status", blindStatus)
    print("______________________________________________________________")
    sleep(2)
    
GPIO.cleanup()

