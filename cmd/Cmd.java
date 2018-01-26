package cmd;
import java.io.*;
import java.util.*;
import java.util.regex.*;

class Cmd {
	static String[] argArr;
	static LinkedList<String> que = new LinkedList<String>();
	static final int MAX_SIZE = 5;
	
	static File curDir;
	
	/* get current directory */
	static {
		try {
			curDir = new File(System.getProperty("user.dir"));
		} catch (Exception e){
			e.getStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);

		while (true) {
			try {
				String prompt = curDir.getCanonicalPath() + ">> ";
				System.out.print(prompt);
				
				/* get input from user */
				String input = s.nextLine();
				if (input.trim().equals(""))  continue;
				
				save(input);
				
				/* tuning the input */
				argArr = input.trim().split(" +");
				String command = argArr[0].trim().toLowerCase();
				
				if (command.equals(""))  continue;
				
				/* execute the command */
				if (command.equals("q")) {
					System.exit(0);
				} else if (command.equals("history")) {
					history();
				} else if (command.equals("ls")) {
					dir(curDir);
				} else if (command.equals("cat")) {
					cat();
				} else if (command.equals("find")) {
					find();
				} else if (command.equals("findall")) {
					findAll();
				} else if (command.equals("cd")) {
					cd();
				} else if (command.equals("help")) {
					help();
				} else {
					System.out.println(input + " is not a existing command");
					System.out.println("typing 'help' will help you");
				}	
			} catch (Exception e) {
				System.out.println("wrong input");
				e.printStackTrace();
			}
		}
	}
	
	/* save input in que for history */
	public static void save(String input) {
		que.add(input);
		if (que.size() > MAX_SIZE) {
			que.remove();
		}
	}
	
	/* show the history of input commands */
	public static void history() {
		int i = 0;
		for (String in : que) {
			System.out.println(++i + ". " + in);
		}
	}
	
	public static void dir(File directory) {
		String pattern = "";
		
		File[] files = directory.listFiles();
		
		switch(argArr.length) {
		/* show all files and directories */
		case 1:
			printFiles(files, Pattern.compile(".*"));
			break;
		
		/* show specific files and directories */
		case 2:
			pattern = argArr[1].toLowerCase();
				
			/* change the pattern to regex form */
			pattern = regex(pattern);
			
			printFiles(files, Pattern.compile(pattern));
			break;
			
		default:
			System.out.println("USAGE: ls [FILE_PATTERN]");
		}
	}
	
	/* print the file contents */
	public static void cat() throws IOException {
		if (argArr.length != 2) {
			System.out.println("USAGE: vi FILE_NAME");
			return;
		}
		
		String fileName = argArr[1];
		
		File toRead = new File(curDir, fileName);
		
		if (toRead.exists()) {
			if (!toRead.isDirectory()) {
				try {
					FileReader fr = new FileReader(toRead);
					BufferedReader br = new BufferedReader(fr);
					
					String line;
					while ((line = br.readLine()) != null) {
						System.out.println(line);
					}
				} catch (Exception e) {
					e.getStackTrace();
				}	
			} else {
				System.out.println(fileName + " is Directory not a file");
				System.out.println("-----" + fileName + "-----");
				String[] tmp = new String[1];
				argArr = tmp;
				dir(toRead);
			}
		} else {
			System.out.println("file not existing");
		}
		
		return;
	}
	
