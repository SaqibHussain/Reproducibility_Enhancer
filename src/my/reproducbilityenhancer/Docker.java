/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.reproducbilityenhancer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 *
 * @author saqib
 */
public class Docker {

    private String baseImage;
    private String dependencies;
    private String password;
    private String dUsername;
    private String dEmail;
    private String dPassword;
    private String repo;
    private String tag;
    private String image;
    private ArrayList<File> fileList = new ArrayList<File>();

    public Docker() {
    }

    public Docker(String baseImage, String dependencies, ArrayList<File> fileList, String password, String repo, String tag, String dUsername, String dPassword, String dEmail) {
        this.baseImage = baseImage;
        this.dependencies = dependencies;
        this.fileList = fileList;
        this.password = password;
        this.repo = repo.toLowerCase();
        this.tag = tag.toLowerCase();
        this.dUsername = dUsername;
        this.dPassword = dPassword;
        this.dEmail = dEmail;
        image = String.format("%s/%s:%s", dUsername, repo, tag);
    }

    public void getDockerfile(String dockerfile) {
        ExecuteShellCommand obj = new ExecuteShellCommand();
        String[] cmd = new String[]{"bash", "-c", "cp", dockerfile, " ./"};
        obj.executeCommandWithoutTimeout(cmd);
    }

    public String searchImages(String image) {
        ExecuteShellCommand obj = new ExecuteShellCommand();
        String[] command = {"bash", "-c", "docker search " + image};
        return obj.executeCommandWithTimeout(command);
    }

    public void buildDockerfile() {
        StringBuilder s = new StringBuilder();
        s.append("FROM " + baseImage + "\n");
        s.append("MAINTAINER " + dUsername + "\n");
        s.append("RUN apt-get update \n");
        s.append("RUN apt-get install -y");
        for (String d : dependencies.split("\n")) {
            s.append(" " + d);
        }
        s.append("\nRUN useradd -m -p password mpiuser\n");
        s.append("RUN mkdir /home/mpiuser/.ssh");
        createKeys();
        s.append("\nADD id_rsa /home/mpiuser/.ssh/id_rsa");
        s.append("\nADD id_rsa.pub /home/mpiuser/.ssh/id_rsa.pub");
        s.append("\nADD authorized_keys /home/mpiuser/.ssh/authorized_keys");
        s.append("\nADD hosts /home/mpiuser/hosts");
        s.append(addFiles());
        s.append("\nRUN sudo chown -R mpiuser:mpiuser /home/mpiuser/.ssh");
        s.append("\nRUN echo 'mpiuser:password' | chpasswd");
        s.append("\nRUN echo '/usr/sbin/sshd' >> /etc/bash.bashrc");
        try {
            PrintWriter writer = new PrintWriter("Dockerfile", "UTF-8");
            writer.print(s.toString());
            writer.close();
        } catch (Exception e) {
        }
    }

    private void createKeys() {
        try {
            String[] cmd = {"/bin/bash", "-c", "rm id_rsa*"};
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            cmd = new String[]{"ssh-keygen", "-t", "rsa", "-N", "", "-C", "testkey", "-f", "id_rsa"};
            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            String s = new String(Files.readAllBytes(Paths.get("id_rsa.pub")), Charset.defaultCharset());
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("authorized_keys", true)));
            out.println(s);
            out.flush();
            String[] cmd2 = {"/bin/bash", "-c", "echo " + password + " | sudo -S ssh-add " + Paths.get("id_rsa")};
            p = Runtime.getRuntime().exec(cmd2);
            p.waitFor();
        } catch (Exception e) {

        }
    }

    private String addFiles() {
        StringBuilder s = new StringBuilder();
        for (File f : fileList) {
            String[] cmd = {"cp", f.getPath(), "./"};
            ExecuteShellCommand obj = new ExecuteShellCommand();
            obj.executeCommandWithoutTimeout(cmd);
            s.append("\nADD " + f.getName() + " /home/mpiuser/" + f.getName());
        }
        return s.toString();
    }

    public void setPermissions() {
        ExecuteShellCommand obj = new ExecuteShellCommand();
        String[] cmd = new String[]{"/bin/bash", "-c", "echo " + password + " | sudo -S chmod o+rw /var/run/docker.sock"};
        obj.executeCommandWithoutTimeout(cmd);
    }

    public void buildContainer(JTextArea ta) {
        ExecuteShellCommand obj = new ExecuteShellCommand();
        String[] cmd = new String[]{"bash", "-c", "docker build -t " + dUsername + "/" + repo + ":" + tag + " ."};
        obj.executeCommandWithoutTimeout(cmd, ta);
    }

    public String login() {
        ExecuteShellCommand obj = new ExecuteShellCommand();
        String[] cmd = new String[]{"docker", "login", "-u", dUsername, "-p", dPassword, "-e", dEmail};
        return obj.executeCommandWithoutTimeout(cmd);
    }

    public void push(JTextArea ta) {
        ExecuteShellCommand obj = new ExecuteShellCommand();
        String[] cmd = new String[]{"bash", "-c", "docker push " + image};
        obj.executeCommandWithoutTimeout(cmd, ta);
    }
}
