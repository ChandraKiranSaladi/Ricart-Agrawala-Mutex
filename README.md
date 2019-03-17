## Ricart-Agrawala Algorithm with Roucairol-Carvalho Optimization

O.S.F. Carvalho and G. Roucairol. On Mutual Exclusion in Computer Networks (Technical Correspondence). Communications of the ACM, February 1983.
***
### Technical Specs
    * Java-8
    * Ubuntu
    * Shell script
  
  AOS & AOSServer Folder contains the source files for Client & Server respectively
***   
### Prerequisites
* Java-8
* MultiThreading and Socket Programming
* Basic Data Structures and Protocol understanding

### Description:

This is an interesting algorithm which takes <= 2(N-1) messages to enter the critical Section, unlike Lamports Mutex which utilizes 3(N-1). For more 
***
## Important Note

1) If you run directly after Unzipping the code, the code should run fine. Check the filePaths and if you have password less login setup. 
   <h4><a href="#password-less-login">Passwordless login</a></h4> automates spawning of terminals without physically entering them
   
2) The project is created in Eclipse, therefore launcher.sh and launchServer.sh after execution compiles the code to /bin directory. Change the shell scripts if you don't want it to compile the source code. 
   
3) Changing UIDs to a different order than 0 1 2 3 4 , throws exception. 
   
4) Authorize, reply deferred, using, waiting information is implemented as arrays for simplicity, not HashMaps.
   
5) The Given code will not work in LocalHost. Check the other branch to test it on the Windows LocalMachine
   
6) configAOS.txt should have unique DC Machine as their hostname. Code will not work if 2 UIDs have same hostname
   
7) File Server HostName and Ports are harcoded in the RicartAgrawala.java for simplicity
   
8) ParseConfigFile.java reads lines from readFile.txt in a specified format. Check the code for more understanding.
***
### **Execution** 
1) Unzip or clone the project.
2) Update the respective paths in configAOS.txt & configAOSServer.txt to select the paths for your Servers to host the files (Path should be inside the DC Machine). No need to create directories, code takes care of it. *Config files guide the shell script to the locations of each node in the network*.
3) As per the project description Executing the code on DC machines is mandatory. Make sure you have password less login setup before you run the Shell script. Details can be found below. ( To access DC Machines you have to be a UTD Student )
   <br/> <h4><a href="#password-less-login">Passwordless login</a></h4>
4) Update the paths inside ParseConfigFile.java, readFile.txt.
5) **Config files and shell scripts will only be in your local machine**
   All the code will reside in the Server. Copying the files in csgrads1 will update the code in all DC machines as DC Machine implements Distributed Shared File System
6) Push the code in csgrads1.utdallas.edu, by using winscp or sftp
7) Give executable permission to the scripts
   ```shell
    $chmod +x *.sh

8) Compiles the java code
    ```shell
    $ cd AOS
    $ javac -cp "./bin" -d "./bin" ./src/*.java
    $ cd ../AOSServer
    $ javac -cp "./bin" -d "./bin" ./src/*.java
    ```

9)  "launchServer.sh" will setup the terminals for Server<br>
    "launch.sh" sets up the terminals for Clients<br>
    "cleanup.sh" closes all the ssh connections of the terminals <br>

10) Open a terminal and go into the directory of shell scripts
    ```shell
    $ ./launchServer.sh 
    ```
11) Open a terminal and go into the directory of shell scripts
    ```shell
    $ ./launch.sh
    ```
12) Terminals will pop up and start executing the code.
    In the end view your files in the Server. All the respective files will have the same values.

13) Open a terminal and go into the directory of shell scripts
    ```shell
    $ ./cleanup.sh
    ```
This cleanups all the sockets you have used in the dc machines.
* The Details of the Project Description can be found in Project_1 Description.pdf
***
## [Password Less Login](#password-less-login)
Note: $ is a prompt in the terminal: Ignore $ while copying these commands into the terminal

1) Open terminal, type
```shell
  $ cd
  $ cd .ssh
``` 
Now you'll be in the .ssh directory

2) Type
```shell
  $ ssh netid@dc01.utdallas.edu
```
3) if prompted, 
```shell
  Are you sure you want to continue connecting (yes/no)? 
```
Type yes, then enter

4) enter password 

dc01: will be your current system. 

5) Type
```shell
  $ cd .ssh
```
Now you'll be in dc01/.ssh directory

6) Type
```shell
  $ ssh-keygen -t rsa
```
 Just press enter for passphrase … … <br>
The key fingerprint is: … something … , hit enter <br>
The keys randomart image is: … … (special image), hit enter <br>

10) type 
```shell
  $ pwd
```
copy this path

11) type
```shell
  $ cat id_rsa.pub >> authorized_keys
```
12) type
```shell
  $ exit
```
Now you'll be in your local machine

12) type
```shell
  $ sftp netid@dc01.utdallas.edu
```
13) enter your password

14) Type
```shell
  sftp> lcd .ssh
```
Now you'll be in dc01/.ssh directory

13) Type
```shell
  sftp> get id_rsa
```

14) type
```shell
  $ exit
```
Now you'll be in your local machine
* You dont have to enter password to enter to any dc machine from your local machine now.
***


#### Feedback
Some effort in refactoring can make this code really elegant. <br>
So, I'd appreciate any suggestions
***