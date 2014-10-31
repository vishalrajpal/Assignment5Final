import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * class AudioProcessableFiles: Description: This class converts a physical path
 * to an instance of AudioProcessableFile. The instance is only returned if and
 * only if the file is of supported type.
 * 
 * CURRENT SUPPORTED TYPE/s: ["WAVE"]
 * 
 * WAVE: The audio file's header is validated against a specific format for a
 * WAVE file. If the header is valid below are the operations possible on an
 * instance of AudioProcessableFile
 * 
 * a) readSamples: Reads the audio samples of the audio file. b) compare:
 * Compares another instance of AudioProcessableFile with this. c)
 * getFileLength: Returns the file length of the audio file corresponding to
 * this.
 */

public abstract class AudioProcessableFiles {
    /**
     * make : String -> AudioProcessableFile
     * 
     * @param filePath
     *            : The filePath for which to create a AudioProcessableFile
     * @return processableFile: If the value of 'filePath' is null throws
     *         NullPointerException, else returns an instance of type
     *         AudioProcessableFile
     */
    public static AudioProcessableFile make(String filePath) {
        if (filePath != null) {
            if (filePath.endsWith(".wav")) {
                AudioProcessableFile processableFile = new WAVAudioProcessableFile(
                        filePath);
                return processableFile;
            } else if (filePath.endsWith(".mp3")) {
                AudioProcessableFile processableFile = new MP3AudioProcessableFile(
                        filePath);
                return processableFile;
            } else {
                String exString = "ERROR: File Format not found : " + filePath;
                IllegalArgumentException ex = new IllegalArgumentException(
                        exString);
                throw ex;
            }
        } else {
            String exString = "ERROR: Passed Invalid File Path : " + filePath;
            NullPointerException ex = new NullPointerException(exString);
            throw ex;
        }
    }

    /** Implementation of AudioProcessableFile ADT */
    private static abstract class AudioProcessableBase implements
            AudioProcessableFile {
        /* @see AudioProcessableFile#readSamples() */
        public abstract float[] readSamples();

        /* @see AudioProcessableFile#validateFile() */
        public abstract void validateFile();

        /* @see AudioProcessableFile#compare(AudioProcessableFile) */
        public abstract void compare(AudioProcessableFile ap);

        /* @see AudioProcessableFile#getFileLength() */
        public abstract long getFileLength();

        /* @see AudioProcessableFile#getFileShortName() */
        public abstract String getFileShortName();
    }

    /** Create new AudioProcessableFile */
    private static class WAVAudioProcessableFile extends AudioProcessableBase {
        // Constants
        private final static int RIFF_HEXA_EQUIVALENT = 0x46464952;
        private final static int WAVE_HEXA_EQUIVALENT = 0x45564157;
        private final static int fmt_HEXA_EQUIVALENT = 0x20746D66;
        private final static int data_HEXA_EQUIVALENT = 0x61746164;
        private final static int AUDIO_FORMAT_EQUIVALENT = 1;
        private final static int STEREO_EQUIVALENT = 2;
        private final static int WAVE_SAMPLING_RATE = 44100;
        private final static int BITS_PER_SAMPLE = 16;

        // Instance Variables
        private long fileLength;
        private int noOfSamplesPerChannel;
        private int bitsPerSample;
        private int bytesPerSample;
        private int noOfChannels;
        private int bytesPerFrame;
        private int samplesPerFrame;
        private int frameLength;
        private FileInputStream audioFileInputStream;
        private File audioFile;
        private String filePath;

        /**
         * Constructor : String -> WAVAudioProcessableFile
         * 
         * @param filePath
         *            : The file path for which an AudioProcessableFile has to
         *            be created
         * @effect : The constructor implicitly returns an instance of type
         *         Create
         */
        WAVAudioProcessableFile(String filePath) {
            this.filePath = filePath;
            fetchFileIntoFileInputStream();
            validateFile();
        }

