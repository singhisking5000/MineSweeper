package com.example;


import java.net.Socket;
import java.sql.Connection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
//okay time to write 2 lines of comments and stop for the day
public class Server
{
    public static final int LISTENING_PORT = 9876;

    // Upon creating a server, establish all of the following connections
    public Server(){
        ServerSocket listener;
        GameLogic logic = new GameLogic();
        try {
            listener = new ServerSocket(LISTENING_PORT);

            while(true){
                 //Keep creating new ConnectionHandlers
                ConnectionHandler temp = new ConnectionHandler(listener.accept(), logic);
                System.out.println("Successsfully connected to (" + temp.socket.getInetAddress().toString() + ")!");
                temp.start();
                temp.out.writeObject(logic.field);
            }
        } catch (Exception e) {
            System.out.println("Sorry, the server has shut down.");
            System.out.println("Error:  " + e);
            return;
        }
    }

    public static void main(String[] args)
    {
        Server server = new Server();
    }


    // public Tile[][] getBoard()
    // {
    //     return board;
    // }
    
    private class GameLogic
    {
        /*
            lOGIC NOTES:
                - We need an ArrayList of all the altered tiles after each move, which is sent to all the clients
                  to update visuals
                - On the field, 1 represents a bomb, 0 represents a safe tile, and 2 should represent a cleared
                  tile, which should be fed into the ArrayList of altered tiles to update the visuals
        */
        
        private int mines = 10;
        private int rows = 9;
        private int cols = 9;

        private int[][] field = createField();
        
        private int[][] createField() {
            int[] tempField = new int[rows*cols];
            int[][] newField = new int[rows][cols];

            for(int i = 0; i<mines; i++) {
                tempField[i] = 1;
            }

            //Collections.shuffle an array isn't shuffling correctly
            // so I used a function someone else created to do it.
            //Collections.shuffle(Arrays.asList(tempField));
            shuffleArray(tempField);

            //turn into 2d array
            for(int i=0; i<tempField.length; i++) {
                int row = i / cols;
                int col = i % rows;
                newField[row][col] = tempField[i];
            }

            System.out.println(Arrays.toString(newField));
            String str = "";
            for (int r = 0; r < newField.length; r++)
            {
                for (int c = 0; c < newField.length; c++)
                {
                    if(c != newField[r].length-1)
                    {
                        str += newField[r][c] + ", ";
                    } else {
                        str += newField[r][c] + "\n";
                    }
                }
            }
            System.out.println(str);
            return newField;
        }

        private static void shuffleArray(int[] ar)
        {
            Random rnd = new Random();
            for (int i = ar.length - 1; i > 0; i--)
            {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
            }
        }
        private int countNearbyBombs(int row, int col) {
            if(field[row][col] == 1) {
                return 0;
            }
            int count = 0;

            //check surrounding tile is on the board AND is a bomb
            if(col-1 > 0 && field[row][col-1] > 0) {
                count++;
            }
            if(col+1 < cols+1 && field[row][col+1] > 0) {
                count++;
            }
            if(row-1 > 0 && field[row-1][col] > 0) {
                count++;
            }
            if(row+1 < rows+1 && field[row+1][col] > 0) {
                count++;
            }
            //diagonals
            if(col-1 >0 && row-1 >0 && field[row-1][col-1] >0) {
                count++;
            }
            if(col+1 < cols+1 && row-1 >0 && field[row-1][col+1] >0) {
                count++;
            }
            if(col-1 >0 && row+1 < rows+1 && field[row+1][col-1] >0) {
                count++;
            }
            if(col+1 < cols+1 && row+1 < rows+1 && field[row+1][col+1] >0) {
                count++;
            }
            return count;
        }

        //0 is hidden safe
        //1 is hidden bomb
        //2 is cleared safe
        //3 is cleared BOMB
        //basically, bombs are odds
        //jamshed wanted this over enums so thats on him
        // returns an array of tiles to be update in visuals
        public ArrayList<String> logicUpdate(int row, int col) //col = x, row = y
        {
            ArrayList<String> affectedTiles = new ArrayList<String>();
            if(field[row][col] == 1) // if we click a bomb
            {
                // REVEAL THE WHOLE BOARD 
                for(int y = 0; y < rows; y++)
                {
                    for(int x = 0; x < cols; x++)
                    {
                        if(field[y][x] == 0)
                        {
                            field[y][x] = 2;
                            affectedTiles.add(y + "." + x);
                        } else if (field[y][x] == 1)
                        {
                            field[y][x] = 3;
                            affectedTiles.add(y + "." + x);
                        }
                        // ignore others, as they stay the same
                    }
                }
            } else if (field[row][col] == 0) // if we clicked on a safe
            {
                // ALTERS THE BOARD VVVVV
                String changes = clearSafeTile(row, col);
                // now decode changes and add them to the arraylist
                // encoded as this:
                // r.c!  where r is row, c is column, and they are a pair as r.c, and seperate pairs by !
                String[] vals = changes.split("!");
                for (String val : vals)
                {
                    if (!val.isEmpty()) {
                        affectedTiles.add(val);
                    }
                }
            }

            return affectedTiles;
        }


        public String clearSafeTile(int row, int col) {
            // Check if we are in bounds to save us from null errors
            if(row < 0 || row >= rows) { 
                return "";
            }
            if(col < 0 || col >= cols) {
                return "";
            }

            if(countNearbyBombs(row, col) == 0) { // if its safe, add it AND ALL of the other ones
                field[row][col] = 2;
                return (row + "." + col + "!") 
                    + clearSafeTile(row-1, col-1) // above
                    + clearSafeTile(row-1, col)
                    + clearSafeTile(row-1, col+1)

                    + clearSafeTile(row, col-1) //   side
                    + clearSafeTile(row, col+1)

                    + clearSafeTile(row+1, col-1) // below
                    + clearSafeTile(row+1, col)
                    + clearSafeTile(row+1, col+1);
            }
            // If its 1, 2, or 3, add nothing, they stay the same
            return "";
        }

        //assuming that the click is actually on a tile
        //tries to make the first time you click on the board a safe tile
        public void clearFirstClick(int row, int col) {
            if(field[row][col] == 0) {
                return;
            }
            for(int i=0; i<cols; i++) {
                if(field[0][i] == 0) {
                    field[row][col] = 0;
                    field[0][i] = 2;
                }
            }
        }
    }

    private class ConnectionHandler extends Thread
    {
        private static ArrayList<ConnectionHandler> handlers;
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;
        GameLogic gameLogic;

        public ConnectionHandler(Socket s, GameLogic gl) // Establish connection to streams
        {
            socket = s;
            if (handlers == null)
            {
                handlers = new ArrayList<ConnectionHandler>();
            }
            handlers.add(this);
            gameLogic = gl;
            //Attempt to connect in and out streams
            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run ()
        {
            while(true)
            {
                try {
                    // Get incoming coordinates of where a player clicked
                    String coords = (String)in.readObject();
                    // break up the coords into r and y, then use logicUpdate to update the board
                    // and get all the changed tiles
                    String[] c = coords.split("\\.");
                    ArrayList<String> updates = gameLogic.logicUpdate(Integer.parseInt(c[0]), Integer.parseInt(c[1]));
                    sendUpdates(updates);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // end of while
        } // end of run

        private void sendUpdates(ArrayList<String> inputs) // send out our array of coordinates
        {
            synchronized(this) // focus me
            {
                for (ConnectionHandler handler : handlers)
                {
                    try {
                        synchronized(handler) // focus everyone individually
                        {
                            handler.out.writeObject(inputs); //send out the update 
                            handler.out.flush(); // immediately
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}



    