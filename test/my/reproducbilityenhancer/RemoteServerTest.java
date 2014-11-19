/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.reproducbilityenhancer;

import java.io.InputStream;
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
public class RemoteServerTest {
    
    public RemoteServerTest() {
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
     * Test of setCommand method, of class RemoteServer.
     */
    @Test
    public void testSetCommand() {
        System.out.println("setCommand");
        String command = "";
        RemoteServer instance = null;
        instance.setCommand(command);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of runSudo method, of class RemoteServer.
     */
    @Test
    public void testRunSudo() {
        System.out.println("runSudo");
        RemoteServer instance = null;
        String expResult = "";
        String result = instance.runSudo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class RemoteServer.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        RemoteServer instance = null;
        String expResult = "";
        String result = instance.run();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendFile method, of class RemoteServer.
     */
    @Test
    public void testSendFile() {
        System.out.println("sendFile");
        String lfile = "";
        String rfile = "";
        RemoteServer instance = null;
        instance.sendFile(lfile, rfile);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of recevieFile method, of class RemoteServer.
     */
    @Test
    public void testRecevieFile() {
        System.out.println("recevieFile");
        String lfile = "";
        String rfile = "";
        RemoteServer instance = null;
        instance.recevieFile(lfile, rfile);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkAck method, of class RemoteServer.
     */
    @Test
    public void testCheckAck() throws Exception {
        System.out.println("checkAck");
        InputStream in = null;
        int expResult = 0;
        int result = RemoteServer.checkAck(in);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
