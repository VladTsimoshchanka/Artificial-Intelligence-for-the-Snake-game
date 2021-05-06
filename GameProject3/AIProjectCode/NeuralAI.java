package AIProjectCode;

import java.util.ArrayList;

import ProjectThreeEngine.*;

//TODO: figure out how to assess Fitness and save out the output to use in the next game
public class NeuralAI implements Player
{
    int my_num;
    final int SIZE = 20;
    final int hidden_nodes = 16;
    final int hidden_layers = 2;
    float mutationRate = 0.05f;
    NeuralNet brain;
    GameState curState;
    float[] vision;
    float[] decision;

    ArrayList<FoodPiece> foodList;  //list of food positions (used to replay the best snake)
    
    FoodPiece food;
    int score = 3;
    


//stuff I'm not sure we need
    boolean replayBest = true;  //shows only the best of each generation
    boolean modelLoaded = false;
    ArrayList<Integer> evolution;
    
    //This function is called when the game starts
    public void begin(GameState init_state, int play_num)
    {
	    my_num = play_num;
        curState = init_state;

//ADD INPUTS AND PREPROCESSING HERE
       vision = new float[24];               //makes an array of floats for vision
        
        decision = new float[4];              //decision is a vector of 4 directions to move
        ArrayList<FoodPiece> foodList;  //list of food positions (used to replay the best snake)


//WARNING: Brain is hardcoded for 24 inputs from vision
        brain = new NeuralNet(24,hidden_nodes,4,hidden_layers);    //in, hidden nodes, out, hiddenlayers 

//if have model, load up model into brain
        
//NOt sure if needed
        foodList = new ArrayList<FoodPiece>();
        //foodList.add(new FoodPiece());
        
    }

    //You can return null to just keep going straight
    public DirType getMove(GameState state)
    {
        
        look(state);                         //how the snake AI runs
        DirType dir = think();
//TODO: figure out how to assess Fitness and save out the output to use in the next game
        return dir;

       // pop.calculateFitness();
        //pop.naturalSelection();
    }

    boolean foodCollide(GameState state, int x, int y)
     {  
        return state.isFood(x, y);
     }
     GamePiece checkCollision(GameState state, int x, int y)
     {
         return state.getPiece(x, y);
     }

    public String getPlayName()
    {
	    return "Neural AI";
    }

    void mutate() {  //mutate the snakes brain
        brain.mutate(mutationRate); 
     }
    
 //IMPORTANT: Calculate efficacy of snake
    /* void calculateFitness() 
     {  //calculate the fitness of the snake
        if(score < 10) 
        {
           fitness = (int)(lifetime * lifetime) * (float)Math.pow(2,score); 
        } else 
        {
           fitness = (int)(lifetime * lifetime);
           fitness *= Math.pow(2,10);
           fitness *= (score-9);
        }
     }*/

    ///////////////////Basic Nerual Net Working Logic
//IMPORTANT: checks surroundings    
    void look(GameState state) {  //look in all 8 directions and check for food, body and wall
        vision = new float[24];
        

        //examine each possible movable direction and diagonals
          float[] temp = lookInCardinal(state, 0, true);      //looks up
          vision[0] = temp[0];
          vision[1] = temp[1];
          vision[2] = temp[2];

          temp = lookInCardinal(state, 12, true);        //look down
          vision[3] = temp[0];
          vision[4] = temp[1];
          vision[5] = temp[2];

          temp = lookInCardinal(state, 0, false);       //lookLeft
          vision[6] = temp[0];
          vision[7] = temp[1];
          vision[8] = temp[2];
          
          temp = lookInCardinal(state, 12, false);       //look Right
          vision[9] = temp[0];
          vision[10] = temp[1];
          vision[11] = temp[2];

          temp = lookDiagonal(state, 0, 0);        //up left
          vision[12] = temp[0];
          vision[13] = temp[1];
          vision[14] = temp[2];
          
          temp = lookDiagonal(state, 12, 0);     //up right
          vision[15] = temp[0];
          vision[16] = temp[1];
          vision[17] = temp[2];
          
          temp = lookDiagonal(state, 0,12);      //down left
          vision[18] = temp[0];
          vision[19] = temp[1];
          vision[20] = temp[2];
          
          temp = lookDiagonal(state, 12, 12);    //down right
          vision[21] = temp[0];
          vision[22] = temp[1];
          vision[23] = temp[2];
      }
    
