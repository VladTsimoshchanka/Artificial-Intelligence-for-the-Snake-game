package AIProjectCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.lang.model.util.ElementScanner14;

import ProjectThreeEngine.*;
import java.lang.Math;
public class DeperadoSnake implements Player
{
    int my_num;
    List<Integer> enemyPLayerNumbers = new List<Integer>();

    int perfectX;
    int perfectY;
    public void begin(GameState init_state, int play_num)
    {
        
         int numPlayers = init_state.getNumPlayers();
        my_num = play_num;
        for(int i = 0; i < numPlayers; i++)
        {
            if(i != my_num)
            {
                enemyPLayerNumbers.add(i);      //gets all enemies
            }
        }

      
        }
    public DirType getMove(GameState state)
    {	
        int[] enemiesX = new int[state.getNumPlayers()];
        int[] enemiesY =  new int[state.getNumPlayers()];

        HeadPiece myHead = state.getSnake(my_num).getHead();
        int headX = myHead.getX();
        int headY = myHead.getY();

        for (int i : enemyPLayerNumbers) //get all head coordinates
        {
            HeadPiece head = state.getSnake(i).getHead(); 
            enemiesX[i] = head.getX();
            enemiesY[i]  = head.getY();
        }

       int minDist = 999;
       int closestX;
       int closestY;
       for(int i = 0; i < enemiesX.length; i++)
       {
           int xDist = Math.abs(enemiesX[i] - headX);
           int yDist = Math.abs(enemiesY[i] - headX);
           int totalSteps = xDist + yDist;
           if(totalSteps < minDist)
           {
               closestX = enemiesX[i];
               closestY = enemiesY[i];
               minDist = totalSteps;
           }
       }

       //getDirection of nearest enemy head
       boolean n, e = false;
       
       if(closestX > headX)
            e = true;
        else
            e = false;
        if(closesY > headY)
            n = true;
        else    
            n = false;

        
            //get possible directions to move
        ArrayList<DirType> PossibleMoves;
        GamePiece onTheLeft = state.getPiece(state.getSnake(my_num).head.getX()-1, state.getSnake(my_num).head.getY());
        GamePiece onTheRight = state.getPiece(state.getSnake(my_num).head.getX()+1, state.getSnake(my_num).head.getY());
        GamePiece Up = state.getPiece(state.getSnake(my_num).head.getX(), state.getSnake(my_num).head.getY()-1);
        GamePiece Down = state.getPiece(state.getSnake(my_num).head.getX(), state.getSnake(my_num).head.getY()+1);
        if(onTheLeft == null || state.isFood(onTheLeft.getX(), onTheLeft.getY())){
            if(state.getSnake(my_num).head.getX() > 0){
                PossibleMoves.add(DirType.West);
            }
        }
        if(onTheRight == null || state.isFood(onTheRight.getX(), onTheRight.getY())){
            if(state.getSnake(my_num).head.getX() < 15){
                PossibleMoves.add(DirType.East);
            }
        }
        if(Up == null || state.isFood(Up.getX(), Up.getY())){
            if(state.getSnake(my_num).head.getY() > 0){
                PossibleMoves.add(DirType.North);
            }
        }
        if(Down == null || state.isFood(Down.getX(), Down.getY())){
            if(state.getSnake(my_num).head.getY() < 15){
                PossibleMoves.add(DirType.South);
            }
        }

        if(n) //makes twice as likely
        {
            if(PossibleMoves.contains(DirType.North))
                PossibleMoves.add(DirType.North);
        }
        else if(PossibleMoves.contains(DirType.South))
            PossibleMoves.add(DirType.South);
        
    
        if(e)
        {
            if(PossibleMoves.contains(DirType.East))
                PossibleMoves.add(DirType.East);
        }
        else if(PossibleMoves.contains(DirType.West))
            PossibleMoves.add(DirType.West);
        
         
            Random rand = new Random();
            DirType finalMove = PossibleMoves.get(rand.nextInt(givenList.size()));
            return finalMove;
                
            
       
    }
    

    public String getPlayName()
    {
    return "Avid Snake";
    }
     
}