// To play with your AI player, you need to do two things:
// 1) Add an import statement to import your AI player
// 2) Change the lines inside start that say:
//      Player_0 =
//      Player_1 =

package ProjectThreeEngine;
import AIProjectCode.BareSnake;
import AIProjectCode.NetTrainer;
import AIProjectCode.ReinforcementAI;

import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

import java.util.List;
import java.util.ArrayList;

public class AIGameText{
    static Player Player_0;
    static Player Player_1;
    
    static GameState state;
    static GameStateWriter gs_writer;

    public static void main(String[] args) throws IOException
	{
		ReinforcementAI p0 = new ReinforcementAI();
		int numGames = 1;
		for(int i = 0; i < numGames; i++)
		{
			run_game("transcript.txt", p0);
		}
		
    }	


    public static void run_game(String filename, Player p0) throws IOException{
	List<GameState> the_states = new ArrayList<GameState>();
	gs_writer = new GameStateWriter(filename);
	
	//IMPORTANT : Change these lines to change who is playing!
	Player_0 = p0;
	Player_1 =  new RandomPlayer();


	//Set up the names in the state object
	String name_0;
	String name_1;
	name_0 = Player_0.getPlayName();
	name_1 = Player_1.getPlayName();


	state = new GameState(name_0, name_1 );
	Player_0.begin(new GameState(state), 0 );
	Player_1.begin(new GameState(state), 1 );

	the_states.add(state);

	while( !state.isGameOver() ){
	    state = nextTurn();
	    the_states.add(state);
	}
	
		
	Player_0.Train(state);
		

	gs_writer.WriteGame(the_states);
	
    }


    static GameState nextTurn()
	{
		List<Move> moves = new ArrayList<Move>();
		
		if (! state.isGameOver() )
		{
			DirType new_dir;

			new_dir = Player_0.getMove( new GameState(state) );
			if(new_dir != null){
			moves.add(new Move(0, new_dir));
			}

			new_dir = Player_1.getMove( new GameState(state) );
			if(new_dir != null){
			moves.add(new Move(1, new_dir));
			}
			
			state = GameRules.makeMoves(state, moves);
		
		}
		
		return state;
    }
}

	


	
