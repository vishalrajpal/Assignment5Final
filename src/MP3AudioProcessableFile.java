import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MP3AudioProcessableFile implements AudioProcessableFile {

    private static final int first16bitsWithCRC = 0xFFFA;
    private static final int first16bitsWithoutCRC = 0xFFFB;

    private final InputStream audioFileInputStream;
    private boolean isCRC = false;

    /**
     * Constructor : String -> MP3AudioProcessableFile
     * 
     * @param filePath
     *            : The file path for which an AudioProcessableFile has to be
     *            created
     * @effect : The constructor implicitly returns an instance of type
     *         MP3AudioProcessableFile
     */
    public MP3AudioProcessableFile(String filePath) {
        this.audioFileInputStream = getInputStream(filePath);
        validateFile();
    }

    private InputStream getInputStream(String filePath) {
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            AssertTests.assertTrue(filePath + " File not found", false);
            // won't reach here since assertTrue will exit the system (dead
            // code)
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
        // http://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header
        try {
            byte[] first16bits = new byte[2];
            audioFileInputStream.read(first16bits);
            short first2bytes = byteArrToShort(first16bits);
            if (first2bytes == first16bitsWithCRC) {
                isCRC = true;

            } else if (first2bytes != first16bitsWithoutCRC) {
                isCRC = false;
            } else {
                AssertTests.assertTrue(
                        "Invalid first 16 bits in the MP3 header", false);
            }

            // Reads the next byte which contains the bit rate, frequency and
            // pad
            // bit.
            byte[] bitRateFreqPad = new byte[1];
            audioFileInputStream.read(bitRateFreqPad);
            // After reading we get the byte from the array[1]
            final byte byteRateFreqPad = bitRateFreqPad[0];

            // Get the bits that are required for bitRate in header
            int intBitRate = byteRateFreqPad & 0x000000F0;
            int bitRate = intBitRate >>> 4;
            // Bit Rate 15 is reserved; hence invalid
            AssertTests.assertTrue("Bit Rate is invalid", bitRate != 15);

            // Get the bits that are required for Sampling rate in header
            int intSamplingRate = byteRateFreqPad & 0x0000000C;
            int samplingRate = intSamplingRate >>> 2;
            // Sampling Rate 3 is reserved; hence invalid
            AssertTests.assertTrue("Sampling Rate is invalid",
                    samplingRate != 3);

            // Get the bit that is required for padding in header
            int intPadding = byteRateFreqPad & 0x00000002;
            int padding = intSamplingRate >>> 1;

            byte[] lastHeaderByte = new byte[1];
            audioFileInputStream.read(lastHeaderByte);
            byte lastbyte = lastHeaderByte[0];
            int intChannel = lastbyte & 0x000000C0;
            int channel = intChannel >>> 6;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void compare(AudioProcessableFile fileToCmp) {

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

    private static short byteArrToShort(byte[] x) {
        ByteBuffer wrapped = ByteBuffer.wrap(x); // big-endian by default
        short num = wrapped.getShort();
        return num;
    }

}
