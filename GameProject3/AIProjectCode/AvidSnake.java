package AIProjectCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ProjectThreeEngine.*;
import java.lang.Math;
public class AvidSnake implements Player{
    int my_num;
    Random r;
    List<FoodPiece> foods;
    int perfectX;
    int perfectY;
    public void begin(GameState init_state, int play_num){
        my_num = play_num;
        r = new Random();
        foods = new ArrayList<FoodPiece>();
        }
    public DirType getMove(GameState state){	
        foods.clear();

        foods = state.getFoods();
        perfectX = foods.get(0).getX();
        perfectY = foods.get(0).getY();
        for(FoodPiece crumb : foods){
            int xDifference = Math.abs(crumb.getX()-state.getSnake(my_num).getHead().getX());
            int yDifference = Math.abs(crumb.getY()-state.getSnake(my_num).getHead().getY());
            int sum = xDifference + yDifference;
            int perfectSum = Math.abs(perfectX-state.getSnake(my_num).getHead().getX()) + Math.abs(perfectY-state.getSnake(my_num).getHead().getY());
            if(sum < perfectSum){
                perfectX = crumb.getX();
                perfectY = crumb.getY();
            }
        }

        if(perfectX > state.getSnake(my_num).getHead().getX()){
            return DirType.East;
        }
        if(perfectX < state.getSnake(my_num).getHead().getX()){
            return DirType.West;
        }
        if(perfectY > state.getSnake(my_num).getHead().getY()){
            return DirType.South;
        }
        if(perfectX < state.getSnake(my_num).getHead().getY()){
            return DirType.North;
        }
        return null; 
         }
        
            
        
    public String getPlayName(){
    return "Avid Snake";
     }
}