import java.util.ArrayList;

public class NonRandomPlayer implements Player
{
    
    private int my_num;
    private int enemy_num;
    
    public NonRandomPlayer()
    {

    }

    
    public void begin(GameState init_state, int play_num)
    {
        my_num = play_num;
        
        //hard-coded for two players
        if(my_num == 0)
        {
            enemy_num = 1;
        }
        else
        {
            enemy_num = 0;
        }
    }
    
    
    public DirType getMove(GameState state)
    {
        
        //get the head of our snake object
        Snake the_snake = state.getSnake(my_num);
        HeadPiece the_head = the_snake.getHead();
        
        //get our coords
        int xPos = the_head.getX();
        int yPos = the_head.getY();
        
        //build a list of possible moves
        ArrayList<DirType> moves = new ArrayList<DirType>();
        moves.add(DirType.North);
        moves.add(DirType.East);
        moves.add(DirType.South);
        moves.add(DirType.West);
        
        //stop the player from colliding with itself or the opponent
        moves = removeBodyCollisions(xPos, yPos, the_snake, moves);
        moves = removeBodyCollisions(xPos, yPos, state.getSnake(enemy_num), moves);
        
        //prevent the player from colliding with any walls
        moves = removeWallCollisions(xPos, yPos, state, moves);
        
        //find the nearest food
        Tup foodPos = getNearestFood(xPos, yPos, state);
        
        return moveNearestFood(xPos, yPos, foodPos, moves);
    }
    
    
    //Note: passing the player snake should remove directions where the player would collide
    //      with its own body. Passing the opponent should remove directions where th player
    //      would collide with its opponent's body.
    private ArrayList<DirType> removeBodyCollisions(int playerX, int playerY, Snake snake, ArrayList<DirType> posDirs)
    {
        if(snake.isPresent(northFrom(playerX, playerY).x, northFrom(playerX, playerY).y))
        {
            posDirs.remove(DirType.North);
        }
        if(snake.isPresent(eastFrom(playerX, playerY).x, eastFrom(playerX, playerY).y))
        {
            posDirs.remove(DirType.East);
        }
        if(snake.isPresent(southFrom(playerX, playerY).x, southFrom(playerX, playerY).y))
        {
            posDirs.remove(DirType.South);
        }
        if(snake.isPresent(westFrom(playerX, playerY).x, westFrom(playerX, playerY).y))
        {
            posDirs.remove(DirType.West);
        }
        
        
        return posDirs;
    }
    
    
    private ArrayList<DirType> removeWallCollisions(int playerX, int playerY, GameState state, ArrayList<DirType> posDirs)
    {
        if(northFrom(playerX, playerY).y < 0)
        {
            posDirs.remove(DirType.North);
        }
        if(eastFrom(playerX, playerY).x > state.max_x)
        {
            posDirs.remove(DirType.East);
        }
        if(southFrom(playerX, playerY).y > state.max_y)
        {
            posDirs.remove(DirType.South);
        }
        if(westFrom(playerX, playerY).x < 0)
        {
            posDirs.remove(DirType.West);
        }
        
        return posDirs;
    }
    
    
    private Tup getNearestFood(int playerX, int playerY, GameState state)
    {
        Tup nearestFood = new Tup(0, 0);
        double nearestFdDist = Double.POSITIVE_INFINITY;
        
        for(int i = 0; i < state.max_x; i++)
        {
            for(int j = 0; j < state.max_y; j++)
            {
                if (state.isFood(i, j) && dist(playerX, playerY, i, j) < nearestFdDist)
                {
                    nearestFood = new Tup(i, j);
                    nearestFdDist = dist(playerX, playerY, i, j);
                }
            }
        }
        
        return nearestFood;
    }
    
    
    private DirType moveNearestFood(int playerX, int playerY, Tup food, ArrayList<DirType> posDirs)
    {
        
        //return null if no safe moves
        if(posDirs.size() == 0)
        {
            return null;
        }
        
        
        
        DirType current = null;
        double currentDist = Double.POSITIVE_INFINITY;
        
        while(posDirs.size() > 0)
        {
            DirType temp = posDirs.get(0);
            
            if(temp == DirType.North)
            {
                double tempDist = dist(northFrom(playerX, playerY).x, northFrom(playerX, playerY).y, food.x, food.y);
                
                if(tempDist < currentDist)
                {
                    posDirs.remove(current);
                    current = temp;
                    currentDist = tempDist;
                }
                else
                {
                    posDirs.remove(temp);
                }
            }
            else if(temp == DirType.East)
            {
                double tempDist = dist(eastFrom(playerX, playerY).x, eastFrom(playerX, playerY).y, food.x, food.y);
                
                if(tempDist < currentDist)
                {
                    posDirs.remove(current);
                    current = temp;
                    currentDist = tempDist;
                }  
                else
                {
                    posDirs.remove(temp);
                }
            }
            else if(temp == DirType.South)
            {
                double tempDist = dist(southFrom(playerX, playerY).x, southFrom(playerX, playerY).y, food.x, food.y);
                
                if(tempDist < currentDist)
                {
                    posDirs.remove(current);
                    current = temp;
                    currentDist = tempDist;
                }
                else
                {
                    posDirs.remove(temp);
                }
            }
            else    //west
            {
                double tempDist = dist(westFrom(playerX, playerY).x, westFrom(playerX, playerY).y, food.x, food.y);
                
                if(tempDist < currentDist)
                {
                    posDirs.remove(current);
                    current = temp;
                    currentDist = tempDist;
                }
                else
                {
                    posDirs.remove(temp);
                }
            }
        }

        
        return current;
    }
    
    
    
    
    //methods and class for handling coords
    class Tup
    {
        public Tup(int theX, int theY){ x = theX; y = theY;}
        int x;
        int y;
    }
    
    private Tup northFrom(int theX, int theY)
    {
        return new Tup(theX, theY-1);
    }
    private Tup eastFrom(int theX, int theY)
    {
        return new Tup(theX+1, theY);
    }
    private Tup southFrom(int theX, int theY)
    {
        return new Tup(theX, theY+1);
    }
    private Tup westFrom(int theX, int theY)
    {
        return new Tup(theX-1, theY);
    }
    
    private double dist(int x1, int y1, int x2, int y2)
    {
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }
    
    
    
    
    public String getPlayName()
    {
        return "NonRandom";
    }
}
