import java.util.*;
//import java.io.*;

public class Sudoku{
	
	public static Unit [] nums;
	public static int solvedCount;
	
	public static void main(String[]args){
		solvedCount = 0;
		nums = new Unit[81];
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter suduko (0 for blank, seperated with space): ");
		for(int i=0; i<81; i++){
			nums[i] = new Unit(in.nextInt(), i%9, i/9);
		}
		for (int i=0; i<81; i++){
			if(nums[i].solved==true){
				solvedCount++;
				narrowPoss(nums[i].val, nums[i].x, nums[i].y, nums[i].group); 	//narrows down possibilities of other units in the same R, C, or G as each solved unit
			}
		}
		String command = "";
		while (!command.equals("exit")){
			System.out.println("\nSolved: "+solvedCount);
			System.out.print("Enter Command ('h' for help): ");
			command = in.next();
			if (command.equals("print"))
				print();
			else if (command.equals("add"))
				add(in);
			else if (command.equals("possible"))
				showPoss(in);
			else if (command.equals("solveS"))
				solveSingle();
			else if (command.equals("solveG"))
				solveGroup();
			else if (command.equals("solveX"))
				solveX();
			else if (command.equals("solveY"))
				solveY();
			else if (command.equals("recurse") || command.equals("r")) {
				long s1 = System.currentTimeMillis();
				recurse();
				long s2 = System.currentTimeMillis();
				System.out.println("\nRecursive Time: "+(s2-s1)+"ms\n");
			}
			else if (command.equals("solve") || command.equals("s")) {
				long s1 = System.currentTimeMillis();
				solve();
				long s2 = System.currentTimeMillis();
				System.out.println("\nSolve Time: "+(s2-s1)+"ms\n");
			}
			else if (command.equals("unsolved"))
				unsolved();
			else if (command.equals("h")){
				System.out.println("\nprint      (print current board)");
				System.out.println("add        (solve a square)");
				System.out.println("possible   (show possibilities for a square)");
				System.out.println("solveS     (solve all squares with only one possibility)");
				System.out.println("solveG     (solve within groups)");
				System.out.println("solveX     (solve within columns)");
				System.out.println("solveY     (solve within rows)");
				System.out.println("recurse    (solve recursivly)");
				System.out.println("solve      (solve using all logical methods as far as possible, then recursivly");
				System.out.println("unsolved   (list all unsolved squares and their possibilities)");
			}
		}
	}
	
	public static void solve() {
		int startCount;
		int temp;
		do {
			startCount = solvedCount;
			solveSingle();
			solveX();
			if(startCount!=solvedCount)
				continue;
			solveY();
			if(startCount!=solvedCount)
				continue;
			solveGroup();
		} while(startCount!=solvedCount && solvedCount!=81);
//		recurse();
	}	
	
	// Prints current sudoku board into terminal
	public static void print(){
		for (int i=0; i<9; i++){		// i = row #
			if (i==3 || i==6)
				System.out.println("-------+-------+-------");
			System.out.print(" ");
			for (int j=0; j<9; j++){	// j = colomn #
				if (j==3 || j==6)
					System.out.print("| ");
				if (nums[(9*i+j)].val == 0)
					System.out.print(". ");
				else
					System.out.print(nums[(9*i+j)].val+" ");
			}
			System.out.println();
		}
	}
	
	
	// Manually solve a unit
	public static void add(Scanner in){
		System.out.print("Enter the value, how many over (0-8), and how far down (0-8) seperated with spaces: ");
		int v = in.nextInt();
		int x = in.nextInt();
		int y = in.nextInt();
		nums[9*y+x].solve(v);
		narrowPoss(v, x, y, nums[9*y+x].group);
		solvedCount++;
	}
	
	// Removes value os solved unit from possibiliy list of all units in same Row, Column, or Group
	public static void  narrowPoss(int val, int x, int y, int group){
		for (int i=0; i<81; i++){
			if(nums[i].solved == false && 
			  (nums[i].group==group || nums[i].x==x || nums[i].y==y)){
				nums[i].cantBe(val);
			}
		}
	}
	
	// Prints all possibilities for a given unit
	public static void showPoss(Scanner in){
		System.out.print("Enter x (0-8 over) and y (0-8 down) seperated with space: ");
		int x = in.nextInt();
		int y = in.nextInt();
		System.out.print("Possibilities: ");
		for (int i=1; i<=9; i++){
			if(nums[9*y+x].possible.contains(i))
				System.out.print(i+" ");
		}
		System.out.println();
	}
	
	// Solves all unsolved units with only one possibility
	public static void solveSingle(){
		int temp = solvedCount;
		for(int i=0; i<81; i++){
			if(!nums[i].solved && nums[i].possible.size()==1){
				nums[i].val = nums[i].possible.get(0);
				nums[i].solved = true;
				narrowPoss(nums[i].val, nums[i].x, nums[i].y, nums[i].group);
				solvedCount++;
			}
		}
		if(temp!=solvedCount)
			solveSingle();
	}
	
