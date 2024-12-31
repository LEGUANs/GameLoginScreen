import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.sampled.*;
import java.io.*;
import java.io.IOException;
import java.net.Socket;




public class GameLoginScreen extends JFrame {
    private JCheckBox agreeCheckBox;
    private Clip backgroundMusicClip;
	private boolean isMusicPlaying = true;
	private Socket clientSocket;


    
    public static void main(String[] args) {
     
    	SwingUtilities.invokeLater(() -> {
    	    GameLoginScreen loginScreen = new GameLoginScreen();
    	    Thread connectThread = new Thread(() -> loginScreen.connectToServer("172.20.10.9", 23456));//改你电脑的ip
    	    connectThread.start();
    	});

    }
    
    
    
    private void connectToServer(String ipAddress, int port) {
        try {
            clientSocket = new Socket(ipAddress, port);
            System.out.println("Connected to server at " + ipAddress + ":" + port);
            clientSocket.getOutputStream();
            clientSocket.getInputStream();
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    
    private void loadBackgroundMusic() {
        try {
            File musicFile = new File("冲击星.wav"); //你改个音乐文件，直接放文件夹就行
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioStream);
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusicClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public GameLoginScreen() {
    	

        setTitle("CSGO自造登录界面");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
           
        ImageIcon bg = new ImageIcon("巨龙传说.jpg");//改背景图片，放文件夹
        JLabel background = new JLabel(bg);
        background.setLayout(new BorderLayout());
        add(background);

        ImageIcon gbg = new ImageIcon("背景4.jpg");//图标文件，放文件夹
        JLabel gbl = new JLabel(gbg);
        gbl.setBounds(0, -30, 200, 150);
        background.add(gbl);
        
       
        loadBackgroundMusic();
       
          
                ImageIcon musicOpen = new ImageIcon("音乐开启.png");//音乐开启图标，放文件夹
                ImageIcon musicOff = new ImageIcon("音乐暂停.png");//音乐关闭图标，放文件夹
                final JButton musicButton = new JButton(musicOpen);
                musicButton.setBounds(700, 0, 90, 90);
                background.add(musicButton);

                musicButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (isMusicPlaying) {
                            backgroundMusicClip.stop();
                            musicButton.setIcon(musicOff);
                        } else {
                            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                            musicButton.setIcon(musicOpen);
                        }
                        isMusicPlaying = !isMusicPlaying;
                    }
                });

      
       
                
       
        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.setLayout(new GridLayout(6, 2));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(200, 80, 200, 80));
        background.add(loginPanel, BorderLayout.CENTER);
        
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setForeground(Color.RED);
        loginPanel.add(usernameLabel);
        
        JTextField usernameField = new JTextField();
        usernameField.setBackground(Color.gray);
        loginPanel.add(usernameField);
        
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setForeground(Color.RED);
        loginPanel.add(passwordLabel);
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBackground(Color.gray);
        loginPanel.add(passwordField);
        
        agreeCheckBox = new JCheckBox("我同意服务协议");
        agreeCheckBox.setBackground(Color.gray);
        loginPanel.add(agreeCheckBox);
        
        JLabel agreementLabel = new JLabel("点击查看服务协议");
        agreementLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        agreementLabel.setForeground(Color.GREEN);
        agreementLabel.addMouseListener(new MouseAdapter() {
            
        public void mouseClicked(MouseEvent e) {
                showServiceAgreementDialog();
            }
              });
        loginPanel.add(agreementLabel);
        
      
        JButton loginButton = new JButton("登录");
        ImageIcon www=new ImageIcon("登录.png");
        loginButton.setIcon(www);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginPanel.add(loginButton);
        
        
          
        JButton registerButton = new JButton("注册");
        ImageIcon abc=new ImageIcon("注册.png");
        registerButton.setIcon(abc);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        loginPanel.add(registerButton);
        
