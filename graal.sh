sudo mkdir -p /usr/lib/jvm

# Download (Example for x64 - replace with aarch64 if on Pi)
#wget https://download.oracle.com/graalvm/25/latest/graalvm-jdk-25_linux-x64_bin.tar.gz
#RPI 5
wget https://download.oracle.com/graalvm/25/latest/graalvm-jdk-25_linux-aarch64_bin.tar.gz

# Extract
sudo tar -xzf graalvm-jdk-25_linux-aarch64_bin.tar.gz -C /usr/lib/jvm/

# Find the exact folder name created (likely jdk-25.x.x)
ls /usr/lib/jvm/

#graalvm-jdk-25.0.2+10.1

# Register 'java'
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/graalvm-jdk-25.0.2+10.1/bin/java 2500

# Register 'javac'
sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/graalvm-jdk-25.0.2+10.1/bin/javac 2500

sudo update-alternatives --install /usr/bin/native-image native-image /usr/lib/jvm/graalvm-jdk-25.0.2+10.1/bin/native-image 2500

#sudo update-alternatives --config java

export JAVA_HOME=/usr/lib/jvm/graalvm-jdk-25.0.2+10.1  # Update with your actual folder name
export PATH=$JAVA_HOME/bin:$PATH

# Clean and build the native executable
#./mvnw -Pnative native:compile