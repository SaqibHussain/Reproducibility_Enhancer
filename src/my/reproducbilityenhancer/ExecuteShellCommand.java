/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.reproducbilityenhancer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author saqib
 */
public class ExecuteShellCommand {

    public String executeCommandWithTimeout(String[] command) {
        Process p;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            p = Runtime.getRuntime().exec(command);
            long now = System.currentTimeMillis();
            long timeoutInMillis = 1000L * 10;
            long finish = now + timeoutInMillis;
            while (isAlive(p)) {
                Thread.sleep(10);
                if (System.currentTimeMillis() > finish) {
                    p.destroy();
                    throw new Exception("Timeout error");
                }
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String executeCommandWithoutTimeout(String[] command) {
        Process p;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void executeCommandWithoutTimeout(String[] command, javax.swing.JTextArea ta) {
        Process p;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = reader.readLine()) != null) {
                ta.setText(ta.getText() + "\n" + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

}
