package AIProjectCode;

import java.util.Random;

//Note: change all clone calls to duplicate in starter code
//Adam Optimization adapted from : https://github.com/sagarvegad/Adam-optimizer/blob/master/Adam.py
//Not really sure if I did this right
class Matrix 
{
  
    int rows, cols;
    float[][] matrix;
    
     Matrix(int r, int c) 
     {
       rows = r;
       cols = c;
       matrix = new float[rows][cols];
     }
     
     Matrix(float[][] m) 
     {
        matrix = m;
        rows = matrix.length;
        cols = matrix[0].length;
     }
     
     void output() 
     {
        for(int i = 0; i < rows; i++) 
        {
           for(int j = 0; j < cols; j++) 
           {
              System.out.print(matrix[i][j] + " \n"); 
           }
           System.out.println("\n");
        }
        System.out.println("\n");
     }
     
     Matrix dot(Matrix n) 
     {
       Matrix result = new Matrix(rows, n.cols);
       
       if(cols == n.rows) {
          for(int i = 0; i < rows; i++) {
             for(int j = 0; j < n.cols; j++) {
                float sum = 0;
                for(int k = 0; k < cols; k++) {
                   sum += matrix[i][k]*n.matrix[k][j];
                }  
                result.matrix[i][j] = sum;
             }
          }
       }
       return result;
     }
     
     void randomize() 
     {
       
        for(int i = 0; i < rows; i++) 
        {
           for(int j = 0; j < cols; j++) 
           {
              matrix[i][j] = random(-1f,1f); 
           }
        }
     }
     
     Matrix singleColumnMatrixFromArray(float[] arr) 
     {
        Matrix n = new Matrix(arr.length, 1);      
        for(int i = 0; i < arr.length; i++) 
        {
           n.matrix[i][0] = arr[i]; 
        }
        return n;
     }
     
     float[] toArray() 
     {
        float[] arr = new float[rows*cols];
        for(int i = 0; i < rows; i++) 
        {
           for(int j = 0; j < cols; j++) 
           {
              arr[j+i*cols] = matrix[i][j]; 
           }
        }
        return arr;
     }
     
     Matrix addBias() {
        Matrix n = new Matrix(rows+1, 1);    //adds a row of 1's to the current single column matrix
        for(int i = 0; i < rows; i++) {
           n.matrix[i][0] = matrix[i][0]; 
        }
        n.matrix[rows][0] = 1;
        return n;
     }
     
     Matrix activate() {
        Matrix n = new Matrix(rows, cols);         //makes a new copy matrix
        for(int i = 0; i < rows; i++) {            
           for(int j = 0; j < cols; j++) {
              n.matrix[i][j] = relu(matrix[i][j]);    //for each layer, makes any negative weights 0, keeps all positive weights
           }
        }
        return n;
     }
     
     //sigmoid function
     float relu(float x) {
         //return Math.max(0,x);
         return (float)Math.tanh(x);
     }
     
     void mutate(float mutationRate) 
     {
      Random r = new Random();
        for(int i = 0; i < rows; i++) 
        {
           for(int j = 0; j < cols; j++) 
           {
              float rand = random(1f);          //picks a random number, if it's less than the mutation rate, do the following
              if(rand < mutationRate) 
              {
                 matrix[i][j] += (float)r.nextGaussian()/5;          //rand float 0-1 divided by 5 adds it to current weight
                 
                 if(matrix[i][j] > 1) 
                 {                  //nakes the weight between +1 and -1
                    matrix[i][j] = 1;
                 }
                 if(matrix[i][j] <-1) 
                 {
                   matrix[i][j] = -1;
                 }
              }
           }
        }
     }

     void AdamOptimize(float learnRate)         //source: https://github.com/sagarvegad/Adam-optimizer/blob/master/Adam.py
     {
        float beta1 = .9f;
        float beta2 = .999f;
        float movingAvg = 0;
        float sqMovingAvg = 0;
        float epsilon = (float)Math.pow(1, -8);                    //not sure if needs to be a parameter or not

         for(int i = 0; i < rows; i++) 
         {
            for(int j = 0; j < cols; j++) 
            {
               int t = 0;
                  while(true)
                  {
                     float oldVal = matrix[i][j];
                     t+= 1;
                     float gradVal = AdamGradFunc(oldVal);                          //computes the gradient of the stochastic function
                     movingAvg = beta1*movingAvg + (1-beta1)*gradVal;      //updates the moving averages of the gradient
                     sqMovingAvg = beta2*sqMovingAvg + (1-beta2)*(gradVal*gradVal);	            //updates the moving averages of the squared gradient
                     float movingBiasCorrectedEstimate = (float)(movingAvg/(1-Math.pow((double)beta1, (double)t)));		//calculates the bias-corrected estimates
                     float sqMovingBiasCorrectedEstimate = (float)(sqMovingAvg/(1-Math.pow((double)beta2,(double)t)));		//calculates the bias-corrected estimates
                     matrix[i][j] = (float)(matrix[i][j] - (learnRate*movingBiasCorrectedEstimate) /(Math.sqrt(sqMovingBiasCorrectedEstimate + epsilon)));    //updates the parameters
                     if(matrix[i][j] == oldVal)		                                                         //checks if it is converged or not
                           break;
                  }
                  
                  ////////////////UNSURE IF NEEDS THE BOUNDING OR NOT, PROBABLY DOES


                  //ensures bounds of values of weights
                  if(matrix[i][j] > 1) 
                  {                  //nakes the weight between +1 and -1
                     matrix[i][j] = 1;
                  }
                  if(matrix[i][j] <-1) 
                  {
                  matrix[i][j] = -1;
                  }
               
            }
         }
     }
     
     float AdamFunc(float x)
     {
      return (x*x) - (4*x) + 4;
     }

     float AdamGradFunc(float x)
     {
        return 2*x - 4;
     }


     Matrix crossover(Matrix partner) 
     {
        Random rand = new Random();
        Matrix child = new Matrix(rows, cols);           //makes a child matrix
        int randC = rand.nextInt(cols);                  //picks a random column and row threshold
        int randR = rand.nextInt(rows);
        
        for(int i = 0; i < rows; i++) {
           for(int j = 0;  j < cols; j++) {
              if((i  < randR) || (i == randR && j <= randC)) {       //if row is less than random row OR element is in random row AND less than random column
                 child.matrix[i][j] = matrix[i][j];                  //copy from source matrix
              } else {
                child.matrix[i][j] = partner.matrix[i][j];           //otherwise, copy from provided partner matrix
              }
           }
        }
        return child;
     }
     
     Matrix Clone()       
     {
        Matrix clone = new Matrix(rows, cols);
        for(int i = 0; i < rows; i++) {
           for(int j = 0; j < cols; j++) {
              clone.matrix[i][j] = matrix[i][j]; 
           }
        }
        return clone;
     }

      float random(Float min, Float max)
     {
         Random r = new Random();
         float rand = min + r.nextFloat() * (max - min);
         return rand;
     }

     float random(Float max)
     {
      Random r = new Random();
      float rand = 0 + r.nextFloat() * (max - 0);
      return rand;
     }
  }