        /**
         * fetchFileIntoFileInputStream : -> void
         * 
         * @effect: Creates and loads a new FileInputStream with the file
         *          located at 'filePath'. If file not found throws FileNotFound
         *          Exception.
         */
        private void fetchFileIntoFileInputStream() {
            audioFile = new File(filePath);
            try {
                audioFileInputStream = new FileInputStream(audioFile);
            } catch (FileNotFoundException e) {
                AssertTests.assertTrue(filePath + " File not found", false);
            }
        }

        /* @see AudioProcessableFiles.AudioProcessableBase#validateFile() */
        public void validateFile() {
            byte[] arrayFor2Bytes = new byte[2];
            byte[] arrayFor4Bytes = new byte[4];
            try {
                String notSupportedFormatError = " is not a supported format";
                // First 4 bytes are 'RIFF'
                audioFileInputStream.read(arrayFor4Bytes);
                String riffErr = filePath + notSupportedFormatError;
                long riffLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
                AssertTests.assertTrue(riffErr,
                        riffLitEnd == RIFF_HEXA_EQUIVALENT);

                // Skip the chunkSize
                audioFileInputStream.skip(4);

                // These 4 bytes should be WAVE'
                audioFileInputStream.read(arrayFor4Bytes);
                String waveErr = filePath + notSupportedFormatError;
                long waveLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
                AssertTests.assertTrue(waveErr,
                        waveLitEnd == WAVE_HEXA_EQUIVALENT);

                // These 4 bytes should be 'fmt '
                audioFileInputStream.read(arrayFor4Bytes);
                String fmtError = filePath + " The chunk should be of type fmt";
                long fmtLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
                AssertTests.assertTrue(fmtError,
                        fmtLitEnd == fmt_HEXA_EQUIVALENT);

                // Skip the chunkSize
                audioFileInputStream.skip(4);

                // The AudioFormat should be 1 i.e. PCM (Linear Quantization)
                audioFileInputStream.read(arrayFor2Bytes);
                String pcmError = filePath
                        + " The Audio Format should be of type PCM";
                long pcmLitEnd = getLittleEndian(arrayFor2Bytes, 0, 2);
                AssertTests.assertTrue(pcmError,
                        pcmLitEnd == AUDIO_FORMAT_EQUIVALENT);

                // These 2 bytes should mention number of channels and should be
                // 2(Stereo)
                audioFileInputStream.read(arrayFor2Bytes);
                String noOfChanError = filePath
                        + " The audio should be of type Stereo";
                noOfChannels = (int) getLittleEndian(arrayFor2Bytes, 0, 2);
                AssertTests.assertTrue(noOfChanError,
                        noOfChannels == STEREO_EQUIVALENT);

                // The Sample rate should be 44.1 kHz
                audioFileInputStream.read(arrayFor4Bytes);
                String samRtError = filePath
                        + " The sampling rate should be 44.1 kHz";
                long samRtLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
                AssertTests.assertTrue(samRtError,
                        samRtLitEnd == WAVE_SAMPLING_RATE);

                // Skip the ByteRate(4 Bytes) and BlockAlign(2 Bytes)
                audioFileInputStream.skip(6);

                // Bits per Sample should be 16
                audioFileInputStream.read(arrayFor2Bytes);
                String bitError = filePath + " There should be 16 bits/sample";
                bitsPerSample = (int) getLittleEndian(arrayFor2Bytes, 0, 2);
                AssertTests.assertTrue(bitError,
                        bitsPerSample == BITS_PER_SAMPLE);

                bytesPerSample = bitsPerSample / 8;

                // The data chunk gets started and should start with 'data' for
                // 4 bytes
                audioFileInputStream.read(arrayFor4Bytes);
                String dataError = filePath
                        + " There should be a proper data chunk";
                long dataLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
                AssertTests.assertTrue(dataError,
                        dataLitEnd == data_HEXA_EQUIVALENT);

                // The next 4 bytes determine the length of the data chunk
                audioFileInputStream.read(arrayFor4Bytes);
                fileLength = getLittleEndian(arrayFor4Bytes, 0, 4);

                bytesPerFrame = bytesPerSample * noOfChannels;
                samplesPerFrame = bytesPerFrame / bytesPerSample;
                frameLength = (int) fileLength / bytesPerFrame;
                noOfSamplesPerChannel = (frameLength * samplesPerFrame)
                        / noOfChannels;

            } catch (IOException e) {
                AssertTests
                        .assertTrue(filePath + " Invalid File Header", false);
            }
        }

