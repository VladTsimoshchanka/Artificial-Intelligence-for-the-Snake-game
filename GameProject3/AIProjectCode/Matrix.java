package AIProjectCode;

import java.util.Random;

//Note: change all clone calls to duplicate in starter code
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
     
     float relu(float x) {
         return Math.max(0,x);
     }
     
     void mutate(float mutationRate) 
     {
      Random r = new Random();
        for(int i = 0; i < rows; i++) {
           for(int j = 0; j < cols; j++) {
              float rand = random(1f);          //picks a random number, if it's less than the mutation rate, do the following
              if(rand<mutationRate) {
                 matrix[i][j] += (float)r.nextGaussian()/5;          //rand float 0-1 divided by 5 adds it to current weight
                 
                 if(matrix[i][j] > 1) {                  //nakes the weight between +1 and -1
                    matrix[i][j] = 1;
                 }
                 if(matrix[i][j] <-1) {
                   matrix[i][j] = -1;
                 }
              }
           }
        }
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