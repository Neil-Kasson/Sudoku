import java.util.*;
import java.io.*;

public class Sudoku{
	
	public static Unit [] nums;
	
	public static void main(String[]args){
		nums = new Unit[81];
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter suduko (0 for blank, seperated with space): ");
		for(int i=0; i<81; i++){
			nums[i] = new Unit(in.nextInt(), i%9, i/9);
		}
		for (int i=0; i<81; i++){
			if(nums[i].solved==true)
				narrowPoss(nums[i].val, nums[i].x, nums[i].y, nums[i].group);
		}
		String command = "";
		while (!command.equals("exit")){
			System.out.print("Enter Command: ");
			command = in.next();
			if (command.equals("print"))
				print();
			else if (command.equals("add"))
				add(in);
			else if (command.equals("possible"))
				showPoss(in);
			else if (command.equals("group"))
				gr(in);
		}
	}
	
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
	
	public static void add(Scanner in){
		System.out.print("Enter the value, how many over (0-8), and how far down (0-8) seperated with spaces: ");
		int v = in.nextInt();
		int x = in.nextInt();
		int y = in.nextInt();
		nums[9*y+x].solve(v);
		narrowPoss(v, x, y, nums[9*y+x].group);
	}
	
	public static void  narrowPoss(int val, int x, int y, int group){
		for (int i=0; i<81; i++){
			if(nums[i].solved == false && 
			  (nums[i].group==group || nums[i].x==x || nums[i].y==y)){
				nums[i].cantBe(val);
			}
		}
	}
	
	public static void showPoss(Scanner in){
		System.out.print("Enter x (0-8 over) and y (0-8 down) seperated with space: ");
		int x = in.nextInt();
		int y = in.nextInt();
		System.out.print("Possibilities: ");
//		for (int i=0; i<9; i++){
//			if(nums[9*y+x].possible[i] == 1)
//				System.out.print((i+1)+" ");
		}
		System.out.println();
	}
	
	public static void gr(Scanner in){
		System.out.print("Enter x (0-8 over) and y (0-8 down) seperated with space: ");
		int x = in.nextInt();
		int y = in.nextInt();
		System.out.println("the unit is in group number "+nums[9*y+x].group);
	}
}
/*
0 0 0 0 0 0 0 0 7 0 0 3 9 0 0 5 8 0 5 0 0 2 0 1 0 9 0 0 0 7 0 0 8 1 0 0 9 6 8 1 0 0 0 0 0 3 1 0 0 5 0 8 2 0 6 0 0 0 4 7 0 5 0 0 0 0 6 1 0 0 0 0 0 0 1 5 9 0 7 0 0 

0 2 0 1 7 8 0 3 0 0 4 0 3 0 2 0 9 0 1 0 0 0 0 0 0 0 6 0 0 8 6 0 3 5 0 0 3 0 0 0 0 0 0 0 4 0 0 6 7 0 9 2 0 0 9 0 0 0 0 0 0 0 2 0 8 0 9 0 1 0 6 0 0 1 0 4 3 6 0 5 0
*/
