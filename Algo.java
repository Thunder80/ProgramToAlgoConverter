/*
This is a simple, not so flexible program, to convert a program into an its algorithm. You need to edit the ouput of this program but
this does your 95% job.

How to use this:-
Step 1: Create a input.txt file in the file in the folder where this program is located
Step 2: Paste the C code in the input.txt file, you just have to paste the funtion of whose algorithm you want
		ONLY ONE FUNCTION AT A TIME SHOULD BE PASTED IN THE FILE TO GET THE DESIRED RESULTS
Step 3: Run this program
Step 4: The output should be printed in the console.

Assumtions:
1. Your program is syntactically correct
2. You have used the standard spacings
3. This program cannot handle switch case or strcutures algorithm so these should not be included in the code

*/
import java.io.*;
import java.util.*;
import java.util.regex.*;

class algo{
	public static void main(String[] args) throws Exception{
		//taking the input source code from user
		FileReader f = new FileReader("input.txt");
		BufferedReader br = new BufferedReader(f);
		/*
		loopCounter variable keeps track of the current block the system is currently in(that is
		whether it is in a for block, if block etc). Currently existing block: FOR, IF, IFELSE, ELSE
		DO, WHILE.
		*/
		Stack<String> loopCounter = new Stack<String>();
		/*
		loopBacktracking variable keeps track of the starting position of a block. The hashmap's key is 
		an integer which represents the current depth of the bracket(nth bracket that the system is 
		currently in) and the value of the hashmap stores the 
		*/
		HashMap<Integer, BracketAC> loopBacktracking = new HashMap<>();
		/*
		loopFronttracking variable keeps track of the ending postion of a certain block with respect to its 
		bracket depth.
		*/
		HashMap<Integer, Integer> loopFronttracking = new HashMap<>();
		//brackets variable keep tracks of the depth of the bracket(number of opening brackets)
		int brackets = 0;
		//rawInput variable stores the input from the file as a string
		//input variable stores the rawInput after it has been formatted
		String rawInput = "", input;
		String output = "";
		String line;
		while((line=br.readLine()) != null)
		{
			rawInput += line.trim() + '\n';
		}
		input = functionFormatter(rawInput);
		StringTokenizer st = new StringTokenizer(input, "\n");
		int steps = 1;
		while(st.hasMoreTokens())
		{
			String s = st.nextToken();
			StringTokenizer currLine = new StringTokenizer(s, " (");
			String firstToken = currLine.nextToken();
			if(s.charAt(s.length()-1) == ';')
			{
				if(firstToken.equals("scanf"))
				{
					int ampPos = s.indexOf("&");
					while(ampPos != -1)
					{	
						output += "Step " + steps++ + ": " + "Taking input in " + s.substring(ampPos+1, ampPos+2) + '\n';
						ampPos = s.indexOf("&", ampPos+2);
					}
				}
				else if(s.indexOf("while") != -1)
				{
					output += "Step " + steps++ + ": " + "if (" + s.substring(s.indexOf('(')+1, s.indexOf(')')) + ") then go to step " + loopBacktracking.get(brackets-1).getAddress() + " else go to step " + steps + '\n';
					loopBacktracking.remove(brackets-1);
				}
				else if(firstToken.equals("break;"))
				{
					output += "Step " + steps++ + ": Go to step /*STEP NUMBER*/" + '\n';
				}
				else if(firstToken.equals("printf") || firstToken.equals("puts"))
				{
					if(s.indexOf(',') != -1)
						output += "Step " + steps++ + ": Printing " + s.substring(s.indexOf(',')+1, s.indexOf(')')) + '\n';
					else
						output += "Step " + steps++ + ": Printing " + s.substring(s.indexOf('(')+2, s.indexOf(')')-1) + '\n';
				}
				else if(!firstToken.equals("int") && !firstToken.equals("float") && !firstToken.equals("char"))
					output += "Step " + steps++ + ": " + s.substring(0, s.length()-1) + '\n';
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
						output += "Step " + steps++ + ": " + initializations.substring(0, initializations.length()-2) + '\n';
				}
				if(s.indexOf('{') != -1)
					brackets++;
				if(s.indexOf('}') != -1)
				{	
					brackets--;
					if(loopCounter.size() > 0)
						loopCounter.pop();
				}

			}

			else 
			{
				if(firstToken.equals("for"))
				{
					String initialCondition = s.substring(s.indexOf('(')+1, s.indexOf(';'));
					String terminatingCondition = s.substring(s.indexOf(';')+1, s.indexOf(';', s.indexOf(';')+1));
					String increment = s.substring(s.indexOf(';', s.indexOf(';')+1)+1, s.indexOf(')'));
					output += "Step " + steps++ + ": " + initialCondition + '\n';
					output += "Step " + steps++ + ": " + "if (" + terminatingCondition + ") then go to step " + steps + " else go to step /*STEP NUMBER*/" + '\n';
					loopBacktracking.put(brackets, new BracketAC(steps-1, increment));
					loopCounter.push("FOR");
				}
				else if(firstToken.equals("if"))
				{
					String condition = s.substring(s.indexOf('(')+1, s.indexOf(')'));
					output += "Step " + steps++ + ": if (" + condition + ") then go to step " + steps + " else go to step /*STEP NUMBER*/" + '\n';
					loopCounter.push("IF");
					loopBacktracking.put(brackets, new BracketAC(steps-1, ""));
				}
				else if((firstToken.equals("else") && currLine.hasMoreTokens() && (currLine.nextToken()).equals("if")))
				{
					String condition = s.substring(s.indexOf('(')+1, s.indexOf(')'));
					output += "Step " + steps++ + ": if (" + condition + ") then go to step " + steps + " else go to step /*STEP NUMBER*/" + '\n';
					loopCounter.push("IFELSE");
					loopBacktracking.put(brackets, new BracketAC(steps-1, ""));
				}
				else if(firstToken.equals("else"))
				{
					loopCounter.push("ELSE");
				}
				else if(firstToken.equals("while"))
				{
					String condition = s.substring(s.indexOf('(')+1, s.indexOf(')'));
					output += "Step " + steps++ + ": if (" + condition + ") then go to step " + steps + " else go to step /*STEP NUMBER*/" + '\n';
					loopCounter.push("WHILE");
					loopBacktracking.put(brackets, new BracketAC(steps-1, ""));
				}
				else if(firstToken.equals("do"))
				{
					loopCounter.push("DO");
					loopBacktracking.put(brackets, new BracketAC(steps, ""));
				}
				else if(s.indexOf('{') != -1)
					brackets++;
				else if(s.indexOf('}') != -1)
				{
					brackets--;
					if(loopCounter.size() > 0)
					{	
						if(loopCounter.peek().equals("FOR"))
						{
							output += "Step " + steps++ + ": " + loopBacktracking.get(brackets).getContent() + ", go to step " + loopBacktracking.get(brackets).getAddress()+ '\n';
							loopFronttracking.put(loopBacktracking.get(brackets).getAddress(), steps);
							loopBacktracking.remove(brackets);
						}
						if(loopCounter.peek().equals("WHILE"))
						{
							loopFronttracking.put(loopBacktracking.get(brackets).getAddress(), steps);
							loopBacktracking.remove(brackets);
						}
						if(loopCounter.peek().equals("DO"))
							loopBacktracking.remove(brackets);
						if(loopCounter.peek().equals("IF") || loopCounter.peek().equals("IFELSE"))
						{
							loopFronttracking.put(loopBacktracking.get(brackets).getAddress(), steps);
							loopBacktracking.remove(brackets);
						}
					}
					if(loopCounter.size() > 0)
						loopCounter.pop();
				}
			}
		}
		output += "Step " + steps + ": End";
		String lines[] = new String[steps];
		int k = 0;
		StringTokenizer outputTokenizer = new StringTokenizer(output, "\n");
		while(outputTokenizer.hasMoreTokens())
		{
			lines[k++] = outputTokenizer.nextToken();
		}
		for(Map.Entry<Integer, Integer> e: loopFronttracking.entrySet())
		{
			lines[e.getKey()-1] = lines[e.getKey()-1].replace(" else go to step /*STEP NUMBER*/", " else go to step " + e.getValue());
		}
		output = "";
		for(int i = 0;i < steps;i++)
			output += lines[i] + '\n';
		/*output = output.replace("=", " <- ");
		output = output.replace(" <-  <- ", "==");
		output = output.replace("&&", " AND ");
		output = output.replace("||", " OR ");
		output = output.replace("%", " MOD ");
		output = output.replace("!=", " NOTEQUALTO ");*/
		System.out.println(output);
	}


	static String functionFormatter(String s)
	{
		String output1 = "";
		s = s.replaceAll("\\/\\*.*\\*\\/", "");
		/*Pattern p = Pattern.compile("(for\\s*\\()|(if\\s*\\()|(while\\s*\\()|else");
		Matcher m = p.matcher(s);
		StringBuilder stemp = new StringBuilder(s);
		while(m.find())
		{
			int k = s.indexOf(')', m.end()+1);
			boolean found = false;
			while(!found)
			{
				if(s.charAt(k) != ' ')
				{
					stemp.insert(k+1, '{');
					stemp.insert(s.indexOf(';', k+2)+1, '}');
					found = true;
				}
				if(s.charAt(k) == '{')
					found = true;
				k++;
			}
		}
		s = stemp.toString();
		*/
		for(int i = 0;i < s.length();i++)
		{
			char c = s.charAt(i);
			if(c == ';')
			{
				output1 += c;
				output1 += '\n';
			}
			else if(c == '{' || c == '}')
			{
				output1 += '\n';
				output1 += c;
				output1 += '\n';
			}
			else
				output1 += c;
		}

		StringTokenizer brOutput1 = new StringTokenizer(output1, "\n");
		String l = "", output2 = "";
		while(brOutput1.hasMoreTokens())
		{
			l = brOutput1.nextToken();
			l = l.replaceAll("^\\s+",""); 
			if(l.indexOf("//") != -1)
				l = l.substring(0, l.indexOf("//"));
			if(indexOfRegex(Pattern.compile("for\\s*\\("), l) != -1)
			{
				output2 += l;
				l = brOutput1.nextToken();
				l = l.replaceAll("^\\s+",""); 
				if(l.indexOf("//") != -1)
					l = l.substring(0, l.indexOf("//"));
				output2 += l;
				l = brOutput1.nextToken();
				l = l.replaceAll("^\\s+",""); 
				if(l.indexOf("//") != -1)
					l = l.substring(0, l.indexOf("//"));
				output2 += l+'\n';
			}
			else
				output2 += l+'\n';
		}

		return output2;
	}

	static int indexOfRegex(Pattern p, String toFind)
	{
		Matcher m = p.matcher(toFind);
		return m.find() ? m.start():-1;
	}
}


class BracketAC{
	private int address;
	private String content;
	BracketAC(int address, String content)
	{
		this.address = address;
		this.content = content;
	}

	String getContent()
	{
		return content;
	}

	int getAddress()
	{
		return address;
	}
}
