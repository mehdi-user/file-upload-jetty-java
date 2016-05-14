package jettyFileUploadPackage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class JettyUploadFile
{
    public static void main( String[] args ) throws Exception
    {
        // Create a basic jetty server object that will listen on port 8080.
        // Note that if you set this to port 0 then a randomly available port
        // will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
        Server server = new Server(8080);

        // The ServletHandler is a dead simple way to create a context handler
        // that is backed by an instance of a Servlet.
        // This handler then needs to be registered with the Server object.
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        // Passing in the class for the Servlet allows jetty to instantiate an
        // instance of that Servlet and mount it on a given context path.

        // IMPORTANT:
        // This is a raw Servlet, not a Servlet that has been configured
        // through a web.xml @WebServlet annotation, or anything similar.
        handler.addServletWithMapping(FileUploadHandler.class, "/*");

        // Start things up!
        server.start();

        // The use of server.join() the will make the current thread join and
        // wait until the server is done executing.
        // See
        // http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
        server.join();
    }
    
    @SuppressWarnings("serial")
	public static class FileUploadHandler extends HttpServlet 
	{
        private final String UPLOAD_DIRECTORY = "/home/username" + File.separator + "upload_dir";
        
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                            		  throws ServletException, IOException {
        	// GET...
        }
      
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
          
            //process only if its multipart content
            if(ServletFileUpload.isMultipartContent(request)){
                try {
                	System.out.println("inTry");
                    List<FileItem> multiparts = new ServletFileUpload(
                                             new DiskFileItemFactory()).parseRequest(request);
                  
                    for(FileItem item : multiparts){
                        if(!item.isFormField()){
                            String name = new File(item.getName()).getName();
                            item.write( new File( UPLOAD_DIRECTORY + File.separator + name));
                        }
                    }
               
                   //File uploaded successfully
                    response.getWriter().println("File Uploaded Successfully");
                    response.setStatus(HttpServletResponse.SC_OK);
                    
                } catch (Exception ex) {
                	response.getWriter().println("File Upload Failed due to " + ex);
                	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }          
             
            } else {
            	response.getWriter().println("Sorry this Servlet only handles file upload request");
            	request.setAttribute("message", "Sorry this Servlet only handles file upload request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            
            response.setContentType("text/html");
            response.getWriter().println("<h1>end</h1>");
         
        }
    }
}