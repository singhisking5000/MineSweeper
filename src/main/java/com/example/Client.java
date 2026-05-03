package com.example;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Client
{
    private static JFrame f;
    private static JLabel top;
    private static JPanel board;
    private static int size = 40;
    private static int[][] tiles;
    private static int rows = 9;
    private static int cols = 9;
    
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {
        //get the localhost IP address, if server is running on some other IP, you need to use that
        System.out.println("Running main in client!");
        InetAddress host = InetAddress.getLocalHost();
 
        Socket socket = new Socket(host.getHostName(), 9876);

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        

        updater updates = new updater(in); // A thread that actively listens for new updates
        updates.start();
        System.out.println("going to call createGUI");
        setupGUI(socket, in, out, updates);
    }


    private static void setupGUI(Socket socket, ObjectInputStream i, ObjectOutputStream o, updater u)
    {
        f = new JFrame();
        f.setPreferredSize(new Dimension(size*cols, size*rows));
        f.setSize(new Dimension(size*cols, size*rows));
        f.setLocation(500,200);
        top = new JLabel();
        board = new JPanel();
        f.setLayout(new BorderLayout());
        board.setLayout(new GridBagLayout());
        f.add(top, BorderLayout.NORTH);
        f.add(board, BorderLayout.CENTER);

        // for later use
        GridBagConstraints c = new GridBagConstraints();
        c.gridheight = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;

        // Now set up the board
        // to be implemented:
        // if 0 = make a blank tile
        // if 1 = still make blank
        // if 2 = cleared, change icon to a number or CLEARED blank
        // if 3 = change to bomb tile


        try {
            // TEMPORARY creates its own custom board to test functions. DO NOT USE THIS ALMOST AT ALL
            // USE ONLY AS REFERENCE FOR IMAGE SCALING, LOGIC, AND OTHERS, DO NOT USE OTHERWISE!!!!!!!
            for(int x = 0; x < tiles.length; x++){
                for(int y = 0; y < tiles[x].length; y++){
                    ImageIcon img;
                    img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/BlankTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT)); // default to a bomb! :)
                    
                    // if(tiles[x][y] != 1) // If we're not a bomb...
                    // {
                    //     switch (countNearbyBombs(x,y)) // pick a picture
                    //     {
                    //         case 1:
                    //             img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/OneTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                    //             break;
                    //         case 2:
                    //             img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/TwoTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                    //             break;
                    //         case 3:
                    //             img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/ThreeTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                    //             break;
                    //         case 4:
                    //             img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/FourTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                    //             break;
                    //         case 5:
                    //             img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/FiveTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                    //             break;
                    //         case 6:
                    //             img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/SixTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                    //             break;
                    //         case 7:
                    //             img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/SevenTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                    //             break;
                    //         case 8:
                    //             img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/EightTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                    //             break;
                    //         default:
                    //             img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/BlankTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                    //             break;
                    //     } // end of switch
                    // } // end of
                    c.gridx = x;
                    c.gridy = y;
                    JLabel temp = new JLabel(img);
                    board.add(temp, c);
                } // end of for y
            } // end of for x
        } catch (Exception e) {
            e.printStackTrace();
        }

        f.setVisible(true);
    }

    /*
        0 = hidden safe
        1 = hidden bomb
        2 = cleared safe
        3 = cleared BOMB
    */
    public static int countNearbyBombs(int row, int col) {
        if(tiles[row][col] == 1) {
            return 0;
        }
        int count = 0;

        //check surrounding tile is on the board AND is a bomb
        if(col-1 > 0 && tiles[row][col-1] % 2 > 0) {
            count++;
        }
        if(col+1 < cols && tiles[row][col+1] % 2 > 0) {
            count++;
        }

        if(row-1 > 0 && tiles[row-1][col] % 2 > 0) {
            count++;
        }
        if(row+1 < rows && tiles[row+1][col] % 2 > 0) {
            count++;
        }
        //diagonals
        if(col-1 >0 && row-1 >0 && tiles[row-1][col-1] % 2 >0) {
            count++;
        }
        if(col+1 < cols && row-1 >0 && tiles[row-1][col+1] % 2 >0) {
            count++;
        }

        if(col-1 >0 && row+1 < rows && tiles[row+1][col-1] % 2 >0) {
            count++;
        }
        if(col+1 < cols && row+1 < rows && tiles[row+1][col+1] % 2 >0) {
            count++;
        }
        return count;
    }
    

    private static class updater extends Thread
    {
        //Catch all updates into our input stream
        ObjectInputStream updateStream;
        private static boolean first = true;

        public updater(ObjectInputStream i) {
            updateStream = i;
        }

        public void run() {
            while(true)
            {
                try
                {
                    //Our first packet of info is our field, so if its the first use this vvv
                    if(first)
                    {
                        tiles = (int[][])updateStream.readObject();
                    }
                    ArrayList<String> incomingUpdates = (ArrayList<String>) updateStream.readObject();
                    SwingUtilities.invokeLater(() -> {
                        System.out.println(incomingUpdates);
                        // messageArea.setText(messageArea.getText() + "\n" + incomingMessage);
                        // messageArea.setCaretPosition(messageArea.getDocument().getLength());

                        //Take the array of string coordinates and make changes to there states
                        for(String pair : incomingUpdates)
                        {
                            String[] splitPair = pair.split("\\.");
                            // our position to lock in
                            int row = Integer.parseInt(splitPair[0]);
                            int col = Integer.parseInt(splitPair[1]);
                            
                            // now update the picture icon
                            //img = new ImageIcon((ImageIO.read(new File("src/main/java/com/example/OneTile.png"))).getScaledInstance(size,size, Image.SCALE_DEFAULT));
                            String location = "src/main/java/com/example/";
                            // we need to somehow find the exact icon, and edit it
                            // we just need a way to grab an element KNOWING its a picture
                            // ;
                            int index = (row * cols) + col;
                            JLabel tile = (JLabel)board.getComponent(index); 
                            try {
                                switch (tiles[row][col])
                                {
                                    case 2:
                                        switch (countNearbyBombs(row, col))
                                        {
                                            case 1:
                                                tile.setIcon(new ImageIcon(ImageIO.read(new File(location + "OneTile.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT)));
                                            case 2:
                                                tile.setIcon(new ImageIcon(ImageIO.read(new File(location + "TwoTile.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT)));
                                            case 3:
                                                tile.setIcon(new ImageIcon(ImageIO.read(new File(location + "ThreeTile.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT)));
                                            case 4:
                                                tile.setIcon(new ImageIcon(ImageIO.read(new File(location + "FourTile.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT)));
                                            case 5:
                                                tile.setIcon(new ImageIcon(ImageIO.read(new File(location + "FiveTile.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT)));
                                            case 6:
                                                tile.setIcon(new ImageIcon(ImageIO.read(new File(location + "SixTile.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT)));
                                            case 7:
                                                tile.setIcon(new ImageIcon(ImageIO.read(new File(location + "SevenTile.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT)));
                                            case 8:
                                                tile.setIcon(new ImageIcon(ImageIO.read(new File(location + "EightTile.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT)));
                                            default:
                                                tile.setIcon(new ImageIcon(ImageIO.read(new File(location + "Cleared.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT)));
                                        }
                                    case 3:
                                        tile.setIcon(new ImageIcon(ImageIO.read(new File(location + "Bomb.png")).getScaledInstance(size,size, Image.SCALE_DEFAULT)));
                                    default:
                                        return;
                                }
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });   
                } catch (Exception e)
                {
                    System.err.println("Error at line 105: " + e);
                    break;
                }
            }
        }

        private Component getComponent(int r, int c)
        {
            return board.getComponentAt(r*size, c*size);
        }
    }
}