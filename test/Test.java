class Test{
    public static void main(String[] a){
	System.out.println(new BS().b(20));
    }
}

// This class contains an array of integers and
// methods to initialize, print and search the array
// using Binary Search

class BS{
    public int b(int res){
    	int[] number ;
    	number= new int[2*3];
    	number[2] = 4;
    	res = number[0];
    	return number[2];
    }
}
