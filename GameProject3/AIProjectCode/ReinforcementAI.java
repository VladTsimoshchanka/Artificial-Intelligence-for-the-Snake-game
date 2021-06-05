package AIProjectCode;

import java.awt.List;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import ProjectThreeEngine.*;

//TODO: figure out how to assess Fitness and save out the output to use in the next game
public class ReinforcementAI implements Player
{
    int my_num;
    int oppNum;
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

    //reinforcement learning
    public int reward = 0;
    int UnproductiveFrames = 0;     //number of frames where the snake hasn't collided or eaten food
    int numGames = 0;
    float epsilon = 0;              //controls randomness
    float gamma = 0.9f;                //discount rate
    float maxMemory = 100000;
    float batchSize = 1000;
    float learnRate = .001f;
    float momentumFactor = .0002f;
    Deque<MemoryState> memory;               //check to ensure doesn't exceed maxMemory
    //model
    NetTrainer trainer = new NetTrainer(this, brain, learnRate, gamma);

    int input_Nodes = 26;
    int outputNodes = 4;
    

    public ReinforcementAI()
    {
      memory = new ArrayDeque<MemoryState>(); 
      //initialize model and trainer
    }
    
    //This function is called when the game starts
    public void begin(GameState init_state, int play_num)
    {
	    my_num = play_num;
        curState = init_state;
      if(my_num == 0)
          oppNum = 1;
      else
        oppNum = 0;

//ADD INPUTS AND PREPROCESSING HERE
       vision = new float[input_Nodes];               //makes an array of floats for vision
        
        decision = new float[outputNodes];              //decision is a vector of 4 directions to move
        ArrayList<FoodPiece> foodList;  //list of food positions (used to replay the best snake)


//WARNING: Brain is hardcoded for 24 inputs from vision
        brain = new NeuralNet(input_Nodes,hidden_nodes,outputNodes,hidden_layers);    //in, hidden nodes, out, hiddenlayers 

//if have model, load up model into brain
        
//NOt sure if needed
        foodList = new ArrayList<FoodPiece>();
        //foodList.add(new FoodPiece());
        
    }

    //You can return null to just keep going straight
    public DirType getMove(GameState state)
    {
        
        look(state);                         //how the snake AI runs
        DirType dir = think(state);
        
        //check if snake eats food or makes collision for gameOver
        
        int[] pos = getHeadPos(state);

        if(dir.equals(DirType.North))       //0,0 is top left
            pos[1] -= 1;
        else if(dir.equals(DirType.East))
            pos[0] += 1;
        else if(dir.equals(DirType.South))
            pos[1] += 1;
        else if(dir.equals(DirType.West))
            pos[0] -= 1;

        UnproductiveFrames ++;
        if(foodCollide(state, pos[0], pos[1]))
        {
            UnproductiveFrames = 0;
            reward += 10;
        }
       
        
        return dir;

    }

    public float[] getPredictionValues(GameState state)         //Q  values
    {
      look(state);
      float[] prediction = brain.output(vision);
      return prediction;

    }
    public int getUnproductiveFrames()
    {
        return UnproductiveFrames;
    }
    
    public int getBodyLength(GameState state)
    {
        Snake s = state.getSnake(my_num);
        ArrayList<SnakePiece> body = (ArrayList<SnakePiece>) s.getBody();
        return body.size();
    }
    int[] getHeadPos(GameState state)
    {
        int[] pos = new int[2];
        HeadPiece head = state.getSnake(my_num).getHead(); 
        pos[0] = head.getX();
        pos[1]= head.getY();
        return pos;
    }

