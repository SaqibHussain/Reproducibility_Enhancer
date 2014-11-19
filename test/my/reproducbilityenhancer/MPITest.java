/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.reproducbilityenhancer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JTextArea;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author saqib
 */
public class MPITest {

    public MPITest() {
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
     * Test of getExecutable method, of class MPI.
     */
    @Test
    public void testGetExecutable() {
        System.out.println("getExecutable");
        String[] nodes = {"192.168.1.61"};
        MPI instance = new MPI("100 100", "", "exec", "", "saqib", "sh7692", nodes, "", "1");
        String expResult = "exec";
        String result = instance.getExecutable();
        assertEquals(expResult, result);
    }

    /**
     * Test of getArgs method, of class MPI.
     */
    @Test
    public void testGetArgs() {
        System.out.println("getArgs");
        String[] nodes = {"192.168.1.61"};
        MPI instance = new MPI("100 100", "", "exec", "", "saqib", "sh7692", nodes, "", "1");
        String expResult = "100 100";
        String result = instance.getArgs();
        assertEquals(expResult, result);
    }

    /**
     * Test of makeHostFile method, of class MPI.
     */
    @Test
    public void testMakeHostFile() throws Exception {
        System.out.println("makeHostFile");
        String[] nodes = {"192.168.1.61"};
        MPI instance = new MPI("100 100", "1", "exec", "10.10.10.1", "saqib", "sh7692", nodes, "", "1");
        instance.makeHostFile();
        File file = new File("hosts");
        assertTrue(file.exists());
        String result = "mpiuser@10.10.10.1 slots=1";
        BufferedReader reader = new BufferedReader(new FileReader("hosts"));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        assertEquals(sb.toString().trim(), result);
    }

    /**
     * Test of fetch method, of class MPI.
     */
    @Test
    public void testFetch() throws Exception {
        System.out.println("fetch");
        String results = "testfile";
        String[] nodes = {"192.168.1.61"};
          MPI instance = new MPI("100 100", "1", "jacobi", "172.16.42.99", "saqib", "sh6791", nodes, "saqhuss/mpi:v5", "1");
        instance.fetch(results);
                File file = new File("testfile");
        assertTrue(file.exists());
        String result = "Testing 123";
        BufferedReader reader = new BufferedReader(new FileReader("results"));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        assertEquals(sb.toString().trim(), result);
    }

}
