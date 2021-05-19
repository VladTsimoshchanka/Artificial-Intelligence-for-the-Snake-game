import java.util.ArrayList;

public class NonRandomPlayer implements Player
{
    
    private int my_num;
    
    
    public NonRandomPlayer()
    {

    }

    
    public void begin(GameState init_state, int play_num)
    {
        my_num = play_num;
    }
    
    
    public DirType getMove(GameState state)
    {
        DirType choice = null;
        
        //get the our snake object
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
        
        
        return choice;
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
    
    
    
    public String getPlayName()
    {
        return "NonRandom";
    }
}
