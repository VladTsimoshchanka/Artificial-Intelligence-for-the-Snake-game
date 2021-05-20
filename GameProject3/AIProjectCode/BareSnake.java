package AIProjectCode;
import java.util.ArrayList;
import java.util.Random;
import ProjectThreeEngine.*;
public class BareSnake implements Player{
    int my_num;
    ArrayList<DirType> PossibleMoves;
    Random r;
    public void begin(GameState init_state, int play_num){
        my_num = play_num;
        PossibleMoves = new ArrayList<DirType>();
        r = new Random();
        }
    public DirType getMove(GameState state){	
        PossibleMoves.clear();
        GamePiece onTheLeft = state.getPiece(state.getSnake(my_num).getHead().getX()-1, state.getSnake(my_num).getHead().getY());
        GamePiece onTheRight = state.getPiece(state.getSnake(my_num).getHead().getX()+1, state.getSnake(my_num).getHead().getY());
        GamePiece Up = state.getPiece(state.getSnake(my_num).getHead().getX(), state.getSnake(my_num).getHead().getY()-1);
        GamePiece Down = state.getPiece(state.getSnake(my_num).getHead().getX(), state.getSnake(my_num).getHead().getY()+1);
        if(onTheLeft == null || state.isFood(onTheLeft.getX(), onTheLeft.getY())){
            if(state.getSnake(my_num).getHead().getX() > 0){
                PossibleMoves.add(DirType.West);
            }
        }
        if(onTheRight == null || state.isFood(onTheRight.getX(), onTheRight.getY())){
            if(state.getSnake(my_num).getHead().getX() < 15){
                PossibleMoves.add(DirType.East);
            }
        }
        if(Up == null || state.isFood(Up.getX(), Up.getY())){
            if(state.getSnake(my_num).getHead().getY() > 0){
                PossibleMoves.add(DirType.North);
            }
        }
        if(Down == null || state.isFood(Down.getX(), Down.getY())){
            if(state.getSnake(my_num).getHead().getY() < 15){
                PossibleMoves.add(DirType.South);
            }
        }
        if(!PossibleMoves.isEmpty()){
            int random = r.nextInt(PossibleMoves.size());
            return PossibleMoves.get(random);
        }
        return null; 
         }
        
            
        
    public String getPlayName(){
    return "Bare Snake";
     }
}