        /* @see AudioProcessableFiles.AudioProcessableBase#readSamples() */
        public float[] readSamples() {
            float[] readSamples = new float[noOfSamplesPerChannel];
            byte[] twoByteArray = new byte[2];
            float toDivide = (float) (2 << 15);
            for (int i = 0; i < noOfSamplesPerChannel; i++) {
                try {
                    audioFileInputStream.read(twoByteArray);
                    readSamples[i] = (float) getLittleEndian(twoByteArray, 0, 2)
                            / toDivide;
                    audioFileInputStream
                            .skip(2 * noOfChannels / bytesPerSample);
                } catch (IOException e) {
                    AssertTests.assertTrue(filePath + " I/O Error", false);
                }
            }
            return readSamples;
        }

        /*
         * @see
         * AudioProcessableFiles.AudioProcessableBase#compare(AudioProcessableFile
         * )
         */
        public void compare(AudioProcessableFile fileToCmp) {
            if (getFileLength() != fileToCmp.getFileLength()) {
                printNoMatchAndExit();
            }
            FFT thisFFT = new FFT(readSamples());
            FFT fileToCmpFFT = new FFT(fileToCmp.readSamples());
            int mse = thisFFT
                    .calculateMSE(fileToCmpFFT.getTransformedSamples());
            if (mse == 0) {
                String fileToCmpShrtName = fileToCmp.getFileShortName();
                printMatchAndExit(getFileShortName(), fileToCmpShrtName);
            } else {
                printNoMatchAndExit();
            }
        }

        /* @see AudioProcessableFiles.AudioProcessableBase#getFileLength() */
        public long getFileLength() {
            return fileLength;
        }

        /* @see AudioProcessableFiles.AudioProcessableBase#getFileShortName() */
        public String getFileShortName() {
            return audioFile.getName();
        }
    }

    /**
     * getLittleEndian : byte[], int, int -> long
     * 
     * @param arr
     *            : The array of type byte, the values of which will be
     *            converted to little endian
     * @param offset
     *            : The offset from where to start in 'arr'
     * @param numOfBytes
     *            : The number of indexes to convert
     * @return val : The little endian value of the values in the arr starting
     *         from 'offset' and ending at 'numOfBytes' from 'offset'
     */
    private static long getLittleEndian(byte[] arr, int offset, int numOfBytes) {
        numOfBytes--;
        int endIndex = offset + numOfBytes;
        long val = 0;
        if (endIndex > arr.length)
            return val;

        val = arr[endIndex] & 0xFF;
        for (int i = 0; i < numOfBytes; i++) {
            val = (val << 8) + (arr[--endIndex] & 0xFF);
        }
        return val;
    }

    private static void printMatchAndExit(String fileName1, String fileName2) {
        System.out.println("MATCH " + fileName1 + " " + fileName2);
        System.exit(0);
    }

    private static void printNoMatchAndExit() {
        System.out.println("NO MATCH");
        System.exit(0);
    }

    private static class MP3AudioProcessableFile extends AudioProcessableBase {

        private static final int first16bitsWithCRC = 0xFFFA;
        private static final int first16bitsWithoutCRC = 0xFFFB;

        private static final HashMap<Integer, Integer> samplingRateMap = new HashMap<Integer, Integer>();
        private static final HashMap<Integer, Integer> bitRateMap = new HashMap<Integer, Integer>();

