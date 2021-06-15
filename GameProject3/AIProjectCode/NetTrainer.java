package AIProjectCode;

import ProjectThreeEngine.GameState;

import java.util.Arrays;
import java.util.Collections;

import ProjectThreeEngine.*;

public class NetTrainer 
{
    ReinforcementAI player;
    NeuralNet brain;
    float learnRate;
    float gamma;
    float momentumFactor = .0002f;

    public NetTrainer(ReinforcementAI player, NeuralNet brain, float learnRate, float gamma)
    {
        this.player = player;
        this.brain = brain;
        this.learnRate = learnRate;
        this.gamma = gamma;
    }

    void TrainStep(MemoryState m)
    {
        float[] prediction = player.getPredictionValues(m.getState());
        float[] target = prediction.clone();
        float[] newPreds = player.getPredictionValues(m.getNextState());
        float maxAction = getMaxOfArray(newPreds);
        float predictionNew = m.getReward() + (gamma * maxAction);
        
        int maxIndex = 0;
        for(int i = 0; i < newPreds.length; i++)
        {
            if(newPreds[i] == maxAction)
                maxIndex = i;
        }

        if(!m.getIsOver())
        {
           
           
            target[maxIndex] = maxAction;
        }
        else
            target[maxIndex] = m.getReward();

        float[] loss = MSELossFunction(target, prediction);
    
        brain.backPropagate(target, prediction, learnRate, momentumFactor);
   
        //brain.AdamOptimize(learnRate);
       
    }

    void TrainStep(GameState state, DirType lastMove, int reward, GameState nextState, boolean isOver)
    {
        
    

        float[] prediction = player.getPredictionValues(state);
        float[] newPreds = player.getPredictionValues(nextState);
        float maxAction = getMaxOfArray(newPreds);
        
        float predictionNew = reward + (gamma * maxAction);

        int maxIndex = 0;
        for(int i = 0; i < newPreds.length; i++)
        {
            if(newPreds[i] >= maxAction)
                maxIndex = i;
        }

        float[] target = prediction.clone();

        if(!isOver)
        {
            target[maxIndex] = maxAction;
        }
        else
            target[maxIndex] = reward;

        
        float[] loss = MSELossFunction(target, prediction);

        brain.backPropagate(target, prediction, learnRate, momentumFactor);
       // for(Matrix we : brain.weights) 
        //{
          // we.print();             
        //}
        //use error to get the gradientfunction
       // brain.AdamOptimize(learnRate);

    }

    float getMaxOfArray(float[] numArray) 
    {
        float max = -999;
        for (float f : numArray) 
        {
            if(f > max)
            {
                max = f;
            }   
        }
        return max;
  
      }
    
    float[] MSELossFunction(float[] target, float[] prediction)
    {
        float[] mse = new float[target.length];
        for(int i = 0; i < target.length; i++)
        {
            mse[i] = (float)Math.pow((target[i] - prediction[i]), 2);   //I think this is correct
        }
        return mse;
    }
}
