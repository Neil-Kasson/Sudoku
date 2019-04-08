public class Unit{
	
	public boolean solved;
	public int val;
	public int [] possible;
	public int x;	// (0-8) how far over
	public int y;	// (0-8) how far down
	public int group;	//which block the unit is in:
						// 1 | 2 | 3
						//---+---+---
						// 4 | 5 | 6
						//---+---+---
						// 7 | 8 | 9
	
	public Unit(int value, int xCoord, int yCoord){
		x = xCoord;
		y = yCoord;
		if (y<3){
			if (x<3){
				group = 1;
			}else if (x>5){
				group = 3;
			}else{
				group = 2;
			}
		}else if (y>5){
			if (x<3){
				group = 7;
			}else if (x>5){
				group = 9;
			}else{
				group = 8;
			}
		}else{
			if (x<3){
				group = 4;
			}else if (x>5){
				group = 6;
			}else{
				group = 5;
			}
		} 
		val = value;
		possible = new int[9];
		if (val==0){
			solved = false;
			for (int i=0; i<9; i++){
				possible[i] = 1;
			}
		}else{
			solved = true;
		}
	}
	
	public void cantBe(int x){
		possible[x-1]=0;
	}
	
	public void solve(int x){
		val = x;
		for (int i=0; i<9; i++){
			possible[i] = 0;
		}
		solved = true;
	}	
}
