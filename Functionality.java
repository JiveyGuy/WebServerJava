import java.net.Socket;
import java.lang.Runnable;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.Scanner;
import java.io.ByteArrayOutputStream;

public class Functionality{
  
  
  private String location;
  private String type;
  private String name;
  private static final boolean STATIC_DEBUG = true;
  private final boolean DEBUG = STATIC_DEBUG;
  private final String[] TYPES = {"html","gif","png","jpeg","ico"};
  
  
  public Functionality(String location) throws InvalidLocationException{
    this.name = findName(location);
    this.type = findType(location);
    this.location = (System.getProperty("user.dir") + name + "." + type).trim();
  }  
  
  
  public String getLocation(){
    return location;
  }
  
  
  public String getType(){
    return type; 
  }
  
  
  public String getName(){
    return name; 
  }
  
  
  private String[] getHTMLFile() throws Exception{
    String result = "";
    
    Scanner file = new Scanner(new File( location ));
    Date d = new Date();
    DateFormat df = DateFormat.getDateTimeInstance();
    while( file.hasNextLine() ){
      result += file.nextLine().replace("<cs371date>",df.format(d)).replace("<cs371server>","Jivey's Workstation")
        + "linebreakitemthatnoonehaseverusedbefore";
    }
    return result.split("linebreakitemthatnoonehaseverusedbefore");
    
  }
  
  
  public String[] getFileData() throws Exception{
    return getHTMLFile();
  }
  
  
  public ByteArrayOutputStream getByteFile() throws Exception{
    
    InputStream inputStream = new FileInputStream(location);
    int byteRead;
    ByteArrayOutputStream bb = new ByteArrayOutputStream();
    while ((byteRead = inputStream.read()) != -1) {
      bb.write(byteRead);
    }
    return bb;
  }
  
  public String getContentType(){
    if( type.equals( "html" ) )
      return "text/html";
    else 
      return "image/"+type;
    
  }
  
  /**
   * Checks that a file exists, is html (if no .html type it's added ), is not dir, if is too short to have .html tag
   * Will auto add .html if missing. 
   **/
  private boolean fileExists(String input){
    File file = new File ( location );
    return file.exists() && 
      !file.isDirectory() && 
      file.isFile();
  }
  
  
  private String findName(String input) throws InvalidLocationException{
    debug( "Calling findName with input = " + input );
    String[] temp = input.split("\\.");
    String result;
    if( temp.length > 1 ){
      debug( "findName: temp.length > 0 temp[l-1] == " + temp[ temp.length - 1 ]);
      if( isValidType( temp[ temp.length - 1 ] ) ){
        result = temp[0];
        debug("is validType for; " + temp[ temp.length - 1] +"\n Starting name append with; \""+result+"\"");
        for(int x = 1; x < temp.length-1; x++)
          result += "." + temp[x];
        debug("Final name version = " + result);
        return result;
      }
    } else {
      
      debug("temp.length < 0");
      for( String itor : TYPES ) {
        debug("Checking if " + System.getProperty("user.dir") + input + "." + itor + " exists ");
        if( (new File( System.getProperty("user.dir") + input + "." + itor )).exists() )
          return input;       
      }
    }
    throw new InvalidLocationException("no file found for " + input); 
  }
  
  
  private String findType(String input) throws InvalidLocationException{
    String[] temp = input.split("\\.");
    if( temp.length > 1 ){
      if( isValidType( temp[ temp.length - 1 ] ) ){
        return temp[ temp.length - 1 ];
      } 
    } else {
      for( String itor : TYPES ) {
        if( (new File( System.getProperty("user.dir") + input + "." + itor )).exists() )
          return itor;       
      }
    } 
    throw new InvalidLocationException("Cannot find type for " + input );
  }
  
  
  private boolean isValidType(String test){
    test = test.toLowerCase().trim();
    for( String itor : TYPES )
      if ( test.equals( itor ) ) return true;
    return false;
  }
  
  
  public static void main(String args[]) throws InvalidLocationException{
    switch( Integer.parseInt( args[0] ) ){
      case 0 :
        sDebug( "calling main tester for constructor with string = " + args[1] );
        Functionality test;
        test = new Functionality( args[1] );
        System.out.println( "TESTING CONST:\nlocation = " + test.location + "\nname = " + test.name + "\ntype = " + test.type);
        break;
    } 
  }
  
  
  public void debug(String...input){
    if ( !DEBUG ) return;
    for( String itor : input )
      System.out.println("DEBUG$$: " + itor);
  }  
  
  
  public static void sDebug(String...input){
    if ( !STATIC_DEBUG ) return;
    for( String itor : input )
      System.out.println("DEBUG$$: " + itor);
  }  
}




class InvalidLocationException extends Exception{
  public InvalidLocationException(String s){
    super( s ); 
  }
}