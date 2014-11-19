/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.reproducbilityenhancer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JTextArea;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author saqib
 */
public class DockerTest {

    public DockerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDockerfile method, of class Docker.
     */
    @Test
    public void testGetDockerfile() throws Exception {
        System.out.println("getDockerfile");
        Path path = Paths.get("/tmp/test.txt");
        Files.createDirectories(path.getParent());
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            System.err.println("already exists: " + e.getMessage());
        }
        Docker instance = new Docker();
        instance.getDockerfile("TestFile");
        File file = new File(path.toString());
        assertTrue(file.exists());
        Files.delete(path);
    }

    /**
     * Test of searchImages method, of class Docker.
     */
    @Test
    public void testSearchImages() {
        System.out.println("searchImages");
        String image = "saqhuss";
        Docker instance = new Docker();
        String expResult = "NAME";
        String[] result = instance.searchImages(image).split("\n");;
        String[] res = result[0].split(" ");
        assertEquals(expResult, res[0].trim());
    }

    /**
     * Test of buildDockerfile method, of class Docker.
     */
    @Test
    public void testBuildDockerfile() throws Exception {
        System.out.println("buildDockerfile");
        //Create temp file to copy
        Path path = Paths.get("/tmp/dockerfiletest.txt");
        Files.createDirectories(path.getParent());
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            System.err.println("already exists: " + e.getMessage());
        }
        //Create list of files to add to constructor
        File file = new File(path.toString());
        ArrayList<File> files = new ArrayList<File>();
        files.add(file);
        //Create object
        Docker instance = new Docker("ubuntu", "git", files, "sh6791", "matrix", "v1", "saqhuss", "dockersh6791", "saqhuss@aol.com");
        instance.buildDockerfile();

        //Check if file was copied
        file = new File("dockerfiletest.txt");
        assertTrue(file.exists());
        //Check if ssh-keys were generated
        file = new File("id_rsa");
        assertTrue(file.exists());
        file = new File("id_rsa.pub");
        assertTrue(file.exists());
        file = new File("authorized_keys");
        assertTrue(file.exists());
        //Expected result of dockerfile
        String result = "FROM ubuntu\n"
                + "MAINTAINER saqhuss\n"
                + "RUN apt-get update \n"
                + "RUN apt-get install -y git\n"
                + "RUN useradd -m -p password mpiuser\n"
                + "RUN mkdir /home/mpiuser/.ssh\n"
                + "ADD id_rsa /home/mpiuser/.ssh/id_rsa\n"
                + "ADD id_rsa.pub /home/mpiuser/.ssh/id_rsa.pub\n"
                + "ADD authorized_keys /home/mpiuser/.ssh/authorized_keys\n"
                + "ADD hosts /home/mpiuser/hosts\n"
                + "ADD dockerfiletest.txt /home/mpiuser/dockerfiletest.txt\n"
                + "RUN sudo chown -R mpiuser:mpiuser /home/mpiuser/.ssh\n"
                + "RUN echo 'mpiuser:password' | chpasswd\n"
                + "RUN echo '/usr/sbin/sshd' >> /etc/bash.bashrc";

        BufferedReader reader = new BufferedReader(new FileReader("Dockerfile"));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        //Check result and read in of Dockerfile are equal
        assertEquals(sb.toString().trim(), result);

        //Delete all created temp files
        Files.delete(path);
        Files.delete(Paths.get("dockerfiletest.txt"));
        Files.delete(Paths.get("id_rsa"));
        Files.delete(Paths.get("id_rsa.pub"));
        Files.delete(Paths.get("authorized_keys"));
    }

    /**
     * Test of setPermissions method, of class Docker.
     */
    @Test
    public void testSetPermissions() {
        System.out.println("setPermissions");
        ArrayList<File> files = new ArrayList<File>();
        Docker instance = new Docker("ubuntu", "git", files, "sh6791", "matrix", "v1", "saqhuss", "dockersh6791", "saqhuss@aol.com");
        instance.setPermissions();
        Process p;
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            p = Runtime.getRuntime().exec(new String[]{"docker", "ps"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            line = reader.readLine();
        } catch (Exception e) {

        }
        assertEquals(line.split(" ")[0].trim(), "CONTAINER");
    }
    /**
     * Test of buildContainer method, of class Docker.
     */
    @Test
    public void testBuildContainer() throws Exception {
        System.out.println("buildContainer");
        JTextArea ta = new JTextArea();
        //Create temp host file
        try {
            //"False" in file writer for no append
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("hosts", false)));
            out.println("mpiuser@192.168.1.1");
            out.flush();
        } catch (Exception e) {

        }
        ArrayList<File> files = new ArrayList<File>();
        Docker instance = new Docker("ubuntu", "git", files, "sh6791", "matrix", "v1", "saqhuss", "dockersh6791", "saqhuss@aol.com");
        instance.buildDockerfile();
        instance.buildContainer(ta);
        Process p;
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            p = Runtime.getRuntime().exec(new String[]{"docker", "images"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {

        }
        assertEquals(sb.toString().split("\n")[1].split(" ")[0].trim(), "saqhuss/matrix");
        Files.delete(Paths.get("Dockerfile"));
        Files.delete(Paths.get("hosts"));
        Files.delete(Paths.get("id_rsa"));
        Files.delete(Paths.get("id_rsa.pub"));
        Files.delete(Paths.get("authorized_keys"));
    }

    /**
     * Test of login method, of class Docker.
     */
    @Test
    public void testLogin() {
        System.out.println("login");
        ArrayList<File> files = new ArrayList<File>();
        Docker instance = new Docker("ubuntu", "git", files, "sh6791", "matrix", "v1", "saqhuss", "dockersh6791", "saqhuss@aol.com");
        String expResult = "Login Succeeded";
        String result = instance.login();
        System.out.println(result);
        assertEquals(expResult, result.trim());
    }

//    /**
//     * Test of push method, of class Docker.
//     */
//    @Test
//    public void testPush() {
//        System.out.println("push");
//        JTextArea ta = new JTextArea("");
//         ArrayList<File> files = new ArrayList<File>();
//        Docker instance = new Docker("ubuntu", "git", files, "sh6791", "matrix", "v1", "saqhuss", "dockersh6791", "saqhuss@aol.com");
//        instance.push(ta);
//        System.out.println(ta.getText());
//        assertEquals("Sending Image List", ta.getText().split("\n")[0].trim());
//    }
}