        static {
            samplingRateMap.put(0, 44100);
            samplingRateMap.put(1, 48000);
            samplingRateMap.put(2, 32000);

            bitRateMap.put(0, 0);
            bitRateMap.put(1, 32000);
            bitRateMap.put(2, 40000);
            bitRateMap.put(3, 48000);
            bitRateMap.put(4, 56000);
            bitRateMap.put(5, 64000);
            bitRateMap.put(6, 80000);
            bitRateMap.put(7, 96000);
            bitRateMap.put(8, 112000);
            bitRateMap.put(9, 128000);
            bitRateMap.put(10, 160000);
            bitRateMap.put(11, 192000);
            bitRateMap.put(12, 224000);
            bitRateMap.put(13, 256000);
            bitRateMap.put(14, 320000);

        }

        private final InputStream audioFileInputStream;
        private final String fileName;
        private boolean isCRC = false;

        /**
         * Constructor : String -> MP3AudioProcessableFile
         * 
         * @param filePath
         *            : The file path for which an AudioProcessableFile has to
         *            be created
         * @effect : The constructor implicitly returns an instance of type
         *         MP3AudioProcessableFile
         */
        private MP3AudioProcessableFile(String filePath) {
            this.fileName = new File(filePath).getName();
            this.audioFileInputStream = getInputStream(filePath);
            System.out.println("First check");
            validateFile();
            System.out.println("Second check");
            validateFile();
            System.out.println("Third check");
            validateFile();
            // Validating three headers to confirm it is a mp3
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
            /*
             * Verifying if it is Layer3 with/without CRC
             */
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

                // Reads the next byte which contains the bit rate, frequency
                // and padding.
                byte[] bitRateFreqPad = new byte[1];
                audioFileInputStream.read(bitRateFreqPad);
                // After reading we get the byte from the array[1]
                final byte byteRateFreqPad = bitRateFreqPad[0];

                // Get the bits that are required for bitRate in header
                int intBitRate = byteRateFreqPad & 0x000000F0;
                int bitRateIndex = intBitRate >>> 4;
                // Bit Rate 15 is reserved; hence invalid
                AssertTests.assertTrue("Bit Rate is invalid",
                        bitRateIndex != 15);
                int bitRate = bitRateMap.get(bitRateIndex);
                System.out.println("Bit Rate: " + bitRate);

                // Get the bits that are required for Sampling rate in header
                int intSamplingRate = byteRateFreqPad & 0x0000000C;
                int samplingRateIndex = intSamplingRate >>> 2;
                // Sampling Rate 3 is reserved; hence invalid
                AssertTests.assertTrue("Sampling Rate is invalid",
                        samplingRateIndex != 3);
                int samplingRate = samplingRateMap.get(samplingRateIndex);
                System.out.println("Sampling Rate: " + samplingRate);

                // Get the bit that is required for padding in header
                int intPadding = byteRateFreqPad & 0x00000002;
                int padding = intSamplingRate >>> 1;

                byte[] lastHeaderByte = new byte[1];
                audioFileInputStream.read(lastHeaderByte);
                byte lastbyte = lastHeaderByte[0];
                int intChannel = lastbyte & 0x000000C0;
                int channel = intChannel >>> 6;
                // Based on Piazza post mono or stereo can be given.
                int FrameLengthInBytes = 144 * bitRate / samplingRate + padding;
                System.out.println("Frame length in bytes: "
                        + FrameLengthInBytes);

                // CRC is 16 bits long and, if it exists, immediately follows the
                // frame header. After the CRC comes the audio data.

                final int CRCBytes = 2;
                final int AudioDataInBytes;
                if (isCRC == true) {
                    AudioDataInBytes = FrameLengthInBytes - CRCBytes;
                    System.out.println("Data in bytes with CRC: "
                            + AudioDataInBytes);
                    audioFileInputStream.skip(2);
                } else {
                    AudioDataInBytes = FrameLengthInBytes;
                    System.out.println("Data in bytes without CRC: "
                            + AudioDataInBytes);
                }

                audioFileInputStream.skip(AudioDataInBytes);

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
            return fileName;
        }

        private static short byteArrToShort(byte[] x) {
            ByteBuffer wrapped = ByteBuffer.wrap(x);
            short num = wrapped.getShort();
            return num;
        }

    }

}