      float[] lookInCardinal(GameState state, float endPos, boolean isYaxis) 
      {  //look in a direction and check for food, body and wall
        
        float look[] = new float[3];
        int distance = 1;
        HeadPiece head = state.getSnake(my_num).getHead(); 
        int x = head.getX();
        int y = head.getY();

        if(isYaxis)
        {
            if(y >= endPos)      //moving up
            {
              while ((y-distance) >= 0) 
              {               //while not hitting wall, check food and body collision and report 1 if hit in corresponding index of look
  
                GamePiece piece = checkCollision(state, x, (y-distance));
                if(piece != null) 
                {
                  if(piece instanceof FoodPiece)
                  {
                    //foodFound = true;
                    look[0] = 1;
                  }
                  else
                  {
//maybe distinguish between our snake and opponent snake
                      //bodyFound = true;
                      look[1] = 1;
                  }
                  
                }
                distance += 1;
              }
            }
            else                                        //look down
            {
              while ((y+distance) <= 12) 
              {               //while not hitting wall, check food and body collision and report 1 if hit in corresponding index of look
  
                GamePiece piece = checkCollision(state, x, (y+distance));
                if(piece != null) 
                {
                  if(piece instanceof FoodPiece)
                  {
                    //foodFound = true;
                    look[0] = 1;
                  }
                  else
                  {
//maybe distinguish between our snake and opponent snake
                      //bodyFound = true;
                      look[1] = 1;
                  }
                }
                distance += 1;
              }         
            }

            look[2] = 1/distance;   
            return look;
        }
        else        //move along x axis
        {
          if(x >= endPos)      //moving left
          {
            while ((x-distance) >= 0) 
            {               //while not hitting wall, check food and body collision and report 1 if hit in corresponding index of look

              GamePiece piece = checkCollision(state, (x-distance), y);
              if(piece != null) 
              {
                if(piece instanceof FoodPiece)
                {
                  //foodFound = true;
                  look[0] = 1;
                }
                else
                {
//maybe distinguish between our snake and opponent snake
                    //bodyFound = true;
                    look[1] = 1;
                }
                
              }
              distance += 1;
            }
          }
          else                                        //look down
          {
            while ((x+distance) <= 12) 
            {               //while not hitting wall, check food and body collision and report 1 if hit in corresponding index of look

              GamePiece piece = checkCollision(state, (x+distance), y);
              if(piece != null) 
              {
                if(piece instanceof FoodPiece)
                {
                  //foodFound = true;
                  look[0] = 1;
                }
                else
                {
//maybe distinguish between our snake and opponent snake
                    //bodyFound = true;
                    look[1] = 1;
                }
              }
              distance += 1;
            }         
          }

          
        } 
        look[2] = 1/distance;   
        return look;
    }

    float[] lookDiagonal(GameState state, int endX, int endY)
    {
      float look[] = new float[3];
      int distance = 1;
      HeadPiece head = state.getSnake(my_num).getHead(); 
      int x = head.getX();
      int y = head.getY();

      if(x > endX)    //up 
      {
        if(y > endY)              //up left
        {
          while ((x-distance) >= 0 && (y-distance) >= 0) 
          {               //while not hitting wall, check food and body collision and report 1 if hit in corresponding index of look
  
            GamePiece piece = checkCollision(state, (x-distance), (y-distance));
            if(piece != null) 
            {
              if(piece instanceof FoodPiece)
              {
                //foodFound = true;
                look[0] = 1;
              }
              else
              {
  //maybe distinguish between our snake and opponent snake
                  //bodyFound = true;
                  look[1] = 1;
              }
              
            }
            distance += 1;
          }     
        }
        else                    //down left
        {
          while ((x-distance) >= 0 && (y+distance) <= 12) 
          {               //while not hitting wall, check food and body collision and report 1 if hit in corresponding index of look
  
            GamePiece piece = checkCollision(state, (x-distance), (y+distance));
            if(piece != null) 
            {
              if(piece instanceof FoodPiece)
              {
                //foodFound = true;
                look[0] = 1;
              }
              else
              {
  //maybe distinguish between our snake and opponent snake
                  //bodyFound = true;
                  look[1] = 1;
              }
              
            }
            distance += 1;
          }     
        }
  
      }
      else            //move down
      {
        if(y < endY)              //up right
        {
          while ((x+distance) <= 12 && (y-distance) >= 0) 
          {               //while not hitting wall, check food and body collision and report 1 if hit in corresponding index of look
  
            GamePiece piece = checkCollision(state, (x+distance), (y-distance));
            if(piece != null) 
            {
              if(piece instanceof FoodPiece)
              {
                //foodFound = true;
                look[0] = 1;
              }
              else
              {
  //maybe distinguish between our snake and opponent snake
                  //bodyFound = true;
                  look[1] = 1;
              }
              
            }
            distance += 1;
          }     
        }
        else                    //down left
        {
          while ((x+distance) <= 12 && (y+distance) <= 12) 
          {               //while not hitting wall, check food and body collision and report 1 if hit in corresponding index of look
  
            GamePiece piece = checkCollision(state, (x+distance), (y+distance));
            if(piece != null) 
            {
              if(piece instanceof FoodPiece)
              {
                //foodFound = true;
                look[0] = 1;
              }
              else
              {
  //maybe distinguish between our snake and opponent snake
                  //bodyFound = true;
                  look[1] = 1;
              }
              
            }
            distance += 1;
          }     
        }
      }
      look[2] = 1/distance;   
      return look;
    }
      
