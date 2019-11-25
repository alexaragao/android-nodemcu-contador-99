#include <ESP8266WiFi.h>
#include <WiFiServer.h>

#define pinClock D0
#define pinUpDown D1
#define pinReset D2
 
WiFiServer servidor(8080);
WiFiClient cliente;
int contagem = 0;

const char* ssid = "wifissid";
const char* password = "wifipassword";

void setup() {
  Serial.begin(9600);
 
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
   
  Serial.println(WiFi.localIP());
  servidor.begin();
 
  pinMode(pinClock, OUTPUT);
  pinMode(pinUpDown, OUTPUT);
  pinMode(pinReset, OUTPUT);
}
 
void loop() {
  http();
}

void sendPulseSignal(int pin) {
  digitalWrite(pin, HIGH);
  delay(50);
  digitalWrite(pin, LOW);
  delay(50);
}
 
void http() {
  cliente = servidor.available();
  if (cliente == true) {
    String req = cliente.readStringUntil('\r');
    Serial.println(req);
    
    if (req.indexOf("/increase") > -1) {
      digitalWrite(pinUpDown, HIGH);
      if (contagem < 99){
        sendPulseSignal(pinClock);
        contagem += 1;
      }
    }

    if (req.indexOf("/decrease") > -1) {
      digitalWrite(pinUpDown, LOW);
      if (contagem > 0) {
        sendPulseSignal(pinClock);
        contagem -= 1; 
      }
    }

    if (req.indexOf("/reset") > -1) {
      sendPulseSignal(pinReset);
      contagem = 0;
    }

    cliente.print(prepareHtmlPage(String(contagem)));
    delay(100);
  }
}

String prepareHtmlPage(String dado) {
  String htmlPage =
     String("HTTP/1.1 200 OK\r\n") +
            "Content-Type: text/html\r\n" +
            "Connection: close\r\n" +
            "\r\n" + dado;
  return htmlPage;
}
