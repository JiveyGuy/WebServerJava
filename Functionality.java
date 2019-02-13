public class Functionality{
  private String location;
  private String type;
  private String name;
  private final boolean DEBUG = false;
  private final String[] TYPES = {"html","gif","png","jpeg"};
  
  public Functionality(String location) throws InvalidLocationException{
    this.name = findName(location);
    this.type = findType(location);
    this.location = (System.getProperty("user.dir") + findName(location) + "." + findType( location ));
  }
  
  private void parseFileInfo(){
    
  }
  
  /**
   * Checks that a file exists, is html (if no .html type it's added ), is not dir, if is too short to have .html tag
   * Will auto add .html if missing. 
   **/
  private boolean fileExists(String input){
    String fileName = (System.getProperty("user.dir") + findName(input) + "." + findType( input ));
    File file = new File ( fileName );
    return file.exists() && 
      !file.isDirectory() && 
      file.isFile();
  }
  
  private String findType(String input){
    String[] temp = input.split("//.");
    if( temp.length > 0 ){
      if( isValidType( temp[ temp.length - 1 ] ) ){
        return temp[ temp.length - 1 ];
      } else {
        for( String itor : TPYES ) {
          if( (new File( input + "." + itor )).exists() )
            return itor;       
        }
        throw new InvalidLocationException("no file found for " + input); 
      }
    }
  
  private boolean isValidType(String test){
    test = test.toLowerCase().trim();
    for( String itor : TYPES )
      if ( test.equals( itor ) ) return true;
    return false;
  }
  
  public static void main(String args[]){
    switch( Integer.parseInt( args[0] ) ){
      case 0 :
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
  
}

class InvalidLocationException extends Exception{
  public InvalidLocationException(String s){
    super( s ); 
  }
}