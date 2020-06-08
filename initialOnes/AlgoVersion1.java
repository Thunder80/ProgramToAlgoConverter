
  
/*
This is a simple, not so flexible program, to convert a program into an its algorithm. You need to edit the ouput of this program but
this does your 90% job.
How to use this:-
Step 1: Create a input.txt file in the file in the folder where this program is located
Step 2: Paste the C code in the input.txt file, you just have to paste the funtion of whose algorithm you want
		ONLY ONE FUNCTION AT A TIME SHOULD BE PASTED IN THE FILE TO GET THE DESIRED RESULTS
Step 3: Run this program
Step 4: The output should be printed in the console.
Assumtions:
1. Your program is syntactically correct
2. Your program does not contain any unnecessary curly brackets
3. You have used the standard spacings
4. The curly brackets for for-loop, while-loop, if, else, do-while-loop should start on a new line.
5. This program cannot handle switch case or strcutures algorithm so these should not be included in the code
*/
import java.io.*;
import java.util.*;

class algo{
	public static void main(String[] args) throws Exception{
		FileReader f = new FileReader("input.txt");
		BufferedReader br = new BufferedReader(f);
		Stack<String> whileLoopCounter = new Stack<String>();
		HashMap<Integer, String> map = new HashMap<>();
		int brackets = 0;
		String input = "";
		String line;
		while((line=br.readLine()) != null)
		{
			input += line.trim() + "\n";
		}

		StringTokenizer st = new StringTokenizer(input, "\n");
		int steps = 1;
		while(st.hasMoreTokens())
		{
			String s = st.nextToken();
			StringTokenizer currLine = new StringTokenizer(s, " (");
			String firstToken = currLine.nextToken();
			if(s.charAt(s.length()-1) == ';')
			{
				if(s.indexOf("while") != -1)
				{
					System.out.println("Step " + steps++ + ": " + "if (" + s.substring(s.indexOf('(')+1, s.indexOf(')')) + ") then go to step /*STEP NUMBER*/ else go to step /*STEP NUMBER*/");
				}
				else if(firstToken.equals("break;"))
				{
					System.out.println("Step " + steps++ + ": Go to step /*STEP NUMBER*/");
				}
				else if(firstToken.equals("printf") || firstToken.equals("puts"))
				{
					if(s.indexOf(',') != -1)
						System.out.println("Step " + steps++ + ": Printing " + s.substring(s.indexOf(',')+1, s.indexOf(')')));
					else
						System.out.println("Step " + steps++ + ": Printing " + s.substring(s.indexOf('(')+2, s.indexOf(')')-1));
				}
				else if(!firstToken.equals("int") && !firstToken.equals("float") && !firstToken.equals("char"))
					System.out.println("Step " + steps++ + ": " + s.substring(0, s.length()-1));
				else
				{
					String initializations = "";
					StringTokenizer currLineCopy = new StringTokenizer(s.substring(s.indexOf(' ')), ",");
					while(currLineCopy.hasMoreTokens())
					{
						String variable = currLineCopy.nextToken();

						if(variable.indexOf('=') != -1)
							initializations += variable + ", ";
					}
					if(initializations.length() > 0)
						System.out.println("Step " + steps++ + ": " + initializations.substring(0, initializations.length()-2));
				}

			}

			else 
			{
				if(firstToken.equals("for"))
				{
					String initialCondition = s.substring(s.indexOf('(')+1, s.indexOf(';'));
					String terminatingCondition = s.substring(s.indexOf(';')+1, s.indexOf(';', s.indexOf(';')+1));
					String increment = s.substring(s.indexOf(';', s.indexOf(';')+1)+1, s.indexOf(')'));
					System.out.println("Step " + steps++ + ": " + initialCondition);
					System.out.println("Step " + steps++ + ": " + "if (" + terminatingCondition + ") then go to step " + steps + " else go to step /*STEP NUMBER*/");
					map.put(brackets, increment);
					whileLoopCounter.push("FOR");
				}
				else if(firstToken.equals("if") || (firstToken.equals("else") && currLine.hasMoreTokens() && (currLine.nextToken()).equals("if")))
				{
					String condition = s.substring(s.indexOf('(')+1, s.indexOf(')'));
					System.out.println("Step " + steps++ + ": if (" + condition + ") then go to step " + steps + "/*else go to step /*STEP NUMBER*/*/");
					whileLoopCounter.push("IF");
				}
				else if(firstToken.equals("while"))
				{
					String condition = s.substring(s.indexOf('(')+1, s.indexOf(')'));
					System.out.println("Step " + steps++ + ": if (" + condition + ") then go to step " + steps + " else go to step /*STEP NUMBER*/");
					whileLoopCounter.push("WHILE");
				}
				else if(firstToken.equals("do"))
				{
					whileLoopCounter.push("DO");
				}
				else if(firstToken.equals("{"))
					brackets++;
				else if(firstToken.equals("}"))
				{
					brackets--;
					if(whileLoopCounter.size() > 0 && (whileLoopCounter.peek()).equals("FOR"))
					{	
						System.out.println("Step " + steps++ + ": " + map.get(brackets) + ", go to step /*STEP NUMBER*/");
						map.remove(brackets);
					}
					if(whileLoopCounter.size() > 0)
					whileLoopCounter.pop();
				}
			}
		}
		System.out.println("Step " + steps++ + ": End");
		
	}
}

