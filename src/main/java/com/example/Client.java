package com.example;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Client
{
    private static JFrame f;
    private static JLabel top;
    private static JLabel bottom;
    private static JPanel board;
    private static int size = 40;
    private static int[][] tiles = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 1, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
        };;
    private static int rows = 9;
    private static int cols = 9;

    // public static void main(String[] args)
    // {
    //     setupGUI();
    // }
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {
        //get the localhost IP address, if server is running on some other IP, you need to use that
        System.out.println("Running main in client!");
        InetAddress host = InetAddress.getLocalHost();

 
        Socket socket = new Socket(host.getHostName(), 9876);

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        

        inputReader incoming = new inputReader(in);
        incoming.start();
        System.out.println("going to call createGUI");
        setupGUI();
        // createGUI(out, socket, in, incoming);
    }


    private static void setupGUI()
    {
        f = new JFrame();
        f.setPreferredSize(new Dimension(size*cols, size*rows));
        f.setSize(new Dimension(size*cols, size*rows));
        f.setLocation(500,200);
        top = new JLabel();
        bottom = new JLabel();
        board = new JPanel();
        f.setLayout(new BorderLayout());
        board.setLayout(new GridLayout(rows, cols));
        f.add(top, BorderLayout.NORTH);
        f.add(board, BorderLayout.CENTER);
        f.add(bottom, BorderLayout.SOUTH);

        // for later use
        GridBagConstraints c = new GridBagConstraints();

        // Now set up the board
        try {

            for(int x = 0; x < tiles.length; x++){
                for(int y = 0; y < tiles[x].length; y++){
                    ImageIcon img;
                    img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/Bomb.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT)); // default to a bomb! :)
                    if(tiles[x][y] != 1) // If we're not a bomb...
                    {
                        switch (countNearbyBombs(x,y)) // pick a picture
                        {
                            case 1:
                                img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/OneTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                                break;
                            case 2:
                                img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/TwoTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                                break;
                            case 3:
                                img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/ThreeTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                                break;
                            case 4:
                                img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/FourTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                                break;
                            case 5:
                                img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/FiveTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                                break;
                            case 6:
                                img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/SixTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                                break;
                            case 7:
                                img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/SevenTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                                break;
                            case 8:
                                img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/EightTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                                break;
                            default:
                                img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/BlankTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                                break;
                        } // end of switch
                    } // end of if

                    JLabel temp = new JLabel(img);
                    board.add(temp);
                } // end of for y
            } // end of for x
        } catch (Exception e) {
            e.printStackTrace();
        }

        f.setVisible(true);
    }

    public static int countNearbyBombs(int row, int col) {
        if(tiles[row][col] == 1) {
            return 0;
        }
        int count = 0;

        //check surrounding tile is on the board AND is a bomb
        if(col-1 > 0 && tiles[row][col-1] > 0) {
            count++;
        }
        if(col+1 < cols && tiles[row][col+1] > 0) {
            count++;
        }

        if(row-1 > 0 && tiles[row-1][col] > 0) {
            count++;
        }
        if(row+1 < rows && tiles[row+1][col] > 0) {
            count++;
        }

        //diagonals
        if(col-1 >0 && row-1 >0 && tiles[row-1][col-1] >0) {
            count++;
        }
        if(col+1 < cols && row-1 >0 && tiles[row-1][col+1] >0) {
            count++;
        }

        if(col-1 >0 && row+1 < rows && tiles[row+1][col-1] >0) {
            count++;
        }
        if(col+1 < cols && row+1 < rows && tiles[row+1][col+1] >0) {
            count++;
        }
        return count;
    }
    

    private static class inputReader extends Thread
    {
        //Catch all updates into our input stream
        ObjectInputStream incomingMessageStream;

        public inputReader(ObjectInputStream i) {
            incomingMessageStream = i;
        }

        public void run() {
            while(true)
            {
                try
                {
                    String incomingMessage = (String) incomingMessageStream.readObject();
                    SwingUtilities.invokeLater(() -> {
                        System.out.println(incomingMessage);
                        // messageArea.setText(messageArea.getText() + "\n" + incomingMessage);
                        // messageArea.setCaretPosition(messageArea.getDocument().getLength());
                    });   
                } catch (Exception e)
                {
                    System.err.println("Error at line 105: " + e);
                    break;
                }
            }
        }
    }
}