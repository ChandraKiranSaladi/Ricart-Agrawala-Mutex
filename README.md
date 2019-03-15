## Ricart-Agrawala Algorithm with Roucairol-Carvalho Optimization

O.S.F. Carvalho and G. Roucairol. On Mutual Exclusion in Computer Networks (Technical Correspondence). Communications of the ACM, February 1983.
***
### Technical Specs
    * Java-8
    * Ubuntu
    * Shell script
  
  AOS & AOSServer Folder contains the source files for Client & Server respectively
***   
### **Execution** 
1) Unzip or clone the project.
2) Update the respective paths in configAOS.txt & configAOSServer.txt to select the paths for your Servers to host the files (Path should be inside the DC Machine). No need to create directories, code takes care of it. *Config files guide the shell script to the locations of each node in the network*.
3) As per the project description Executing the code on DC machines is mandatory. Make sure you have password less login setup before you run the Shell script. Details can be found below. ( To access DC Machines you have to be a UTD Student )
   <br/> <h4><a href="#login">Passwordless login</a></h4>
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
9)  launchServer.sh" will setup the terminals for Server<br>
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
    View your files in the Server. All the respective files will have the same values.

* The Details of the Project Description can be found
***
## <a href="login">Password Less Login</a>
1) Open terminal, type 
 ssh netid@csgrads1.utdallas.edu
2) enter password
3) type
ssh dc02 
4)enter password
5) type
ssh-keygen -t rsa
6) //Just press enter for passphrase … … 
The key fingerprint is: … something … , press enter
The keys randomart image is: … … (special image), type enter 
7) dc02: will be your current system. 
type 
cd~ 
8) type
cd .ssh
9) now you will be in dc02/.ssh directory
10) type pwd
copy this path
11) type
cat id_rsa.pub >> authorized_keys
12) type
sftp netid@csgrads1.utdallas.edu
enter your password
13) type
Get your_path_previously_copied/id_rsa .ssh
now, you dont have to enter password to enter to any dc machine from csgrads1.
 
14) now type exit press enter, type exit and again press enter
15) now you will be in your local machine
type 
sftp netid@csgrads1.utdallas.edu
enter your password
16) type
Get your_path_previously_copied/id_rsa .ssh
Now you can login without using password 

***
### Prerequisites
* Java-8
* MultiThreading and Socket Programming
* Basic Data Structures and Protocol understanding

### Description:

This is an interesting algorithm which takes <= 2(N-1) messages to enter the critical Section, unlike Lamports Mutex which utilizes 3(N-1).

## Note
    Changing UIDs to a different order than 0 1 2 3 4 , throws exception. Authorize, reply deferred information is implemented as arrays for simplicity, not HashMaps.
    The Given code will not work in LocalHost. Check the other branch to test it on the Windows LocalMachine
    
#### Feedback
A lot of Refactoring can make this code really elegant.   