# [AVR Ipla TV Box](https://devtomek.pl/post/5d6e03eae38b6d5860a00e23/avr-iplatv-box-czyli-ipla-tv-na-twoim-telewizorze)

## Run
```
git clone https://github.com/DevTomek-pl/AVR-Ipla-TV-Box.git      
cd ./AVR-Ipla-TV-Box
mvn clean compile
mvn javafx:run
```
  
## Pre-requirements

#### Install:
```
sudo apt-get install maven
sudo apt-get install ant
```

#### Check Java version (required Java 9+):
```
java -version
javac -version
mvn -version
```

## Troubleshooting

#### To change current Java version:
```
sudo update-alternatives --config java
```

#### To change current Java compiler version:
```
sudo update-alternatives --config javac
```

#### To change current Java version for Maven:
```
sudo xed ~/.bashrc
 
Add below line to '.bashrc' file:
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
```

#### To install RxTx lib:
```
sudo apt-get install librxtx-java
```

#### To add current user to 'dialout' group:
```
sudo usermod -a -G dialout $USER
```

