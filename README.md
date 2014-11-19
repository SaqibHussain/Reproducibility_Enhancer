Reproducibility Enhancer
======

This project was created as part of my Thesis undertaken at Cranfield University.

The thesis title was: Improving the Reproducibility of Parallel Computation Experiments using Docker Containers.

![alt tag](https://raw.githubusercontent.com/SaqibHussain/Reproducibility_Enhancer/master/ui.png)



Abstract
-----
In recent years, shifts in funding priorities in academia have caused many scientists to adopt a ``publish or perish'' attitude and publish research results that are not of the highest quality, and a number of high-profile retractions have lead to a crisis in public confidence in scientific research. Scientists and programmers from an extensive range of professions including but not limited to neuroscience and computer science are appealing for a better, more refined paradigm by which new research or findings are submitted into the scientific community, while some have already taken on initiatives to improve the repeatability and reproducibility of experiments and analysis, particularly when these are computationally driven. Currently, the most prominent methods for addressing these issues are through the use of virtualisation, typically with virtual machines which suffer from huge hypervisor overheads and lengthy boot times. Within this contest, an easy to use graphical user interface application is proposed which uses Docker, a version-controlled container management system created by and for the web development community, to improve the reproducibility of parallel scientific experiments. The Linux-kernel based containers managed by Docker provide many of the capabilities offered by virtual machine technology in terms of operating system isolation and resource management, but with minimal overheads and near-native performance. The application aims to reduce the drawbacks of virtual machines, provide as much automation as possible to incentivize its use and provide an efficient mechanism for someone else to reproduce the original results of an experiment by outputting configuration files which can be loaded into any instance of the application to re-run the exact original experiment automatically.


Instructions for running the Reproducbility Enhancer application
-----
The application is written in Java and can be opened with any suitable IDE, it must be run from a linux machine on the same network as the compute nodes.

Before running the application, it is necessary to set up networking and dependancies on compute nodes.

Docker and Open vSwitch v2.0 must be installed on compute nodes.

The following script can be used to set up networking between compute nodes to enable Docker containers to communicate:
The REMOTE_IP variable must be set to the relevant remote IP address for the node which will be communicated with.
The BRIDGE_ADDRESS must be a unique adress, preferably sequential, which will be assigned to the docker0 interface

\# The 'other' host
REMOTE_IP=192.168.1.5
\# Name of the bridge
BRIDGE_NAME=docker0
\# Bridge address
BRIDGE_ADDRESS=172.16.42.1/24


\# Deactivate the docker0 bridge
ip link set $BRIDGE_NAME down
\# Remove the docker0 bridge
brctl delbr $BRIDGE_NAME
\# Delete the Open vSwitch bridge
ovs-vsctl del-br br0
\# Add the docker0 bridge
brctl addbr $BRIDGE_NAME
\# Set up the IP for the docker0 bridge
ip a add $BRIDGE_ADDRESS dev $BRIDGE_NAME
\# Activate the bridge
ip link set $BRIDGE_NAME up
\# Add the br0 Open vSwitch bridge
ovs-vsctl add-br br0
\# Create the tunnel to the other host and attach it to the
\# br0 bridge
ovs-vsctl add-port br0 gre0 -- set interface gre0 \
type=gre options:remote_ip=$REMOTE_IP
\# Add the br0 bridge to docker0 bridge
brctl addif $BRIDGE_NAME br0


Once networking is enabled and you are able to ping any docker0 virtual interface from any node, the application is ready to be used.


The first screen you are prompted with when running the application will allow you to run a new experiment. The following instructions will show you how to give correct inputs.

Cluster Config
--------------
Nodes: This is your local list of line separated nodes e.g. 192.168.1.51 192.168.1.52 192.168.1.53
Username: A username with sudo priviliges on the compute nodes
Password: Password for the user
Start host: Host from which to begin building conatiner IP addresses. Must begin above those assigned to the docker0 virtual interfaces.
Slots per node: How many compute slots are available on each compute node.

Docker config
-------------
Username: Docker Hub username
Password: Docker Hub password
Email:    Docker Hub Email

MPI config
----------
Number of processors: How many processors to execute the MPI application with.
Executable name: The name of the executable MPI file copied to the container.
Execution Arguments: Any run time arguments to be supplied
Fecth MPI Results file: Check box if you want a results file to be fectched from the head node after computation.

Conatiner Config
----------------
Provide Dockerfile manually: Check if you have written your own Dockerfile and do not want the application to build one. Must supply a file if this is checked.
Container Dependencies: Line seperated list of any applications to install on the container
Base Image: Can be used to search for a base image e.g. ubuntu/base
Files to add to container: Press button to copy local files to container
Container Tag: value to tag container with when pushing to Docker Hub
Repo: Name of repository to push image to.



Notes:
An MPI file must be suppied within the "Files to add to container", this will be the file that is executed. A sample Jacobi MPi application is provided in the Reproducibility_Enhancer folder.



