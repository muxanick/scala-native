package javax.tools

import java.io.File
import java.lang.{Iterable, String}
import scala.scalanative.annotation.stub

/** File manager based on java.io.File.  A common way
 *  to obtain an instance of this class is using getStandardFileManager, for example:
 * 
 *  
 *    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 *     DiagnosticCollector<JavaFileObject> diagnostics =
 *        new  DiagnosticCollector<JavaFileObject>();
 *    StandardJavaFileManager fm = compiler.getStandardFileManager(diagnostics, null, null);
 *  
 * 
 *  This file manager creates file objects representing regular
 *  files,
 *  zip file entries, or entries in
 *  similar file system based containers.  Any file object returned
 *  from a file manager implementing this interface must observe the
 *  following behavior:
 * 
 *  
 *    
 *      File names need not be canonical.
 *    
 *    
 *      For file objects representing regular files
 *      
 *        
 *          the method FileObject.delete()
 *          is equivalent to File.delete(),
 *        
 *        
 *          the method FileObject.getLastModified()
 *          is equivalent to File.lastModified(),
 *        
 *        
 *          the methods FileObject.getCharContent(boolean),
 *          FileObject.openInputStream(), and
 *          FileObject.openReader(boolean)
 *          must succeed if the following would succeed (ignoring
 *          encoding issues):
 *          
 *            new FileInputStream(new File(fileObject.toUri()))
 *          
 *        
 *        
 *          and the methods
 *          FileObject.openOutputStream(), and
 *          FileObject.openWriter() must
 *          succeed if the following would succeed (ignoring encoding
 *          issues):
 *          
 *            new FileOutputStream(new File(fileObject.toUri()))
 *          
 *        
 *      
 *    
 *    
 *      The URI returned from
 *      FileObject.toUri()
 *      
 *        
 *          must be absolute (have a schema), and
 *        
 *        
 *          must have a normalized
 *          path component which
 *          can be resolved without any process-specific context such
 *          as the current directory (file names must be absolute).
 *        
 *      
 *    
 *  
 * 
 *  According to these rules, the following URIs, for example, are
 *  allowed:
 *  
 *    
 *      file:///C:/Documents%20and%20Settings/UncleBob/BobsApp/Test.java
 *    
 *    
 *      jar:///C:/Documents%20and%20Settings/UncleBob/lib/vendorA.jar!com/vendora/LibraryClass.class
 *    
 *  
 *  Whereas these are not (reason in parentheses):
 *  
 *    
 *      file:BobsApp/Test.java (the file name is relative
 *      and depend on the current directory)
 *    
 *    
 *      jar:lib/vendorA.jar!com/vendora/LibraryClass.class
 *      (the first half of the path depends on the current directory,
 *      whereas the component after ! is legal)
 *    
 *    
 *      Test.java (this URI depends on the current
 *      directory and does not have a schema)
 *    
 *    
 *      jar:///C:/Documents%20and%20Settings/UncleBob/BobsApp/../lib/vendorA.jar!com/vendora/LibraryClass.class
 *      (the path is not normalized)
 *    
 *  
 */
trait StandardJavaFileManager extends JavaFileManager {

    /** Gets file objects representing the given files. */
    @stub
    def getJavaFileObjects(files: File*): Iterable[_ <: JavaFileObject] = ???

    /** Gets file objects representing the given file names. */
    //@stub
    //def getJavaFileObjects(names: String*): Iterable[_ <: JavaFileObject] = ???

    /** Gets file objects representing the given files. */
    @stub
    def getJavaFileObjectsFromFiles(files: Iterable[_ <: File]): Iterable[_ <: JavaFileObject] = ???

    /** Gets file objects representing the given file names. */
    @stub
    def getJavaFileObjectsFromStrings(names: Iterable[String]): Iterable[_ <: JavaFileObject] = ???

    /** Gets the path associated with the given location. */
    @stub
    def getLocation(location: JavaFileManager.Location): Iterable[_ <: File] = ???

    /** Compares two file objects and return true if they represent the
     *  same canonical file, zip file entry, or entry in any file
     *  system based container.
     */
    @stub
    override def isSameFile(a: FileObject, b: FileObject): Boolean = ???

    /** Associates the given path with the given location. */
    @stub
    def setLocation(location: JavaFileManager.Location, path: Iterable[_ <: File]): Unit = ???
}
