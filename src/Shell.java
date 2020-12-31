import java.util.*;
import java.io.*;

public class Shell {
	String cwd;
	File curDir;
	
	public Shell() {
		cwd = ".";
		curDir = new File(cwd);
		cwd = curDir.getAbsolutePath();
		cwd = cwd.substring(0, cwd.length()-2);
		curDir = new File(cwd);
	}
	boolean fileNameCountCheck(String[] args, int cnt) {
		if(args.length != cnt) {
			System.out.println(args[0] + ": " + (cnt-1) + "개의 파일 이름을 지정해야 합니다.");
			return true;
		}
		return false;
	}
	boolean fileNotFoundCheck(File f) {
		if(!f.exists()) {
			System.out.println(f.getName() + ": 파일 또는 디렉터리가 존재하지 않습니다.");
			return true;
		}
		return false;
	}
	boolean fileExistCheck(File f) {
		if(f.exists()) {
			System.out.println(f.getName() + ": 파일 또는 디렉터리가 이미 존재합니다.");
			return true;
		}
		return false;
	}
	
	public void cat(String[] args) throws IOException {
		if(fileNameCountCheck(args, 2)) return;
		File src = new File(cwd, args[1]);
		if(fileNotFoundCheck(src)) return;
		FileInputStream fin = new FileInputStream(src);
		byte[] buf = new byte[1024*8];
		for(int n; (n=fin.read(buf)) > 0; )
			System.out.write(buf, 0, n);
		fin.close();
	}
	
	public void cd(String[] args) {
		if(fileNameCountCheck(args, 2)) return;
		String wd;
		if(args[1].equals(".."))
			wd = curDir.getParent();
		else if(args[1].charAt(0)=='\\' || args[1].charAt(1) == ':')
			wd = args[1];
		else
			wd = cwd + "\\" + args[1];
		File dir = new File(wd);
		if(fileNotFoundCheck(dir)) return;
		cwd = wd;
		curDir = dir;
	}
	
	public void cp(String[] args) throws IOException {
		if(fileNameCountCheck(args, 3)) return;
		File src = new File(cwd, args[1]);
		if(fileNotFoundCheck(src)) return;
		File dst = new File(cwd, args[2]);
		if(fileExistCheck(dst))	return;
		FileInputStream fin = new FileInputStream(src);
		FileOutputStream fout = new FileOutputStream(dst);
		BufferedInputStream bin = new BufferedInputStream(fin);
		BufferedOutputStream bout = new BufferedOutputStream(fout);
		for(int c; (c = bin.read()) != -1; )
			bout.write(c);
		bin.close();
		bout.close();
	}
	
	public void ls(String[] args) {
		if(fileNameCountCheck(args, 1)) return;
		System.out.println(curDir.getPath()+" 디렉터리");
		
		File[] subFiles = curDir.listFiles();
		for(int i = 0; i < subFiles.length; i++) {
			File f = subFiles[i];
			long t = f.lastModified();
			System.out.print(f.getName());
			System.out.print(f.isFile()?"\t파일":"\t디렉터리");
			System.out.print("\t파일 크기: " + f.length());
			System.out.printf("\t수정한 시간: %tb %td %ta %tT\n", t, t, t, t);
		}
	}
	
	public void mkdir(String[] args) {
		if(fileNameCountCheck(args, 2)) return;
		File src = new File(cwd, args[1]);
		if(fileExistCheck(src)) return;
		src.mkdir();
	}
	
	public void mv(String[] args) {
		if(fileNameCountCheck(args, 3)) return;
		File src = new File(cwd, args[1]);
		if(fileNotFoundCheck(src)) return;
		File dst = new File(cwd, args[2]);
		if(fileExistCheck(dst)) return;
		src.renameTo(dst);
	}
	
	public void pwd(String[] args) {
		if(fileNameCountCheck(args, 1)) return;
		System.out.println(curDir.getPath());
	}
	
	public void rm(String[] args) {
		if(fileNameCountCheck(args, 2))	return;
		File src = new File(cwd, args[1]);
		if(fileNotFoundCheck(src)) return;
		if(!src.delete())
			System.out.println("에러: 정상적으로 삭제되지 않았습니다. 디렉터리의 경우 빈 디렉터리여야 합니다.");
	}
	
	public void run() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("명령어 해석기 입니다. 종료를 위해선 \"exit\"를 입력하세요.");
		for(boolean isRun = true; isRun; ) {
			System.out.print(curDir.getName()+"> ");
			String line = scanner.nextLine();
			String [] args = line.split(" ");
			String cmd = args[0];
			try {
				switch(cmd) {
				case "exit" : isRun = false; break;
				case "cat" : cat(args); break;
				case "cd" : cd(args); break;
				case "cp" : cp(args); break;
				case "ls" : ls(args); break;
				case "mkdir" : mkdir(args); break;
				case "mv" : mv(args); break;
				case "pwd" : pwd(args); break;
				case "rm" : rm(args); break;
				case "" : break;
				default : System.out.println(cmd + ": 인식할 수 없는 명령어입니다."); break;
				}
			}
			catch(IOException e) {
				System.out.println(e);
			}
		}
		System.out.println("명렁어 해석기를 종료합니다.");
		scanner.close();
	}
	public static void main(String[] args) {
		Shell shell = new Shell();
		shell.run();
	}
}
