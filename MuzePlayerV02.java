package muze.player.v0.pkg2;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Dimension;
import java.awt.Color;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.Image;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

//import mp3agic.*;

/* @author Kshitij Nair */

public class MuzePlayerV02 {

    MuzePlayerV02(){                         //main class const def
        createWelcome();                     //func call to create Welc Panel
    }

    class WelcomePane extends JPanel {       //Panel class

        //varibale defs for BG color
        private static final long serialVersionUID = -3936526023655045114L;
        private static final int WIDE = 640;
        private static final int HIGH = 240;
        private static final float HUE_MIN = 0;
        private static final float HUE_MAX = 1;
        private final Timer time;
        private float hue = HUE_MIN;
        private Color color1 = Color.white;
        private Color color2 = Color.black;
        private float delta = 0.01f;
        JLabel wel = new JLabel();
        JLabel nex = new JLabel();

        WelcomePane(JFrame f){                       //constr for JPanel class

            //creating the components
            ImageIcon img =  new ImageIcon("C:\\Users\\thekn\\Downloads\\"
                    + "Webp.net-resizeimage.png");
            nex.setIcon(img);
            wel.setFont(new java.awt.Font("Segoe UI", 0, 36));
            wel.setForeground(new java.awt.Color(33,33,33));
            wel.setText("Welcome to Muze");

            //setbounds for components
            wel.setBounds(200,100, 300,100);
            nex.setBounds(320,290,40,40);

            //adding components to panel
            add(nex);
            add(wel);
            setOpaque(false);
            setFocusable(true);
            setBackground(new Color(0, 0, 0, 190));
            nex.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    createHome();
                    f.dispose();
                }
            });

            //frame layout
            setSize(700,500);
            setLayout(null);
            setVisible(true);

            ActionListener action = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    hue += delta;
                    if (hue > HUE_MAX) {
                        hue = HUE_MIN;
                    }
                    color1 = Color.getHSBColor(hue, 1, 1);
                    color2 = Color.getHSBColor(hue + 12 * delta, 1, 1);

                    repaint();
                }
            };
            time = new Timer(100, action);
            time.start();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint p = new GradientPaint(
            0, 0, color1, getWidth(), 0, color2);
            g2d.setPaint(p);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(WIDE, HIGH);
        }

    }

    public void createWelcome(){             //func def for Welc Panel
        JFrame f = new JFrame("Welcome");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WelcomePane W = new WelcomePane(f);   //Constructor for frame
        f.add(W);
        f.pack();
        f.setSize(700,500);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.setResizable(false);

        //createHome();                        //fnc call for Home Panel
    }

    class HomePane extends JPanel {

        //varibale defs for BG color
        private static final long serialVersionUID = -3936526023655045114L;
        private static final int WIDE = 640;
        private static final int HIGH = 240;
        private static final float HUE_MIN = 0;
        private static final float HUE_MAX = 1;
        private Timer time;
        private float hue = HUE_MIN;
        private Color color1 = Color.white;
        private Color color2 = Color.black;
        private float delta = 0.01f;
        private AudioPlayer player = new AudioPlayer();
	private Thread playbackThread;
	private PlayingTimer timer;

	private boolean isPlaying = false;
	private boolean isPause = false;

	private String audioFilePath;
	private String lastOpenPath;

        //Swing Components
	private JLabel labelFileName = new JLabel("Playing File:");
	private JLabel labelTimeCounter = new JLabel("00:00:00");
	private JLabel labelDuration = new JLabel("00:00:00");
        
        //private JList songList = new JList();
        DefaultListModel<String> li = new DefaultListModel<>();
        JList<String> songList = new JList<>(li);
        
        JScrollPane scroll = new JScrollPane(songList);

	private final JButton buttonSelect = new JButton("Select New File");
	private final JButton buttonPlay = new JButton();
	private final JButton buttonPause = new JButton();
	private final JPanel playPanel, listPanel;
	private final JSlider sliderTime = new JSlider();
        
        ImageIcon play = new ImageIcon("C:\\Users\\thekn\\Downloads"
                + "\\play-circle.png");
        ImageIcon pause = new ImageIcon("C:\\Users\\thekn\\Downloads"
                + "\\pause-circle.png");
        ImageIcon stop = new ImageIcon("C:\\Users\\thekn\\Downloads"
                + "\\stop-circle.png");
        ImageIcon solo = new ImageIcon("E:\\Downloads\\Chrome Downloads\\soloCover");
        ImageIcon loca = new ImageIcon("E:\\Downloads\\Chrome Downloads\\locaCover");
        ImageIcon fire = new ImageIcon("E:\\Downloads\\Chrome Downloads\\fireworkCover");
        
        private Image getScaledImage(Image srcImg, int w, int h){
            BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resizedImg.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(srcImg, 0, 0, w, h, null);
            g2.dispose();

            return resizedImg;
        }
        
        
        Image soloArt = solo.getImage();
        //soloArt = getScaledImage(soloArt, 150, 150);
        
            
        HomePane(){

            playPanel = new JPanel();
            listPanel = new JPanel();

            playPanel.setBounds(400,0, 300,500);
            listPanel.setBounds(0,0, 400,500);
            
            scroll.setBounds(50,100,200,300);
            scroll.setOpaque(false);
            songList.setFixedCellHeight(35);
            songList.setFixedCellWidth(100);
            songList.setCellRenderer(new TransparentListCellRenderer());
            songList.setOpaque(false);
            
            
            buttonSelect.setFont(new Font("Sans", Font.BOLD, 11));
            buttonSelect.setBounds(125,50, 150,25);
            listPanel.add(buttonSelect);

            buttonPlay.setIcon(play);
            buttonPlay.setBounds(10,300, 48,48);
            buttonPlay.setOpaque(false);
            buttonPlay.setContentAreaFilled(false);
            buttonPlay.setBorderPainted(false);
            playPanel.add(buttonPlay);

            buttonPause.setIcon(pause);
            buttonPause.setBounds(190,300,48,48);
            buttonPause.setOpaque(false);
            buttonPause.setContentAreaFilled(false);
            buttonPause.setBorderPainted(false);
            playPanel.add(buttonPause);

            sliderTime.setPreferredSize(new Dimension(200, 20));
            sliderTime.setEnabled(false);
            sliderTime.setValue(0);
            sliderTime.setOpaque(false);
            sliderTime.setBounds(50,250, 200,20);
            playPanel.add(sliderTime);

            labelTimeCounter.setFont(new Font("Sans", Font.BOLD, 10));
            labelTimeCounter.setBounds(5,260, 80,50);
            playPanel.add(labelTimeCounter);

            labelDuration.setFont(new Font("Sans", Font.BOLD, 10));
            labelDuration.setBounds(225, 260, 80,50);
            playPanel.add(labelDuration);

            sliderTime.setPreferredSize(new Dimension(200, 15));
            sliderTime.setEnabled(false);
            sliderTime.setValue(0);
            sliderTime.setBackground(Color.darkGray);
            playPanel.add(sliderTime);

            add(playPanel);
            add(listPanel);

            playPanel.setOpaque(false);
            playPanel.setFocusable(true);
            playPanel.setBackground(new Color(0, 0, 0, 190));
            playPanel.setLayout(null);
            playPanel.setVisible(true);
            listPanel.setBackground(Color.darkGray);

            listPanel.setLayout(null);
            listPanel.setVisible(true);
            setSize(700,500);
            setLayout(null);
            setVisible(true);
            
            JFileChooser fileChooser = null;

//            JButton test = new JButton("test");
//            test.setBounds(0,0, 100,100);
//            listPanel.add(test);

            songList.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2){
                        System.out.println("a"+songList.getSelectedValue()+"b");
                        System.out.println(lastOpenPath);
                        audioFilePath = lastOpenPath + "\\" + songList.getSelectedValue() + ".wav" ;
                        System.out.println(audioFilePath);
                        if (isPlaying || isPause) {
                            stopPlaying();
                            while (player.getAudioClip().isRunning()) {
                                try {
                                    Thread.sleep(100);
                                }
                                catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    playBack();
                    }
                }                    
            });
            
            buttonSelect.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    openFile();
                }
            });

            buttonPlay.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!isPlaying) {
			playBack();
                    }
                    else {
			stopPlaying();
                    }
                }
            });

            buttonPause.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!isPause) {
			pausePlaying();
                    }
                    else {
			resumePlaying();
                    }
                }
            });

        ActionListener action = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    hue += delta;
                    if (hue > HUE_MAX) {
                        hue = HUE_MIN;
                    }
                    color1 = Color.getHSBColor(hue, 1, 1);
                    color2 = Color.getHSBColor(hue + 12 * delta, 1, 1);

                    repaint();
                }
            };
            time = new Timer(100, action);
            time.start();
        }
        
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint p = new GradientPaint(
            0, 0, color1, getWidth(), 0, color2);
            g2d.setPaint(p);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(WIDE, HIGH);
        }

        /* it returns all file names in that directoru
        */
        
        private void selDir(){
            
        }
        
        private void selSong(){
            
        }
        private void openFile() {
		JFileChooser fileChooser = null;
                File f = null;
                String[] paths;
                String name;
		if (lastOpenPath != null && !lastOpenPath.equals("")) {
			fileChooser = new JFileChooser(lastOpenPath);
		} 
                else {
			fileChooser = new JFileChooser();
                }

		FileFilter wavFilter = new FileFilter() {
                    @Override
                    public String getDescription() {
                        return "Sound file (*.wav)";
                    }

                    @Override
                    public boolean accept(File file) {
                        if (file.isDirectory()) {
                            return true;
                        } 
                        else {
                            return file.getName().toLowerCase().endsWith(".wav");
                        }
                    }
		};

                fileChooser.setFileFilter(wavFilter);
		fileChooser.setDialogTitle("Open Audio File");
		fileChooser.setAcceptAllFileFilterUsed(false);

		int userChoice = fileChooser.showOpenDialog(this);
		if (userChoice == JFileChooser.APPROVE_OPTION) {
                    f = fileChooser.getCurrentDirectory();
                    paths = f.list();
                    for(String i:paths){
                        if(i.endsWith(".wav")){
                            name = i.substring(0,i.indexOf("."));
                            li.addElement(name);
                        }
                    }
                    scroll.setOpaque(false);
                    scroll.getViewport().setOpaque(false);
                    scroll.setBorder(BorderFactory.createEmptyBorder());
                    listPanel.add(scroll);  
                    audioFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                    lastOpenPath = fileChooser.getSelectedFile().getParent();
                    if (isPlaying || isPause) {
			stopPlaying();
			while (player.getAudioClip().isRunning()) {
                            try {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException ex) {
				ex.printStackTrace();
                            }
			}
                    }
                    playBack();
		}
	}
        
        public class TransparentListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setForeground(Color.WHITE);
            setOpaque(isSelected);
            return this;
        }
    }

        private void playBack() {
		timer = new PlayingTimer(labelTimeCounter, sliderTime);
		timer.start();
		isPlaying = true;
		playbackThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					buttonPlay.setIcon(stop);
					//buttonPlay.setIcon(iconStop);
					buttonPlay.setEnabled(true);

					buttonPause.setIcon(pause);
					buttonPause.setEnabled(true);

					player.load(audioFilePath);
					timer.setAudioClip(player.getAudioClip());
					labelFileName.setText("Playing File: " + audioFilePath);
					sliderTime.setMaximum((int) player.getClipSecondLength());

					labelDuration.setText(player.getClipLengthString());
					player.play();

					resetControls();

				} catch (UnsupportedAudioFileException ex) {
					JOptionPane.showMessageDialog(HomePane.this,
					"The audio format is unsupported!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				} catch (LineUnavailableException ex) {
					JOptionPane.showMessageDialog(HomePane.this,
							"Could not play the audio file because line is unavailable!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(HomePane.this,
							"I/O error while playing the audio file!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				}

			}
		});

		playbackThread.start();
	}

        private void stopPlaying() {
		isPause = false;
		buttonPause.setIcon(pause);
		buttonPause.setEnabled(false);
		timer.reset();
		timer.interrupt();
		player.stop();
		playbackThread.interrupt();
	}

        private void pausePlaying() {
		buttonPause.setIcon(play);
		isPause = true;
		player.pause();
		timer.pauseTimer();
		playbackThread.interrupt();
	}

        private void resumePlaying() {
		buttonPause.setIcon(play);
		isPause = false;
		player.resume();
		timer.resumeTimer();
		playbackThread.interrupt();
	}

	private void resetControls() {
		timer.reset();
		timer.interrupt();

		buttonPlay.setIcon(play);
		//buttonPlay.setIcon(iconPlay);

		buttonPause.setEnabled(false);

		isPlaying = false;
	}

    }

    public void createHome(){                //func def for Home Panel
        JFrame f = new JFrame("Muze");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        HomePane H = new HomePane();         //Constructor for frame
        f.add(H);
        f.pack();
        f.setSize(700,500);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.setResizable(false);
    }


    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
        catch (Exception ex) {
            ex.printStackTrace();
	}
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
            new MuzePlayerV02();
            }
        });
    }
}
