package AIProjectCode;

import java.util.Random;

import ProjectThreeEngine.*;

public class Training 
{
    public NeuralAI[] neurals;
		public NeuralAI bestAI;

		int bestNeuralScore = 0;
		public int gen = 0;
		int sameBest = 0;
		 float bestFitness = 0;
		 float fitnessSum = 0;

         boolean acceleratedMutation = false; //double mutation if true

        public Training(int size)
         {
            neurals = new NeuralAI[1000]; //1000 plays
            for(int i = 0; i < neurals.length; i++)
            {

                neurals[i] = new NeuralAI();
//load up best snake score here

            }
    
            bestAI = neurals[0].clone();
            //don't use relay
         }

         boolean done() //check if all snakes in training set are dead
         {
             for(int i = 0; i < neurals.length; i++)
             {
                 if(!neurals[i].dead)
                 return false;
             }

             if(! bestAI.dead)
             {
                 return false;
             }
             return true;
         }

         public void setBestAI()
         {
             float max = 0;
             int maxIndex = 0;
             for(int i = 0; i < neurals.length; i++)
             {
//maybe unnecessary                 
                 neurals[i].calculateFitness();
                 if(neurals[i].fitness > max)
                 {
                     max = neurals[i].fitness;
                     maxIndex = i;

                 }
             }

             if(max > bestFitness)
             {
                 bestFitness = max;
                 bestAI = neurals[maxIndex].clone(); //could clone for a replay here
                 bestNeuralScore = neurals[maxIndex].score; 
                 sameBest = 0;  //genertions of same best AI is rest    
                acceleratedMutation = false;
                }
             else
             {
                 bestAI = bestAI.clone();
                 sameBest ++;
                 if(sameBest > 2)
                 {
                        acceleratedMutation = true;
                        sameBest = 0;
                 }
             }
         }

         public NeuralAI selectParent()
         {
              //selects a random number in range of the fitnesssum and if a snake falls in that range then select it
            Random rand = new Random();  
            float r = rand.nextFloat() * fitnessSum;
              float summation = 0;
              for(int i = 0; i < neurals.length; i++) 
              {
                 summation += neurals[i].fitness;
                 if(summation > r) 
                 {
                   return neurals[i];
                 }
              }
              return neurals[0];
         }

         public void naturalSelection()
          {
            NeuralAI[] newNeurals = new NeuralAI[neurals.length];
            
            setBestAI();
            calculateFitnessSum();
            
            newNeurals[0] = bestAI.clone();  //add the best snake of the prior generation into the new generation
            for(int i = 1; i < neurals.length; i++) {
               NeuralAI child = selectParent().crossover(selectParent());
               child.mutate(acceleratedMutation);
               newNeurals[i] = child;
            }
            neurals = neurals.clone();

            //we aren't making a graph
            //evolution.add(bestNeuralScore);
            gen+=1;
         }

         void mutate() {
            for(int i = 1; i < neurals.length; i++) 
            {  //start from 1 as to not override the best snake placed in index 0
               neurals[i].mutate(acceleratedMutation); 
            }
        }

        //probably notused
        public void calculateFitness() {  //calculate the fitnesses for each snakeAI
            for(int i = 0; i < neurals.length; i++) {
               neurals[i].calculateFitness(); 
            }
         }

         void calculateFitnessSum() 
         {  //calculate the sum of all the snakeAIs fitnesses
            fitnessSum = 0;
            for(int i = 0; i < neurals.length; i++) {
              fitnessSum += neurals[i].fitness; 
           }
        }


        void fileSelectedOut(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    String path = selection.getAbsolutePath();
    Table modelTable = new Table();
    Snake modelToSave = pop.bestSnake.clone();
    Matrix[] modelWeights = modelToSave.brain.pull();
    float[][] weights = new float[modelWeights.length][];
    for(int i=0; i<weights.length; i++) {
       weights[i] = modelWeights[i].toArray(); 
    }
    for(int i=0; i<weights.length; i++) {
       modelTable.addColumn("L"+i); 
    }
    modelTable.addColumn("Graph");
    int maxLen = weights[0].length;
    for(int i=1; i<weights.length; i++) {
       if(weights[i].length > maxLen) {
          maxLen = weights[i].length; 
       }
    }
    int g = 0;
    for(int i=0; i<maxLen; i++) {
       TableRow newRow = modelTable.addRow();
       for(int j=0; j<weights.length+1; j++) {
           if(j == weights.length) {
             if(g < evolution.size()) {
                newRow.setInt("Graph",evolution.get(g));
                g++;
             }
           } else if(i < weights[j].length) {
              newRow.setFloat("L"+j,weights[j][i]); 
           }
       }
    }
    saveTable(modelTable, path);
    
  }
}




        
}