        setVisible(true);
        
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean agreed = agreeCheckBox.isSelected();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(GameLoginScreen.this, "用户名和密码不能为空", "警告", JOptionPane.WARNING_MESSAGE);
                } else if (!agreed) {
                    JOptionPane.showMessageDialog(GameLoginScreen.this, "请同意服务协议", "警告", JOptionPane.WARNING_MESSAGE);
                } else {
                    
                    try {
                        Socket socket = new Socket("172.20.10.9", 23456);//改ip
                        
                       
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("verify:" + username + ":" + password);
                        
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String response = in.readLine();
                        
                        
                        socket.close();

                        
                        if (response.equals("请求处理完成")) {
                            JOptionPane.showMessageDialog(GameLoginScreen.this, "登录成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(GameLoginScreen.this, "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(GameLoginScreen.this, "与服务器连接出错", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        
        registerButton.addActionListener(new ActionListener() {           
            public void actionPerformed(ActionEvent e) {
                new RegisterDialog(GameLoginScreen.this);
            }
        });
    
    }
    
        public class RegisterDialog extends JDialog {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JPasswordField confirmPasswordField;
        private JLabel errorLabel;

        public RegisterDialog(Frame parent) {
            super(parent, "注册", true);
            setSize(400, 300);
            setResizable(false);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(null);

            JLabel usernameLabel = new JLabel("用户名:");
            usernameLabel.setBounds(20, 20, 80, 25);
            contentPanel.add(usernameLabel);

            usernameField = new JTextField();
            usernameField.setBounds(110, 20, 250, 25);
            contentPanel.add(usernameField);

            JLabel passwordLabel = new JLabel("设置密码:");
            passwordLabel.setBounds(20, 60, 80, 25);
            contentPanel.add(passwordLabel);

            passwordField = new JPasswordField();
            passwordField.setBounds(110, 60, 250, 25);
            contentPanel.add(passwordField);

            JLabel confirmPasswordLabel = new JLabel("确认密码:");
            confirmPasswordLabel.setBounds(20, 100, 80, 25);
            contentPanel.add(confirmPasswordLabel);

            confirmPasswordField = new JPasswordField();
            confirmPasswordField.setBounds(110, 100, 250, 25);
            contentPanel.add(confirmPasswordField);

            JButton registerButton = new JButton("注册");
            registerButton.setBounds(110, 140, 100, 30);
            contentPanel.add(registerButton);

            JButton closeButton = new JButton("关闭");
            closeButton.setBounds(260, 140, 100, 30);
            contentPanel.add(closeButton);

            errorLabel = new JLabel();
            errorLabel.setBounds(20, 180, 340, 25);
            errorLabel.setForeground(Color.RED);
            contentPanel.add(errorLabel);

            add(contentPanel, BorderLayout.CENTER);

            registerButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());
                    String confirmPassword = new String(confirmPasswordField.getPassword());

                    if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        errorLabel.setText("用户名、设置密码和确认密码不能为空");
                    } else if (password.length() < 8) {
                        errorLabel.setText("设置密码长度不能少于8位");
                    } else if (!password.equals(confirmPassword)) {
                        errorLabel.setText("设置密码和确认密码不一致");
                    } else {
                        try {
                            
                            Socket socket = new Socket("192.168.3.5", 23456);
                            System.out.println("已连接到服务器");

                           
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            String request = "register:" + username + ":" + password;
                            out.println(request);
                            System.out.println("已发送注册请求: " + request);

                            
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String response = in.readLine();
                            System.out.println("收到服务器响应: " + response);

                          
                            socket.close();
                            System.out.println("连接已关闭");

                            
                            if (response.equals("请求处理完成")) {
                                
                                errorLabel.setText("注册成功");
                            } else {
                                errorLabel.setText("注册失败");
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });



            closeButton.addActionListener(new ActionListener() {               
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            setVisible(true);
        }


    }



          
    private void showServiceAgreementDialog() {
        JDialog agreementDialog = new JDialog(this, "服务协议", true);
        agreementDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        agreementDialog.setSize(500, 400);
        agreementDialog.setResizable(false);
        agreementDialog.setLocationRelativeTo(this);

       
        ImageIcon backgroundImage = new ImageIcon("巨龙传说.jpg");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());

        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.add(backgroundLabel);

        JTextArea agreementText = new JTextArea();
        agreementText.setText("您一旦进入本游戏，就表示您已经同意并保证遵守下述条款：\n\n" +
                "1． 尊重网络道德，遵守中华人民共和国的各项有关法律法规。\n\n" +
                "2． 承担一切因用户的行为而直接或间接导致的民事、行政或刑事法律责任。\n\n" +
                "3． 不利用提供的游戏进行赌博或其他违法活动。\n\n" +
                "4． 不在游戏中利用程序或游戏规则的漏洞伤害其他用户的利益。\n\n" +
                "5． 不在游戏中故意超时不动。\n\n" +
                "6． 不在游戏中故意演员其他用户。\n\n" +
                "7． 不在游戏中特意接受其他用户所送的分。\n\n" +
                "8． 不在游戏中使用任何形式的作弊软件。\n\n" +
                "9． 不创建有违法性质名字的游戏室。\n\n" +
                "10． 不发布任何仿造系统消息和权限命令消息的聊天语句。\n\n" +
                "11． 服从本游戏对游戏秩序的统一管理。\n\n" +
                "12． 服从本游戏制定的本游戏作弊处罚条例。");
        agreementText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(agreementText);
        scrollPane.setBounds(20, 20, 400, 250); 
        backgroundLabel.add(scrollPane);

        JButton agreeButton = new JButton("同意");
        JButton exitButton = new JButton("退出");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(agreeButton);
        buttonPanel.add(exitButton);
        buttonPanel.setBounds(10, 300, 470, 30);
        backgroundLabel.add(buttonPanel);

        agreeButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                agreeCheckBox.setSelected(true);
                agreementDialog.dispose();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                agreementDialog.dispose();
            }
        });

        agreementDialog.setContentPane(contentPane); 
        agreementDialog.setVisible(true);
    }   
}