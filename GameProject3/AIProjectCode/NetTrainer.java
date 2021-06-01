package AIProjectCode;

import ProjectThreeEngine.GameState;

public class NetTrainer 
{
    NeuralNet brain;
    float learnRate;
    float gamma;
    public NetTrainer(NeuralNet brain, float learnRate, float gamma)
    {
        this.brain = brain;
        this.learnRate = learnRate;
        this.gamma = gamma;
    }

    void TrainStep(MemoryState m)
    {
        float[] prediction = brain.getPredictionValues(m.getState());
        float[] target = prediction.clone();
        float[] newPreds = brain.getPredictionValues(nextState);
        float maxAction = getMaxOfArray(newPreds);
        float predictionNew = reward + (gamma * maxAction);
        
        int maxIndex = -1;
        for(i = 0; I < maxPreds.length; i++)
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
       
    }

    void TrainStep(GameState state, DirType lastMove, int reward, GameState nextState, boolean isOver)
    {
        float[] prediction = brain.getPredictionValues(state);
        float[] newPreds = brain.getPredictionValues(nextState);
        float maxAction = getMaxOfArray(newPreds);
        
        float predictionNew = reward + (gamma * maxAction));

        int maxIndex = -1;
        for(i = 0; I < maxPreds.length; i++)
        {
            if(newPreds[i] == maxAction)
                maxIndex = i;
        }

        float[] target = prediction.clone();

        if(!m.getIsOver())
        {
            target[maxIndex] = maxAction;
        }
        else
            target[maxIndex] = reward;

        float[] loss = MSELossFunction(target, prediction);

        //backPropagation of brain
        brain.AdamOptimize(learnRate);

    }

    float getMaxOfArray(float[] numArray) 
    {
        float max = Collections.max(Arrays.asList(numArray));
  
      }
    
    float[] MSELossFunction(float[] target, float[] prediction)
    {
        float[] mse = new float[target.length];
        for(int i = 0; i < target.length; i++)
        {
            mse[i] = Math.pow((target[i] - prediction[i]), 2);   //I think this is correct
        }
        return mse;
    }
}