	// Solves units by checking possibilities in Group
	public static void solveGroup(){
		ArrayList<Unit> temp;
		int [] count;
		int preCount = solvedCount;			//solvedCount at start of method
		int tempCount;						//solvedCount at start of each group
		for (int g=1; g<=9; g++){
			tempCount = -1;
			while (tempCount!=solvedCount){
				tempCount = solvedCount;
				temp = new ArrayList<Unit>();
				count = new int[9];
				
				for(int i=0; i<81; i++)						//add unsolved units in group to 'temp'
					if(g==nums[i].group && !nums[i].solved)
						temp.add(nums[i]);
				
				for(int i=0; i<temp.size(); i++)						//go through Units in 'temp'
					for(int j=0; j<temp.get(i).possible.size(); j++)	//go through each 'possible' within temp
						count[temp.get(i).possible.get(j)-1]++;			//count how many of each possible number there are
				
				for(int i=0; i<9; i++){		// i = current "possibility"
					int x; 
					int y;
					if(count[i]==1){
						for(int j=0; j<temp.size(); j++){					//go through each 'temp' Unit
							if(temp.get(j).possible.contains(i+1)){			//find the only Unit that can accept 'i'
								x = temp.get(j).x;
								y = temp.get(j).y;
								nums[9*y+x].solve(i+1);
								narrowPoss(i+1, x, y, nums[9*y+x].group);
								solvedCount++;
							}
						}
					}
				}
			}
		}
		if(preCount!=solvedCount)
			solveGroup();
	}
	
	// Solves by Row
	public static void solveX(){
		ArrayList<Unit> temp;
		int [] count;
		int preCount = solvedCount;			//solvedCount at start of method
		int tempCount;						//solvedCount at start of each group
		for (int g=0; g<9; g++){
			tempCount = -1;
			while (tempCount!=solvedCount){
				tempCount = solvedCount;
				temp = new ArrayList<Unit>();
				count = new int[9];
				
				for(int i=0; i<81; i++)						//add unsolved units in group to 'temp'
					if(g==nums[i].x && !nums[i].solved)
						temp.add(nums[i]);
				
				for(int i=0; i<temp.size(); i++)						//go through Units in 'temp'
					for(int j=0; j<temp.get(i).possible.size(); j++)	//go through each 'possible' within temp
						count[temp.get(i).possible.get(j)-1]++;			//count how many of each possible number there are
			
				for(int i=0; i<9; i++){
					int x; 
					int y;
					if(count[i]==1){
						for(int j=0; j<temp.size(); j++){
							if(temp.get(j).possible.contains(i+1)){
								x = temp.get(j).x;
								y = temp.get(j).y;
								nums[9*y+x].solve(i+1);
								narrowPoss(i+1, x, y, nums[9*y+x].group);
								solvedCount++;
							}
						}
					}
				}
			}
		}
		if(preCount!=solvedCount)
			solveX();
	}
	
	// Solves by Column
	public static void solveY(){
		ArrayList<Unit> temp;
		int [] count;
		int preCount = solvedCount;			//solvedCount at start of method
		int tempCount;						//solvedCount at start of each group
		for (int g=0; g<9; g++){
			tempCount = -1;
			while (tempCount!=solvedCount){
				tempCount = solvedCount;
				temp = new ArrayList<Unit>();
				count = new int[9];
				
				for(int i=0; i<81; i++)						//add unsolved units in group to 'temp'
					if(g==nums[i].y && !nums[i].solved)
						temp.add(nums[i]);
				
				for(int i=0; i<temp.size(); i++)						//go through Units in 'temp'
					for(int j=0; j<temp.get(i).possible.size(); j++)	//go through each 'possible' within temp
						count[temp.get(i).possible.get(j)-1]++;				//count how many of each possible number there are
			
				for(int i=0; i<9; i++){
					int x; 
					int y;
					if(count[i]==1){
						for(int j=0; j<temp.size(); j++){
							if(temp.get(j).possible.contains(i+1)){
								x = temp.get(j).x;
								y = temp.get(j).y;
								nums[9*y+x].solve(i+1);
								narrowPoss(i+1, x, y, nums[9*y+x].group);
								solvedCount++;
							}
						}
					}
				}
			}
		}
		if(preCount!=solvedCount)
			solveY();
	}
	
	// Prints a list of all unsolved Units and their possibilities
	public static void unsolved(){
		for (int i=0; i<81; i++){
			if(!nums[i].solved){
				System.out.println("Unit ("+nums[i].x+", "+nums[i].y+"): "+nums[i].possible.toString());	
			}
		}
	}
	
