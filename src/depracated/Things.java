import java.io.File;


public class Things {

	public static void main(String[] args) {
	    walkin(new File("./")); //Replace this with a suitable directory
	}
 	//recursive walk through files and folders
	public static void walkin(File dir) {

	    File listFile[] = dir.listFiles();
	    if (listFile != null) {
	        for (int i=0; i<listFile.length; i++) {
	            if (listFile[i].isDirectory()) {
	              System.out.println("|\t\t");  
	              walkin(listFile[i]);
	            } else {
	                System.out.println("+---"+listFile[i].getAbsolutePath().toString());
	            }
	        }
	    }
	}
}