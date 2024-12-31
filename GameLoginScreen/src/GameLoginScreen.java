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
    	    Thread connectThread = new Thread(() -> loginScreen.connectToServer("172.20.10.9", 23456));//������Ե�ip
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
            File musicFile = new File("�����.wav"); //��ĸ������ļ���ֱ�ӷ��ļ��о���
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
    	

        setTitle("CSGO�����¼����");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
           
        ImageIcon bg = new ImageIcon("������˵.jpg");//�ı���ͼƬ�����ļ���
        JLabel background = new JLabel(bg);
        background.setLayout(new BorderLayout());
        add(background);

        ImageIcon gbg = new ImageIcon("����4.jpg");//ͼ���ļ������ļ���
        JLabel gbl = new JLabel(gbg);
        gbl.setBounds(0, -30, 200, 150);
        background.add(gbl);
        
       
        loadBackgroundMusic();
       
          
                ImageIcon musicOpen = new ImageIcon("���ֿ���.png");//���ֿ���ͼ�꣬���ļ���
                ImageIcon musicOff = new ImageIcon("������ͣ.png");//���ֹر�ͼ�꣬���ļ���
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
        
        JLabel usernameLabel = new JLabel("�û���:");
        usernameLabel.setForeground(Color.RED);
        loginPanel.add(usernameLabel);
        
        JTextField usernameField = new JTextField();
        usernameField.setBackground(Color.gray);
        loginPanel.add(usernameField);
        
        JLabel passwordLabel = new JLabel("����:");
        passwordLabel.setForeground(Color.RED);
        loginPanel.add(passwordLabel);
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBackground(Color.gray);
        loginPanel.add(passwordField);
        
        agreeCheckBox = new JCheckBox("��ͬ�����Э��");
        agreeCheckBox.setBackground(Color.gray);
        loginPanel.add(agreeCheckBox);
        
        JLabel agreementLabel = new JLabel("����鿴����Э��");
        agreementLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        agreementLabel.setForeground(Color.GREEN);
        agreementLabel.addMouseListener(new MouseAdapter() {
            
        public void mouseClicked(MouseEvent e) {
                showServiceAgreementDialog();
            }
              });
        loginPanel.add(agreementLabel);
        
      
        JButton loginButton = new JButton("��¼");
        ImageIcon www=new ImageIcon("��¼.png");
        loginButton.setIcon(www);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginPanel.add(loginButton);
        
        
          
        JButton registerButton = new JButton("ע��");
        ImageIcon abc=new ImageIcon("ע��.png");
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
                    JOptionPane.showMessageDialog(GameLoginScreen.this, "�û��������벻��Ϊ��", "����", JOptionPane.WARNING_MESSAGE);
                } else if (!agreed) {
                    JOptionPane.showMessageDialog(GameLoginScreen.this, "��ͬ�����Э��", "����", JOptionPane.WARNING_MESSAGE);
                } else {
                    
                    try {
                        Socket socket = new Socket("172.20.10.9", 23456);//��ip
                        
                       
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("verify:" + username + ":" + password);
                        
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String response = in.readLine();
                        
                        
                        socket.close();

                        
                        if (response.equals("���������")) {
                            JOptionPane.showMessageDialog(GameLoginScreen.this, "��¼�ɹ�", "�ɹ�", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(GameLoginScreen.this, "�û������������", "����", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(GameLoginScreen.this, "����������ӳ���", "����", JOptionPane.ERROR_MESSAGE);
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
            super(parent, "ע��", true);
            setSize(400, 300);
            setResizable(false);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(null);

            JLabel usernameLabel = new JLabel("�û���:");
            usernameLabel.setBounds(20, 20, 80, 25);
            contentPanel.add(usernameLabel);

            usernameField = new JTextField();
            usernameField.setBounds(110, 20, 250, 25);
            contentPanel.add(usernameField);

            JLabel passwordLabel = new JLabel("��������:");
            passwordLabel.setBounds(20, 60, 80, 25);
            contentPanel.add(passwordLabel);

            passwordField = new JPasswordField();
            passwordField.setBounds(110, 60, 250, 25);
            contentPanel.add(passwordField);

            JLabel confirmPasswordLabel = new JLabel("ȷ������:");
            confirmPasswordLabel.setBounds(20, 100, 80, 25);
            contentPanel.add(confirmPasswordLabel);

            confirmPasswordField = new JPasswordField();
            confirmPasswordField.setBounds(110, 100, 250, 25);
            contentPanel.add(confirmPasswordField);

            JButton registerButton = new JButton("ע��");
            registerButton.setBounds(110, 140, 100, 30);
            contentPanel.add(registerButton);

            JButton closeButton = new JButton("�ر�");
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
                        errorLabel.setText("�û��������������ȷ�����벻��Ϊ��");
                    } else if (password.length() < 8) {
                        errorLabel.setText("�������볤�Ȳ�������8λ");
                    } else if (!password.equals(confirmPassword)) {
                        errorLabel.setText("���������ȷ�����벻һ��");
                    } else {
                        try {
                            
                            Socket socket = new Socket("192.168.3.5", 23456);
                            System.out.println("�����ӵ�������");

                           
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            String request = "register:" + username + ":" + password;
                            out.println(request);
                            System.out.println("�ѷ���ע������: " + request);

                            
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String response = in.readLine();
                            System.out.println("�յ���������Ӧ: " + response);

                          
                            socket.close();
                            System.out.println("�����ѹر�");

                            
                            if (response.equals("���������")) {
                                
                                errorLabel.setText("ע��ɹ�");
                            } else {
                                errorLabel.setText("ע��ʧ��");
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
        JDialog agreementDialog = new JDialog(this, "����Э��", true);
        agreementDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        agreementDialog.setSize(500, 400);
        agreementDialog.setResizable(false);
        agreementDialog.setLocationRelativeTo(this);

       
        ImageIcon backgroundImage = new ImageIcon("������˵.jpg");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());

        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.add(backgroundLabel);

        JTextArea agreementText = new JTextArea();
        agreementText.setText("��һ�����뱾��Ϸ���ͱ�ʾ���Ѿ�ͬ�Ⲣ��֤�����������\n\n" +
                "1�� ����������£������л����񹲺͹��ĸ����йط��ɷ��档\n\n" +
                "2�� �е�һ�����û�����Ϊ��ֱ�ӻ��ӵ��µ����¡����������·������Ρ�\n\n" +
                "3�� �������ṩ����Ϸ���жĲ�������Υ�����\n\n" +
                "4�� ������Ϸ�����ó������Ϸ�����©���˺������û������档\n\n" +
                "5�� ������Ϸ�й��ⳬʱ������\n\n" +
                "6�� ������Ϸ�й�����Ա�����û���\n\n" +
                "7�� ������Ϸ��������������û����͵ķ֡�\n\n" +
                "8�� ������Ϸ��ʹ���κ���ʽ�����������\n\n" +
                "9�� ��������Υ���������ֵ���Ϸ�ҡ�\n\n" +
                "10�� �������κη���ϵͳ��Ϣ��Ȩ��������Ϣ��������䡣\n\n" +
                "11�� ���ӱ���Ϸ����Ϸ�����ͳһ����\n\n" +
                "12�� ���ӱ���Ϸ�ƶ��ı���Ϸ���״���������");
        agreementText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(agreementText);
        scrollPane.setBounds(20, 20, 400, 250); 
        backgroundLabel.add(scrollPane);

        JButton agreeButton = new JButton("ͬ��");
        JButton exitButton = new JButton("�˳�");

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