	/* 
	 * find the keyword in file and print it
	 * return true when find 
	 */
	public static boolean find() {
		boolean ret = false;
		if (argArr.length != 3) {
			System.out.println("USAGE: find KEYWORD FILE_NAME");
			return ret;
		}
		
		File toFind = new File(curDir, argArr[2]);
		String keyword = argArr[1];
		
		if (toFind.exists()) {
			try {
				String line;
				int lineNum = 0;
				
				FileReader fr = new FileReader(toFind);
				BufferedReader br = new BufferedReader(fr);
				
				while ((line = br.readLine()) != null) {
					++lineNum;
					if (line.contains(keyword)) {
						System.out.println(lineNum + ": " + line);
						ret = true;
					}
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		} else {
			System.out.println("file not existing");
		}
		return ret;
	}
	
	/* find the keyword in files that matche the pattern */
	public static void findAll() {
		if (argArr.length != 3) {
			System.out.println("USAGE: find KEYWORD FILE_NAME");
			return;
		}
		
		File[] files = curDir.listFiles();
		
		String pattern = argArr[2].toLowerCase();
		
		/* change the pattern to regex form */
		pattern = regex(pattern);
		
		for (File file : files) {
			if (Pattern.compile(pattern).matcher(file.getName()).matches()) {
				argArr[2] = file.getName();
				
				System.out.println("-----" + argArr[2] + "-----");
				if (!find()) {
					System.out.println("no search result found");
				}
			}
		}
	}
	
	/* move the current directory */
	public static void cd() {
		if (argArr.length == 1) {
			return;
		} else if (argArr.length > 2) {
			System.out.println("USAGE: cd PATH");
			return;
		}
		
		String subDir = argArr[1];
		
		if (subDir.equals(".." )) {
			File tmp = curDir.getParentFile();
			if (tmp != null) {
				curDir = tmp;
			} else {
				System.out.println(subDir + " is not existing");
			}
		} else if (subDir.equals(".")) {
		} else {
			File toMove = new File(curDir, subDir);
			if (toMove.exists()) {
				if (toMove.isDirectory()) {
					try {
						curDir = new File(toMove.getCanonicalPath());
					} catch (IOException e) {
						e.getStackTrace();
					}
				} else {
					System.out.println(subDir + " is a file not a directory");
					argArr[1] = subDir;
					try {
						cat();
					} catch (Exception e) {
						e.getStackTrace();
					}
				}
			} else {
				System.out.println(subDir + " is not existing");
			}
		}
	}
	
	/* show helps */
	public static void help() {
		System.out.println("-----possible commands-----");
		System.out.println("history    - show the lastest 5 commands");
		System.out.println("cd PATH    - move current directory to PATH");
		System.out.println("q          - exit the program");
		System.out.println("help       - show this page");
		System.out.println("cat FILE_NAME       - show file contents");
		System.out.println("ls [FILE_PATTERN]   - show all files and directories in current directory");
		System.out.println("             OPTION : FILE_PATTERN - show f/d that matches the pattern");
		System.out.println("                      * : all charaters including null");
		System.out.println("                      ? : a charater except null");
		System.out.println("find KEYWORD FILE_NAME       - find KEYWORD in FILE_NAME");
		System.out.println("findall KEYWORD FILE_PATTERN - find KEYWORD in files that matches FILE_PATTERN");
		System.out.println("                               FILE_PATTERN rule is same as ls [FILE_PATTERN]");

	}
	
	/* return regex form of the input */
	public static String regex(String pattern) {
		char[] regexChar
			= {'^', '$', '.', '+', '[', ']', '{', '}', '(', ')', '|'};
			
		for (char ch : regexChar) {
			String tmp = "\\" + ch;
			pattern = pattern.replace(ch+"", tmp);
		}
		pattern = pattern.replace("*", ".*").replace("?", ".{1}");

		return pattern;
	}
	
	/* print the file list that matches the pattern */
	public static void printFiles(File[] files, Pattern pattern) {
		int nextLine = 0, fileLength = 0;
		
		for (File file : files) {
			String fileName = file.getName().toLowerCase();
			
			if (pattern.matcher(fileName).matches()) {
				if (file.isDirectory())
					System.out.print("[" + file.getName() + "]\t");
				else
					System.out.print(file.getName() + "\t");
				
				fileLength += file.getName().length();
				if (++nextLine % 4 == 0 || fileLength > 40) {
					System.out.println();
					fileLength = nextLine = 0;
				}
			}
		}

		if (nextLine % 4 != 0)
			System.out.println();
	}
}
