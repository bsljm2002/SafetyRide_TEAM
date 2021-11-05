#include<SoftwareSerial.h>//블루투스 통신을 위한 헤더파일

SoftwareSerial btSerial(7,8);

int x,y,z;
int xpin=A0;
int ypin=A1;
int zpin=A2;
int ledPin=13;
int sensorValue;


void setup()
{
 Serial.begin(9600);// 통신 속도 9600bps로 PC와 시리얼 통신 시작
 btSerial.begin(9600);  //통신 속도 9600bps로 블루투스 통신 시작
 pinMode(ledPin,OUTPUT);
 
}

void loop()
{
 x=analogRead(xpin);
 y=analogRead(ypin);
 z=analogRead(zpin);
 Serial.print("X: ");
 
 delay(300);

 
 int data=1;
 int noData=0;
 
 if(x>450||x<270){
  
  digitalWrite(ledPin,HIGH);
  btSerial.println(data);
  Serial.println(data);
  }//낙상이 감지가 되면 data 1이 블루투스를 통해 전달 됨. 3축 자이로 센서가 올바르게 작동되면 LED가 켜짐
  else
  {
    digitalWrite(ledPin,LOW);
    Serial.println(noData);
    btSerial.println(noData);
    } 

}
