/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.reproducbilityenhancer;

import com.jcraft.jsch.*;
import java.io.*;

/**
 *
 * @author saqib
 */
public class RemoteServer {

    String host;
    String user;
    String password;
    String command;

    public void setCommand(String command) {
        this.command = command;
    }

//    public String getCommand() {
//        return this.command;
//    }

    public RemoteServer(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public String runSudo() {
        StringBuilder sb = new StringBuilder();
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("sudo -S -p '' " + command);
            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();
            ((ChannelExec) channel).setErrStream(System.err);
            channel.connect();
            out.write((password + "\n").getBytes());
            out.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
        return sb.toString();
    }

    public String run() {
        StringBuilder sb = new StringBuilder();
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            channelExec.setCommand(command);
            channelExec.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            channelExec.disconnect();
            session.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
        return sb.toString();
    }

    public void sendFile(String lfile, String rfile) {
        FileInputStream fis = null;
        try {

            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);

            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            boolean ptimestamp = true;

// exec 'scp -t rfile' remotely
            String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

// get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            if (checkAck(in) != 0) {
                System.exit(0);
            }
            File _lfile = new File(lfile);

            if (ptimestamp) {
                command = "T " + (_lfile.lastModified() / 1000) + " 0";
// The access time should be sent here,
// but it is not accessible with JavaAPI ;-<
                command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
                out.write(command.getBytes());
                out.flush();
                if (checkAck(in) != 0) {
                    System.exit(0);
                }
            }

// send "C0644 filesize filename", where filename should not include '/'
            long filesize = _lfile.length();
            command = "C0644 " + filesize + " ";
            if (lfile.lastIndexOf('/') > 0) {
                command += lfile.substring(lfile.lastIndexOf('/') + 1);
            } else {
                command += lfile;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            }

// send a content of lfile
            fis = new FileInputStream(lfile);
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) {
                    break;
                }
                out.write(buf, 0, len); //out.flush();
            }
            fis.close();
            fis = null;
// send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            }
            out.close();

            channel.disconnect();
            session.disconnect();

        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ee) {
            }
        }
    }

    //lfile = from, rfile = to
    public void recevieFile(String lfile, String rfile) {
        FileOutputStream fos = null;
        try {
            String prefix = null;
            if (new File(lfile).isDirectory()) {
                prefix = lfile + File.separator;
            }

            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // exec 'scp -f rfile' remotely
            String command = "scp -f " + rfile;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] buf = new byte[1024];

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        // error
                        break;
                    }
                    if (buf[0] == ' ') {
                        break;
                    }
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }
                String file = null;
                for (int i = 0;; i++) {
                    in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();

                // read a content of lfile
                fos = new FileOutputStream(prefix == null ? lfile : prefix + file);
                int foo;
                while (true) {
                    if (buf.length < filesize) {
                        foo = buf.length;
                    } else {
                        foo = (int) filesize;
                    }
                    foo = in.read(buf, 0, foo);
                    if (foo < 0) {
                        // error 
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize -= foo;
                    if (filesize == 0L) {
                        break;
                    }
                }
                fos.close();
                fos = null;

                if (checkAck(in) != 0) {
                    System.exit(0);
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }
            session.disconnect();
        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ee) {
            }
        }
    }

    static int checkAck(InputStream in) throws IOException {
        int b = in.read();
// 0 for success,
// 1 for error,
// 2 for fatal error,
// -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

}
