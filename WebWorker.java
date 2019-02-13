/**
 * Web worker: an object of this class executes in its own new thread
 * to receive and respond to a single HTTP request. After the constructor
 * the object executes on its "run" method, and leaves when it is done.
 *
 * One WebWorker object is only responsible for one client connection. 
 * This code uses Java threads to parallelize the handling of clients:
 * each WebWorker runs in its own thread. This means that you can essentially
 * just think about what is happening on one client at a time, ignoring 
 * the fact that the entirety of the webserver execution might be handling
 * other clients, too. 
 *
 * This WebWorker class (i.e., an object of this class) is where all the
 * client interaction is done. The "run()" method is the beginning -- think
 * of it as the "main()" for a client interaction. It does three things in
 * a row, invoking three methods in this class: it reads the incoming HTTP
 * request; it writes out an HTTP header to begin its response, and then it
 * writes out some HTML content for the response content. HTTP requests and
 * responses are just lines of text (in a very particular format). 
 *
 **/

import java.net.Socket;
import java.lang.Runnable;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.Scanner;

public class WebWorker implements Runnable
{
  private Socket socket;
  private final boolean DEBUG = true;
  
  /**
   * print debugging statements, works for iterables and single strings
   * ( info ) to print out print former System.err messages set DEBUG = true
   * @param input the string to print
   */
  private void debug(String...input){
    if( !DEBUG )return;
    for( String itor : input )
      System.out.println("DEBUG::  "+itor);
  }
  
  /**
   * Constructor: must have a valid open socket
   **/
  public WebWorker(Socket s)
  {
    socket = s;
  }
  
  /**
   * Worker thread starting point. Each worker handles just one HTTP 
   * request and then returns, which destroys the thread. This method
   * assumes that whoever created the worker created it with a valid
   * open socket object.
   **/
  public void run()
  {
    
    String locationString = "";
    debug("Connection incomming.");
    
    try {
      
      InputStream  is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      try{ 
        
        locationString = readHTTPRequest(is).trim();
        Functionality funcs = new Functionality( locationString );
        writeHTTPHeader(os, funcs.getContentType());
        writeContent(os, funcs);
      } catch ( InvalidLocationException ile ){
        
        debug( ile.toString() );
        write404(os,  locationString);
      }
      
      os.flush();
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
      debug("Output error: " + e.toString());
    }
    
    debug("Done handling connection.");
    return;
  }
  
  /**
   * Read the HTTP request header.
   **/
  private String readHTTPRequest(InputStream is)
  {
    String line, result = "/default";
    BufferedReader r = new BufferedReader(new InputStreamReader(is));
    
    while (true) {
      try {
        while (!r.ready()) Thread.sleep(1);
        line = r.readLine();
        if( line.contains("GET") && !line.contains("GET / HTTP/1.1")){
          
          //debug("is request and unique location string = " + line.substring(3,line.length()-8));
          result = line.substring(4,line.length()-8);
          
        } else {
          //debug("Is not request, no location string. Result default.");
        }
        
        debug("Request line: ("+line+")");
        if (line.length()==0) break;
        
      } catch (Exception e) {
        debug("Request error: "+e);
        break;
      }
    }
    return result;
  }
  
  /**
   * Write the HTTP header lines to the client network connection.
   * @param os is the OutputStream object to write to
   * @param contentType is the string MIME content type (e.g. "text/html")
   **/
  private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
  {
    Date d = new Date();
    DateFormat df = DateFormat.getDateTimeInstance();
    df.setTimeZone(TimeZone.getTimeZone("GMT")); 
    debug("response http OK");
    os.write("HTTP/1.1 200 OK\n".getBytes());
    os.write("Date: ".getBytes());
    os.write((df.format(d)).getBytes());
    os.write("\n".getBytes());
    os.write("Server: Jon's very own server\n".getBytes());
    os.write("Connection: close\n".getBytes());
    os.write("Content-Type: ".getBytes());
    os.write(contentType.getBytes());
    os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
    return;
  }
  
  /**
   * Write the HTTP 404 message
   * @param os is the OutputStream object to write to
   * @param contentType is the string MIME content type (e.g. "text/html")
   **/
  private void write404(OutputStream os, String locationString) throws Exception
  {
    os.write(("HTTP/1.0 404 Not Found\r\n"+
              "Content-type: text/html\r\n\r\n"+
              "<!DOCTYPE html PUBLIC \"-//IETF//"+
              "DTD HTML 2.0//EN\"><html><head><meta"+
              " http-equiv=\"content-type\" content=\"text/html;"+
              " charset=windows-1252\"><title>404 Not Found</title></head><body>"+
              "<h1>Not Found</h1><p>The requested URL "+locationString+
              " was not found on this server.</p></body></html>").getBytes());
    return;
  }
  
  /**
   * Write the data content to the client network connection. This MUST
   * be done after the HTTP header has been written out.
   * @param os is the OutputStream object to write to
   **/
  private void writeContent(OutputStream os, Functionality funcs) throws Exception
  {
 
    if(funcs.getType().equals("html")){
      String[] data = funcs.getFileData();
      for( String itor :  data ){
        
        debug( "writting : " + itor );
        os.write( (itor+"\r\n").getBytes() );
      }
    } else {
      ByteArrayOutputStream byteData = funcs.getByteFile();
      byteData.writeTo( os ); 
    }
  }  
  
} 


