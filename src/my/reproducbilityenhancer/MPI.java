/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.reproducbilityenhancer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

/**
 *
 * @author saqib
 */
public class MPI {

    private String args;
    private String numProc;
    private String executable;
    private String startIP;
    private String username;
    private String password;
    private String[] nodes;
    private String image;
    private String slots;
    List<String> containers = new ArrayList<String>();

    public MPI(String args, String numProc, String executable, String startIP, String username, String password, String[] nodes, String image, String slots) {
        this.args = args;
        this.numProc = numProc;
        this.executable = executable;
        this.startIP = startIP;
        this.username = username;
        this.password = password;
        this.nodes = nodes;
        this.image = image;
        this.slots = slots;
    }

    public String getExecutable() {
        return this.executable;
    }

    public String getArgs() {
        return this.args;
    }

    public void makeHostFile() {
        StringBuilder hosts = new StringBuilder();
        int startHost = Integer.parseInt(startIP.substring(startIP.lastIndexOf(".") + 1));
        String host = startIP.substring(0, startIP.lastIndexOf(".") + 1);
        for (int i = 0; i < nodes.length; i++) {
            hosts.append("mpiuser@" + host + startHost + " slots=" + slots + "\n");
            startHost++;
        }
        try {
            //"False" in file writer for no append
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("hosts", false)));
            out.println(hosts.toString().trim());
            out.flush();
        } catch (Exception e) {

        }
    }

    public String runMPI(JTextArea ta) {       
        //Send host file to head node
        RemoteServer rs = new RemoteServer(nodes[0], username, password);
        rs.sendFile("hosts", "hosts");

        String[] command = new String[]{"mpiexec -n " + numProc + " --machinefile hosts " + executable + " " + args};
        ExecuteShellCommand obj = new ExecuteShellCommand();
        return obj.executeCommandWithoutTimeout(command);
    }

    public void deployContainers(JTextArea ta) {
        //Start containers on all nodes
        int startHost = Integer.parseInt(startIP.substring(startIP.lastIndexOf(".") + 1));
        String host = startIP.substring(0, startIP.lastIndexOf(".") + 1);
        for (String s : nodes) {
            ta.setText("\n" + ta.getText() + "Configuring: " + s);
            RemoteServer rs = new RemoteServer(s, username, password);
                    rs.setCommand("docker run -d -i -t --net=none " + image + " /bin/bash");
                    String id = rs.runSudo();
                    containers.add(id);
                    rs.setCommand("docker inspect -f '{{.State.Pid}}' " + id);
                    String pid = rs.runSudo();
                    pid = pid.trim();
                    rs.setCommand("mkdir -p /var/run/netns");
                    rs.runSudo();
                    rs.setCommand("ln -s /proc/" + pid + "/ns/net /var/run/netns/" + pid);
                    rs.runSudo();
                    rs.setCommand("ip link delete A");
                    rs.runSudo();
                    rs.setCommand("ip link add A type veth peer name B");
                    rs.runSudo();
                    rs.setCommand("brctl addif docker0 A");
                    rs.runSudo();
                    rs.setCommand("ip link set A up");
                    rs.runSudo();
                    rs.setCommand("ip link set B netns " + pid);
                    rs.runSudo();
                    rs.setCommand("ip netns exec " + pid + " ip link set dev B name eth0");
                    rs.runSudo();
                    rs.setCommand("ip netns exec " + pid + " ip link set eth0 up");
                    rs.runSudo();
                    rs.setCommand("ip netns exec " + pid + " ip addr add " + host + startHost + "/16 dev eth0");
                    rs.runSudo();
                    rs.setCommand("ifconfig docker0 | grep 'inet addr' | awk -F: '{print $2}' | awk '{print $1}'");
                    String interfaceIP = rs.runSudo();
                    rs.setCommand("ip netns exec " + pid + " ip route add default via " + interfaceIP);
                    rs.runSudo();;
            startHost++;
        }
    }

    public void removeContainers(JTextArea ta) {
        for (String s : nodes) {
            ta.setText(ta.getText() + "Removing containers & networking from: " + s);
            RemoteServer rs = new RemoteServer(s, username, password);
            rs.setCommand("docker stop " + containers.get(0));
            containers.remove(0);
            rs.runSudo();
        }
    }

    public void fetch(String results) {
        RemoteServer rs = new RemoteServer(nodes[0], username, password);
        rs.recevieFile("results", results);
    }
}