    DirType think() 
    {  //think about what direction to move
          decision = brain.output(vision);
          int maxIndex = 0;
          float max = 0;
          for(int i = 0; i < decision.length; i++) 
          {
            if(decision[i] > max) {
              max = decision[i];
              maxIndex = i;
            }
          }
          
          switch(maxIndex) {
             case 0:
               return DirType.North;
             case 1:
               return DirType.South;
             case 2:
               return DirType.West;
             case 3: 
               return DirType.East;
              default:
                return null;
          }
      }

//file saving parts: change until we got something that saves
   /* void fileSelectedIn(File selection)    //loads in a file to fill the hidden node values so as to use saved data 
    {
        if (selection == null) 
        {
          println("Window was closed or the user hit cancel.");
        } 
        else 
        {
          String path = selection.getAbsolutePath();
          Table modelTable = loadTable(path,"header");        //loads a model table, makes a mmatrix fromthe model table
          Matrix[] weights = new Matrix[modelTable.getColumnCount()-1];
          float[][] in = new float[hidden_nodes][25];
          for(int i=0; i< hidden_nodes; i++) 
          {
            for(int j=0; j< 25; j++) 
            {
              in[i][j] = modelTable.getFloat(j+i*25,"L0");
            }  
          }
          weights[0] = new Matrix(in);
          
          for(int h=1; h<weights.length-1; h++) 
          {
             float[][] hid = new float[hidden_nodes][hidden_nodes+1];
             for(int i=0; i< hidden_nodes; i++) 
             {
                for(int j=0; j< hidden_nodes+1; j++) 
                {
                  hid[i][j] = modelTable.getFloat(j+i*(hidden_nodes+1),"L"+h);
                }  
             }
             weights[h] = new Matrix(hid);
          }
          
          float[][] out = new float[4][hidden_nodes+1];
          for(int i=0; i< 4; i++) {
            for(int j=0; j< hidden_nodes+1; j++) 
            {
              out[i][j] = modelTable.getFloat(j+i*(hidden_nodes+1),"L"+(weights.length-1));
            }  
          }
          weights[weights.length-1] = new Matrix(out);
          
          evolution = new ArrayList<Integer>();
          int g = 0;
          int genscore = modelTable.getInt(g,"Graph");
          while(genscore != 0) {
             evolution.add(genscore);
             g++;
             genscore = modelTable.getInt(g,"Graph");
          }
          modelLoaded = true;
          humanPlaying = false;
          model = new Snake(weights.length-1);
          model.brain.load(weights);
        }
    }

    void fileSelectedOut(File selection) //outputs table to a file with all values stored for neural network
    {
        if (selection == null) 
        {
          println("Window was closed or the user hit cancel.");
        } else 
        {
          String path = selection.getAbsolutePath();
          Table modelTable = new Table();
          Snake modelToSave = pop.bestSnake.clone();
          Matrix[] modelWeights = modelToSave.brain.pull();
          float[][] weights = new float[modelWeights.length][];
          for(int i=0; i<weights.length; i++) 
          {
             weights[i] = modelWeights[i].toArray(); 
          }
          for(int i=0; i<weights.length; i++) 
          {
             modelTable.addColumn("L"+i); 
          }
          modelTable.addColumn("Graph");
          int maxLen = weights[0].length;
          for(int i=1; i<weights.length; i++) 
          {
             if(weights[i].length > maxLen) 
             {
                maxLen = weights[i].length; 
             }
          }
          int g = 0;
          for(int i=0; i<maxLen; i++) 
          {
             TableRow newRow = modelTable.addRow();
             for(int j=0; j<weights.length+1; j++) 
             {
                 if(j == weights.length) 
                 {
                   if(g < evolution.size()) 
                   {
                      newRow.setInt("Graph",evolution.get(g));
                      g++;
                   }
                 } else if(i < weights[j].length) 
                 {
                    newRow.setFloat("L"+j,weights[j][i]); 
                 }
             }
          }
          saveTable(modelTable, path);
          
        }
      }*/
      
}


/////////////////////////////potential added things:

//shows how to call for save and load input

/* if(loadButton.collide(mouseX,mouseY)) {
       selectInput("Load Snake Model", "fileSelectedIn");
   }
   if(saveButton.collide(mouseX,mouseY)) {
       selectOutput("Save Snake Model", "fileSelectedOut");
   }*/