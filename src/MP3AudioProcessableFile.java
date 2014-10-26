import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MP3AudioProcessableFile implements AudioProcessableFile {

    private final File file;
    private final InputStream audioFileInputStream;
    
    /**
     * Constructor : String -> MP3AudioProcessableFile
     * 
     * @param filePath
     *            : The file path for which an AudioProcessableFile has to be
     *            created
     * @effect : The constructor implicitly returns an instance of type MP3AudioProcessableFile
     */
    public MP3AudioProcessableFile(String filePath) {
        this.file = new File(filePath);
        this.audioFileInputStream = getInputStream();
        validateFile();
    }
    
    private InputStream getInputStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            AssertTests.assertTrue(file + " File not found", false);
            // won't reach here since assertTrue will exit the system (dead code)
            return null;
        }
    }

    @Override
    public float[] readSamples() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void validateFile() {
        // Reference file for validation MP3 format: 
        //http://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header
        
//        this.audioFileInputStream
        

    }

    @Override
    public void compare(AudioProcessableFile fileToCmp) {
        // TODO Auto-generated method stub

    }

    @Override
    public long getFileLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getFileShortName() {
        // TODO Auto-generated method stub
        return null;
    }

}
