import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Client
{
    private static JFrame f;
    private static JLabel top;
    private static JLabel bottom;
    private static JPanel board;
    private static int[][] tiles;
    private static int rows = 9;
    private static int cols = 9;

    public static void main(String[] args)
    {
        int[][] tiles = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 1, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
        };
        setupGUI();
    }

    private static void setupGUI()
    {
        f = new JFrame();
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
                for(int y = 0; y < tiles[x].length; x++){
                    ImageIcon img;
                    img = new ImageIcon(ImageIO.read(new File("Bomb.png"))); // default to a bomb! :)
                    if(tiles[x][y] != 1) // If we're not a bomb...
                    {
                        switch (countNearbyBombs(x,y)) // pick a picture
                        {
                            case 1:
                                img = new ImageIcon(ImageIO.read(new File("OneTile.png")));
                                break;
                            case 2:
                                img = new ImageIcon(ImageIO.read(new File("TwoTile.png")));
                                break;
                            case 3:
                                img = new ImageIcon(ImageIO.read(new File("ThreeTile.png")));
                                break;
                            case 4:
                                img = new ImageIcon(ImageIO.read(new File("FourTile.png")));
                                break;
                            case 5:
                                img = new ImageIcon(ImageIO.read(new File("FiveTile.png")));
                                break;
                            case 6:
                                img = new ImageIcon(ImageIO.read(new File("SixTile.png")));
                                break;
                            case 7:
                                img = new ImageIcon(ImageIO.read(new File("SevenTile.png")));
                                break;
                            case 8:
                                img = new ImageIcon(ImageIO.read(new File("EightTile.png")));
                                break;
                            default:
                                img = new ImageIcon(ImageIO.read(new File("BlankTile.png")));
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
    

    // private static int[][] createField(int r, int c, int m) {
    //     int[] tempField = new int[r*c];
    //     for(int i = 0; i<m; i++) {
    //         tempField[i] = 1;
    //     }

    //     Collections.shuffle(Arrays.asList(tempField));

    //     //turn into 2d array
    //     int[][] f = new int[r][c];
    //     for(int i=0; i<tempField.length; i++) {
    //         int row = i / c;
    //         int col = i % r;
    //         f[row][col] = tempField[i];
    //     }
    //     return f;
    // }

    public static int countNearbyBombs(int row, int col) {
        if(tiles[row][col] == 1) {
            return 0;
        }
        int count = 0;

        //check surrounding tile is on the board AND is a bomb
        if(col-1 > 0 && tiles[row][col-1] > 0) {
            count++;
        }
        if(col+1 < cols+1 && tiles[row][col+1] > 0) {
            count++;
        }

        if(row-1 > 0 && tiles[row-1][col] > 0) {
            count++;
        }
        if(row+1 < rows+1 && tiles[row+1][col] > 0) {
            count++;
        }

        //diagonals
        if(col-1 >0 && row-1 >0 && tiles[row-1][col-1] >0) {
            count++;
        }
        if(col+1 < cols+1 && row-1 >0 && tiles[row-1][col+1] >0) {
            count++;
        }

        if(col-1 >0 && row+1 < rows+1 && tiles[row+1][col-1] >0) {
            count++;
        }
        if(col+1 < cols+1 && row+1 < rows+1 && tiles[row+1][col+1] >0) {
            count++;
        }
        return count;
    }
    // private void updateGUI(LogicPacket info)
    // {

    // }
}