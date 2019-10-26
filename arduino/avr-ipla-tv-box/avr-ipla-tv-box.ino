/*
   AVR Ipla TV Box.

   version: v1.1.0
   author: DevTomek.pl
*/
#include <LiquidCrystal.h> // LiquidCrystal by Arduino, Adafruit version 1.0.7
#include <IRremote.h> // IRremote by shirriff version 2.2.3

// LED
const int INFO_LED = 13;

// IR
const int IR_SENSOR = 2;
IRrecv irrecv(IR_SENSOR);
decode_results results;

// LCD
const int SCREEN_WIDTH = 16;
const int SCREEN_HEIGHT = 2;
const int RS = 7, EN = 8, D4 = 9, D5 = 10, D6 = 11, D7 = 12;
LiquidCrystal lcd(RS, EN, D4, D5, D6, D7);

// Serial Read
const char READ_DATA_SEPARATOR = '|';
const uint8_t READ_BUFFER_SIZE = 255;
char readBuffer[READ_BUFFER_SIZE];
boolean hasNewData = false;

// Global Variables
String line1 = "AVR-Ipla-TV-Box";
String line1Buffer = line1;
unsigned int line1Length = 16;
String line2 = "    DevTomek    ";
String line2Buffer = line2;
int stringStart, stringStop = 0;
int scrollCursor = SCREEN_WIDTH;
volatile uint8_t timer1 = 0;


void setup() {

  /* Based on: Arduino Timer Interrupts Calculator */
  // TIMER 1 for interrupt frequency 100 Hz:
  cli(); // stop interrupts
  TCCR1A = 0; // set entire TCCR1A register to 0
  TCCR1B = 0; // same for TCCR1B
  TCNT1  = 0; // initialize counter value to 0
  // set compare match register for 100 Hz increments
  OCR1A = 19999; // = 16000000 / (8 * 100) - 1 (must be <65536)
  // turn on CTC mode
  TCCR1B |= (1 << WGM12);
  // Set CS12, CS11 and CS10 bits for 8 prescaler
  TCCR1B |= (0 << CS12) | (1 << CS11) | (0 << CS10);
  // enable timer compare interrupt
  TIMSK1 |= (1 << OCIE1A);
  sei(); // allow interrupts

  Serial.begin(9600);
  Serial.println("Starting initialization...");
  pinMode(INFO_LED , OUTPUT);
  lcd.begin(SCREEN_WIDTH, SCREEN_HEIGHT);
  irrecv.enableIRIn();
  Serial.println("Initialization done!");
}

void loop() {
  readIRData();
  readSerialData();
  updateData();

  if (timer1 > 25) {
    scroll();
    timer1 = 0;
  }

}

ISR(TIMER1_COMPA_vect) {
  timer1++;
}

void readIRData() {
  if (irrecv.decode(&results)) {
    if (results.value != 0xFFFFFFFF) {
      digitalWrite(INFO_LED , HIGH);
      Serial.println(results.value, HEX);
      digitalWrite(INFO_LED , LOW);
    }
    irrecv.resume();
  }
}

void readSerialData() {
  while (hasNewData == false && Serial.available() > 0) {
    static uint8_t ndx = 0;
    char rc = Serial.read();
    if (rc != '\n') {
      readBuffer[ndx] = rc;
      ndx++;
      if (ndx >= READ_BUFFER_SIZE) {
        ndx = READ_BUFFER_SIZE - 1;
      }
    } else {
      readBuffer[ndx] = '\0';
      ndx = 0;
      hasNewData = true;
    }
  }
}

void updateData() {
  if (hasNewData == true) {
    // RX frame structure: [LINE_1_DATA|LINE_2_DATA|IS_FORCE_MODE]
    line1Buffer =  getValue(readBuffer, READ_DATA_SEPARATOR, 0);
    line2Buffer =  getValue(readBuffer, READ_DATA_SEPARATOR, 1);
    boolean isForceMode = getValue(readBuffer, READ_DATA_SEPARATOR, 2) == "true";
    if (isForceMode) {
      updateLcdData();
    }
    hasNewData = false;
  }
}

String getValue(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }

  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}

void scroll() {
  lcd.clear();
  lcd.setCursor(scrollCursor, 0);
  lcd.print(line1.substring(stringStart, stringStop));
  lcd.setCursor(0, 1);
  lcd.print(line2);

  if (stringStart == 0 && scrollCursor > 0) {
    scrollCursor--;
    stringStop++;
  } else if (stringStart == stringStop) {
    updateLcdData(); // update the text as the previous one will be displayed
  } else if (stringStop == line1Length  ) {
    stringStart++;
  } else if (line1Length < SCREEN_WIDTH && stringStop == (SCREEN_WIDTH + line1Length) ) {
    stringStart++;
    stringStop = stringStart;
  } else {
    stringStart++;
    stringStop++;
  }

}

void updateLcdData() {
  stringStart = stringStop = 0;
  scrollCursor = SCREEN_WIDTH;
  line1 = line1Buffer;
  line2 = line2Buffer;
  line1Length =  line1.length();
}
