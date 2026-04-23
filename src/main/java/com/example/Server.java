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
//okay time to write 2 lines of comments and stop for the day
public class Server
{
    public static final int LISTENING_PORT = 9876;
    public Server(){
        ServerSocket listener;

        try {
            listener = new ServerSocket(LISTENING_PORT);

            while(true){
                 //Keep creating new ConnectionHandlers
                ConnectionHandler temp = new ConnectionHandler(listener.accept());
                System.out.println("Successsfully connected to (" + temp.socket.getInetAddress().toString() + ")!");
                temp.start();
                // temp.out.writeObject("You have connected!");
            }
        } catch (Exception e) {
            System.out.println("Sorry, the server has shut down.");
            System.out.println("Error:  " + e);
            return;
        }
    }

    public static void main(String[] args)
    {
        new Server();
    }


    // public Tile[][] getBoard()
    // {
    //     return board;
    // }
    
    private class GameLogic
    {
        private int mines = 10;
        private int rows = 9;
        private int cols = 9;

        private int[][] field = createField();
        
        public int[][] getField() {
            return field;
        }
        
        private int[][] createField() {
            int[] tempField = new int[rows*cols];
            int[][] newField = new int[rows][cols];

            for(int i = 0; i<mines; i++) {
                tempField[i] = 1;
            }

            Collections.shuffle(Arrays.asList(tempField));

            //turn into 2d array
            for(int i=0; i<tempField.length; i++) {
                int row = i / cols;
                int col = i % rows;
                newField[row][col] = tempField[i];
            }

            System.out.println(Arrays.toString(newField));
            return newField;
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


        //0 is safe
        //1 is bomb
        //2 is cleared
        //jamshed wanted this over enums so thats on him
        public void clearSafeTile(int row, int col) {
            if(row <= 0 || row > rows) {
                return;
            }
            if(col <= 0 || col > cols) {
                return;
            }
            if(field[row][col] == 2) {
                return;
            }

            field[row][col] = 2;
            if( countNearbyBombs(row, col) == 0 ) {
                clearSafeTile(row-1, col-1);
                clearSafeTile(row-1, col);
                clearSafeTile(row-1, col+1);

                clearSafeTile(row, col-1);
                clearSafeTile(row, col+1);

                clearSafeTile(row+1, col-1);
                clearSafeTile(row+1, col);
                clearSafeTile(row+1, col+1);
            }
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
        private /* static */ ArrayList<ConnectionHandler> handlers;
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;

        public ConnectionHandler(Socket s)
        {
            socket = s;
            if (handlers == null)
            {
                handlers = new ArrayList<ConnectionHandler>();
            }
            handlers.add(this);
            
            //Attempt to connect in and out streams
            try {
                in = (ObjectInputStream) socket.getInputStream();
                out = (ObjectOutputStream) socket.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run ()
        {
            
        }
    }
}



    