    ArrayList<SnakePiece> getBody(GameState state)
    {
       Snake s = state.getSnake(my_num);
       ArrayList<SnakePiece> body = (ArrayList<SnakePiece>) s.getBody();
       return body;
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
        vision = new float[26];
        

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

          HeadPiece oppHead  = state.getSnake(oppNum).getHead();
          vision[24] = oppHead.getX();
          vision[25] = oppHead.getY();

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
     
   

    DirType think(GameState state) 
    {  //think about what direction to move


      //exploration vs exploitation
      epsilon = 80 - numGames;                                        //hardcoded value: PLAY AROUND WITH THIS
      Random rand = new Random();
      if(rand.nextInt(200) < epsilon)
      {
          // pick random move
          DirType cur_dir = state.getSnake(my_num).getHead().getDir();
          int r = rand.nextInt(6);
          if( cur_dir == DirType.North || cur_dir == DirType.South ){
              if( r == 0){
            return DirType.East;
              }
              if( r == 1){
            return DirType.West;
              }
              return null;
          }
          if( cur_dir == DirType.East || cur_dir == DirType.West ){
              if( r == 0){
            return DirType.North;
              }
              if( r == 1){
            return DirType.South;
              }
              return null;
          }
      }
      else
      {
        //use brain
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
          
          switch(maxIndex) 
          {
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
      return null;  //might cause a problem
        
      }

   

     public void reset()
     {
         score = 0;
         UnproductiveFrames = 0;
     }

     void remember(GameState state, DirType lastMove, int reward, GameState nextState, boolean isOver)
     {
        MemoryState newMem = new MemoryState(state, lastMove, reward, nextState, isOver);
        memory.add(newMem);
        if(memory.size() > maxMemory)
        {
          memory.pop();
        }
     }

     void train()
     {
        

         //uses agent and game
     }
     //trains on long term memory
     void trainLong()                         //needs work
     {
       ArrayList<MemoryState> miniSample = new ArrayList<MemoryState>();
        if(memory.size() >= batchSize)
        {
          //pulls a random sample from memory 
                     
                     Random rand = new Random();
                     while(miniSample.size() < batchSize)
                     {
                        int randIndex = rand.nextInt(memory.size());
                        for(MemoryState m : memory)
                        {
                          if(--randIndex < 0)
                            miniSample.add(m);
                        }
                       
                     }
        }
        else
          miniSample = new ArrayList<MemoryState>(memory);
//TODO: Makle Trainer.TrainStep(take in a memorystate)
          for(MemoryState m : miniSample)
          {
            trainer.TrainStep(m);
          }
     }

     //trains on short term memory
     public void trainShort(GameState state, DirType lastMove, int reward, GameState nextState, boolean isOver)
     {
           trainer.TrainStep(state, lastMove, reward, nextState, isOver);
            

     }

     public void trainShort(MemoryState m)
     {
        trainer.TrainStep(m);
     }

     public void IncrementGamesPlayed()
     {
         numGames++;
     }



  public void WritetoFile(String fileName) throws IOException
  {
    BufferedWriter file_out;
    file_out = new BufferedWriter(new FileWriter( fileName));
    Matrix[] modelWeights = brain.pull();
    float[][] inputWeights = modelWeights[0].matrix;

      //loops through input layer
      for(int j = 0; j <  hidden_nodes; j++)          
      {
          for(int k = 0; k < input_Nodes + 1; k++)       
          {
            file_out.write(inputWeights[j][k] + ",");
          }
          file_out.newLine();
       }

      //loops through most hidden layers
       for(i = 1; i < hidden_layers; i++)
       {
         float[][] hWeights = modelWeights[i].matrix;

          for(int j = 0; j <  hidden_nodes; j++)          
          {
              for(int k = 0; k < hidden_nodes + 1; k++)       
              {
                file_out.write(hWeights[j][k] + ",");
              }
              file_out.newLine();
          }
       }

        //loops through the final weights values
         float[][] finalWeights = modelWeights[modelWeights.length -1].matrix;

          for(int j = 0; j <  outputNodes; j++)          
          {
              for(int k = 0; k < hidden_nodes + 1; k++)       
              {
                file_out.write(finalWeights[j][k] + ",");
              }
              file_out.newLine();
          }

    file_out.close();
  }

  public void LoadFromFIle(String fileName) throws IOException
 {
  Matrix[] newBrain = brain.pull();   //dummy copy
  float[][] inputWeights = new float[hidden_nodes][input_Nodes +1]; //input  dimensions
  float[][] hiddenWeights = new float[hidden_nodes][hidden_nodes +1]; //hidden layer dimensions
  float[][] outputWeights = new float[outputNodes][hidden_nodes + 1];  //last weight dimensions
  //Path path = Paths.get(fileName);
  URL path = ClassLoader.getSystemResource(fileName);
  File f = new File(path.getFile());
  BufferedReader reader = new BufferedReader(new FileReader(f));
  String st;
  int currLine = 0;

  st = reader.readLine(); //get first line
  
  //does input layer of the model
  for(int i = 0; i < hidden_nodes; i++) //for each row of matrix
  {
    float[] wRows = convertRow(st);                 //get separated float values from file
    for(int j = 0; j < input_Nodes + 1; j++)  
    {
        inputWeights[i][j] = wRows[j];         //loads up the layer's value with 
      
    }

    st = reader.readLine();
  }
  newBrain[0] = new Matrix(inputWeights);

  //next, does hidden layers
  for(int i = 1; i < hidden_layers; i++)
  {
    hiddenWeights = new float[hidden_nodes][hidden_nodes +1];  //reset values

    for(int j = 0; j < hidden_nodes; j++) //for each row of matrix
    {
        float[] wRows = convertRow(st);                 //get separated float values from file
        for(int k = 0; k < hidden_Nodes + 1; k++)  
        {
            hiddenWeights[j][k] = wRows[k];         //loads up the layer's value with 
          
        }

        st = reader.readLine();
    }
    newBrain[i] = new Matrix(hiddenWeights);
  }

  //does final layer of weights
   //does input layer of the model
  for(int i = 0; i < outputNodes; i++) //for each row of matrix
  {
    float[] wRows = convertRow(st);                 //get separated float values from file
    for(int j = 0; j < hidden_Nodes + 1; j++)  
    {
        outputWeights[i][j] = wRows[j];         //loads up the layer's value with 
      
    }

    st = reader.readLine();
  }
  newBrain[0] = new Matrix(outputWeights);

  brain = newBrain;   //finally, set the new Brain to work with
  reader.close();;
    
}

  float[] convertRow(String input)
  {
        String[] weightRow = input.split(",");
      float[] wRow = new float[weightRow.length];
      for(int i = 0; i < weightRow.length ; i++)
      {
        wRow[i] = Float.parseFloat(weightRow[i]);
      }
      return wRow;
  }

//file saving parts: change until we got something that saves
//serialize java array
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