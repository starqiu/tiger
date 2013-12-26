class Sum { 
	public static void main(String[] a) {
		try {
			System.out.println(new Doit().doit(101));
		} catch  {
			System.out.println(33);
		}
    }
}

class Doit {
    public int doit(int n) {
        int sum;
        int i;
        
        i = 0;
        sum = 0;
        try {
        	while (i<n){
            	sum = sum + i;
            	i = i+1;
            	
            	System.out.println(1);
            	try {
            		System.out.println(6);
					throw(3);
				} catch  {
					System.out.println(7);
				}
            	sum = this.f();
            }
		} catch(3) {
			System.out.println(2);
		}
		try {
    		System.out.println(8);
			throw;
		} catch  {
			System.out.println(9);
		}
        return sum;
    }
    public int f(){
    	int i;
    	System.out.println(5);
    	i=this.testjmp();
    	return 1;
    }
    public int testjmp(){
    	System.out.println(3);
    	throw;
    	return 1;
    }
}