	public static void recurse() {
		nums = recurse(nums, 0);
		for(int i=0; i<81; i++) {
			if (!nums[i].solved && nums[i].val!=0) {
				nums[i].solve(nums[i].val);
				solvedCount++;
			}
		}
	}
	
	public static Unit[] recurse(Unit [] temp, int i) {
		// Find the first unsolved Unit 

		
		while (i<80 && temp[i].solved)		//skip over all solved units
			i++;
		
		if (i==80 && temp[i].solved)	//end of puzzle - Base case
			return temp;
		
		int next = i+1;
		while (next<80 && temp[next].solved)
			next++;
		for (int j=0; j<temp[i].possible.size(); j++) {		//goes through each possible value
			temp[i].val = temp[i].possible.get(j);
			if(check(temp, i)) {
				if(i==80) 
					return temp;
				temp = recurse(temp, next);
				if (temp[next].val!=0) {
					return temp;
				}
			}
		}
		temp[i].val=0;
		return temp;
	}
	
	public static boolean check(Unit[] temp, int i) {
		Unit u = temp[i];
		for(int j=0; j<81; j++) {
			if(!(j==i) && (temp[j].group==u.group || temp[j].x==u.x || temp[j].y==u.y)) {	// test against all units except for i
				if(u.val==temp[j].val) {
					return false;
				}
			}
		}
		return true;
	}
}
/*

Example 1:
0 0 0 0 0 0 0 0 7 0 0 3 9 0 0 5 8 0 5 0 0 2 0 1 0 9 0 0 0 7 0 0 8 1 0 0 9 6 8 1 0 0 0 0 0 3 1 0 0 5 0 8 2 0 6 0 0 0 4 7 0 5 0 0 0 0 6 1 0 0 0 0 0 0 1 5 9 0 7 0 0 

 8 9 2 | 4 3 5 | 6 1 7 
 1 4 3 | 9 7 6 | 5 8 2 
 5 7 6 | 2 8 1 | 4 9 3 
-------+-------+-------
 2 5 7 | 3 6 8 | 1 4 9 
 9 6 8 | 1 2 4 | 3 7 5 
 3 1 4 | 7 5 9 | 8 2 6 
-------+-------+-------
 6 3 9 | 8 4 7 | 2 5 1 
 7 8 5 | 6 1 2 | 9 3 4 
 4 2 1 | 5 9 3 | 7 6 8 



Example 2:
0 2 0 1 7 8 0 3 0 0 4 0 3 0 2 0 9 0 1 0 0 0 0 0 0 0 6 0 0 8 6 0 3 5 0 0 3 0 0 0 0 0 0 0 4 0 0 6 7 0 9 2 0 0 9 0 0 0 0 0 0 0 2 0 8 0 9 0 1 0 6 0 0 1 0 4 3 6 0 5 0

 6 2 9 | 1 7 8 | 4 3 5 
 8 4 5 | 3 6 2 | 7 9 1 
 1 3 7 | 5 9 4 | 8 2 6 
-------+-------+-------
 2 7 8 | 6 4 3 | 5 1 9 
 3 9 1 | 2 8 5 | 6 7 4 
 4 5 6 | 7 1 9 | 2 8 3 
-------+-------+-------
 9 6 3 | 8 5 7 | 1 4 2 
 5 8 4 | 9 2 1 | 3 6 7 
 7 1 2 | 4 3 6 | 9 5 8 

Hard:
0 0 0 0 3 7 6 0 0 0 0 0 6 0 0 0 9 0 0 0 8 0 0 0 0 0 4 0 9 0 0 0 0 0 0 1 6 0 0 0 0 0 0 0 9 3 0 0 0 0 0 0 4 0 7 0 0 0 0 0 8 0 0 0 1 0 0 0 9 0 0 0 0 0 2 5 4 0 0 0 0 

 9 5 4 | 1 3 7 | 6 8 2 
 2 7 3 | 6 8 4 | 1 9 5 
 1 6 8 | 2 9 5 | 7 3 4 
-------+-------+-------
 4 9 5 | 7 2 8 | 3 6 1 
 6 8 1 | 4 5 3 | 2 7 9 
 3 2 7 | 9 6 1 | 5 4 8 
-------+-------+-------
 7 4 9 | 3 1 2 | 8 5 6 
 5 1 6 | 8 7 9 | 4 2 3 
 8 3 2 | 5 4 6 | 9 1 7 

http://www.sudokuslam.com/hints.html 
0 0 0 2 0 0 0 0 0 0 6 5 0 0 0 9 0 0 0 7 0 0 0 6 0 4 0 0 0 0 0 0 1 0 0 5 7 1 0 0 0 0 0 0 9 0 0 9 0 2 0 0 1 0 0 0 1 0 0 0 7 0 0 0 8 7 3 0 4 0 2 0 0 0 0 0 6 0 0 9 4


*/
