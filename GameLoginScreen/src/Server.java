import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
    private static JTextArea logTextArea;
    private static JButton saveButton;
    private static JButton clearButton;
    private static JButton closeButton;
    private static ServerSocket serverSocket;
    private static boolean isRunning = true;
    private static JDBC jdbc;
    private static final int PORT = 23456;

    public static void main(String[] args) {
        jdbc = null;
        try {
            jdbc = new JDBC();
        } catch (SQLException e) {
            e.printStackTrace();           
            return;
        }

        System.setProperty("user.timezone", "UTC");
        JFrame frame = new JFrame();
        frame.setTitle("服务器端");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("保存日志");
        saveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveLogToFile();
            }
        });
        buttonPanel.add(saveButton);

        clearButton = new JButton("清空日志");
        clearButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                clearLog();
            }
        });
        buttonPanel.add(clearButton);

        closeButton = new JButton("关闭服务器");
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                closeServer();
            }
        });
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("服务器已启动，等待客户端连接...");

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端已连接，处理请求...");

                
                Thread clientThread = new Thread(() -> {
                    try {
                        
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String request = in.readLine();
                        System.out.println("收到客户端请求: " + request);

                        
                        processClientRequest(request);

                        
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        out.println("请求处理完成");
                        out.flush();
                        System.out.println("响应已发送到客户端");

                        
                        clientSocket.close();
                        System.out.println("客户端连接已关闭");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void closeServer() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            appendLog("关闭服务器出错：" + e.getMessage());
        }
    }


    private static void saveLogToFile() {
        String log = logTextArea.getText();
        if (log.isEmpty()) {
            JOptionPane.showMessageDialog(null, "日志为空，无法保存");
            return;
        }

        String fileName = generateFileName();
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(log);
            fileWriter.close();
            JOptionPane.showMessageDialog(null, "日志已保存为 " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "保存日志出错");
        }
    }

    private static String generateFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        return "log_" + timestamp + ".txt";
    }

    private static void clearLog() {
        logTextArea.setText("");
    }

    private static void appendLog(String log) {
        logTextArea.append(log + "\n");
    }

    public static void processClientRequest(String request) {
        
        String[] requestData = request.split(":");
        if (requestData.length == 3) {
            String command = requestData[0];
            String username = requestData[1];
            String password = requestData[2];

            try {
                if (command.equals("register")) {
                    boolean success = jdbc.registerUser(username, password);
                    if (success) {
                        appendLog("用户注册成功：" + username);
                    } else {
                        appendLog("用户注册失败：" + username);
                    }
                } else if (command.equals("verify")) {
                    boolean success = jdbc.verifyUser(username, password);
                    if (success) {
                        appendLog("用户验证通过：" + username);
                    } else {
                        appendLog("用户验证失败：" + username);
                    }
                } else {
                    appendLog("无效的命令：" + command);
                }
            } catch (SQLException e) {
                appendLog("处理客户端请求出错：" + e.getMessage());
            }
        } else {
            appendLog("无效的请求：" + request);
        }
    }
}
